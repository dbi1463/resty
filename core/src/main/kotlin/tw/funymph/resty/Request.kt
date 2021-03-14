/* Request.kt created on 2021/2/27
 *
 * Copyright (c) 2021 Spirit Tu <dbi1463@gmail.com>
 *
 * This file is part of resty under the MIT license.
 */
package tw.funymph.resty

/**
 * This defines the methods to build Restful requests.
 *
 * @author Spirit Tu
 * @version 1.0
 * @since 1.0
 */
interface Request {

	/**
	 * This interface defines method to process the request before the
	 * invocation.
	 *
	 * @author Spirit Tu
	 * @version 1.0
	 * @since 1.0
	 */
	@FunctionalInterface
	public fun interface Processor {

		/**
		 * Process the given request.
		 *
		 * @param request the request
		 */
		fun process(request: Request)
	}

	/**
	 * This interface defines method to encode the request body object
	 * into string compatible with HTTP standard.
	 *
	 * @author Spirit Tu
	 * @version 1.0
	 * @since 1.0
	 */
	@FunctionalInterface
	public fun interface Encoder<BodyType> {

		/**
		 * Encode the given body.
		 * @param <BodyType> the request body type
		 * @param body the request body object
		 */
		fun encode(body: BodyType): String
	}

	val completedURL: String

	/**
	 * Get the named HTTP header value.
	 *
	 * @param name the header name
	 * @return the value
	 */
	fun header(name: String): String?

	/**
	 * Set the given value as the named HTTP header.
	 *
	 * @param name the HTTP header name
	 * @param value the HTTP header value
	 * @return the request itself for chaining style
	 */
	fun header(name: String, value: String): Request

	/**
	 * Add the given value as the named query parameter.
	 *
	 * @param name the query parameter name
	 * @param value the query parameter value
	 * @return the request itself for chaining style
	 */
	fun query(name: String, value: String): Request

	/**
	 * Invoke the request with HTTP GET method.
	 *
	 * @return the response
	 * @throws RequestException if any error occurred
	 */
	@Throws(RequestException::class)
	fun get(): Response

	/**
	 * Invoke the request with HTTP DELETE method.
	 *
	 * @return the response
	 * @throws RequestException if any error occurred
	 */
	@Throws(RequestException::class)
	fun delete(): Response

	/**
	 * Invoke the request with HTTP POST method and return the result as string.
	 *
	 * @param body the request body
	 * @return the result
	 * @throws RequestException if any error occurred
	 */
	@Throws(RequestException::class)
	fun post(body: String): Response

	/**
	 * Invoke the request with HTTP PUT method and return the result as string.
	 *
	 * @param body the request body
	 * @return the response
	 * @throws RequestException if any error occurred
	 */
	@Throws(RequestException::class)
	fun put(body: String): Response
}

/**
 * Invoke the request with HTTP POST method.
 *
 * @param <BodyType> the request body data type
 * @param <DataType> the result data type
 * @param body the request body
 * @param encoder the encoder to encode body as string
 * @return the response
 * @throws RequestException if any error occurred
 */
@Throws(RequestException::class)
fun <BodyType> Request.post(body: BodyType, encoder: Request.Encoder<BodyType>): Response {
	val encoded = encoder.encode(body)
	return post(encoded)
}

/**
 * Invoke the request with PUT method.
 *
 * @param <BodyType> the request body data type
 * @param body the request body
 * @param encoder the encoder to encode body as string
 * @return the response
 * @throws RequestException if any error occurred
 */
@Throws(RequestException::class)
fun <BodyType> Request.put(body: BodyType, encoder: Request.Encoder<BodyType>): Response {
	val encoded = encoder.encode(body)
	return put(encoded)
}
