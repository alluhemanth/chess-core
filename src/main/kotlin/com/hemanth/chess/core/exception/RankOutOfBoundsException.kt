package com.hemanth.chess.core.exception

/**
 * Exception thrown when a rank value is outside the valid range (1..8).
 *
 * @param message the detail message for this exception
 * @param cause the cause of this exception, or null if none
 */
class RankOutOfBoundsException(
    message: String = "Rank value should be in the range of 1..8",
    cause: Throwable? = null
) : ChessCoreException(message, cause)
