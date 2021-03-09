/* ImmutableResponse.kt created on 2021/2/27
 *
 * Copyright (c) 2021 Spirit Tu <dbi1463@gmail.com>
 *
 * This file is part of resty under the MIT license.
 */
package tw.funymph.resty.java

import tw.funymph.resty.Response

/**
 * This class provides an immutable implementation of {@link Response}.
 *
 * @author Spirit Tu
 * @version 1.0
 * @since 1.0
 */
class ImmutableResponse(
	override val body: String,
	override val statusCode: Int,
	private val headers: Map<String, List<String>>): Response {

	override fun headers(name: String): List<String>? {
		return headers[name]
	}
}
