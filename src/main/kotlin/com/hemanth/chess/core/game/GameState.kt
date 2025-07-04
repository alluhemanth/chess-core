package com.hemanth.chess.core.game

import com.hemanth.chess.core.board.Square
import com.hemanth.chess.core.piece.PieceColor

/**
 * Represents the state of a chess game at a given moment.
 *
 * @property currentPlayer The player whose turn it is to move.
 * @property castlingRights The castling rights for each player.
 * @property enPassantTargetSquare The square available for en passant capture, if any.
 * @property halfMoveClock The number of half-moves since the last pawn advance or capture.
 * @property fullMoveNumber The number of the full move (incremented after Black's move).
 */
data class GameState(
    val currentPlayer: PieceColor,
    val castlingRights: Map<PieceColor, CastlingAvailability>,
    val enPassantTargetSquare: Square?,
    val halfMoveClock: Int,
    val fullMoveNumber: Int
) {

    /**
     * Returns the opponent of the current player.
     *
     * @return [PieceColor] representing the opponent.
     */
    fun opponent(): PieceColor {
        return if (currentPlayer == PieceColor.WHITE) PieceColor.BLACK else PieceColor.WHITE
    }

    /**
     * Creates a new game state representing the initial position.
     *
     * @return A [GameState] with default values for a new chess game.
     */
    companion object {
        fun newGame(): GameState {
            return GameState(
                currentPlayer = PieceColor.WHITE,
                castlingRights = mapOf(
                    PieceColor.WHITE to CastlingAvailability(kingside = true, queenside = true),
                    PieceColor.BLACK to CastlingAvailability(kingside = true, queenside = true)
                ),
                enPassantTargetSquare = null,
                halfMoveClock = 0,
                fullMoveNumber = 1
            )
        }
    }
}
