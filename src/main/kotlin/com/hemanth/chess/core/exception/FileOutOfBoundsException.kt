package com.hemanth.chess.core.exception

/**
 * Exception thrown when a file value is outside the valid range (a...h).
 *
 * @param message the detail message for this exception
 * @param cause the cause of this exception, or null if none
 */
class FileOutOfBoundsException(
    message: String = "File value should be in the range of a..h",
    cause: Throwable? = null
) : ChessCoreException(message, cause)
