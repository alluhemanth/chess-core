package com.hemanth.chess.core

import com.hemanth.chess.core.board.Square
import com.hemanth.chess.core.game.GameResult
import com.hemanth.chess.core.move.Move
import com.hemanth.chess.core.piece.PieceColor
import com.hemanth.chess.core.utils.SanUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("unused")
private fun main() {
    val game = ChessGame()

    val stockfish = Stockfish()
    stockfish.start()

    println("Welcome to Chess!")
    println("Enter moves in SAN format (e.g., e4, Nf3, O-O). Type 'quit' to exit.")

    Scanner(System.`in`).use { scanner ->
        gameLoop(game, stockfish, scanner)
    }

    stockfish.stop()
    println("Thanks for playing!")
}

private fun gameLoop(game: ChessGame, stockfish: Stockfish, scanner: Scanner) {
    while (true) {
        printGameState(game)

        if (game.isGameOver()) {
            printGameResult(game.getGameResult())
            break
        }

        when (game.getCurrentPlayer()) {
            PieceColor.WHITE -> {
                if (!handleHumanMove(game, scanner)) break
            }

            PieceColor.BLACK -> {
                if (!handleStockfishMove(game, stockfish)) break
            }
        }
    }
}

private fun printGameState(game: ChessGame) {
    println("\n${game.getBoard()}")
    println("FEN: ${game.getFen()}")
    println("Current player: ${game.getCurrentPlayer()}")
}

private fun printGameResult(result: GameResult) {
    val resultText = when (result) {
        is GameResult.Win -> "${result.winner} wins!"
        is GameResult.Draw -> "Draw!"
        is GameResult.Ongoing -> "Ongoing"
    }
    println("Game Over! Result: $resultText")
}

private fun handleHumanMove(game: ChessGame, scanner: Scanner): Boolean {
    val legalMovesSan = game.getLegalMoves().map {
        SanUtils.moveToSan(it, game.getBoard(), game.getGameState())
    }
    println("Legal moves: ${legalMovesSan.joinToString(", ")}")
    print("Your move: ")
    val input = scanner.nextLine()
    if (input.equals("quit", ignoreCase = true)) return false

    try {
        if (game.makeSanMove(input)) {
            println("You played: $input")
        } else {
            println("Illegal move. Try again.")
        }
    } catch (e: Exception) {
        println("Error parsing move: ${e.message}. Try again.")
    }
    return true
}

private fun handleStockfishMove(game: ChessGame, stockfish: Stockfish): Boolean {
    println("Stockfish is thinking...")
    val legalMovesSan = game.getLegalMoves().map {
        SanUtils.moveToSan(it, game.getBoard(), game.getGameState())
    }
    println("Legal moves: ${legalMovesSan.joinToString(", ")}")

    val bestMoveUci = stockfish.getBestMove(game.getFen(), depth = 15)
    if (bestMoveUci != null) {
        val fromSquare = Square(bestMoveUci.substring(0, 2))
        val toSquare = Square(bestMoveUci.substring(2, 4))
        val promotionChar = bestMoveUci.getOrNull(4)
        val basicStockfishMove = Move(fromSquare, toSquare, promotionChar)
        val stockfishMoveSan = SanUtils.moveToSan(basicStockfishMove, game.getBoard(), game.getGameState())
        val stockfishMove = SanUtils.sanToMove(stockfishMoveSan, game.getBoard(), game.getGameState())

        if (game.makeMove(stockfishMove)) {
            println("Stockfish played: $bestMoveUci")
        } else {
            println("Stockfish made an illegal move: $bestMoveUci. This should not happen.")
        }
    } else {
        println("Stockfish could not find a move.")
        return false
    }
    return true
}

class Stockfish(private val stockfishPath: String = "stockfish") {
    private var process: Process? = null
    private var reader: BufferedReader? = null
    private var writer: OutputStreamWriter? = null

    fun start() {
        try {
            val processBuilder = ProcessBuilder(stockfishPath)
            process = processBuilder.start()
            reader = BufferedReader(InputStreamReader(process!!.inputStream))
            writer = OutputStreamWriter(process!!.outputStream)

            // Initialize UCI
            sendCommand("uci")
            readResponse("uciok")
            sendCommand("isready")
            readResponse("readyok")
        } catch (e: Exception) {
            System.err.println("Error starting Stockfish: ${e.message}")
            e.printStackTrace()
            stop()
        }
    }

    fun stop() {
        process?.destroy()
        process?.waitFor(1, TimeUnit.SECONDS)
        reader?.close()
        writer?.close()
    }

    private fun sendCommand(command: String) {
        writer?.write("$command\n")
        writer?.flush()
    }

    private fun readResponse(expected: String? = null, timeoutMs: Long = 5000): String {
        val startTime = System.currentTimeMillis()
        val response = StringBuilder()
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            if (reader?.ready() == true) {
                val line = reader?.readLine()
                if (line != null) {
                    response.append(line).append("\n")
                    if (expected != null && line.contains(expected)) {
                        return response.toString()
                    }
                }
            }
            Thread.sleep(10)
        }
        if (expected != null) {
            System.err.println("Stockfish did not return expected response '$expected' within timeout.")
        }
        return response.toString()
    }

    fun getBestMove(fen: String, depth: Int = 10): String? {
        sendCommand("ucinewgame")
        sendCommand("position startpos")
        sendCommand("position fen $fen")
        sendCommand("go depth $depth")
        val response = readResponse("bestmove")
        val match = "bestmove (\\S+)".toRegex().find(response)
        return match?.groupValues?.get(1)
    }
}