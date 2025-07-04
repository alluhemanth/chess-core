package com.hemanth.chess.core.move.piece

import com.hemanth.chess.core.board.*
import com.hemanth.chess.core.game.GameState
import com.hemanth.chess.core.move.Move
import com.hemanth.chess.core.move.Offset
import com.hemanth.chess.core.piece.Piece
import com.hemanth.chess.core.piece.PieceColor
import com.hemanth.chess.core.piece.PieceType

/**
 * Generates all pseudo-legal moves for a king, including castling.
 */
internal class KingMoveGenerator : PieceMoveGenerator {

    /**
     * Generates all pseudo-legal king moves from the given square, including castling moves.
     *
     * @param piece The king piece.
     * @param fromSquare The square the king is moving from.
     * @param board The current board state.
     * @param gameState The current game state.
     * @return List of possible moves for the king.
     */
    override fun generatePseudoLegalMoves(
        piece: Piece,
        fromSquare: Square,
        board: Board,
        gameState: GameState,
    ): List<Move> {
        require(piece.pieceType == PieceType.KING) { "Piece must be a KING" }

        val moves = mutableListOf<Move>()
        val kingColor = piece.color
        val offsets = listOf(
            Offset(0, 1), Offset(0, -1), Offset(1, 0), Offset(-1, 0),
            Offset(1, 1), Offset(1, -1), Offset(-1, 1), Offset(-1, -1)
        )
        val fileRange = File.getRange()
        val rankRange = Rank.getRange()

        for (offset in offsets) {
            val targetFile = fromSquare.file + offset.fileDelta
            val targetRank = fromSquare.rank + offset.rankDelta

            if (targetRank in rankRange && targetFile in fileRange) {
                val targetSquare = Square(targetFile, targetRank)
                val targetPiece = board[targetSquare]

                if (targetPiece == null) {
                    moves.add(Move(fromSquare, targetSquare))
                } else if (targetPiece.color != kingColor) {
                    moves.add(Move(fromSquare, targetSquare, isCapture = true))
                }
            }
        }

        val isOnInitialSquare = fromSquare.file.isEqualTo('e') &&
                ((kingColor == PieceColor.WHITE && fromSquare.rank.isEqualTo(1)) ||
                        (kingColor == PieceColor.BLACK && fromSquare.rank.isEqualTo(8)))

        if (isOnInitialSquare) {
            gameState.castlingRights[kingColor]?.let { rights ->
                addCastlingMoveIfLegal(
                    board,
                    fromSquare,
                    kingColor,
                    rights.kingside,
                    true,
                    ::getKingsideCastlingSquares,
                    moves
                )
                addCastlingMoveIfLegal(
                    board,
                    fromSquare,
                    kingColor,
                    rights.queenside,
                    false,
                    ::getQueensideCastlingSquares,
                    moves
                )
            }
        }

        return moves
    }

    /**
     * Returns the squares the king must cross and the destination for kingside castling.
     *
     * @param kingRank The rank of the king.
     * @return Pair of squares to clear and the king's destination square.
     */
    private fun getKingsideCastlingSquares(kingRank: Rank): Pair<List<Square>, Square> =
        Pair(
            listOf(Square("f$kingRank"), Square("g$kingRank")),
            Square("g$kingRank")
        )

    /**
     * Returns the squares the king must cross and the destination for queenside castling.
     *
     * @param kingRank The rank of the king.
     * @return Pair of squares to clear and the king's destination square.
     */
    private fun getQueensideCastlingSquares(kingRank: Rank): Pair<List<Square>, Square> =
        Pair(
            listOf(Square("d$kingRank"), Square("c$kingRank"), Square("b$kingRank")),
            Square("c$kingRank")
        )

    /**
     * Adds a castling move to the move list if the castling rights and board state allow it.
     *
     * @param board The current board state.
     * @param fromSquare The king's starting square.
     * @param kingColor The color of the king.
     * @param rights Whether castling rights are available.
     * @param isKingside True if kingside castling, false if queenside.
     * @param getCastlingSquares Function to get the squares to clear and destination.
     * @param moves The list to add the move to.
     */
    private fun addCastlingMoveIfLegal(
        board: Board,
        fromSquare: Square,
        kingColor: PieceColor,
        rights: Boolean,
        isKingside: Boolean,
        getCastlingSquares: (Rank) -> Pair<List<Square>, Square>,
        moves: MutableList<Move>
    ) {
        if (!rights) return

        val (squaresToClear, kingDestination) = getCastlingSquares(fromSquare.rank)
        val rookFile = if (isKingside) 'h' else 'a'
        val rookSquare = Square(File(rookFile), fromSquare.rank)
        val rook = board[rookSquare]

        if (
            squaresToClear.all { board[it] == null } &&
            rook?.pieceType == PieceType.ROOK &&
            rook.color == kingColor
        ) {
            moves.add(
                Move(
                    from = fromSquare,
                    to = kingDestination,
                    isCastlingKingside = isKingside,
                    isCastlingQueenside = !isKingside
                )
            )
        }
    }


}