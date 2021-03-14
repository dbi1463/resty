/* InvokableRequest.kt created on 2021/2/27
 *
 * Copyright (c) 2021 Spirit Tu <dbi1463@gmail.com>
 *
 * This file is part of resty under the MIT license.
 */
package tw.funymph.resty.java

import java.net.URI.create;
import java.net.URLEncoder.encode;
import java.nio.charset.StandardCharsets.UTF_8;
import java.net.http.HttpClient;
import java.net.http.HttpResponse.BodyHandlers.ofString;
import java.net.http.HttpRequest.BodyPublishers.ofString;

import tw.funymph.resty.Request
import tw.funymph.resty.RequestException
import tw.funymph.resty.Response
import java.net.http.HttpRequest

private val sharedClient = HttpClient.newHttpClient()

/**
 * This class provides the default implementation of {@link Request}.
 *
 * @author Spirit Tu
 * @version 1.0
 * @since 1.0
 */
class InvokableRequest(
	private val url: String,
	private val processors: List<Response.Processor>,
	private val client: HttpClient = sharedClient): Request {

	private val httpHeaders: MutableMap<String, String>
	private val queryParameters: MutableMap<String, String>

	init {
		httpHeaders = LinkedHashMap()
		queryParameters = LinkedHashMap()
	}

	override val completedURL: String
		get() {
			return if (queryParameters.isEmpty()) url else "$url?$encodedQueryPart"
		}

	override fun header(name: String): String? {
		return httpHeaders[name]
	}

	override fun header(name: String, value: String): Request {
		httpHeaders[name] = value
		return this
	}

	override fun query(name: String, value: String): Request {
		queryParameters[name] = value
		return this
	}

	@Throws(RequestException::class)
	override fun get(): Response {
		return invoke(requestBuilder().GET().build());
	}

	@Throws(RequestException::class)
	override fun delete(): Response {
		return invoke(requestBuilder().DELETE().build());
	}

	@Throws(RequestException::class)
	override fun post(body: String): Response {
		return invoke(requestBuilder().POST(ofString(body)).build());
	}

	@Throws(RequestException::class)
	override fun put(body: String): Response {
		return invoke(requestBuilder().PUT(ofString(body)).build());
	}

	private fun invoke(request: HttpRequest): Response {
		try {
			val rawResponse = this.client.send(request, ofString())
			val response = ImmutableResponse(rawResponse.body(), rawResponse.statusCode(), rawResponse.headers().map())
			processors.forEach { processor -> processor.process(response) }
			return response
		} catch (e: Throwable) {
			throw RequestException(e)
		}
	}

	private fun requestBuilder(): HttpRequest.Builder {
		val builder = HttpRequest.newBuilder(create(completedURL))
		httpHeaders.forEach(builder::header)
		return builder
	}

	private val encodedQueryPart: String
		get() {
			return queryParameters
				.map { (name, value) -> "${encode(name, UTF_8)}=${encode(value, UTF_8)}" }
				.joinToString("&")
		}
}
