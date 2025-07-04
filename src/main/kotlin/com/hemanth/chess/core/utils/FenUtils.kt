package com.hemanth.chess.core.utils

import com.hemanth.chess.core.board.Board
import com.hemanth.chess.core.board.File
import com.hemanth.chess.core.board.Rank
import com.hemanth.chess.core.board.Square
import com.hemanth.chess.core.exception.InvalidFenException
import com.hemanth.chess.core.game.CastlingAvailability
import com.hemanth.chess.core.game.GameState
import com.hemanth.chess.core.piece.Piece
import com.hemanth.chess.core.piece.PieceColor
import com.hemanth.chess.core.piece.PieceType

/**
 * Utility object for parsing and generating FEN (Forsyth-Edwards Notation) strings.
 * Provides methods to convert between FEN strings and board/game state objects.
 *
 * ## Overview
 * - **Parsing:** Converts FEN strings into [Board] and [GameState] objects.
 * - **Rendering:** Converts [Board] and [GameState] objects into FEN strings.
 * - **Utility:** Provides helper methods for FEN-related operations.
 *
 * ### Parsing Methods
 * - **parseFen:** Parses a FEN string into a [Board] and [GameState].
 *
 * ### Rendering Methods
 * - **getFenFromBoardAndState:** Generates a FEN string from a [Board] and [GameState].
 * - **renderPiecePlacement:** Converts the board's piece placement into FEN format.
 * - **renderCastlingRights:** Converts castling availability into FEN format.
 *
 * ### Utility Methods
 * - **pieceTypeFromFenChar:** Maps FEN characters to [PieceType].
 *
 * ### Example Usage
 * ```Kotlin
 * val (board, gameState) = FenUtils.parseFen(FenUtils.DEFAULT_FEN)
 * val fen = FenUtils.getFenFromBoardAndState(board, gameState)
 * ```
 */
object FenUtils {

    /** The default FEN string for the standard chess starting position. */
    const val DEFAULT_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

    /**
     * Parses a FEN string into a [Board] and [GameState].
     *
     * @param fen The FEN string to parse.
     * @return A pair of [Board] and [GameState] representing the position.
     * @throws InvalidFenException if the FEN string is invalid.
     */
    fun parseFen(fen: String): Pair<Board, GameState> {
        val parts = fen.trim().split(" ")
        if (parts.size != 6) throw InvalidFenException("Invalid FEN string (requires 6 parts): $fen")

        val board = Board()
        val piecePlacement = parts[0]
        val ranks = piecePlacement.split("/")
        if (ranks.size != 8) throw InvalidFenException("Invalid FEN: must have 8 ranks, got ${ranks.size}")

        var whiteKingCount = 0
        var blackKingCount = 0

        for ((rankIdx, rankStr) in ranks.withIndex()) {
            var fileIdx = 0
            for (char in rankStr) {
                when {
                    char.isDigit() -> fileIdx += char.digitToInt()
                    char.isLetter() -> {
                        if (fileIdx > 7) throw InvalidFenException("Invalid FEN: File index out of bounds in rank ${8 - rankIdx}")

                        val file = File(('a' + fileIdx))
                        val rank = Rank(8 - rankIdx)
                        val color = if (char.isUpperCase()) PieceColor.WHITE else PieceColor.BLACK
                        val type = pieceTypeFromFenChar(char.lowercaseChar())

                        // Check for pawns on rank 1 or 8
                        if (type == PieceType.PAWN && (rank.value == 1 || rank.value == 8)) {
                            throw InvalidFenException("Invalid FEN: pawn on invalid rank ${rank.value} at ${file}${rank.value}")
                        }

                        // Track kings count
                        if (type == PieceType.KING) {
                            if (color == PieceColor.WHITE) whiteKingCount++
                            else blackKingCount++
                        }

                        board[file, rank] = Piece(type, color)
                        fileIdx++
                    }

                    else -> throw InvalidFenException("Invalid FEN: Unexpected character '$char' in piece placement")
                }
            }
            if (fileIdx != 8) throw InvalidFenException("Invalid FEN: Rank ${8 - rankIdx} does not have 8 files")
        }

        // Validate exactly one king for each color
        if (whiteKingCount != 1 || blackKingCount != 1) {
            throw InvalidFenException("Invalid FEN: must have exactly one king for each color (found $whiteKingCount white, $blackKingCount black)")
        }

        val activeColor = when (parts[1]) {
            "w" -> PieceColor.WHITE
            "b" -> PieceColor.BLACK
            else -> throw InvalidFenException("Invalid FEN: active color must be 'w' or 'b'")
        }

        val castlingString = parts[2]
        if (!castlingString.matches(Regex("(-|[KQkq]+)"))) {
            throw InvalidFenException("Invalid FEN: castling rights string: $castlingString")
        }

        val whiteCastling = CastlingAvailability(
            kingside = castlingString.contains('K'),
            queenside = castlingString.contains('Q')
        )
        val blackCastling = CastlingAvailability(
            kingside = castlingString.contains('k'),
            queenside = castlingString.contains('q')
        )

        val castlingRights = mapOf(
            PieceColor.WHITE to whiteCastling,
            PieceColor.BLACK to blackCastling
        )

        val enPassantTargetSquare = if (parts[3] == "-") {
            null
        } else {
            val square = try {
                Square(parts[3])
            } catch (e: Exception) {
                throw InvalidFenException("Invalid en passant square in FEN: ${parts[3]}", e)
            }

            // En passant square must be on rank 3 or 6
            if (square.rank.value !in setOf(3, 6)) {
                throw InvalidFenException("Invalid en passant square in FEN: ${parts[3]}")
            }

            square
        }

        val halfMoveClock = parts.getOrNull(4)?.toIntOrNull()
            ?: throw InvalidFenException("Invalid FEN: missing or invalid halfmove clock")

        val fullMoveNumber = parts.getOrNull(5)?.toIntOrNull()
            ?: throw InvalidFenException("Invalid FEN: missing or invalid fullmove number")

        val gameState = GameState(
            currentPlayer = activeColor,
            castlingRights = castlingRights,
            enPassantTargetSquare = enPassantTargetSquare,
            halfMoveClock = halfMoveClock,
            fullMoveNumber = fullMoveNumber
        )

        return Pair(board, gameState)
    }

    /**
     * Returns the [PieceType] corresponding to the given FEN character.
     *
     * @param c The FEN character representing a piece.
     * @return The corresponding [PieceType].
     * @throws InvalidFenException if the character does not represent a valid piece type.
     */
    private fun pieceTypeFromFenChar(c: Char): PieceType {
        return when (c) {
            'p' -> PieceType.PAWN
            'r' -> PieceType.ROOK
            'n' -> PieceType.KNIGHT
            'b' -> PieceType.BISHOP
            'q' -> PieceType.QUEEN
            'k' -> PieceType.KING
            else -> throw InvalidFenException("Invalid FEN piece char: $c")
        }
    }

    // Rendering Methods
    /**
     * Generates a FEN string from the given [board] and [gameState].
     *
     * @param board The board to convert.
     * @param gameState The game state to convert.
     * @return The FEN string representing the position.
     */
    fun getFenFromBoardAndState(board: Board, gameState: GameState): String {
        val fields = listOf(
            renderPiecePlacement(board),
            if (gameState.currentPlayer == PieceColor.WHITE) "w" else "b",
            renderCastlingRights(
                gameState.castlingRights[PieceColor.WHITE],
                gameState.castlingRights[PieceColor.BLACK]
            ),
            gameState.enPassantTargetSquare?.toString() ?: "-",
            gameState.halfMoveClock.toString(),
            gameState.fullMoveNumber.toString()
        )
        return fields.joinToString(" ")
    }


    /**
     * Renders the piece placement part of a FEN string from the given [board].
     *
     * @param board The board to render.
     * @return The piece placement string.
     */
    private fun renderPiecePlacement(board: Board): String {
        val builder = StringBuilder()
        for (rankIndex in 8 downTo 1) {
            var emptyCount = 0
            for (fileIndex in 0..7) {
                val file = File(('a' + fileIndex))
                val rank = Rank(rankIndex)
                val piece = board[file, rank]
                if (piece == null) {
                    emptyCount++
                } else {
                    if (emptyCount > 0) {
                        builder.append(emptyCount)
                        emptyCount = 0
                    }
                    builder.append(piece.toString())
                }
            }
            if (emptyCount > 0) builder.append(emptyCount)
            if (rankIndex != 1) builder.append("/")
        }
        return builder.toString()
    }

    /**
     * Renders the castling rights part of a FEN string from the given castling availabilities.
     *
     * @param white The white castling availability.
     * @param black The black castling availability.
     * @return The castling rights string.
     */
    private fun renderCastlingRights(white: CastlingAvailability?, black: CastlingAvailability?): String {
        val rights = mutableListOf<Char>()
        if (white?.kingside == true) rights += 'K'
        if (white?.queenside == true) rights += 'Q'
        if (black?.kingside == true) rights += 'k'
        if (black?.queenside == true) rights += 'q'
        return if (rights.isEmpty()) "-" else rights.joinToString("")
    }
}