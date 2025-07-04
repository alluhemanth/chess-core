package com.hemanth.chess.core.board

/**
 * The Rank class represents a row on a chessboard, ranging from 1 to 8. It provides methods for
 * manipulating and querying rank values, performing arithmetic operations, and validating ranges.
 *
 * ## Overview
 * - A rank is represented by an integer (1 to 8).
 * - Provides utility methods for arithmetic operations, comparisons, and range validation.
 *
 * ### Arithmetic Operations
 * - **Addition:** You can add an integer to a rank, resulting in a new rank shifted by the given amount.
 * - **Subtraction:** You can subtract an integer from a rank, resulting in a new rank shifted by the given amount.
 * - These operations are useful for calculating neighboring ranks or iterating over ranks programmatically.
 *
 * ### Example Usage
 * ```Kotlin
 * val rank = Rank(5)
 * val nextRank = rank + 1 // Rank(6)
 * val isEqual = rank.isEqualTo(5) // true
 * ```
 *
 * @property value The integer representing the rank (1 to 8).
 */
@JvmInline
value class Rank(
    /**
     * The integer value of the rank (1 to 8).
     */
    val value: Int
) {
    /**
     * Returns the string representation of the rank.
     *
     * @return The rank as a string.
     */
    override fun toString(): String = value.toString()

    // Comparison Methods
    /**
     * Checks if the rank is equal to the given integer.
     *
     * @param other The integer to compare with.
     * @return `true` if the rank is equal to the given integer, `false` otherwise.
     */
    fun isEqualTo(other: Int): Boolean {
        return this.value == other
    }

    // Arithmetic Operations
    /**
     * Adds an integer to the rank, resulting in a new rank shifted by the given amount.
     *
     * @param other The integer to add.
     * @return A new [Rank] shifted by the given amount.
     */
    operator fun plus(other: Int): Rank {
        return Rank(this.value + other)
    }

    /**
     * Subtracts an integer from the rank, resulting in a new rank shifted by the given amount.
     *
     * @param other The integer to subtract.
     * @return A new [Rank] shifted by the given amount.
     */
    operator fun minus(other: Int): Rank {
        return Rank(this.value - other)
    }

    /*
     * Converts the rank to a zero-based index (0 for rank 1, 7 for rank 8).
     */
    internal fun toArrayIndex(): Int {
        return this.value - 1
    }

    companion object {
        /**
         * Returns the valid range of rank values (1 to 8).
         *
         * @return The integer range for ranks.
         */
        internal fun getRange(): IntRange {
            return 1..8
        }
    }
}

/**
 * Extension function to check if a [Rank] is within an [IntRange].
 *
 * @param rank The [Rank] to check.
 * @return `true` if the rank is within the range, `false` otherwise.
 */
operator fun IntRange.contains(rank: Rank): Boolean = rank.value in this