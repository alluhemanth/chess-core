package com.hemanth.chess.core.exception

/**
 * Exception thrown when a square string is not in the correct format.
 *
 * @param message the detail message for this exception
 * @param cause the cause of this exception, or null if none
 */
class SquareFormatException(
    message: String,
    cause: Throwable? = null
) : ChessCoreException(message, cause)
