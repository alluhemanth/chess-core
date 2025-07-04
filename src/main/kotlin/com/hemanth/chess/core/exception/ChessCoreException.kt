package com.hemanth.chess.core.exception

/**
 * Base exception for all chess-core related errors.
 *
 * @param message the detail message for this exception
 * @param cause the cause of this exception, or null if none
 */
sealed class ChessCoreException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)
