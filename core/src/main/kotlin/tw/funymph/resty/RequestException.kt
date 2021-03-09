/* RequestException.kt created on 2021/2/27
 *
 * Copyright (c) 2021 Spirit Tu <dbi1463@gmail.com>
 * 
 * This file is part of resty under the MIT license.
 */
package tw.funymph.resty

/**
 * This exception encapsulate the I/O exceptions.
 *
 * @author Spirit Tu
 * @version 1.0
 * @since 1.0
 */
class RequestException(cause: Throwable) : Exception(cause) {
}
