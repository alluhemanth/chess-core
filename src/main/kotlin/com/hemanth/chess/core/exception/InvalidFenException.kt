package com.hemanth.chess.core.exception

/**
 * Exception thrown when a FEN string is invalid or cannot be parsed.
 *
 * @param message the detail message for this exception
 * @param cause the cause of this exception, or null if none
 */
class InvalidFenException(
    message: String,
    cause: Throwable? = null
) : ChessCoreException(message, cause)
