package com.hemanth.chess.core.utils

import com.hemanth.chess.core.board.Board
import com.hemanth.chess.core.game.GameState
import com.hemanth.chess.core.game.GameUtils
import com.hemanth.chess.core.game.PgnGame

/**
 * Utility object for parsing and handling PGN (Portable Game Notation) chess games.
 * Provides methods to parse PGN strings, extract games, and apply moves to a board and game state.
 *
 * ## Overview
 * - **Parsing:** Converts PGN strings into [PgnGame] objects.
 * - **Move Application:** Applies PGN moves to a board and game state.
 *
 * ### Parsing Methods
 * - **parsePgn:** Parses a single PGN game string into a [PgnGame] object.
 * - **parsePgnGames:** Parses multiple PGN games from a text containing one or more games.
 *
 * ### Move Application
 * - **applyPgnMoves:** Applies the moves from a [PgnGame] to an initial board and game state.
 *
 * ### Example Usage
 * ```Kotlin
 * val pgnGame = PgnUtils.parsePgn(pgnString)
 * val (board, gameState) = PgnUtils.applyPgnMoves(pgnGame)
 * ```
 */
object PgnUtils {

    /**
     * Parses a single PGN game string into a [PgnGame] object.
     *
     * @param pgnString The PGN string representing a single game.
     * @return The parsed [PgnGame].
     */
    fun parsePgn(pgnString: String): PgnGame {
        val resultTokens = setOf("1-0", "0-1", "1/2-1/2", "*")

        val lines = pgnString.lines().map { it.trim() }.filter { it.isNotEmpty() }

        val tags = mutableMapOf<String, String>()
        val moveTextBuilder = StringBuilder()
        var parsingTags = true

        for (line in lines) {
            if (parsingTags && line.startsWith("[") && line.endsWith("]")) {
                val tagName = line.substringAfter("[").substringBefore(" ")
                val tagValue = line.substringAfter("\"").substringBeforeLast("\"")
                tags[tagName] = tagValue
            } else {
                parsingTags = false
                moveTextBuilder.append(line).append(" ")
            }
        }

        var fullMoveText = moveTextBuilder.toString().trim()

        fullMoveText = fullMoveText
            .replace(Regex("\\{[^}]*}"), "")
            .replace(Regex("\\([^)]*\\)"), "")
            .replace(Regex("\\d+\\.{1,3}\\s*"), "")
            .trim()

        val lastToken = fullMoveText.split(Regex("\\s+")).lastOrNull()
        val result = tags["Result"] ?: if (lastToken in resultTokens) lastToken!! else "*"

        if (lastToken == result) {
            fullMoveText = fullMoveText.removeSuffix(result).trim()
        } else if (tags["Result"] != null) {
            fullMoveText = fullMoveText.replace(Regex("\\s+${Regex.escape(result)}\\s*$"), "").trim()
        }

        val moves = fullMoveText
            .split(Regex("\\s+"))
            .filter { it.isNotEmpty() && it !in resultTokens }

        return PgnGame(tags, moves, result)
    }

    /**
     * Parses multiple PGN games from a text containing one or more games.
     *
     * @param pgnText The PGN text containing one or more games.
     * @return A list of [PgnGame] objects.
     */
    fun parsePgnGames(pgnText: String): List<PgnGame> {
        val gameSections = pgnText
            .split(Regex("(?=\\[Event)"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        return gameSections.map { parsePgn(it) }
    }

    /**
     * Applies the moves from a [PgnGame] to an initial board and game state.
     *
     * @param pgnGame The PGN game whose moves to apply.
     * @return The resulting [Board] and [GameState] after applying all moves.
     */
    fun applyPgnMoves(
        pgnGame: PgnGame
    ): Pair<Board, GameState> {
        var (currentBoard, currentGameState) = FenUtils.parseFen(FenUtils.DEFAULT_FEN)
        val sanUtils = SanUtils

        for (sanMove in pgnGame.moves) {
            try {
                val move = sanUtils.sanToMove(sanMove, currentBoard, currentGameState)
                val (newBoard, newGameState) = GameUtils.makeMove(currentBoard, currentGameState, move)
                currentBoard = newBoard
                currentGameState = newGameState
            } catch (e: IllegalArgumentException) {
                println("Warning: Could not apply move '$sanMove': ${e.message}")
                break
            }
        }
        return Pair(currentBoard, currentGameState)
    }
}