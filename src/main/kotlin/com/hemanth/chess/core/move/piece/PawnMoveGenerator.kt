package com.hemanth.chess.core.move.piece

import com.hemanth.chess.core.board.*
import com.hemanth.chess.core.game.GameState
import com.hemanth.chess.core.move.Move
import com.hemanth.chess.core.piece.Piece
import com.hemanth.chess.core.piece.PieceColor
import com.hemanth.chess.core.piece.PieceType
import kotlin.math.abs

/**
 * Generates all pseudo-legal moves for a pawn, including forward moves, captures,
 * promotions, and en passant.
 */
internal class PawnMoveGenerator : PieceMoveGenerator {

    /**
     * Holds pawn movement properties for a given color.
     */
    private data class PawnMovementProperties(
        val moveDirection: Int,
        val startingRank: Rank,
        val promotionRank: Rank,
        val enPassantEligibleRank: Rank
    )

    companion object {
        private val PROMOTABLE_PIECE_TYPES = setOf(
            PieceType.QUEEN,
            PieceType.ROOK,
            PieceType.BISHOP,
            PieceType.KNIGHT
        )
    }

    /**
     * Generates all pseudo-legal pawn moves from the given square.
     *
     * @param piece The pawn piece.
     * @param fromSquare The square the pawn is moving from.
     * @param board The current board state.
     * @param gameState The current game state.
     * @return List of possible moves for the pawn.
     */
    override fun generatePseudoLegalMoves(
        piece: Piece,
        fromSquare: Square,
        board: Board,
        gameState: GameState,
    ): List<Move> {
        require(piece.pieceType == PieceType.PAWN) { "Piece must be a PAWN" }

        val rank1 = Rank(1)
        val rank2 = Rank(2)
        val rank4 = Rank(4)
        val rank5 = Rank(5)
        val rank7 = Rank(7)
        val rank8 = Rank(8)

        val props = when (piece.color) {
            PieceColor.WHITE -> PawnMovementProperties(1, rank2, rank8, rank5)
            PieceColor.BLACK -> PawnMovementProperties(-1, rank7, rank1, rank4)
        }

        val moves = mutableListOf<Move>()
        addForwardMoves(moves, fromSquare, board, props)
        addCaptureMoves(moves, fromSquare, board, props)
        addEnPassantMoves(moves, fromSquare, gameState, props)
        return moves
    }

    /**
     * Adds forward pawn moves (including double step and promotions) to the move list.
     */
    private fun addForwardMoves(
        moves: MutableList<Move>,
        fromSquare: Square,
        board: Board,
        props: PawnMovementProperties,
    ) {
        val file = fromSquare.file
        val rank = fromSquare.rank

        val oneStepRank = rank + props.moveDirection
        if (oneStepRank !in Rank.getRange()) return

        val oneStepSquare = Square(file, oneStepRank)
        if (board[oneStepSquare] == null) {
            addMoveOrPromotions(moves, fromSquare, oneStepSquare, false, props.promotionRank)

            if (rank == props.startingRank) {
                val twoStepRank = rank + 2 * props.moveDirection
                val twoStepSquare = Square(file, twoStepRank)
                if (board[twoStepSquare] == null) {
                    moves.add(Move(fromSquare, twoStepSquare, isCapture = false))
                }
            }
        }
    }

    /**
     * Adds pawn capture moves (including promotions) to the move list.
     */
    private fun addCaptureMoves(
        moves: MutableList<Move>,
        fromSquare: Square,
        board: Board,
        props: PawnMovementProperties,
    ) {
        val file = fromSquare.file
        val rank = fromSquare.rank
        val captureRank = rank + props.moveDirection

        for (fileOffset in listOf(-1, 1)) {
            val targetFile = file + fileOffset
            if (targetFile !in File.getRange()) continue

            val targetSquare = Square(targetFile, captureRank)
            val targetPiece = board[targetSquare]
            if (targetPiece != null && targetPiece.color != board[fromSquare]?.color) {
                addMoveOrPromotions(moves, fromSquare, targetSquare, true, props.promotionRank)
            }
        }
    }

    /**
     * Adds en passant capture moves to the move list if available.
     */
    private fun addEnPassantMoves(
        moves: MutableList<Move>,
        fromSquare: Square,
        gameState: GameState,
        props: PawnMovementProperties,
    ) {
        val enPassantSquare = gameState.enPassantTargetSquare ?: return
        if (fromSquare.rank == props.enPassantEligibleRank &&
            enPassantSquare.rank == fromSquare.rank + props.moveDirection &&
            abs(enPassantSquare.file.value.code - fromSquare.file.value.code) == 1
        ) {
            moves.add(
                Move(
                    from = fromSquare,
                    to = enPassantSquare,
                    isCapture = true,
                    isEnPassantCapture = true
                )
            )
        }
    }

    /**
     * Adds a move or all promotion moves to the move list, depending on the rank.
     */
    private fun addMoveOrPromotions(
        moves: MutableList<Move>,
        fromSquare: Square,
        toSquare: Square,
        isCapture: Boolean,
        promotionRank: Rank
    ) {
        if (toSquare.rank == promotionRank) {
            for (type in PROMOTABLE_PIECE_TYPES) {
                moves.add(Move(fromSquare, toSquare, isCapture = isCapture, promotionPieceType = type))
            }
        } else {
            moves.add(Move(fromSquare, toSquare, isCapture = isCapture))
        }
    }
}