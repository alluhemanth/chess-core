package com.hemanth.chess.core.move.piece

import com.hemanth.chess.core.board.*
import com.hemanth.chess.core.game.GameState
import com.hemanth.chess.core.move.Move
import com.hemanth.chess.core.move.Offset
import com.hemanth.chess.core.piece.Piece
import com.hemanth.chess.core.piece.PieceType

/**
 * Generates all pseudo-legal moves for a knight.
 */
internal class KnightMoveGenerator : PieceMoveGenerator {

    /**
     * Generates all pseudo-legal knight moves from the given square.
     *
     * @param piece The knight piece.
     * @param fromSquare The square the knight is moving from.
     * @param board The current board state.
     * @param gameState The current game state.
     * @return List of possible moves for the knight.
     */
    override fun generatePseudoLegalMoves(
        piece: Piece,
        fromSquare: Square,
        board: Board,
        gameState: GameState,
    ): List<Move> {
        require(piece.pieceType == PieceType.KNIGHT) { "Piece must be a KNIGHT" }

        val moves = mutableListOf<Move>()
        val knightColor = piece.color
        val offsets = listOf(
            Offset(1, 2), Offset(1, -2), Offset(-1, 2), Offset(-1, -2),
            Offset(2, 1), Offset(2, -1), Offset(-2, 1), Offset(-2, -1)
        )

        for (offset in offsets) {
            val targetFile = fromSquare.file + offset.fileDelta
            val targetRank = fromSquare.rank + offset.rankDelta

            if (targetFile in File.getRange() && targetRank in Rank.getRange()) {
                val targetSquare = Square(targetFile, targetRank)
                val targetPiece = board[targetSquare]

                if (targetPiece == null) {
                    moves.add(Move(fromSquare, targetSquare, isCapture = false))
                } else if (targetPiece.color != knightColor) {
                    moves.add(Move(fromSquare, targetSquare, isCapture = true))
                }
            }
        }
        return moves
    }
}