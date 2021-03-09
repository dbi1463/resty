/* InvokeRequestTests.kt created on 2021/2/28
 *
 * Copyright (c) 2021 Spirit Tu <dbi1463@gmail.com>
 * 
 * This file is part of resty under the MIT license.
 */
package tw.funymph.resty.java

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockserver.client.MockServerClient
import org.mockserver.integration.ClientAndServer
import org.mockserver.integration.ClientAndServer.startClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import tw.funymph.resty.*

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * This class tests the functionalities of {@link InvokeRequest}.
 *
 * @author Spirit Tu
 * @version 1.0
 * @since 1.0
 */
class InvokeRequestTests {

	private val port = 2345
	private val host = "http://127.0.0.1:$port"

	companion object {

		private var server: ClientAndServer? = null

		@BeforeAll
		@JvmStatic
		fun startServer() {
			server = startClientAndServer(2345)
		}

		@AfterAll
		@JvmStatic
		fun stopServer() {
			server?.stop()
		}
	}

	val formEncoder = { values: Map<String, String> ->
		values.map { (key, value) -> "${key}=${value}" }.joinToString("&")
	}

	@Test
	fun testGet() {
		val path = "/healthCheck"
		givenRequest(path).apply {
			MockServerClient("127.0.0.1", 2345)
				.`when`(request()
					.withMethod("GET")
					.withPath(path))
				.respond(response()
					.withStatusCode(200)
					.withBody("""{ "data": "ok" }"""))
		}.run {
			val (_, request) = this
			request.get()
		}.apply {
			assertEquals(200, this.statusCode)
			assertTrue { this.succeeded }
		}
	}

	@Test
	fun testGetWithResponseProcessor() {
		val path = "/token"
		val processor = { response: Response ->
			val token = response.header("Token")!!
			assertEquals("a251e1d1-9c62-4f7d-a4ab-3ecd6ce142f7", token)
		}
		givenRequest(path, processor).apply {
			MockServerClient("127.0.0.1", 2345)
				.`when`(request()
					.withMethod("GET")
					.withHeader("Refresh-Token", "e0ae1511-4c75-41db-a974-7966d73affce")
					.withPath(path))
				.respond(response()
					.withStatusCode(200)
					.withHeader("Token", "a251e1d1-9c62-4f7d-a4ab-3ecd6ce142f7")
					.withBody("""{ "data": "ok" }"""))
		}.run {
			val (_, request) = this
			request.header("Refresh-Token", "e0ae1511-4c75-41db-a974-7966d73affce")
				.get()
		}.apply {
			assertEquals(200, this.statusCode)
			assertTrue { this.succeeded }
		}
	}

	@Test
	fun testPostWithEncoder() {
		val body = mapOf(
			Pair("account", "someone@test.com"),
			Pair("password", "somepassword"))
		val path = "/login"
		givenRequest(path).apply {
			MockServerClient("127.0.0.1", 2345)
				.`when`(request()
					.withPath(path)
					.withBody("account=someone@test.com&password=somepassword")
					.withMethod("POST"))
				.respond(response()
					.withStatusCode(200)
					.withBody("""{ "data": { "id": "e0ae1511-4c75-41db-a974-7966d73affce" }}"""))
		}.run {
			val (_, request) = this
			request.post(body, formEncoder)
		}.apply {
			assertEquals(200, this.statusCode)
		}
	}

	class AccountState(
		val id: String,
		val state: String
	)

	@Test
	fun testPutWithDecoder() {
		val mapper = ObjectMapper().registerModule(KotlinModule())
		val body = mapOf(Pair("next", "cleared"))
		val decoder = { json: String ->
			mapper.readValue<AccountState>(json)
		}
		val path = "/state/cleared"
		givenRequest(path).apply {
			MockServerClient("127.0.0.1", 2345)
				.`when`(request()
					.withPath(path)
					.withMethod("PUT")
					.withBody("""next=cleared"""))
				.respond(response()
					.withStatusCode(200)
					.withBody("""{"id":"e0ae1511-4c75-41db-a974-7966d73affce","state":"cleared"}"""))
		}.run {
			val (_, request) = this
			request.put(body, formEncoder)
		}.apply {
			assertEquals(200, this.statusCode)
			val account = this.result(decoder)
			assertEquals("cleared", account?.state)
			assertEquals("e0ae1511-4c75-41db-a974-7966d73affce", account?.id)
		}
	}

	@Test
	fun testDelete() {
		val path = "/authentications/mine"
		givenRequest(path).apply {
			MockServerClient("127.0.0.1", 2345)
				.`when`(request()
					.withMethod("DELETE")
					.withPath(path))
				.respond(response()
					.withStatusCode(404)
					.withBody("""{ "error": "not found" }"""))
		}.run {
			val (_, request) = this
			request.delete()
		}.apply {
			assertEquals(404, this.statusCode)
			assertFalse { this.succeeded }
		}
	}

	@Test
	fun testQuery() {
		val path = "/feeds"
		givenRequestWithoutServer(path).run {
			val (_, request) = this
			request.query("size", "20")
				.query("from", "120")
		}.apply {
			assertEquals("http://127.0.0.1:2345/feeds?size=20&from=120", completedURL)
		}
	}

	private fun givenRequest(path: String, processor: Response.Processor? = null): Pair<RestClient, Request> {
		val client = RestClientImpl(host)
		processor?.also {
			client.addResponseProcessor(it)
		}
		val request = client.request(path)
		return Pair(client, request)
	}

	private fun givenRequestWithoutServer(path: String): Pair<RestClient, Request> {
		val client = RestClientImpl(host)
		val request = client.request(path)
		return Pair(client, request)
	}
}
