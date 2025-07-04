package com.hemanth.chess.core.move

import com.hemanth.chess.core.board.Square
import com.hemanth.chess.core.piece.PieceType
import kotlin.test.Test
import kotlin.test.assertEquals

class MoveTest {

    @Test
    fun movesWithDifferentFromSquaresAreNotEqual() {
        val move1 = Move(from = Square("e2"), to = Square("e4"))
        val move2 = Move(from = Square("d2"), to = Square("e4"))
        kotlin.test.assertNotEquals(move1, move2)
    }

    @Test
    fun movesWithDifferentToSquaresAreNotEqual() {
        val move1 = Move(from = Square("e2"), to = Square("e4"))
        val move2 = Move(from = Square("e2"), to = Square("e5"))
        kotlin.test.assertNotEquals(move1, move2)
    }

    @Test
    fun toStringReturnsCorrectFormatWithoutPromotion() {
        val move = Move(from = Square("e2"), to = Square("e4"))
        assertEquals("e2e4", move.toString())
    }

    @Test
    fun toStringReturnsCorrectFormatWithPromotion() {
        val move = Move(from = Square("e7"), to = Square("e8"), promotionPieceType = PieceType.QUEEN)
        assertEquals("e7e8q", move.toString())
    }

}