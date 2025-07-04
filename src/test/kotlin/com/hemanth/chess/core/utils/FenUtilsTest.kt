package com.hemanth.chess.core.utils

import com.hemanth.chess.core.ChessGame
import com.hemanth.chess.core.board.Square
import com.hemanth.chess.core.exception.InvalidFenException
import com.hemanth.chess.core.piece.PieceColor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File

class FenUtilsTest {

    @Test
    fun `parseFen should correctly parse default start position`() {
        val (board, state) = FenUtils.parseFen(FenUtils.DEFAULT_FEN)
        assertEquals(PieceColor.WHITE, state.currentPlayer, "Active color should be white")
        assertTrue(
            state.castlingRights[PieceColor.WHITE]?.kingside == true,
            "White kingside castling should be available"
        )
        assertTrue(
            state.castlingRights[PieceColor.BLACK]?.queenside == true,
            "Black queenside castling should be available"
        )
        assertNull(state.enPassantTargetSquare, "No en passant square at start")
        assertEquals(0, state.halfMoveClock, "Halfmove clock should be 0")
        assertEquals(1, state.fullMoveNumber, "Fullmove number should be 1")
        val newFen = FenUtils.getFenFromBoardAndState(board, state)
        assertEquals(FenUtils.DEFAULT_FEN, newFen, "FEN round-trip failed for default position")
    }

    @Test
    fun `parseFen should correctly handle mid-game FEN with en passant and castling rights`() {
        val fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR b KQkq c6 0 2"
        val (board, state) = FenUtils.parseFen(fen)

        assertEquals(PieceColor.BLACK, state.currentPlayer, "Active color should be black")
        assertEquals(Square("c6"), state.enPassantTargetSquare, "En passant square should be c6")
        assertEquals(0, state.halfMoveClock, "Halfmove clock should be 0")
        assertEquals(2, state.fullMoveNumber, "Fullmove number should be 2")
        assertTrue(
            state.castlingRights[PieceColor.WHITE]?.kingside == true,
            "White kingside castling should be available"
        )
        assertTrue(
            state.castlingRights[PieceColor.BLACK]?.queenside == true,
            "Black queenside castling should be available"
        )
        val newFen = FenUtils.getFenFromBoardAndState(board, state)
        assertEquals(fen, newFen, "FEN round-trip failed for mid-game position")
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq",
            "",
            "8/8/8/8/8/8/8/8/8 w - - 0 1",
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0"
        ]
    )
    fun `parseFen should throw on invalid FENs`(badFen: String) {
        val exception = assertThrows<InvalidFenException> {
            FenUtils.parseFen(badFen)
        }
        assertTrue(exception.message!!.contains("Invalid FEN"), "Exception message should mention invalid FEN")
    }

    @Test
    fun `parseFen should throw on invalid FEN with illegal piece char`() {
        val badFen = "rnbqkbnx/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
        val exception = assertThrows<InvalidFenException> {
            FenUtils.parseFen(badFen)
        }
        assertTrue(
            exception.message!!.contains("Invalid FEN piece char"),
            "Exception message should mention invalid piece char"
        )
    }

    @Test
    fun `parseFen should throw on invalid en passant square`() {
        val badFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq z9 0 1"
        val exception = assertThrows<InvalidFenException> {
            FenUtils.parseFen(badFen)
        }
        assertTrue(
            exception.message!!.contains("Invalid en passant square"),
            "Exception message should mention invalid en passant square"
        )
    }

    @Test
    fun `getFenFromBoardAndState should serialize castling rights correctly`() {
        val (board, gameState) = FenUtils.parseFen(FenUtils.DEFAULT_FEN)
        val fen = FenUtils.getFenFromBoardAndState(board, gameState)
        assertTrue(fen.contains("KQkq"), "FEN should contain all castling rights")
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
            "r1bqkbnr/pppp1ppp/2n5/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 2 3",
            "rnbqk2r/ppppbppp/5n2/4p3/1P2P3/P1N2N2/2PP1PPP/R1BQKB1R w KQkq - 0 7",
            "rnbqkbnr/pppppppp/8/8/3P4/8/PPP1PPPP/RNBQKBNR b KQkq d3 0 1"
        ]
    )
    fun `FEN round-trip should preserve original FEN`(fen: String) {
        val (board, state) = FenUtils.parseFen(fen)
        val generatedFen = FenUtils.getFenFromBoardAndState(board, state)
        assertEquals(fen, generatedFen, "FEN round-trip mismatch for: $fen")
    }

    @Test
    fun testInvalidFenMissingKing() {
        assertThrows<InvalidFenException> {
            FenUtils.parseFen("8/8/8/8/8/8/8/8 w - - 0 1")
        }
    }

    @Test
    fun `convert PGN games to final FEN positions`() {
        val filePath = "src/test/resources/lichess_db_standard_rated_2013-01.pgn"
        val pgnText = File(filePath).readText()

        val games = PgnUtils.parsePgnGames(pgnText)

        assertTrue(games.size == 5297)

        games.take(50).forEachIndexed { index, pgnGame ->
            val game = ChessGame()
            val result = runCatching {
                pgnGame.moves.forEach { moveSan ->
                    game.makeSanMove(moveSan)
                }
            }
            assertTrue(result.isSuccess, "Game ${index + 1} failed to apply moves")
            assertNotNull(game.getFen())
        }
    }


}