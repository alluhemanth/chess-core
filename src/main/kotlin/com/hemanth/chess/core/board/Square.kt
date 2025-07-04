package com.hemanth.chess.core.board

import com.hemanth.chess.core.exception.FileOutOfBoundsException
import com.hemanth.chess.core.exception.RankOutOfBoundsException
import com.hemanth.chess.core.exception.SquareFormatException

/**
 * The Square class represents a square on a chessboard, defined by a [File] (column) and a [Rank] (row).
 * It provides methods for parsing algebraic notation, determining square color, and string representation.
 *
 * ## Overview
 * - A square is defined by a file ('a' to 'h') and a rank (1 to 8).
 * - Provides utility methods for parsing, color determination, and string representation.
 *
 * ### Parsing
 * - **From Algebraic Notation:** The [Square] can be constructed from algebraic notation (e.g., "e4").
 * - Validates the notation and extracts the file and rank components.
 *
 * ### Square Color
 * - **Dark Square:** A square is dark if the sum of its file and rank indices is even.
 * - **Light Square:** A square is light if the sum of its file and rank indices is odd.
 *
 * ### Example Usage
 * ```Kotlin
 * val square = Square("e4")
 * val isDark = square.isDarkSquare() // true
 * val notation = square.toString() // "e4"
 * ```
 *
 * @property file The file (column) of the square.
 * @property rank The rank (row) of the square.
 */
data class Square(
    val file: File,
    val rank: Rank
) {
    /**
     * Constructs a [Square] from algebraic notation (e.g., "e4").
     *
     * @param notation The algebraic notation of the square.
     * @throws SquareFormatException if the notation is invalid.
     */
    constructor(notation: String) : this(
        file = parseFile(notation),
        rank = parseRank(notation)
    )

    /**
     * Returns the string representation of the square in algebraic notation (e.g., "e4").
     *
     * @return The algebraic notation of the square.
     */
    override fun toString(): String = "${file.value}${rank.value}"

    // Utility Methods
    /**
     * Returns true if the square is a dark square.
     *
     * @return `true` if the square is dark, `false` otherwise.
     */
    internal fun isDarkSquare(): Boolean {
        val fileIndex = file.toArrayIndex()
        val rankIndex = rank.toArrayIndex()
        return (fileIndex + rankIndex) % 2 == 0
    }

    /**
     * Returns true if the square is a light square.
     *
     * @return `true` if the square is light, `false` otherwise.
     */
    internal fun isLightSquare(): Boolean {
        return !isDarkSquare()
    }

    private companion object {
        /**
         * Parses the file from algebraic notation.
         *
         * @param notation The algebraic notation of the square.
         * @return The [File] component.
         * @throws SquareFormatException if the file is invalid.
         */
        private fun parseFile(notation: String): File {
            if (notation.length != 2)
                throw SquareFormatException("Invalid square notation length: '$notation'")

            val fileChar = notation[0]
            return try {
                File(fileChar)
            } catch (e: FileOutOfBoundsException) {
                throw SquareFormatException("Invalid file in square notation: '$notation'", e)
            }
        }

        /**
         * Parses the rank from algebraic notation.
         *
         * @param notation The algebraic notation of the square.
         * @return The [Rank] component.
         * @throws SquareFormatException if the rank is invalid.
         */
        private fun parseRank(notation: String): Rank {
            if (notation.length != 2)
                throw SquareFormatException("Invalid square notation length: '$notation'")

            val rankChar = notation[1]
            return try {
                Rank(rankChar.digitToInt())
            } catch (e: RankOutOfBoundsException) {
                throw SquareFormatException("Invalid rank in square notation: '$notation'", e)
            }
        }
    }
}