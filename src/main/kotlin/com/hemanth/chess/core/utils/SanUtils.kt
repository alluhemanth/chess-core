package com.hemanth.chess.core.utils

import com.hemanth.chess.core.ChessGame
import com.hemanth.chess.core.board.Board
import com.hemanth.chess.core.board.File
import com.hemanth.chess.core.board.Square
import com.hemanth.chess.core.game.GameResult
import com.hemanth.chess.core.game.GameState
import com.hemanth.chess.core.game.GameUtils
import com.hemanth.chess.core.move.LegalMoveGenerator
import com.hemanth.chess.core.move.Move
import com.hemanth.chess.core.piece.PieceType

/**
 * Utility object for converting between SAN (Standard Algebraic Notation) and [Move] objects.
 * Provides methods to parse SAN strings into moves and to generate SAN strings from moves.
 *
 * ## Overview
 * - **Parsing:** Converts SAN strings into [Move] objects.
 * - **Rendering:** Converts [Move] objects into SAN strings.
 * - **Utility:** Provides helper methods for SAN-related operations.
 *
 * ### Parsing Methods
 * - **sanToMove:** Parses a SAN string into a [Move] object.
 *
 * ### Rendering Methods
 * - **moveToSan:** Converts a [Move] object into its SAN string representation.
 *
 * ### Utility Methods
 * - **pieceTypeFromSanChar:** Maps SAN characters to [PieceType].
 * - **pieceToChar:** Maps [PieceType] to SAN characters.
 *
 * ### Example Usage
 * ```Kotlin
 * val move = SanUtils.sanToMove("e4", board, gameState)
 * val san = SanUtils.moveToSan(move, board, gameState)
 * ```
 */
object SanUtils {

    private val legalMoveGenerator = LegalMoveGenerator()

    /**
     * Converts a SAN (Standard Algebraic Notation) string to a [Move] object for the given [board] and [gameState].
     *
     * @param san The SAN string representing the move.
     * @param board The current board state.
     * @param gameState The current game state.
     * @return The corresponding [Move] object.
     * @throws IllegalArgumentException if the SAN string is invalid or ambiguous.
     */
    fun sanToMove(san: String, board: Board, gameState: GameState): Move {
        val legalMoves = legalMoveGenerator.getAllLegalMoves(board, gameState)

        if (san == "O-O") return legalMoves.first { it.isCastlingKingside }
        if (san == "O-O-O") return legalMoves.first { it.isCastlingQueenside }

        val cleanSan = san.replace(Regex("[+#]"), "")

        val promotionPieceType = if (cleanSan.contains("=")) {
            when (cleanSan.last().uppercaseChar()) {
                'Q' -> PieceType.QUEEN
                'R' -> PieceType.ROOK
                'B' -> PieceType.BISHOP
                'N' -> PieceType.KNIGHT
                else -> throw IllegalArgumentException("Invalid promotion piece: ${cleanSan.last()}")
            }
        } else {
            null
        }

        val sanWithoutPromotion = cleanSan.substringBefore("=")

        val pieceType = when {
            sanWithoutPromotion.first().isUpperCase() -> pieceTypeFromSanChar(sanWithoutPromotion.first())
            else -> PieceType.PAWN
        }

        val destinationString = sanWithoutPromotion.takeLast(2)
        val destinationSquare = Square(destinationString)

        var possibleMoves = legalMoves.filter {
            val piece = board[it.from]
            piece?.pieceType == pieceType && it.to == destinationSquare && it.promotionPieceType == promotionPieceType
        }

        if (possibleMoves.isEmpty()) {
            throw IllegalArgumentException("Illegal move: $san")
        }

        if (possibleMoves.size == 1) {
            return possibleMoves.first()
        }

        val sanPrefix = sanWithoutPromotion.dropLast(2)
        val disambiguationHint = if (pieceType == PieceType.PAWN) {
            sanPrefix.replace("x", "")
        } else {
            sanPrefix.drop(1).replace("x", "")
        }

        if (disambiguationHint.isNotEmpty()) {
            possibleMoves = possibleMoves.filter { move ->
                if (disambiguationHint.length == 1) {
                    val disChar = disambiguationHint.first()
                    if (disChar in File.getRange()) {
                        move.from.file.value == disChar
                    } else {
                        move.from.rank.value == disChar.digitToInt()
                    }
                } else if (disambiguationHint.length == 2) {
                    move.from == Square(disambiguationHint)
                } else {
                    false
                }
            }
        }

        if (possibleMoves.size == 1) {
            return possibleMoves.first()
        }

        if (possibleMoves.isEmpty()) {
            throw IllegalArgumentException("Illegal move: $san")
        }

        throw IllegalArgumentException("Ambiguous move: $san")
    }

    /**
     * Converts a [Move] object to its SAN (Standard Algebraic Notation) string representation.
     *
     * @param move The move to convert.
     * @param board The current board state.
     * @param gameState The current game state.
     * @return The SAN string representing the move.
     */
    fun moveToSan(move: Move, board: Board, gameState: GameState): String {
        if (move.isCastlingKingside) return "O-O"
        if (move.isCastlingQueenside) return "O-O-O"

        val piece = board[move.from] ?: return ""
        val pieceType = piece.pieceType

        val san = StringBuilder()

        if (pieceType != PieceType.PAWN) {
            san.append(piece.toString().uppercase())
        }

        val legalMoves = legalMoveGenerator.getAllLegalMoves(board, gameState)
        val ambiguousMoves = legalMoves.filter {
            it != move &&
                    board[it.from]?.pieceType == pieceType &&
                    it.to == move.to
        }

        if (ambiguousMoves.isNotEmpty() && pieceType != PieceType.PAWN) {
            val fileAmbiguous = ambiguousMoves.any { it.from.file == move.from.file }
            val rankAmbiguous = ambiguousMoves.any { it.from.rank == move.from.rank }

            if (!fileAmbiguous) {
                san.append(move.from.file.value)
            } else if (!rankAmbiguous) {
                san.append(move.from.rank.value)
            } else {
                san.append(move.from.toString())
            }
        }

        if (board[move.to] != null || move.isEnPassantCapture) {
            if (pieceType == PieceType.PAWN) {
                san.append(move.from.file.value)
            }
            san.append("x")
        }

        san.append(move.to.toString())

        if (move.promotionPieceType != null) {
            san.append("=").append(pieceToChar(move.promotionPieceType))
        }

        val (nextBoard, nextGameState) = GameUtils.makeMove(board, gameState, move)
        if (GameUtils.isKingInCheck(nextGameState.currentPlayer, nextBoard)) {
            val tempGame = ChessGame(nextBoard, nextGameState)
            val isCheckmate = tempGame.getGameResult() is GameResult.Win
            san.append(if (isCheckmate) "#" else "+")
        }

        return san.toString()
    }

    /**
     * Returns the [PieceType] corresponding to the given SAN character.
     *
     * @param char The SAN character representing a piece.
     * @return The corresponding [PieceType].
     * @throws IllegalArgumentException if the character does not represent a valid piece type.
     */
    private fun pieceTypeFromSanChar(char: Char): PieceType {
        return when (char.uppercaseChar()) {
            'N' -> PieceType.KNIGHT
            'B' -> PieceType.BISHOP
            'R' -> PieceType.ROOK
            'Q' -> PieceType.QUEEN
            'K' -> PieceType.KING
            else -> throw IllegalArgumentException("Invalid piece type SAN char: $char")
        }
    }


    /**
     * Returns the SAN character corresponding to the given [PieceType].
     *
     * @param pieceType The piece type.
     * @return The SAN character for the piece type.
     */
    private fun pieceToChar(pieceType: PieceType): Char {
        return when (pieceType) {
            PieceType.PAWN -> 'P'
            PieceType.KNIGHT -> 'N'
            PieceType.BISHOP -> 'B'
            PieceType.ROOK -> 'R'
            PieceType.QUEEN -> 'Q'
            PieceType.KING -> 'K'
        }
    }
}