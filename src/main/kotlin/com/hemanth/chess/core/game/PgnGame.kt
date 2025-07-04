package com.hemanth.chess.core.game

/**
 * Represents a chess game in PGN (Portable Game Notation) format.
 *
 * @property tags The PGN tags (metadata) for the game.
 * @property moves The list of moves in standard algebraic notation.
 * @property result The result of the game (e.g., "1-0", "0-1", "1/2-1/2", or "*").
 */
data class PgnGame(
    val tags: Map<String, String> = emptyMap(),
    val moves: List<String> = emptyList(),
    val result: String = "*"
)