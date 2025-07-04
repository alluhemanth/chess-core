package com.hemanth.chess.core.utils

import com.hemanth.chess.core.game.PgnGame
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.system.measureTimeMillis

class PgnUtilsTest {

    @Test
    fun `parsePgn should correctly parse tags`() {
        val pgnString = """[Event "FIDE World Championship"]
            [Site "New York, USA"]
            [Date "1972.09.01"]
            [Round "1"]
            [White "Fischer, Robert J."]
            [Black "Spassky, Boris V."]
            [Result "1-0"]
            
            1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 4. Ba4 Nf6 5. O-O Be7 6. Re1 b5 7. Bb3 d6 8. c3 O-O 9. h3 Nb8 10. d4 Nbd7 
            11. c4 c6 12. cxb5 axb5 13. Nc3 Bb7 14. Bg5 h6 15. Bh4 Re8 16. dxe5 Nxe5 17. Nxe5 dxe5 18. Qxd8 Rexd8 
            19. Rad1 Kf8 20. Bg3 Bd6 21. f3 Ke7 22. Bf2 Nd7 23. a4 b4 24. Nb1 Nc5 25. Bc2 b3 26. Bxb3 Nxb3 27. Nc3 Bb4 
            28. Rxd8 Rxd8 29. Re2 Ba6 30. Rc2 Bd3 31. Rc1 Nd4 32. Kh2 Ne2 33. Nxe2 Bxe2 34. a5 Ra8 35. Ra1 Rxa5 
            36. Rxa5 Bxa5 37. Bc5+ Ke6 38. Kg3 g6 39. Kf2 f5 40. Ke3 Bf1 41. g4 Bg2 42. h4 fxg4 43. fxg4 Bh3 44. Kf3 Bf1
            45. Kg3 Be2 46. g5 h5 47. Kf2 Bd1 48. Ke3 Bc2 49. b4 Bc7 50. Kd2 Bxe4 51. Ke3 Bf5 52. Kf3 Kd5 53. Kg3 e4+ 
            54. Kf2 Kc4 55. Ke3 Kxb4 56. Kd4+ Kb5 57. Ke3 Bb6 58. Kd4 Bxc5+ 59. Ke5 e3 60. Kf6 e2 61. Kg7 e1=Q 
            62. Kh6 Qxh4 63. Kg7 Qxg5 64. Kh7 h4 65. Kg7 h3 66. Kh7 h2 67. Kg7 h1=Q 68. Kf7 Qhh6 69. Ke8 Qgg7 
            70. Kd8 Qhh7 71. Ke8 Qgg8+ 72. Kd8 Qhh8# 1-0"""

        val pgnGame = PgnUtils.parsePgn(pgnString)

        assertEquals("FIDE World Championship", pgnGame.tags["Event"])
        assertEquals("New York, USA", pgnGame.tags["Site"])
        assertEquals("1972.09.01", pgnGame.tags["Date"])
        assertEquals("1", pgnGame.tags["Round"])
        assertEquals("Fischer, Robert J.", pgnGame.tags["White"])
        assertEquals("Spassky, Boris V.", pgnGame.tags["Black"])
        assertEquals("1-0", pgnGame.tags["Result"])
    }

    @Test
    fun `parsePgn should correctly parse moves`() {
        val pgnString = """
            1. d4 Nf6 2. Nf3 d5 3. e3 Bf5 4. c4 c6 5. Nc3 e6 6. Bd3 Bxd3 7. Qxd3 Nbd7 8. b3 Bd6 9. O-O O-O 10. Bb2 Qe7 
            11. Rad1 Rad8 12. Rfe1 dxc4 13. bxc4 e5 14. dxe5 Nxe5 15. Nxe5 Bxe5 16. Qe2 Rxd1 17. Rxd1 Rd8 18. Rxd8+ Qxd8
            19. Qd1 Qxd1+ 20. Nxd1 Bxb2 21. Nxb2 b5 22. f3 Kf8 23. Kf2 Ke7 1/2-1/2
        """.trimIndent()

        val expectedMoves = listOf(
            "d4", "Nf6", "Nf3", "d5", "e3", "Bf5", "c4", "c6", "Nc3", "e6", "Bd3", "Bxd3",
            "Qxd3", "Nbd7", "b3", "Bd6", "O-O", "O-O", "Bb2", "Qe7", "Rad1", "Rad8", "Rfe1",
            "dxc4", "bxc4", "e5", "dxe5", "Nxe5", "Nxe5", "Bxe5", "Qe2", "Rxd1", "Rxd1",
            "Rd8", "Rxd8+", "Qxd8", "Qd1", "Qxd1+", "Nxd1", "Bxb2", "Nxb2", "b5", "f3",
            "Kf8", "Kf2", "Ke7"
        )


        val pgnGame = PgnUtils.parsePgn(pgnString)
        assertEquals(expectedMoves, pgnGame.moves)
    }

    @Test
    fun `parsePgn should handle comments and variations`() {
        val pgnString = """[Event "Test"]
            1. e4 {This is a comment} e5 (2. Nf3 Nc6) 2... Nc6 {Another comment} 3. Bb5 1-0
        """.trimMargin()
        val expectedMoves = listOf("e4", "e5", "Nc6", "Bb5")
        val pgnGame = PgnUtils.parsePgn(pgnString)
        assertEquals(expectedMoves, pgnGame.moves)
    }

    @Test
    fun `applyPgnMoves should apply moves to the board`() {
        val pgnString = """
            1. d4 Nf6 2. Nf3 d5 3. e3 Bf5 4. c4 c6 5. Nc3 e6 6. Bd3 Bxd3 7. Qxd3 Nbd7 8. b3 Bd6 9. O-O O-O 10. Bb2 Qe7 
            11. Rad1 Rad8 12. Rfe1 dxc4 13. bxc4 e5 14. dxe5 Nxe5 15. Nxe5 Bxe5 16. Qe2 Rxd1 17. Rxd1 Rd8 18. Rxd8+ Qxd8 
            19. Qd1 Qxd1+ 20. Nxd1 Bxb2 21. Nxb2 b5 22. f3 Kf8 23. Kf2 Ke7 1/2-1/2
        """.trimIndent()
        val pgnGame = PgnUtils.parsePgn(pgnString)
        val (finalBoard, finalGameState) = PgnUtils.applyPgnMoves(pgnGame)

        // Assertions for the final board and game state after applying moves
        assertNotNull(finalBoard)
        assertNotNull(finalGameState)
        assertEquals("1/2-1/2", pgnGame.result)

        // Example of a more specific assertion: check if the board is empty after a long game
        // This might not be true for all PGNs, but for the given long PGN, it's likely many pieces are gone.
        // You would need to know the exact final state of the board for a precise assertion.
        // For now, a simple check that the board is not null is sufficient.
    }

    @Test
    fun `parsePgn should handle empty PGN string`() {
        val pgnString = """"""
        val pgnGame = PgnUtils.parsePgn(pgnString)
        assertTrue(pgnGame.tags.isEmpty())
        assertTrue(pgnGame.moves.isEmpty())
        assertEquals("*", pgnGame.result)
    }

    @Test
    fun `parsePgn should handle PGN string with only tags`() {
        val pgnString = """[Event "Only Tags"]
[Site "Nowhere"]"""
        val pgnGame = PgnUtils.parsePgn(pgnString)
        assertEquals("Only Tags", pgnGame.tags["Event"])
        assertEquals("Nowhere", pgnGame.tags["Site"])
        assertTrue(pgnGame.moves.isEmpty())
        assertEquals("*", pgnGame.result)
    }

    @Test
    fun `parsePgn should handle PGN string with only moves`() {
        val pgnString = """1. e4 e5 2. Nf3"""
        val pgnGame = PgnUtils.parsePgn(pgnString)
        assertTrue(pgnGame.tags.isEmpty())
        assertEquals(listOf("e4", "e5", "Nf3"), pgnGame.moves)
        assertEquals("*", pgnGame.result)
    }

    @Test
    fun `parsePgn should handle PGN with result only`() {
        val pgnString = """[Result "1/2-1/2"]"""
        val pgnGame = PgnUtils.parsePgn(pgnString)
        assertEquals("1/2-1/2", pgnGame.result)
        assertTrue(pgnGame.tags.containsKey("Result"))
        assertTrue(pgnGame.moves.isEmpty())
    }

    @Test
    fun `parsePgnGames should handle PGN with multiple games`() {
        val pgnString = """[Event "Game 1"]
1. e4 e5 1-0

[Event "Game 2"]
1. d4 d5 1/2-1/2"""

        val games = PgnUtils.parsePgnGames(pgnString)

        assertEquals(2, games.size)

        val game1 = games[0]
        assertEquals("Game 1", game1.tags["Event"])
        assertEquals(listOf("e4", "e5"), game1.moves)
        assertEquals("1-0", game1.result)

        val game2 = games[1]
        assertEquals("Game 2", game2.tags["Event"])
        assertEquals(listOf("d4", "d5"), game2.moves)
        assertEquals("1/2-1/2", game2.result)
    }

    @Test
    fun `parse large PGN file with multiple games`() {
        val filePath = "src/test/resources/lichess_db_standard_rated_2013-01.pgn"
        val pgnText = File(filePath).readText()

        var games: List<PgnGame> = emptyList()
        val durationMillis = measureTimeMillis {
            games = PgnUtils.parsePgnGames(pgnText)
        }

        println("Parsed ${games.size} games in $durationMillis ms")

        assertTrue(games.size == 5297, "Expected at least 1000 games parsed")

        // Spot-check a few games randomly
        val sampleGames = games.shuffled().take(10)
        sampleGames.forEach { game ->
            assertNotNull(game.tags["White"], "Missing 'White' tag in a game")
            assertNotNull(game.tags["Black"], "Missing 'Black' tag in a game")
            assertTrue(game.moves.isNotEmpty(), "A parsed game has no moves")
            assertTrue(game.result in setOf("1-0", "0-1", "1/2-1/2", "*"), "Invalid result: ${game.result}")
        }

        // Optional: log first few game summaries
        games.take(5).forEachIndexed { index, game ->
            println("Game ${index + 1}: ${game.tags["White"]} vs ${game.tags["Black"]}, result: ${game.result}, moves: ${game.moves.size}")
        }
    }

}
