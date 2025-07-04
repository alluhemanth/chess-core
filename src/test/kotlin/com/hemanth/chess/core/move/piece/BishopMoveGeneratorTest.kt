package com.hemanth.chess.core.move.piece

import com.hemanth.chess.core.board.Board
import com.hemanth.chess.core.board.Square
import com.hemanth.chess.core.game.GameState
import com.hemanth.chess.core.move.createDefaultGameState
import com.hemanth.chess.core.piece.Piece
import com.hemanth.chess.core.piece.PieceColor
import com.hemanth.chess.core.piece.PieceType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.Test
import kotlin.test.assertEquals

class BishopMoveGeneratorTest {

    private lateinit var bishopMoveGenerator: BishopMoveGenerator
    private lateinit var gameState: GameState
    private lateinit var board: Board

    @BeforeEach
    fun setUp() {
        bishopMoveGenerator = BishopMoveGenerator()
        gameState = createDefaultGameState()
        board = Board()
    }

    private fun placeBishop(square: String, color: PieceColor = PieceColor.WHITE) {
        board[Square(square)] = Piece(PieceType.BISHOP, color)
    }

    @Test
    fun `bishop can move diagonally on an empty board`() {
        placeBishop("d4")
        val moves = bishopMoveGenerator.generatePseudoLegalMoves(
            board[Square("d4")]!!, Square("d4"), board, gameState
        )
        val expectedTargets = setOf(
            "e5", "f6", "g7", "h8", "e3", "f2", "g1",
            "c3", "b2", "a1", "c5", "b6", "a7"
        ).map { Square(it) }.toSet()
        assertEquals(13, moves.size)
        assertEquals(expectedTargets, moves.map { it.to }.toSet())
    }

    @ParameterizedTest
    @CsvSource(
        "a1, b2 c3 d4 e5 f6 g7 h8",
        "a4, b5 c6 d7 e8 b3 c2 d1"
    )
    fun `bishop on edge or corner has correct moves`(start: String, targets: String) {
        placeBishop(start)
        val moves = bishopMoveGenerator.generatePseudoLegalMoves(
            board[Square(start)]!!, Square(start), board, gameState
        )
        val expected = targets.split(" ").map { Square(it) }.toSet()
        assertEquals(expected.size, moves.size)
        assertEquals(expected, moves.map { it.to }.toSet())
    }

    @Test
    fun `bishop cannot move past its own pieces`() {
        placeBishop("d4")
        board[Square("e5")] = Piece(PieceType.PAWN, PieceColor.WHITE)
        board[Square("c3")] = Piece(PieceType.PAWN, PieceColor.WHITE)
        val moves = bishopMoveGenerator.generatePseudoLegalMoves(
            board[Square("d4")]!!, Square("d4"), board, gameState
        )
        val expected = setOf("e3", "f2", "g1", "c5", "b6", "a7").map { Square(it) }.toSet()
        assertEquals(expected.size, moves.size)
        assertEquals(expected, moves.map { it.to }.toSet())
    }

    @Test
    fun `bishop can capture opponent pieces but not move past`() {
        placeBishop("d4")
        board[Square("f6")] = Piece(PieceType.PAWN, PieceColor.BLACK)
        board[Square("b2")] = Piece(PieceType.PAWN, PieceColor.BLACK)
        val moves = bishopMoveGenerator.generatePseudoLegalMoves(
            board[Square("d4")]!!, Square("d4"), board, gameState
        )
        val expected = setOf(
            "e5", "f6", "e3", "f2", "g1", "c3", "c5", "b6", "a7", "b2"
        ).map { Square(it) }.toSet()
        assertEquals(expected.size, moves.size)
        assertEquals(expected, moves.map { it.to }.toSet())
    }

    @Test
    fun `bishop surrounded by pieces cannot move`() {
        placeBishop("d4")
        board[Square("c3")] = Piece(PieceType.PAWN, PieceColor.WHITE)
        board[Square("c5")] = Piece(PieceType.PAWN, PieceColor.WHITE)
        board[Square("e3")] = Piece(PieceType.PAWN, PieceColor.WHITE)
        board[Square("e5")] = Piece(PieceType.PAWN, PieceColor.WHITE)
        val moves = bishopMoveGenerator.generatePseudoLegalMoves(
            board[Square("d4")]!!, Square("d4"), board, gameState
        )
        assertEquals(0, moves.size)
    }

    @Test
    fun `black bishop can capture white pieces`() {
        placeBishop("d4", PieceColor.BLACK)
        board[Square("f6")] = Piece(PieceType.PAWN, PieceColor.WHITE)
        board[Square("b2")] = Piece(PieceType.PAWN, PieceColor.WHITE)
        val moves = bishopMoveGenerator.generatePseudoLegalMoves(
            board[Square("d4")]!!, Square("d4"), board, gameState
        )
        val expected = setOf(
            "e5", "f6", "e3", "c3", "c5", "b2", "b6", "a7", "f2", "g1"
        ).map { Square(it) }.toSet()

        assertEquals(10, moves.size)
        assertEquals(expected, moves.map { it.to }.toSet())
    }

    @Test
    fun `bishop on dark square can only access dark squares`() {
        placeBishop("c1")
        val moves = bishopMoveGenerator.generatePseudoLegalMoves(
            board[Square("c1")]!!, Square("c1"), board, gameState
        )
        for (move in moves) {
            val targetSquare = move.to
            val isDarkSquare = targetSquare.isDarkSquare()
            assert(isDarkSquare) { "Bishop on dark square should only move to dark squares: $targetSquare" }
        }
    }

    @Test
    fun `bishop on light square can only access light squares`() {
        placeBishop("f1")
        val moves = bishopMoveGenerator.generatePseudoLegalMoves(
            board[Square("f1")]!!, Square("f1"), board, gameState
        )
        for (move in moves) {
            val targetSquare = move.to
            val isLightSquare = targetSquare.isLightSquare()
            assert(isLightSquare) { "Bishop on light square should only move to light squares: $targetSquare" }
        }
    }
}