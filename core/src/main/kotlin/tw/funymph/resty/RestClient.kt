/* RestClient.kt created on 2021/2/27
 *
 * Copyright (c) 2021 Spirit Tu <dbi1463@gmail.com>
 *
 * This file is part of resty under the MIT license.
 */
package tw.funymph.resty

/**
 * This class provide a simple client to invoke HTTP requests to the
 * same host and can add processors to handle requests and responses.
 *
 * @author Spirit Tu
 * @version 1.0
 * @since 1.0
 */
interface RestClient {

	/**
	 * Create a customizable request to the path.
	 *
	 * @param path the path
	 * @return a customizable request
	 */
	fun request(path: String): Request

	/**
	 * Add a request processor to process the request before invocation.
	 * The execution order is determined by the order the processor been
	 * added into the list. And the same processor instance can be added
	 * and called only once.
	 *
	 * @param processor the request processor
	 */
	fun addRequestProcessor(processor: Request.Processor)

	/**
	 * Add a response processor to process the response after invocation.
	 * The execution order is determined by the order the processor been
	 * added into the list. And the same processor instance can be added
	 * and called only once.
	 *
	 * @param processor the response processor
	 */
	fun addResponseProcessor(processor: Response.Processor)
}
