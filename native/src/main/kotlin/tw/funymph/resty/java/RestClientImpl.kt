/* RestClientImpl.kt created on 2021/2/28
 *
 * Copyright (c) 2021 Spirit Tu <dbi1463@gmail.com>
 * 
 * This file is part of resty under the MIT license.
 */
package tw.funymph.resty.java

import tw.funymph.resty.Request
import tw.funymph.resty.Response
import tw.funymph.resty.RestClient

/**
 * This class provide the implementation with {@link HttpClient} introduced
 * in Java 11. Thus, to use this implementation, the minimum Java version
 * will be 11.
 *
 * @author Spirit Tu
 * @version 1.0
 * @since 1.0
 */
class RestClientImpl(private val host: String): RestClient {

	private val requestProcessors: MutableList<Request.Processor> by lazy { ArrayList() }
	private val responseProcessors: MutableList<Response.Processor> by lazy { ArrayList() }

	override fun request(path: String): Request {
		val url = "$host$path"
		val request = InvokableRequest(url, this.responseProcessors)
		this.requestProcessors.forEach { processor -> processor.process(request) }
		return request
	}

	override fun addRequestProcessor(processor: Request.Processor) {
		this.requestProcessors += processor
	}

	override fun addResponseProcessor(processor: Response.Processor) {
		this.responseProcessors += processor
	}
}
