/* RestClientImplTests.kt created on 2021/2/28
 *
 * Copyright (c) 2021 Spirit Tu <dbi1463@gmail.com>
 * 
 * This file is part of resty under the MIT license.
 */
package tw.funymph.resty.java

import org.junit.jupiter.api.Test
import tw.funymph.resty.Request
import tw.funymph.resty.RestClient
import kotlin.test.assertEquals

/**
 * This class tests the functionalities of {@link RestClientImpl}.
 *
 * @author Spirit Tu
 * @version 1.0
 * @since 1.0
 */
class RestClientImplTests {

	@Test
	fun testRequest() {
		givenClient().run {
			request("/login")
		}.apply {
			assertEquals("https://ws.funymph.tw/login", completedURL)
		}
	}

	@Test
	fun testRequestWithRequestProcessor() {
		givenClient().apply {
			addRequestProcessor { req: Request -> req.header("Token", "f2135cf0-ba8e-4e37-b87f-4cac54e77db5") }
		}.run {
			request("/me")
		}.apply {
			assertEquals("https://ws.funymph.tw/me", completedURL)
			assertEquals("f2135cf0-ba8e-4e37-b87f-4cac54e77db5", header("Token"))
		}
	}

	private fun givenClient(host: String = "https://ws.funymph.tw"): RestClient {
		return RestClientImpl(host)
	}
}
