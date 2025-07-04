package com.hemanth.chess.core.move.piece

import com.hemanth.chess.core.board.Board
import com.hemanth.chess.core.board.Square
import com.hemanth.chess.core.game.GameState
import com.hemanth.chess.core.move.assertMoveDoesNotExist
import com.hemanth.chess.core.move.assertMoveExists
import com.hemanth.chess.core.move.createDefaultGameState
import com.hemanth.chess.core.piece.Piece
import com.hemanth.chess.core.piece.PieceColor
import com.hemanth.chess.core.piece.PieceType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RookMoveGeneratorTest {

    private lateinit var rookMoveGenerator: RookMoveGenerator
    private lateinit var gameState: GameState
    private lateinit var board: Board

    @BeforeEach
    fun setUp() {
        rookMoveGenerator = RookMoveGenerator()
        gameState = createDefaultGameState()
        board = Board()
    }

    @Test
    fun `rook can move vertically and horizontally on an empty board`() {
        val rookSquare = Square("d4")
        val rook = Piece(PieceType.ROOK, PieceColor.WHITE)
        board[rookSquare] = rook

        val moves = rookMoveGenerator.generatePseudoLegalMoves(rook, rookSquare, board, gameState)
        assertEquals(14, moves.size)

        for (rank in 1..3) assertMoveExists(moves, rookSquare, Square("d$rank"))
        for (rank in 5..8) assertMoveExists(moves, rookSquare, Square("d$rank"))
        for (file in listOf('a', 'b', 'c', 'e', 'f', 'g', 'h')) assertMoveExists(moves, rookSquare, Square("${file}4"))
    }

    @Test
    fun `rook cannot move past its own pieces`() {
        val rookSquare = Square("d4")
        val rook = Piece(PieceType.ROOK, PieceColor.WHITE)
        board[rookSquare] = rook
        board[Square("d5")] = Piece(PieceType.PAWN, PieceColor.WHITE)
        board[Square("e4")] = Piece(PieceType.PAWN, PieceColor.WHITE)

        val moves = rookMoveGenerator.generatePseudoLegalMoves(rook, rookSquare, board, gameState)
        assertEquals(6, moves.size)

        for (rank in 1..3) assertMoveExists(moves, rookSquare, Square("d$rank"))
        for (file in listOf('a', 'b', 'c')) assertMoveExists(moves, rookSquare, Square("${file}4"))
        assertMoveDoesNotExist(moves, rookSquare, Square("d5"))
        assertMoveDoesNotExist(moves, rookSquare, Square("e4"))
        for (rank in 6..8) assertMoveDoesNotExist(moves, rookSquare, Square("d$rank"))
        for (file in listOf('f', 'g', 'h')) assertMoveDoesNotExist(moves, rookSquare, Square("${file}4"))
    }

    @Test
    fun `rook can capture opponent pieces but cannot move past them`() {
        val rookSquare = Square("d4")
        val rook = Piece(PieceType.ROOK, PieceColor.WHITE)
        board[rookSquare] = rook
        board[Square("d5")] = Piece(PieceType.PAWN, PieceColor.BLACK)
        board[Square("e4")] = Piece(PieceType.PAWN, PieceColor.BLACK)

        val moves = rookMoveGenerator.generatePseudoLegalMoves(rook, rookSquare, board, gameState)
        assertEquals(8, moves.size)

        for (rank in 1..3) assertMoveExists(moves, rookSquare, Square("d$rank"))
        for (file in listOf('a', 'b', 'c')) assertMoveExists(moves, rookSquare, Square("${file}4"))
        assertMoveExists(moves, rookSquare, Square("d5"), isCapture = true)
        assertMoveExists(moves, rookSquare, Square("e4"), isCapture = true)
        for (rank in 6..8) assertMoveDoesNotExist(moves, rookSquare, Square("d$rank"))
        for (file in listOf('f', 'g', 'h')) assertMoveDoesNotExist(moves, rookSquare, Square("${file}4"))
    }

    @Test
    fun `rook on edge of board can only move in valid directions`() {
        val rookSquare = Square("a1")
        val rook = Piece(PieceType.ROOK, PieceColor.WHITE)
        board[rookSquare] = rook

        val moves = rookMoveGenerator.generatePseudoLegalMoves(rook, rookSquare, board, gameState)
        assertEquals(14, moves.size)

        for (rank in 2..8) assertMoveExists(moves, rookSquare, Square("a$rank"))
        for (file in listOf('b', 'c', 'd', 'e', 'f', 'g', 'h')) assertMoveExists(moves, rookSquare, Square("${file}1"))
    }

    @Test
    fun `rook surrounded by pieces cannot move`() {
        val rookSquare = Square("d4")
        val rook = Piece(PieceType.ROOK, PieceColor.WHITE)
        board[rookSquare] = rook
        listOf("d5", "d3", "c4", "e4").forEach {
            board[Square(it)] = Piece(PieceType.PAWN, PieceColor.WHITE)
        }

        val moves = rookMoveGenerator.generatePseudoLegalMoves(rook, rookSquare, board, gameState)
        assertEquals(0, moves.size)
        listOf("d5", "d3", "c4", "e4").forEach {
            assertMoveDoesNotExist(moves, rookSquare, Square(it))
        }
    }

    @Test
    fun `black rook generates correct moves on empty board`() {
        val rookSquare = Square("e5")
        val rook = Piece(PieceType.ROOK, PieceColor.BLACK)
        board[rookSquare] = rook

        val moves = rookMoveGenerator.generatePseudoLegalMoves(rook, rookSquare, board, gameState)
        assertEquals(14, moves.size)
        for (rank in 1..4) assertMoveExists(moves, rookSquare, Square("e$rank"))
        for (rank in 6..8) assertMoveExists(moves, rookSquare, Square("e$rank"))
        for (file in listOf('a', 'b', 'c', 'd', 'f', 'g', 'h')) assertMoveExists(moves, rookSquare, Square("${file}5"))
    }

    @Test
    fun `rook blocked by friendly and enemy piece in same direction`() {
        val rookSquare = Square("d4")
        val rook = Piece(PieceType.ROOK, PieceColor.WHITE)
        board[rookSquare] = rook
        board[Square("d5")] = Piece(PieceType.PAWN, PieceColor.WHITE)
        board[Square("d6")] = Piece(PieceType.PAWN, PieceColor.BLACK)

        val moves = rookMoveGenerator.generatePseudoLegalMoves(rook, rookSquare, board, gameState)
        assertMoveDoesNotExist(moves, rookSquare, Square("d5"))
        assertMoveDoesNotExist(moves, rookSquare, Square("d6"))
        for (rank in 1..3) assertMoveExists(moves, rookSquare, Square("d$rank"))
    }

    @Test
    fun `rook can capture on both ends of file and rank`() {
        val rookSquare = Square("d4")
        val rook = Piece(PieceType.ROOK, PieceColor.WHITE)
        board[rookSquare] = rook
        board[Square("d8")] = Piece(PieceType.BISHOP, PieceColor.BLACK)
        board[Square("a4")] = Piece(PieceType.KNIGHT, PieceColor.BLACK)

        val moves = rookMoveGenerator.generatePseudoLegalMoves(rook, rookSquare, board, gameState)
        assertMoveExists(moves, rookSquare, Square("d8"), isCapture = true)
        assertMoveExists(moves, rookSquare, Square("a4"), isCapture = true)
    }
}