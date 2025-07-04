package com.hemanth.chess.core.game

import com.hemanth.chess.core.board.*
import com.hemanth.chess.core.move.Move
import com.hemanth.chess.core.move.PseudoLegalMoveGenerator
import com.hemanth.chess.core.piece.PieceColor
import com.hemanth.chess.core.piece.PieceType

/**
 * Utility functions for chess game logic, including move application, check detection,
 * attack detection, and insufficient material checks.
 *
 * ## Overview
 * - Provides methods for applying moves, detecting checks, evaluating attacks, and determining insufficient material.
 * - Designed to work with the chessboard and game state objects.
 *
 * ### Move Application
 * - **makeMove:** Applies a move to the board and updates the game state.
 *
 * ### Check Detection
 * - **isKingInCheck:** Determines if the king of a given color is in check.
 *
 * ### Attack Detection
 * - **isSquareAttackedBy:** Checks if a square is attacked by pieces of a specific color.
 *
 * ### Material Evaluation
 * - **hasInsufficientMaterial:** Determines if the board position has insufficient material for checkmate.
 *
 * ### Example Usage
 * ```Kotlin
 * val newState = GameUtils.makeMove(board, gameState, move)
 * val isCheck = GameUtils.isKingInCheck(PieceColor.WHITE, board)
 * val isAttacked = GameUtils.isSquareAttackedBy(square, PieceColor.BLACK, board)
 * val insufficientMaterial = GameUtils.hasInsufficientMaterial(board)
 * ```
 */
object GameUtils {

    /**
     * Applies a move to the given [board] and [gameState], returning the new board and game state.
     *
     * Handles updating castling rights, en passant targets, half-move clock, and full-move number.
     *
     * @param board The current board state.
     * @param gameState The current game state.
     * @param move The move to apply.
     * @return A pair of the new board and new game state after the move.
     */
    fun makeMove(board: Board, gameState: GameState, move: Move): Pair<Board, GameState> {
        val newBoard = board.applyMove(move)

        val pieceToMove = board[move.from]
        val capturedPiece = board[move.to]

        if (pieceToMove == null) {
            return Pair(board, gameState)
        }

        val fromRank = move.from.rank
        val toRank = move.to.rank
        val fromFile = move.from.file
        val toFile = move.to.file

        val newCastlingRights = gameState.castlingRights.toMutableMap()

        if (pieceToMove.pieceType == PieceType.KING) {
            newCastlingRights[pieceToMove.color] = CastlingAvailability(kingside = false, queenside = false)
        } else if (pieceToMove.pieceType == PieceType.ROOK) {
            val startingRank = if (pieceToMove.color == PieceColor.WHITE) 1 else 8
            if (fromRank.isEqualTo(startingRank)) {
                if (fromFile.isEqualTo('a')) {
                    newCastlingRights[pieceToMove.color] =
                        newCastlingRights[pieceToMove.color]!!.copy(queenside = false)
                } else if (fromFile.isEqualTo('h')) {
                    newCastlingRights[pieceToMove.color] = newCastlingRights[pieceToMove.color]!!.copy(kingside = false)
                }
            }
        }

        if (capturedPiece != null && capturedPiece.pieceType == PieceType.ROOK) {
            val opponentColor = gameState.currentPlayer.opposite()
            if (capturedPiece.color == opponentColor) {
                val targetRank = if (opponentColor == PieceColor.WHITE) 1 else 8
                if (toFile.isEqualTo('a') && toRank.isEqualTo(targetRank)) {
                    newCastlingRights[opponentColor] = newCastlingRights[opponentColor]!!.copy(queenside = false)
                } else if (toFile.isEqualTo('h') && toRank.isEqualTo(targetRank)) {
                    newCastlingRights[opponentColor] = newCastlingRights[opponentColor]!!.copy(kingside = false)
                }
            }
        }

        val isPawnDoublePush = pieceToMove.pieceType == PieceType.PAWN &&
                (fromRank.isEqualTo(2) && toRank.isEqualTo(4) || fromRank.isEqualTo(7) && toRank.isEqualTo(5))

        val newEnPassantTargetSquare = if (isPawnDoublePush) {
            val rankDirection = if (pieceToMove.color == PieceColor.WHITE) -1 else 1
            Square(toFile, toRank + rankDirection)
        } else {
            null
        }

        val newHalfMoveClock = if (pieceToMove.pieceType == PieceType.PAWN || move.isCapture) {
            0
        } else {
            gameState.halfMoveClock + 1
        }

        val newFullMoveNumber = if (gameState.currentPlayer == PieceColor.BLACK) {
            gameState.fullMoveNumber + 1
        } else {
            gameState.fullMoveNumber
        }

        val newGameState = gameState.copy(
            currentPlayer = gameState.opponent(),
            castlingRights = newCastlingRights,
            enPassantTargetSquare = newEnPassantTargetSquare,
            halfMoveClock = newHalfMoveClock,
            fullMoveNumber = newFullMoveNumber
        )

        return Pair(newBoard, newGameState)
    }

    /**
     * Checks if the king of the given [kingColor] is in check on the [board].
     *
     * @param kingColor The color of the king to check.
     * @param board The board to check on.
     * @return True if the king is in check, false otherwise.
     */
    fun isKingInCheck(kingColor: PieceColor, board: Board): Boolean {
        val kingSquare = board.getKingSquare(kingColor) ?: return false
        return isSquareAttackedBy(kingSquare, kingColor.opposite(), board)
    }

    /**
     * Determines if a [square] is attacked by any piece of [attackerColor] on the [board].
     *
     * @param square The square to check.
     * @param attackerColor The color of the potential attackers.
     * @param board The board to check on.
     * @return True if the square is attacked, false otherwise.
     */
    fun isSquareAttackedBy(square: Square, attackerColor: PieceColor, board: Board): Boolean {
        val pawnAttackDirection = if (attackerColor == PieceColor.WHITE) 1 else -1
        val pawnRank = square.rank - pawnAttackDirection

        if (pawnRank in Rank.getRange()) {
            for (fileOffset in listOf(-1, 1)) {
                val pawnFile = square.file + fileOffset
                if (pawnFile in File.getRange()) {
                    val pawnSquare = Square(pawnFile, pawnRank)
                    val piece = board[pawnSquare]
                    if (piece != null && piece.color == attackerColor && piece.pieceType == PieceType.PAWN) {
                        return true
                    }
                }
            }
        }

        val opponentGameState = GameState(
            currentPlayer = attackerColor,
            castlingRights = emptyMap(),
            enPassantTargetSquare = null,
            halfMoveClock = 0,
            fullMoveNumber = 0
        )

        val opponentMoves = PseudoLegalMoveGenerator.getAllPseudoLegalMoves(board, opponentGameState)
        return opponentMoves.any {
            val piece = board[it.from]
            it.to == square && piece?.pieceType != PieceType.PAWN
        }
    }

    /**
     * Checks if the given [board] position has insufficient material for either player to checkmate.
     *
     * @param board The board to check.
     * @return True if there is insufficient material, false otherwise.
     */
    fun hasInsufficientMaterial(board: Board): Boolean {
        val pieces = board.getAllPieces()

        if (pieces.size == 2) {
            return true
        }

        if (pieces.size == 3) {
            return pieces.any { it.first.pieceType == PieceType.BISHOP || it.first.pieceType == PieceType.KNIGHT }
        }

        val nonKingPieces = pieces.filter { it.first.pieceType != PieceType.KING }

        if (nonKingPieces.all { it.first.pieceType == PieceType.BISHOP }) {
            val firstSquare = nonKingPieces.firstOrNull()?.second
            if (firstSquare != null) {
                val isLight = firstSquare.isLightSquare()
                return nonKingPieces.all { it.second.isLightSquare() == isLight }
            }
        }


        return false
    }
}
