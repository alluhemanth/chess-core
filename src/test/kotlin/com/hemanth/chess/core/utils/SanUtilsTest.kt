package com.hemanth.chess.core.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SanUtilsTest {

    @Test
    fun `test pawn moves`() {
        val (board, gameState) = FenUtils.parseFen(FenUtils.DEFAULT_FEN)
        assertEquals("e2e4", SanUtils.sanToMove("e4", board, gameState).toUci())
    }

    @Test
    fun `test knight moves`() {
        val (board, gameState) = FenUtils.parseFen(FenUtils.DEFAULT_FEN)
        assertEquals("g1f3", SanUtils.sanToMove("Nf3", board, gameState).toUci())
    }

    @Test
    fun `test pawn capture`() {
        val (board, gameState) = FenUtils.parseFen("rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2")
        assertEquals("e4d5", SanUtils.sanToMove("exd5", board, gameState).toUci())
    }

    @Test
    fun `test castling`() {
        val (board, gameState) = FenUtils.parseFen("rnbqk2r/pppp1ppp/5n2/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4")
        assertEquals("e1g1", SanUtils.sanToMove("O-O", board, gameState).toUci())

        val (board2, gameState2) = FenUtils.parseFen("r3k2r/pppq1ppp/2np1n2/2b1p3/2B1P3/2NP1N2/PPP2PPP/R2QK2R b KQkq - 2 7")
        assertEquals("e8c8", SanUtils.sanToMove("O-O-O", board2, gameState2).toUci())
    }

    @Test
    fun `test promotion`() {
        val (board, gameState) = FenUtils.parseFen("8/PPP2PPP/8/8/8/8/1k6/R3K3 w - - 0 1")
        assertEquals("b7b8q", SanUtils.sanToMove("b8=Q", board, gameState).toUci())
    }

    @Test
    fun `test file disambiguation`() {
        val (board, gameState) = FenUtils.parseFen("8/8/8/3n4/8/2N1N3/8/k1K5 w - - 0 1")
        assertEquals("c3d5", SanUtils.sanToMove("Ncd5", board, gameState).toUci())
    }

    @Test
    fun `test rank disambiguation`() {
        val (board, gameState) = FenUtils.parseFen("8/8/8/8/8/8/R6R/k1K5 w - - 0 1")
        assertEquals("a2a1", SanUtils.sanToMove("R2a1", board, gameState).toUci())
    }

    @Test
    fun `test square disambiguation`() {
        val (board, gameState) = FenUtils.parseFen("8/8/8/8/8/8/R6R/k1K5 w - - 0 1")
        assertEquals("h2h1", SanUtils.sanToMove("Rh1", board, gameState).toUci())
    }

    @Test
    fun `test move to san - pawn move`() {
        val (board, gameState) = FenUtils.parseFen(FenUtils.DEFAULT_FEN)
        val move = SanUtils.sanToMove("e4", board, gameState)
        assertEquals("e4", SanUtils.moveToSan(move, board, gameState))
    }

    @Test
    fun `test move to san - knight move`() {
        val (board, gameState) = FenUtils.parseFen(FenUtils.DEFAULT_FEN)
        val move = SanUtils.sanToMove("Nf3", board, gameState)
        assertEquals("Nf3", SanUtils.moveToSan(move, board, gameState))
    }

    @Test
    fun `test move to san - pawn capture`() {
        val (board, gameState) = FenUtils.parseFen("rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2")
        val move = SanUtils.sanToMove("exd5", board, gameState)
        assertEquals("exd5", SanUtils.moveToSan(move, board, gameState))
    }

    @Test
    fun `test move to san - castling`() {
        val (board, gameState) = FenUtils.parseFen("rnbqk2r/pppp1ppp/5n2/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4")
        val move = SanUtils.sanToMove("O-O", board, gameState)
        assertEquals("O-O", SanUtils.moveToSan(move, board, gameState))
    }

    @Test
    fun `test move to san - promotion`() {
        val (board, gameState) = FenUtils.parseFen("8/PPP2PPP/8/8/8/8/1k6/R3K3 w - - 0 1")
        val move = SanUtils.sanToMove("b8=Q+", board, gameState)
        assertEquals("b8=Q+", SanUtils.moveToSan(move, board, gameState))
    }

    @Test
    fun `test move to san - check`() {
        val (board, gameState) = FenUtils.parseFen("rnbqkbnr/pppp1ppp/8/4p3/8/5N2/PPPPPPPP/RNBQKB1R w KQkq - 2 3")
        val move = SanUtils.sanToMove("Nxe5", board, gameState)
        assertEquals("Nxe5", SanUtils.moveToSan(move, board, gameState))
    }

    @Test
    fun `test move to san - checkmate`() {
        val (board, gameState) = FenUtils.parseFen("rnb1kbnr/pppp1ppp/8/4p1q1/6P1/5P2/PPPPP2P/RNBQKBNR b KQkq - 1 3")
        val move = SanUtils.sanToMove("Qh4#", board, gameState)
        assertEquals("Qh4#", SanUtils.moveToSan(move, board, gameState))
    }
}