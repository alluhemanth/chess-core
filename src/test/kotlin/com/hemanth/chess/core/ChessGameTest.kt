package com.hemanth.chess.core

import com.hemanth.chess.core.game.GameResult
import com.hemanth.chess.core.piece.PieceColor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ChessGameTest {

    @Test
    fun initialGameSetupIsCorrect() {
        val game = ChessGame()
        assertEquals(PieceColor.WHITE, game.getCurrentPlayer())
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", game.getFen())
        assertFalse(game.isGameOver())
    }

    @Test
    fun makeMove_legalMove_updatesGameState() {
        val game = ChessGame()
        val initialFen = game.getFen()
        val legalMoves = game.getLegalMoves()
        val move = legalMoves.first { it.toString() == "e2e4" }

        val moveMade = game.makeMove(move)

        assertTrue(moveMade)
        assertEquals(PieceColor.BLACK, game.getCurrentPlayer())
        assertFalse(game.getFen() == initialFen)
        assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", game.getFen())
    }

    @Test
    fun makeMove_illegalMove_doesNotChangeState() {
        val game = ChessGame()
        val initialFen = game.getFen()
        val illegalMove = com.hemanth.chess.core.move.Move(
            com.hemanth.chess.core.board.Square("e2"),
            com.hemanth.chess.core.board.Square("e5")
        )

        val moveMade = game.makeMove(illegalMove)

        assertFalse(moveMade)
        assertEquals(PieceColor.WHITE, game.getCurrentPlayer())
        assertEquals(initialFen, game.getFen())
    }

    @Test
    fun makeSanMove_validSan_updatesGameState() {
        val game = ChessGame()
        val moveMade = game.makeSanMove("e4")
        assertTrue(moveMade)
        assertEquals(PieceColor.BLACK, game.getCurrentPlayer())
        assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", game.getFen())
    }

    @Test
    fun makeUciMove_validUci_updatesGameState() {
        val game = ChessGame()
        val moveMade = game.makeUciMove("e2e4")
        assertTrue(moveMade)
        assertEquals(PieceColor.BLACK, game.getCurrentPlayer())
        assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", game.getFen())
    }

    @Test
    fun undo_afterOneMove_revertsToInitialState() {
        val game = ChessGame()
        val initialFen = game.getFen()
        game.makeSanMove("e4")

        val undone = game.undo()

        assertTrue(undone)
        assertEquals(initialFen, game.getFen())
        assertEquals(PieceColor.WHITE, game.getCurrentPlayer())
    }

    @Test
    fun undo_withNoMoves_doesNothing() {
        val game = ChessGame()
        val initialFen = game.getFen()

        val undone = game.undo()

        assertFalse(undone)
        assertEquals(initialFen, game.getFen())
    }

    @Test
    fun redo_afterUndo_restoresMove() {
        val game = ChessGame()
        game.makeSanMove("e4")
        val fenAfterMove = game.getFen()
        game.undo()

        val redone = game.redo()

        assertTrue(redone)
        assertEquals(fenAfterMove, game.getFen())
        assertEquals(PieceColor.BLACK, game.getCurrentPlayer())
    }

    @Test
    fun redo_withoutUndo_doesNothing() {
        val game = ChessGame()
        game.makeSanMove("e4")
        val fenAfterMove = game.getFen()

        val redone = game.redo()

        assertFalse(redone)
        assertEquals(fenAfterMove, game.getFen())
    }

    @Test
    fun makeMove_afterUndo_clearsRedoHistory() {
        val game = ChessGame()
        game.makeSanMove("e4")
        game.makeSanMove("e5")
        game.undo() // back to after e4
        game.makeSanMove("e6") // make a different move

        val redone = game.redo() // should not be possible to redo e6

        assertFalse(redone)
        assertEquals("rnbqkbnr/pppp1ppp/4p3/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2", game.getFen())
    }

    @Test
    fun getGameResult_foolsMate_returnsWinForBlack() {
        val game = ChessGame()

        game.makeSanMove("f3")     // White
        game.makeSanMove("e5")     // Black
        game.makeSanMove("g4")     // White
        game.makeSanMove("Qh4")    // Black delivers checkmate

        assertTrue(game.isGameOver(), "Game should be over after Fool's Mate")

        val result = game.getGameResult()
        assertTrue(result is GameResult.Win, "Result should be a Win")
        assertEquals(
            PieceColor.BLACK,
            (result as GameResult.Win).winner,
            "Winner should be Black"
        )
    }


    @Test
    fun getGameResult_stalemate_returnsDraw() {
        val game = ChessGame()
        // Set up a stalemate position with black to move and no legal moves
        game.loadFen("7k/5Q2/6K1/8/8/8/8/8 b - - 0 1")

        assertTrue(game.isGameOver(), "Game should be over due to stalemate")
        val result = game.getGameResult()
        assertEquals(
            GameResult.Draw.Stalemate,
            result,
            "Game result should be stalemate"
        )
    }


    @Test
    fun getGameResult_threefoldRepetition_returnsDraw() {
        val game = ChessGame()
        game.makeSanMove("Nf3") // 1. Nf3
        game.makeSanMove("Nf6") // 1... Nf6
        game.makeSanMove("Ng1") // 2. Ng1
        game.makeSanMove("Ng8") // 2... Ng8
        game.makeSanMove("Nf3") // 3. Nf3
        game.makeSanMove("Nf6") // 3... Nf6
        game.makeSanMove("Ng1") // 4. Ng1
        game.makeSanMove("Ng8") // 4... Ng8 -> position repeats for the 3rd time

        assertTrue(game.isGameOver())
        assertEquals(GameResult.Draw.ThreefoldRepetition, game.getGameResult())
    }

    @Test
    fun getGameResult_fiftyMoveRule_returnsDraw() {
        val game = ChessGame()
        // Halfmove clock is 99, about to reach 100 with next move
        game.loadFen("8/8/8/3pP3/8/1k6/8/K7 w - - 99 1")

        game.makeUciMove("a1b1") // Non-capture, non-pawn move -> halfmove clock should become 100

        assertTrue(game.isGameOver(), "Game should be over by fifty-move rule")
        val result = game.getGameResult()
        assertEquals(
            GameResult.Draw.FiftyMoveRule,
            result,
            "Game result should be fifty-move rule draw"
        )
    }


    @Test
    fun getGameResult_insufficientMaterial_returnsDraw() {
        val game = ChessGame()
        game.loadFen("k7/8/8/8/8/8/8/K7 w - - 0 1") // King vs King
        assertTrue(game.isGameOver())
        assertEquals(GameResult.Draw.InsufficientMaterial, game.getGameResult())
    }

    @Test
    fun loadFen_resetsGameToFenPosition() {
        val game = ChessGame()
        game.makeSanMove("e4")
        val fen = "r1bqkbnr/pp1ppppp/2n5/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3"
        game.loadFen(fen)

        assertEquals(fen, game.getFen())
        assertEquals(PieceColor.WHITE, game.getCurrentPlayer())
        assertTrue(game.getLegalMoves().isNotEmpty())
    }

    @Test
    fun getPgn_returnsCorrectPgnString() {
        val game = ChessGame()
        game.makeSanMove("e4")
        game.makeSanMove("e5")
        game.makeSanMove("Nf3")
        game.makeSanMove("Nc6")

        // PGN move numbers are not part of the output, just the SAN moves
        assertEquals("e4 e5 Nf3 Nc6", game.getPgn())
    }
}