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

class QueenMoveGeneratorTest {

    private lateinit var board: Board
    private lateinit var gameState: GameState
    private lateinit var queenMoveGenerator: QueenMoveGenerator

    @BeforeEach
    fun setup() {
        board = Board()
        gameState = createDefaultGameState()
        queenMoveGenerator = QueenMoveGenerator()
    }

    @Test
    fun `queen generates correct number of moves in empty board center`() {
        val queenSquare = Square("d4")
        val queen = Piece(PieceType.QUEEN, PieceColor.WHITE)
        board[queenSquare] = queen

        val moves = queenMoveGenerator.generatePseudoLegalMoves(queen, queenSquare, board, gameState)
        assertEquals(27, moves.size)
    }

    @Test
    fun `queen does not move through friendly pieces in all directions`() {
        val queenSquare = Square("d4")
        val queen = Piece(PieceType.QUEEN, PieceColor.WHITE)
        board[queenSquare] = queen

        // Blockers in all 8 directions
        val blockers = listOf("d5", "e5", "e4", "e3", "d3", "c3", "c4", "c5")
        blockers.forEach { board[Square(it)] = Piece(PieceType.PAWN, PieceColor.WHITE) }

        val moves = queenMoveGenerator.generatePseudoLegalMoves(queen, queenSquare, board, gameState)
        blockers.forEach { assertMoveDoesNotExist(moves, queenSquare, Square(it)) }
    }

    @Test
    fun `queen can capture enemy pieces but not move beyond in all directions`() {
        val queenSquare = Square("d4")
        val queen = Piece(PieceType.QUEEN, PieceColor.WHITE)
        board[queenSquare] = queen

        val enemySquares = listOf("d6", "f4", "b2", "g7")
        enemySquares.forEach { board[Square(it)] = Piece(PieceType.KNIGHT, PieceColor.BLACK) }

        val moves = queenMoveGenerator.generatePseudoLegalMoves(queen, queenSquare, board, gameState)
        enemySquares.forEach {
            assertMoveExists(moves, queenSquare, Square(it), isCapture = true)
        }
        // Should not include moves beyond captured pieces
        assertMoveDoesNotExist(moves, queenSquare, Square("d7"))
        assertMoveDoesNotExist(moves, queenSquare, Square("h4"))
        assertMoveDoesNotExist(moves, queenSquare, Square("a1"))
        assertMoveDoesNotExist(moves, queenSquare, Square("h8"))
    }

    @Test
    fun `queen edge of board generates correct moves`() {
        val queenSquare = Square("h1")
        val queen = Piece(PieceType.QUEEN, PieceColor.WHITE)
        board[queenSquare] = queen

        val moves = queenMoveGenerator.generatePseudoLegalMoves(queen, queenSquare, board, gameState)
        assertEquals(21, moves.size)
    }

    @Test
    fun `queen cannot move to squares occupied by friendly pieces`() {
        val queenSquare = Square("e4")
        val queen = Piece(PieceType.QUEEN, PieceColor.WHITE)
        board[queenSquare] = queen

        board[Square("e6")] = Piece(PieceType.PAWN, PieceColor.WHITE)
        board[Square("g6")] = Piece(PieceType.PAWN, PieceColor.WHITE)

        val moves = queenMoveGenerator.generatePseudoLegalMoves(queen, queenSquare, board, gameState)
        assertMoveDoesNotExist(moves, queenSquare, Square("e6"))
        assertMoveDoesNotExist(moves, queenSquare, Square("g6"))
    }

    @Test
    fun `black queen generates correct moves`() {
        val queenSquare = Square("d4")
        val queen = Piece(PieceType.QUEEN, PieceColor.BLACK)
        board[queenSquare] = queen

        val moves = queenMoveGenerator.generatePseudoLegalMoves(queen, queenSquare, board, gameState)
        assertEquals(27, moves.size)
    }

    @Test
    fun `queen surrounded by friendly pieces has no moves`() {
        val queenSquare = Square("d4")
        val queen = Piece(PieceType.QUEEN, PieceColor.WHITE)
        board[queenSquare] = queen

        // Surround with friendly pieces
        listOf("d5", "e5", "e4", "e3", "d3", "c3", "c4", "c5").forEach {
            board[Square(it)] = Piece(PieceType.PAWN, PieceColor.WHITE)
        }

        val moves = queenMoveGenerator.generatePseudoLegalMoves(queen, queenSquare, board, gameState)
        assertEquals(0, moves.size)
    }
}