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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.Test
import kotlin.test.assertEquals

class KnightMoveGeneratorTest {

    private lateinit var knightMoveGenerator: KnightMoveGenerator
    private lateinit var gameState: GameState
    private lateinit var board: Board

    @BeforeEach
    fun setUp() {
        knightMoveGenerator = KnightMoveGenerator()
        gameState = createDefaultGameState()
        board = Board()
    }

    private fun placeKnight(square: String, color: PieceColor = PieceColor.WHITE) {
        board[Square(square)] = Piece(PieceType.KNIGHT, color)
    }

    private fun placePawn(square: String, color: PieceColor) {
        board[Square(square)] = Piece(PieceType.PAWN, color)
    }

    @Test
    fun `knight moves in L-shape on an empty board`() {
        val knightSquare = Square("d4")
        placeKnight("d4")
        val moves = knightMoveGenerator.generatePseudoLegalMoves(board[knightSquare]!!, knightSquare, board, gameState)
        val expectedTargets = setOf("b3", "b5", "c2", "c6", "e2", "e6", "f3", "f5").map { Square(it) }.toSet()
        assertEquals(8, moves.size)
        assertEquals(expectedTargets, moves.map { it.to }.toSet())
    }

    @ParameterizedTest
    @CsvSource(
        "d4,b3,f5", // white knight, blocked by white pawns
        "d4,c2,e6"  // white knight, blocked by white pawns
    )
    fun `knight cannot move to squares occupied by friendly pieces`(knight: String, block1: String, block2: String) {
        placeKnight(knight)
        placePawn(block1, PieceColor.WHITE)
        placePawn(block2, PieceColor.WHITE)
        val moves =
            knightMoveGenerator.generatePseudoLegalMoves(board[Square(knight)]!!, Square(knight), board, gameState)
        assertMoveDoesNotExist(moves, Square(knight), Square(block1))
        assertMoveDoesNotExist(moves, Square(knight), Square(block2))
    }

    @Test
    fun `knight can capture opponent pieces`() {
        val knightSquare = Square("d4")
        placeKnight("d4")
        placePawn("b3", PieceColor.BLACK)
        placePawn("f5", PieceColor.BLACK)
        val moves = knightMoveGenerator.generatePseudoLegalMoves(board[knightSquare]!!, knightSquare, board, gameState)
        assertMoveExists(moves, knightSquare, Square("b3"), isCapture = true)
        assertMoveExists(moves, knightSquare, Square("f5"), isCapture = true)
    }

    @ParameterizedTest
    @CsvSource(
        "a1,2,b3,c2",    // corner
        "a4,4,b2,b6",    // side, first two moves
        "a4,4,c3,c5",    // side, next two moves
        "h8,2,g6,f7"     // opposite corner
    )
    fun `knight on edge or corner has limited moves`(
        knight: String,
        expectedCount: String,
        move1: String,
        move2: String
    ) {
        placeKnight(knight)
        val moves =
            knightMoveGenerator.generatePseudoLegalMoves(board[Square(knight)]!!, Square(knight), board, gameState)
        assertEquals(expectedCount.toInt(), moves.size)
        assertMoveExists(moves, Square(knight), Square(move1))
        assertMoveExists(moves, Square(knight), Square(move2))
    }

    @Test
    fun `knight can jump over other pieces`() {
        val knightSquare = Square("d4")
        placeKnight("d4")
        // Place blocking pieces
        placePawn("d3", PieceColor.WHITE)
        placePawn("d5", PieceColor.BLACK)
        placePawn("c4", PieceColor.WHITE)
        placePawn("e4", PieceColor.BLACK)
        val moves = knightMoveGenerator.generatePseudoLegalMoves(board[knightSquare]!!, knightSquare, board, gameState)
        assertEquals(8, moves.size)
    }

    @Test
    fun `black knight can capture white pieces`() {
        val knightSquare = Square("d4")
        placeKnight("d4", PieceColor.BLACK)
        placePawn("b3", PieceColor.WHITE)
        placePawn("f5", PieceColor.WHITE)
        val moves = knightMoveGenerator.generatePseudoLegalMoves(board[knightSquare]!!, knightSquare, board, gameState)
        assertMoveExists(moves, knightSquare, Square("b3"), isCapture = true)
        assertMoveExists(moves, knightSquare, Square("f5"), isCapture = true)
    }
}