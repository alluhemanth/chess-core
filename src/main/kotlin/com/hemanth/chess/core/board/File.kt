package com.hemanth.chess.core.board

/**
 * The File class represents a column on a chessboard, ranging from 'a' to 'h'. It provides methods for
 * manipulating and querying file values, converting them to array indices, and performing arithmetic operations.
 *
 * ## Overview
 * - A file is represented by a single character ('a' to 'h').
 * - Provides utility methods for arithmetic operations, comparisons, and conversions.
 *
 * ### Arithmetic Operations
 * - **Addition:** You can add an integer to a file, resulting in a new file shifted by the given amount.
 * - **Subtraction:** You can subtract an integer from a file, resulting in a new file shifted by the given amount.
 * - These operations are useful for calculating neighboring files or iterating over files programmatically.

 * ### Conversion
 * - **To Array Index:** The [toArrayIndex] method converts the file to a zero-based index (0 for 'a', 7 for 'h').
 * - This is useful for accessing arrays or lists that represent chessboard rows or columns.

 * ### Example Usage
 * ```Kotlin
 * val file = File('e')
 * val nextFile = file + 1 // File('f')
 * val index = file.toArrayIndex() // 4
 * val isEqual = file.isEqualTo('e') // true
 * ```
 *
 * @property value The character representing the file ('a' to 'h').
 */
@JvmInline
value class File(
    /**
     * The character representing the file ('a' to 'h').
     */
    val value: Char
) {
    /**
     * Returns the string representation of the file.
     *
     * @return The file as a string.
     */
    override fun toString(): String = value.toString()

    /**
     * Checks if the file is equal to the given character.
     *
     * @param other The character to compare with.
     * @return `true` if the file is equal to the given character, `false` otherwise.
     */
    fun isEqualTo(other: Char): Boolean {
        return this.value == other
    }

    /**
     * Adds an integer to the file, resulting in a new file shifted by the given amount.
     *
     * @param other The integer to add.
     * @return A new [File] shifted by the given amount.
     */
    operator fun plus(other: Int): File {
        return File((this.value + other))
    }

    /**
     * Subtracts an integer from the file, resulting in a new file shifted by the given amount.
     *
     * @param other The integer to subtract.
     * @return A new [File] shifted by the given amount.
     */
    operator fun minus(other: Int): File {
        return File((this.value - other))
    }

    /**
     * Converts the file to a zero-based array index (0 for 'a', 7 for 'h').
     *
     * @return The array index corresponding to the file.
     */
    internal fun toArrayIndex(): Int {
        return value - 'a'
    }

    companion object {
        /**
         * Returns the valid range of file characters ('a' to 'h').
         *
         * @return The character range for files.
         */
        internal fun getRange(): CharRange {
            return 'a'..'h'
        }
    }
}

/**
 * Extension function to check if a [File] is within a [CharRange].
 *
 * @param file The [File] to check.
 * @return `true` if the file is within the range, `false` otherwise.
 */
operator fun CharRange.contains(file: File): Boolean = file.value in this