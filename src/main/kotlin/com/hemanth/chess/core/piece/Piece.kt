package com.hemanth.chess.core.piece

/**
 * Represents a chess piece with a specific type and color.
 *
 * @property pieceType The type of the chess piece (e.g., PAWN, KNIGHT).
 * @property color The color of the chess piece (WHITE or BLACK).
 */
data class Piece(
    val pieceType: PieceType,
    val color: PieceColor
) {
    /**
     * Returns a single-character string representation of the piece.
     * Uppercase for white, lowercase for black.
     *
     * @return The character representing the piece type and color.
     */
    override fun toString(): String {
        val typeChar = when (pieceType) {
            PieceType.PAWN -> 'P'
            PieceType.KNIGHT -> 'N'
            PieceType.BISHOP -> 'B'
            PieceType.ROOK -> 'R'
            PieceType.QUEEN -> 'Q'
            PieceType.KING -> 'K'
        }
        return if (color == PieceColor.WHITE) typeChar.uppercaseChar().toString()
        else typeChar.lowercaseChar().toString()
    }
}
