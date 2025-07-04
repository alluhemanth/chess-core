package com.hemanth.chess.core.move.piece

import com.hemanth.chess.core.board.Board
import com.hemanth.chess.core.board.Square
import com.hemanth.chess.core.game.CastlingAvailability
import com.hemanth.chess.core.game.GameState
import com.hemanth.chess.core.move.assertMoveDoesNotExist
import com.hemanth.chess.core.move.assertMoveExists
import com.hemanth.chess.core.move.createDefaultGameState
import com.hemanth.chess.core.piece.Piece
import com.hemanth.chess.core.piece.PieceColor
import com.hemanth.chess.core.piece.PieceType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.Test
import kotlin.test.assertEquals

class KingMoveGeneratorTest {

    private lateinit var kingMoveGenerator: KingMoveGenerator
    private lateinit var gameState: GameState
    private lateinit var board: Board

    @BeforeEach
    fun setUp() {
        kingMoveGenerator = KingMoveGenerator()
        gameState = createDefaultGameState()
        board = Board()
    }

    private fun placePiece(square: String, type: PieceType, color: PieceColor) {
        board[Square(square)] = Piece(type, color)
    }

    @Test
    fun `king moves one square in all directions on empty board`() {
        val kingSquare = Square("e4")
        placePiece("e4", PieceType.KING, PieceColor.WHITE)

        val moves = kingMoveGenerator.generatePseudoLegalMoves(board[kingSquare]!!, kingSquare, board, gameState)
        assertEquals(8, moves.size)

        val expectedTargets = listOf("d5", "e5", "f5", "d4", "f4", "d3", "e3", "f3")
        expectedTargets.forEach { assertMoveExists(moves, kingSquare, Square(it)) }
    }

    @ParameterizedTest
    @CsvSource(
        "d5,e5", // squares blocked by friendly pieces
        "d4,f4"
    )
    fun `king cannot move to squares with friendly pieces`(block1: String, block2: String) {
        val kingSquare = Square("e4")
        placePiece("e4", PieceType.KING, PieceColor.WHITE)
        placePiece(block1, PieceType.PAWN, PieceColor.WHITE)
        placePiece(block2, PieceType.PAWN, PieceColor.WHITE)

        val moves = kingMoveGenerator.generatePseudoLegalMoves(board[kingSquare]!!, kingSquare, board, gameState)
        assertMoveDoesNotExist(moves, kingSquare, Square(block1))
        assertMoveDoesNotExist(moves, kingSquare, Square(block2))
    }

    @Test
    fun `king can capture opponent pieces`() {
        val kingSquare = Square("e4")
        placePiece("e4", PieceType.KING, PieceColor.WHITE)
        placePiece("d5", PieceType.PAWN, PieceColor.BLACK)
        placePiece("e5", PieceType.PAWN, PieceColor.BLACK)

        val moves = kingMoveGenerator.generatePseudoLegalMoves(board[kingSquare]!!, kingSquare, board, gameState)
        assertMoveExists(moves, kingSquare, Square("d5"), isCapture = true)
        assertMoveExists(moves, kingSquare, Square("e5"), isCapture = true)
    }

    @Test
    fun `white king can castle kingside if rights and path are clear`() {
        val kingSquare = Square("e1")
        placePiece("e1", PieceType.KING, PieceColor.WHITE)
        placePiece("h1", PieceType.ROOK, PieceColor.WHITE)

        val moves = kingMoveGenerator.generatePseudoLegalMoves(board[kingSquare]!!, kingSquare, board, gameState)
        assertMoveExists(moves, kingSquare, Square("g1"))
    }

    @Test
    fun `white king can castle queenside if rights and path are clear`() {
        val kingSquare = Square("e1")
        placePiece("e1", PieceType.KING, PieceColor.WHITE)
        placePiece("a1", PieceType.ROOK, PieceColor.WHITE)

        val moves = kingMoveGenerator.generatePseudoLegalMoves(board[kingSquare]!!, kingSquare, board, gameState)
        assertMoveExists(moves, kingSquare, Square("c1"))
    }

    @Test
    fun `king cannot castle if castling rights are revoked`() {
        val kingSquare = Square("e1")
        placePiece("e1", PieceType.KING, PieceColor.WHITE)
        placePiece("h1", PieceType.ROOK, PieceColor.WHITE)

        gameState = gameState.copy(
            castlingRights = mapOf(
                PieceColor.WHITE to CastlingAvailability(kingside = false, queenside = false),
                PieceColor.BLACK to CastlingAvailability(kingside = true, queenside = true)
            )
        )

        val moves = kingMoveGenerator.generatePseudoLegalMoves(board[kingSquare]!!, kingSquare, board, gameState)
        assertMoveDoesNotExist(moves, kingSquare, Square("g1"))
        assertMoveDoesNotExist(moves, kingSquare, Square("c1"))
    }

    @Test
    fun `king cannot castle if path is blocked`() {
        val kingSquare = Square("e1")
        placePiece("e1", PieceType.KING, PieceColor.WHITE)
        placePiece("h1", PieceType.ROOK, PieceColor.WHITE)
        placePiece("f1", PieceType.BISHOP, PieceColor.WHITE)

        val moves = kingMoveGenerator.generatePseudoLegalMoves(board[kingSquare]!!, kingSquare, board, gameState)
        println(moves)
        assertMoveDoesNotExist(moves, kingSquare, Square("g1"))
    }
}