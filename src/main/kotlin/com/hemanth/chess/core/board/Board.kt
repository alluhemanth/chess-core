package com.hemanth.chess.core.board

import com.hemanth.chess.core.move.Move
import com.hemanth.chess.core.piece.Piece
import com.hemanth.chess.core.piece.PieceColor
import com.hemanth.chess.core.piece.PieceType

/**
 * The Board class represents the positions of pieces on a chessboard at a specific moment. It tracks which [Piece]
 * occupies each of the 64 squares, if any. This class focuses solely on the arrangement of pieces and provides methods
 * for querying and updating positions, applying moves to generate new board configurations, and accessing pieces by
 * square or algebraic notation. It does not track additional game state details such as castling rights, en passant
 * targets, or move history.
 *
 * ## Overview
 * - Each square is uniquely identified by its [File] (column) and [Rank] (row).
 * - The board state is internally managed using a mutable map, allowing efficient access and updates.
 * - The class supports algebraic notation (e.g., "e4") for intuitive square referencing.
 * - Special chess rules such as castling, en passant, and pawn promotion are handled seamlessly.
 *
 * ## Internal Implementation Details
 * ### Board State Storage
 * The board state is stored in a `MutableMap<Square, Piece?>` called `squaresMap`.
 * Each key is a [Square] (which combines a [File] and [Rank]), and the value is either a [Piece] (if a piece occupies
 * that square) or `null` (if the square is empty). All 64 squares are always present as keys in the map.
 *
 * ### Access Patterns
 * - **By Square:** You can access or modify a square directly using the [Square] object.
 * - **By File and Rank:** Overloaded operators allow access via [File] and [Rank] parameters.
 * - **By Algebraic Notation:** You can use algebraic notation (e.g., `"e4"`) to get or set a piece, which is internally
 * converted to a [Square].
 *
 * ### Manipulation
 * - **Setting a Piece:** Assigning a [Piece] (or `null`) to a square updates the corresponding entry in `squaresMap`.
 *
 * - **Applying Moves:** The [applyMove] method creates a deep copy of the board, applies the move (including special
 * rules like promotion, en passant, and castling), and returns the new board.
 *
 * - **Immutability:** Although `squaresMap` is mutable for internal convenience, the class is designed to be used
 * immutably: most operations return a new `Board` instance, preserving the original.
 *
 * - **Copying:** The [copy] method creates a deep copy of the board and all pieces, ensuring no shared mutable state
 * between board instances.
 *
 * ### Example of Internal Access
 * ```
 * // Access by file and rank
 * val piece = board[File('e'), Rank(4)]
 * // Set by square
 * board[Square("e4")] = Piece(PieceType.QUEEN, PieceColor.WHITE)
 * // Access by algebraic notation
 * val pawn = board["e2"]
 * ```
 *
 * ### Usage Example
 * ```Kotlin
 * // Initializing a chess board and applying a move
 * val board = Board.initialBoard()
 * val move = Move("e2","e4")
 * val newBoard = board.applyMove(move)
 * ```
 *
 * @property squaresMap A mutable map from [Square] to [Piece?], representing the current state of the board.
 */
class Board(
    /**
     * A mutable map from [Square] to [Piece?], representing the board state.
     */
    private val squaresMap: MutableMap<Square, Piece?> = mutableMapOf()
) {

    /**
     * Initializes the board with empty squares if not already present in [squaresMap].
     */
    init {
        for (rank in 1..8) {
            for (fileChar in 'a'..'h') {
                val square = Square(File(fileChar), Rank(rank))
                if (!squaresMap.containsKey(square)) {
                    squaresMap[square] = null
                }
            }
        }
    }

    /**
     * Gets the [Piece] at the given [File] and [Rank], or null if the square is empty.
     */
    operator fun get(file: File, rank: Rank): Piece? = squaresMap[Square(file, rank)]

    /**
     * Sets the [Piece] at the given [File] and [Rank].
     */
    internal operator fun set(file: File, rank: Rank, piece: Piece?) {
        squaresMap[Square(file, rank)] = piece
    }

    /**
     * Gets the [Piece] at the given [Square], or null if the square is empty.
     */
    operator fun get(square: Square): Piece? = squaresMap[square]

    /**
     * Sets the [Piece] at the given [Square].
     */
    internal operator fun set(square: Square, piece: Piece?) {
        squaresMap[square] = piece
    }

    /**
     * Gets the [Piece] at the square specified by algebraic notation.
     */
    operator fun get(notation: String): Piece? {
        return get(getSquareAtNotation(notation))
    }

    /**
     * Sets the [Piece] at the square specified by algebraic notation.
     */
    internal operator fun set(notation: String, piece: Piece?) {
        set(getSquareAtNotation(notation), piece)
    }

    /**
     * Returns the [Square] object corresponding to the given algebraic notation.
     * @throws IllegalArgumentException if the notation is invalid.
     */
    private fun getSquareAtNotation(notation: String): Square {
        require(notation.length == 2) { "Notation must be 2 characters (file and rank, e.g., 'e4')" }

        val fileChar = notation[0].lowercaseChar()
        val rankNum = notation[1].digitToInt()

        require(fileChar in 'a'..'h') { "File must be between 'a' and 'h'" }
        require(rankNum in 1..8) { "Rank must be between 1 and 8" }

        return Square(File(fileChar), Rank(rankNum))
    }

    /**
     * Applies a [Move] and returns a new [Board] with the move applied.
     */
    fun applyMove(move: Move): Board {
        val pieceToMove = this[move.from]
            ?: throw IllegalStateException("No piece at from square ${move.from} for move $move")
        val newBoard = this.copy()
        newBoard[move.from] = null
        newBoard[move.to] = if (move.promotionPieceType != null && pieceToMove.pieceType == PieceType.PAWN) {
            Piece(move.promotionPieceType, pieceToMove.color)
        } else {
            pieceToMove
        }

        when {
            move.isEnPassantCapture -> {
                val capturedPawnSquare = Square(move.to.file, move.from.rank)
                newBoard[capturedPawnSquare] = null
            }

            move.isCastlingKingside -> {
                val rookRank = move.from.rank
                val rookOriginalSquare = Square(File('h'), rookRank)
                val rookNewSquare = Square(File('f'), rookRank)
                newBoard[rookNewSquare] = newBoard[rookOriginalSquare]
                newBoard[rookOriginalSquare] = null
            }

            move.isCastlingQueenside -> {
                val rookRank = move.from.rank
                val rookOriginalSquare = Square(File('a'), rookRank)
                val rookNewSquare = Square(File('d'), rookRank)
                newBoard[rookNewSquare] = newBoard[rookOriginalSquare]
                newBoard[rookOriginalSquare] = null
            }
        }
        return newBoard
    }

    /**
     * Returns a list of all pieces on the board, paired with their [Square].
     */
    fun getAllPieces(): List<Pair<Piece, Square>> {
        return squaresMap.entries
            .filter { it.value != null }
            .map { (square, piece) -> piece!! to square }
    }

    /**
     * Returns the [Square] occupied by the king of the given [PieceColor], or null if not found.
     */
    internal fun getKingSquare(color: PieceColor): Square? {
        return squaresMap.entries.find { (_, piece) ->
            piece?.color == color && piece.pieceType == PieceType.KING
        }?.key
    }

    /**
     * Returns a deep copy of the [Board]
     */
    fun copy(): Board {
        val newSquaresMap = mutableMapOf<Square, Piece?>()
        squaresMap.forEach { (square, piece) ->
            newSquaresMap[square] = piece?.copy()
        }
        return Board(newSquaresMap)
    }

    /**
     * Returns a [String] representation of the [Board]
     */
    override fun toString(): String =
        (8 downTo 1).joinToString("\n") { rankNum ->
            ('a'..'h').joinToString(" ") { fileChar ->
                this[File(fileChar), Rank(rankNum)]?.toString() ?: "."
            }
        }

    companion object {
        /**
         * Returns a [Board] initialized to the standard chess starting position.
         */
        fun initialBoard(): Board {
            val squaresMap = mutableMapOf<Square, Piece?>()

            for (rank in 1..8) {
                for (fileChar in 'a'..'h') {
                    squaresMap[Square("$fileChar$rank")] = null
                }
            }

            // Pawns
            for (fileChar in 'a'..'h') {
                squaresMap[Square("${fileChar}2")] = Piece(PieceType.PAWN, PieceColor.WHITE)
                squaresMap[Square("${fileChar}7")] = Piece(PieceType.PAWN, PieceColor.BLACK)
            }

            // Rooks
            squaresMap[Square("a1")] = Piece(PieceType.ROOK, PieceColor.WHITE)
            squaresMap[Square("h1")] = Piece(PieceType.ROOK, PieceColor.WHITE)
            squaresMap[Square("a8")] = Piece(PieceType.ROOK, PieceColor.BLACK)
            squaresMap[Square("h8")] = Piece(PieceType.ROOK, PieceColor.BLACK)

            // Knights
            squaresMap[Square("b1")] = Piece(PieceType.KNIGHT, PieceColor.WHITE)
            squaresMap[Square("g1")] = Piece(PieceType.KNIGHT, PieceColor.WHITE)
            squaresMap[Square("b8")] = Piece(PieceType.KNIGHT, PieceColor.BLACK)
            squaresMap[Square("g8")] = Piece(PieceType.KNIGHT, PieceColor.BLACK)

            // Bishops
            squaresMap[Square("c1")] = Piece(PieceType.BISHOP, PieceColor.WHITE)
            squaresMap[Square("f1")] = Piece(PieceType.BISHOP, PieceColor.WHITE)
            squaresMap[Square("c8")] = Piece(PieceType.BISHOP, PieceColor.BLACK)
            squaresMap[Square("f8")] = Piece(PieceType.BISHOP, PieceColor.BLACK)

            // Queens
            squaresMap[Square("d1")] = Piece(PieceType.QUEEN, PieceColor.WHITE)
            squaresMap[Square("d8")] = Piece(PieceType.QUEEN, PieceColor.BLACK)

            // Kings
            squaresMap[Square("e1")] = Piece(PieceType.KING, PieceColor.WHITE)
            squaresMap[Square("e8")] = Piece(PieceType.KING, PieceColor.BLACK)

            return Board(squaresMap)
        }
    }
}