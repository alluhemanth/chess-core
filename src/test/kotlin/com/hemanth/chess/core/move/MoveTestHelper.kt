package com.hemanth.chess.core.move

import com.hemanth.chess.core.board.Square
import com.hemanth.chess.core.game.CastlingAvailability
import com.hemanth.chess.core.game.GameState
import com.hemanth.chess.core.piece.PieceColor
import kotlin.test.assertEquals

fun createDefaultGameState(): GameState {
    return GameState(
        currentPlayer = PieceColor.WHITE,
        castlingRights = mapOf(
            PieceColor.WHITE to CastlingAvailability(kingside = true, queenside = true),
            PieceColor.BLACK to CastlingAvailability(kingside = true, queenside = true)
        ),
        enPassantTargetSquare = null,
        halfMoveClock = 0,
        fullMoveNumber = 1
    )
}

fun assertMoveExists(
    moves: List<Move>,
    fromSquare: Square,
    toSquare: Square,
    isCapture: Boolean = false
) {
    val moveExists = moves.any {
        it.from == fromSquare &&
                it.to == toSquare &&
                it.isCapture == isCapture
    }
    assertEquals(
        true,
        moveExists,
        "Move from ${fromSquare.file.value}${fromSquare.rank.value} to " +
                "${toSquare.file.value}${toSquare.rank} ${if (isCapture) "(capture)" else ""} should exist"
    )
}

fun assertMoveDoesNotExist(
    moves: List<Move>,
    fromSquare: Square,
    toSquare: Square
) {
    val moveExists = moves.any { it.from == fromSquare && it.to == toSquare }
    assertEquals(
        false,
        moveExists,
        "Move from ${fromSquare.file.value}${fromSquare.rank.value} to " +
                "${toSquare.file.value}${toSquare.rank} should not exist"
    )
}
