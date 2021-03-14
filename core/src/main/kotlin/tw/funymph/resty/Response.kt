/* Response.kt created on 2021/2/27
 *
 * Copyright (c) 2021 Spirit Tu <dbi1463@gmail.com>
 *
 * This file is part of resty under the MIT license.
 */
package tw.funymph.resty

/**
 * This interface defines method to access HTTP request's response.
 *
 * @author Spirit Tu
 * @version 1.0
 * @since 1.0
 */
interface Response {

	/**
	 * This interface defines the method to process the response before returning
	 * the response
	 *
	 * @author Spirit Tu
	 * @version 1.0
	 * @since 1.0
	 */
	@FunctionalInterface
	public fun interface Processor {

		/**
		 * Process the the response.
		 *
		 * @param response the response
		 */
		fun process(response: Response)
	}

	/**
	 * This interface defines the method to decode the response body to any
	 * type of objects.
	 *
	 * @author Spirit Tu
	 * @version 1.0
	 * @since 1.0
	 */
	@FunctionalInterface
	public fun interface Decoder<ResultType> {

		/**
		 * Decode the response body to object.
		 *
		 * @param body the response body
		 * @param <ResultType> the type of decoded object
		 * @return the decoded object
		 */
		fun decode(body: String): ResultType?
	}

	val statusCode: Int

	val body: String

	/**
	 * Get the named header values.
	 *
	 * @param name the header name
	 * @return the values
	 */
	fun headers(name: String): List<String>?
}

/**
 * Get the named header value.
 *
 * @param name the header name
 * @return the value
 */
fun Response.header(name: String): String? {
	return headers(name)?.joinToString(" ")
}

/**
 * Get the result by decoding the response body.
 *
 * @param <ResultType> the type of the decoded result
 * @param decoder the decoder to decode the response body
 * @return the decoded result
 */
fun <ResultType> Response.result(decoder: Response.Decoder<ResultType>): ResultType? {
	return decoder.decode(body)
}

/**
 * Get whether the response comes from a succeeded request.
 */
val Response.succeeded: Boolean
	get() = statusCode in 200..299
