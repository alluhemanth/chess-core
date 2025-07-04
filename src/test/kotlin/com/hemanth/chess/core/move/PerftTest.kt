package com.hemanth.chess.core.move

import com.hemanth.chess.core.board.Board
import com.hemanth.chess.core.game.GameState
import com.hemanth.chess.core.game.GameUtils
import com.hemanth.chess.core.utils.FenUtils
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertEquals

class PerftTest {
    private val legalMoveGenerator = LegalMoveGenerator()

    companion object {
        private const val FEN_DEFAULT = FenUtils.DEFAULT_FEN
        private const val FEN_KIWIPETE = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1"

        private val EXPECTED_DETAILED_RESULTS = mapOf(
            "default_1" to PerftDetailedResults(
                nodes = 20L,
                captures = 0L,
                enPassant = 0L,
                castles = 0L,
                promotions = 0L,
                checks = 0L,
                checkmates = 0L
            ),
            "default_2" to PerftDetailedResults(
                nodes = 400L,
                captures = 0L,
                enPassant = 0L,
                castles = 0L,
                promotions = 0L,
                checks = 0L,
                checkmates = 0L
            ),
            "default_3" to PerftDetailedResults(
                nodes = 8902L,
                captures = 34L,
                enPassant = 0L,
                castles = 0L,
                promotions = 0L,
                checks = 12L,
                checkmates = 0L
            ),
            "default_4" to PerftDetailedResults(
                nodes = 197281L,
                captures = 1576L,
                enPassant = 0L,
                castles = 0L,
                promotions = 0L,
                checks = 469L,
                checkmates = 8L
            ),
            "default_5" to PerftDetailedResults(
                nodes = 4865609L,
                captures = 82719L,
                enPassant = 258L,
                castles = 0L,
                promotions = 0L,
                checks = 27351L,
                checkmates = 347L
            ),

            "kiwipete_1" to PerftDetailedResults(
                nodes = 48L,
                captures = 8L,
                enPassant = 0L,
                castles = 2L,
                promotions = 0L,
                checks = 0L,
                checkmates = 0L
            ),
            "kiwipete_2" to PerftDetailedResults(
                nodes = 2039L,
                captures = 351L,
                enPassant = 1L,
                castles = 91L,
                promotions = 0L,
                checks = 3L,
                checkmates = 0L
            ),
            "kiwipete_3" to PerftDetailedResults(
                nodes = 97862L,
                captures = 17102L,
                enPassant = 45L,
                castles = 3162L,
                promotions = 0L,
                checks = 993L,
                checkmates = 1L
            ),
            "kiwipete_4" to PerftDetailedResults(
                nodes = 4085603L,
                captures = 757163L,
                enPassant = 1929L,
                castles = 128013L,
                promotions = 15172L,
                checks = 25523L,
                checkmates = 43L
            ),
            "kiwipete_5" to PerftDetailedResults(
                nodes = 193690690L,
                captures = 35043416L,
                enPassant = 73365,
                castles = 4993637L,
                promotions = 8392,
                checks = 3309887L,
                checkmates = 30171
            ),

            "kiwipete_6" to PerftDetailedResults(
                nodes = 8031647685,
                captures = 1558445089,
                enPassant = 3577504,
                castles = 184513607,
                promotions = 56627920,
                checks = 92238050,
                checkmates = 360003
            ),


            )
    }

    /**
     * Performs a perft search to the given depth, returning detailed results.
     */
    fun perft(depth: Int, board: Board, gameState: GameState): PerftDetailedResults {
        if (depth == 0) {
            return PerftDetailedResults(nodes = 1L)
        }

        val results = PerftDetailedResults()
        val legalMoves = legalMoveGenerator.getAllLegalMoves(board, gameState)

        if (legalMoves.isEmpty()) {
            return results
        }

        for (move in legalMoves) {
            val (boardAfter, gameStateAfter) = GameUtils.makeMove(board, gameState, move)
            val subResults = perft(depth - 1, boardAfter, gameStateAfter)

            results.nodes += subResults.nodes

            if (depth == 1) {
                if (move.isCapture) results.captures++
                if (move.isEnPassantCapture) results.enPassant++
                if (move.isCastlingKingside || move.isCastlingQueenside) results.castles++
                if (move.promotionPieceType != null) results.promotions++

                val opponentColor = gameState.currentPlayer.opposite()
                if (GameUtils.isKingInCheck(opponentColor, boardAfter)) {
                    results.checks++
                    if (legalMoveGenerator.getAllLegalMoves(boardAfter, gameStateAfter).isEmpty()) {
                        results.checkmates++
                    }
                }
            } else {
                results.captures += subResults.captures
                results.enPassant += subResults.enPassant
                results.castles += subResults.castles
                results.promotions += subResults.promotions
                results.checks += subResults.checks
                results.checkmates += subResults.checkmates
            }
        }

        return results
    }

    fun perftDivide(depth: Int, board: Board, gameState: GameState) {
        if (depth == 0) {
            return
        }

        println("Divide for depth $depth:")
        val legalMoves = legalMoveGenerator.getAllLegalMoves(board, gameState)
        var totalNodes = 0L

        for (move in legalMoves) {
            val (boardAfter, gameStateAfter) = GameUtils.makeMove(board, gameState, move)
            val result = perft(depth - 1, boardAfter, gameStateAfter)
            totalNodes += result.nodes
            println("${move.toUci()}: ${result.nodes}")
        }
        println("Total nodes: $totalNodes")
    }

    /**
     * Runs a perft test and asserts the result.
     */
    private fun runPerftTest(
        fen: String,
        depth: Int,
        expected: PerftDetailedResults?,
        label: String,
        divide: Boolean = false
    ) {
        if (expected == null) {
            println("Skipping $label Depth $depth - No expected data.")
            return
        }
        val (board, gameState) = FenUtils.parseFen(fen)
        if (divide) {
            perftDivide(depth, board, gameState)
        }
        var actualResults: PerftDetailedResults? = null
        val time = measureTimeMillis {
            actualResults = perft(depth, board, gameState)
        }
        println("$label - Depth $depth time: $time ms")
        println("Expected: $expected")
        println("Actual  : $actualResults")
        assertEquals(expected, actualResults, "$label Depth $depth detailed results mismatch")
    }

    // --- Test Cases ---
    @Test
    fun `perft initial position depth 1`() =
        runPerftTest(FEN_DEFAULT, 1, EXPECTED_DETAILED_RESULTS["default_1"], "Initial Pos")

    @Test
    fun `perft initial position depth 2`() =
        runPerftTest(FEN_DEFAULT, 2, EXPECTED_DETAILED_RESULTS["default_2"], "Initial Pos")

    @Test
    fun `perft initial position depth 3`() =
        runPerftTest(FEN_DEFAULT, 3, EXPECTED_DETAILED_RESULTS["default_3"], "Initial Pos")

    @Test
    fun `perft initial position depth 4`() =
        runPerftTest(FEN_DEFAULT, 4, EXPECTED_DETAILED_RESULTS["default_4"], "Initial Pos")

    @Test
    fun `perft initial position depth 5`() =
        runPerftTest(FEN_DEFAULT, 5, EXPECTED_DETAILED_RESULTS["default_5"], "Initial Pos")

    @Test
    fun `perft kiwipete depth 1`() = runPerftTest(FEN_KIWIPETE, 1, EXPECTED_DETAILED_RESULTS["kiwipete_1"], "Kiwipete")

    @Test
    fun `perft kiwipete depth 2`() = runPerftTest(FEN_KIWIPETE, 2, EXPECTED_DETAILED_RESULTS["kiwipete_2"], "Kiwipete")

    @Test
    fun `perft kiwipete depth 3`() = runPerftTest(FEN_KIWIPETE, 3, EXPECTED_DETAILED_RESULTS["kiwipete_3"], "Kiwipete")

    @Test
    fun `perft kiwipete depth 4`() = runPerftTest(FEN_KIWIPETE, 4, EXPECTED_DETAILED_RESULTS["kiwipete_4"], "Kiwipete")
    // @Test fun `perft kiwipete depth 5`() = runPerftTest(FEN_KIWIPETE, 5, EXPECTED_DETAILED_RESULTS["kiwipete_5"], "Kiwipete")
}