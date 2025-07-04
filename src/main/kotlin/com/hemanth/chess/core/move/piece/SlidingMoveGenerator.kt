package com.hemanth.chess.core.move.piece

import com.hemanth.chess.core.board.*
import com.hemanth.chess.core.move.Move
import com.hemanth.chess.core.move.Offset
import com.hemanth.chess.core.piece.Piece

/**
 * Utility object for generating sliding piece moves (rook, bishop, queen).
 */
internal object SlidingMoveGenerator {

    /**
     * Generates all pseudo-legal sliding moves for a piece in the given directions.
     *
     * @param piece The sliding piece.
     * @param fromSquare The square the piece is moving from.
     * @param board The current board state.
     * @param directions List of direction offsets to slide in.
     * @return List of possible moves for the sliding piece.
     */
    fun generateSlidingMoves(
        piece: Piece,
        fromSquare: Square,
        board: Board,
        directions: List<Offset>
    ): List<Move> {
        val moves = mutableListOf<Move>()

        for (dir in directions) {
            var step = 1
            while (true) {
                val targetFile = fromSquare.file + dir.fileDelta * step
                val targetRank = fromSquare.rank + dir.rankDelta * step

                if (targetFile !in File.getRange() || targetRank !in Rank.getRange()) {
                    break
                }

                val targetSquare = Square(targetFile, targetRank)
                val targetPiece = board[targetSquare]

                if (targetPiece == null) {
                    moves.add(Move(fromSquare, targetSquare))
                } else {
                    if (targetPiece.color != piece.color) {
                        moves.add(Move(fromSquare, targetSquare, isCapture = true))
                    }
                    break
                }
                step++
            }
        }
        return moves
    }
}
