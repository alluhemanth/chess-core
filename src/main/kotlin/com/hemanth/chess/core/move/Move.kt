package com.hemanth.chess.core.move

import com.hemanth.chess.core.board.Square
import com.hemanth.chess.core.piece.PieceType

/**
 * Represents a chess move, including source and destination squares,
 * optional promotion, and special move flags.
 *
 * @property from The starting square of the move.
 * @property to The destination square of the move.
 * @property promotionPieceType The piece type to promote to, if applicable.
 * @property isCapture True if the move is a capture.
 * @property isCastlingKingside True if the move is kingside castling.
 * @property isCastlingQueenside True if the move is queenside castling.
 * @property isEnPassantCapture True if the move is an en passant capture.
 */
data class Move(
    val from: Square,
    val to: Square,
    val promotionPieceType: PieceType? = null,
    val isCapture: Boolean = false,
    val isCastlingKingside: Boolean = false,
    val isCastlingQueenside: Boolean = false,
    val isEnPassantCapture: Boolean = false
) {
    /**
     * Secondary constructor for moves with promotion specified as a character.
     *
     * @param from The starting square.
     * @param to The destination square.
     * @param promotionChar The character representing the promotion piece type.
     */
    constructor(from: Square, to: Square, promotionChar: Char?) : this(
        from = from,
        to = to,
        promotionPieceType = promotionChar?.let { char ->
            when (char.lowercaseChar()) {
                'q' -> PieceType.QUEEN
                'r' -> PieceType.ROOK
                'b' -> PieceType.BISHOP
                'n' -> PieceType.KNIGHT
                else -> null
            }
        }
    )

    /**
     * Secondary constructor for moves specified by string coordinates.
     *
     * @param from The starting square in algebraic notation (e.g., "e2").
     * @param to The destination square in algebraic notation (e.g., "e4").
     * @param promotionPieceType The piece type to promote to, if applicable.
     */
    constructor(from: String, to: String, promotionPieceType: PieceType? = null) : this(
        from = Square(from),
        to = Square(to),
        promotionPieceType = promotionPieceType
    )

    /**
     * Returns a string representation of the move in coordinate notation,
     * including promotion if applicable.
     */
    override fun toString(): String {
        return "$from$to${promotionPieceType?.name?.first()?.lowercaseChar() ?: ""}"
    }

    /**
     * Returns the move in UCI (Universal Chess Interface) notation.
     *
     * @return The move as a UCI string.
     */
    fun toUci(): String {
        val promotion = promotionPieceType?.name?.first()?.lowercaseChar() ?: ""
        return "${from}${to}$promotion"
    }

}