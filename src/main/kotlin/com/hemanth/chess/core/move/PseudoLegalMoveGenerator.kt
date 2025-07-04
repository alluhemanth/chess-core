package com.hemanth.chess.core.move

import com.hemanth.chess.core.board.Board
import com.hemanth.chess.core.game.GameState
import com.hemanth.chess.core.move.piece.*
import com.hemanth.chess.core.piece.PieceType

/**
 * Generates all pseudo-legal moves for a given board and game state.
 * Pseudo-legal moves do not consider checks.
 */
internal object PseudoLegalMoveGenerator {

    private val pieceMoveGenerators: Map<PieceType, PieceMoveGenerator> = mapOf(
        PieceType.PAWN to PawnMoveGenerator(),
        PieceType.ROOK to RookMoveGenerator(),
        PieceType.KNIGHT to KnightMoveGenerator(),
        PieceType.BISHOP to BishopMoveGenerator(),
        PieceType.KING to KingMoveGenerator(),
        PieceType.QUEEN to QueenMoveGenerator()
    )

    /**
     * Returns all pseudo-legal moves for the current player on the given board.
     *
     * @param board The current board state.
     * @param gameState The current game state.
     * @return List of pseudo-legal moves.
     */
    fun getAllPseudoLegalMoves(
        board: Board,
        gameState: GameState
    ): List<Move> {
        val playerColor = gameState.currentPlayer
        val allMoves = mutableListOf<Move>()

        for ((piece, square) in board.getAllPieces().filter { it.first.color == playerColor }) {
            val generator = pieceMoveGenerators[piece.pieceType]
                ?: error("No move generator found for ${piece.pieceType}")
            allMoves += generator.generatePseudoLegalMoves(
                piece = piece,
                fromSquare = square,
                board = board,
                gameState = gameState
            )
        }
        return allMoves
    }
}