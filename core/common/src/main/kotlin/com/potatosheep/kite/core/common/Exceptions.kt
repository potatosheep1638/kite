package com.potatosheep.kite.core.common

/**
 * An exception that indicates that the Redlib instance has returned an error.
 */
class RedlibErrorException : Exception {

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)
}
