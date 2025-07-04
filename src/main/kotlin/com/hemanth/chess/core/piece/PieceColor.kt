package com.hemanth.chess.core.piece

/**
 * Enum representing the color of a chess piece.
 */
enum class PieceColor {
    WHITE,
    BLACK;

    /**
     * Returns the opposite color.
     *
     * @return [PieceColor] The opposite color (WHITE <-> BLACK).
     */
    fun opposite(): PieceColor {
        return if (this == WHITE) BLACK else WHITE
    }
}