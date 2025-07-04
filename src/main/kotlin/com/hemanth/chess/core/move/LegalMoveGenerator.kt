package com.hemanth.chess.core.move

import com.hemanth.chess.core.board.Board
import com.hemanth.chess.core.board.File
import com.hemanth.chess.core.board.Square
import com.hemanth.chess.core.game.GameState
import com.hemanth.chess.core.game.GameUtils
import com.hemanth.chess.core.piece.PieceColor

/**
 * Generates all legal moves for the current player, filtering out pseudo-legal moves
 * that would leave the player's king in check.
 */
internal class LegalMoveGenerator {

    /**
     * Returns a list of all legal moves for the current player.
     *
     * @param board The current board state.
     * @param currentPlayerGameState The current game state for the player.
     * @return List of legal moves.
     */
    fun getAllLegalMoves(
        board: Board,
        currentPlayerGameState: GameState
    ): List<Move> {
        val currentPlayerColor = currentPlayerGameState.currentPlayer

        val pseudoLegalMoves = PseudoLegalMoveGenerator.getAllPseudoLegalMoves(board, currentPlayerGameState)

        return pseudoLegalMoves.asSequence().filter { move ->
            val (newBoard, _) = GameUtils.makeMove(board, currentPlayerGameState, move)

            if (move.isCastlingKingside || move.isCastlingQueenside) {
                !GameUtils.isKingInCheck(currentPlayerColor, board) &&
                        isCastlingPathSafe(move, currentPlayerColor, board) &&
                        !GameUtils.isKingInCheck(currentPlayerColor, newBoard)
            } else {
                !GameUtils.isKingInCheck(currentPlayerColor, newBoard)
            }
        }.toList()
    }

    /**
     * Checks if the castling path is safe (i.e., not attacked by the opponent).
     *
     * @param castlingMove The castling move to check.
     * @param kingColor The color of the king.
     * @param board The current board state.
     * @return True if the castling path is safe, false otherwise.
     */
    private fun isCastlingPathSafe(
        castlingMove: Move,
        kingColor: PieceColor,
        board: Board
    ): Boolean {
        val kingRank = castlingMove.from.rank
        val pathSquares = when {
            castlingMove.isCastlingKingside -> listOf(
                Square(File('e'), kingRank),
                Square(File('f'), kingRank),
                Square(File('g'), kingRank)
            )

            castlingMove.isCastlingQueenside -> listOf(
                Square(File('e'), kingRank),
                Square(File('d'), kingRank),
                Square(File('c'), kingRank)
            )

            else -> return false
        }

        return pathSquares.none { square ->
            GameUtils.isSquareAttackedBy(square, kingColor.opposite(), board)
        }
    }
}
