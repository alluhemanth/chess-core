package com.hemanth.chess.core.game

/**
 * Represents the castling rights for a player.
 *
 * @property kingside Whether kingside castling is available.
 * @property queenside Whether queenside castling is available.
 */
data class CastlingAvailability(
    val kingside: Boolean,
    val queenside: Boolean
)