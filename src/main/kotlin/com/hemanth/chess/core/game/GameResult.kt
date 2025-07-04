package com.hemanth.chess.core.game

import com.hemanth.chess.core.piece.PieceColor

/**
 * Represents the result of a chess game.
 */
sealed class GameResult {
    /**
     * The game is still ongoing.
     */
    object Ongoing : GameResult()

    /**
     * The game has been won by a player.
     *
     * @property winner The color of the winning player.
     */
    data class Win(val winner: PieceColor) : GameResult()

    /**
     * Represents a drawn game.
     */
    sealed class Draw : GameResult() {
        /** The game ended in stalemate. */
        object Stalemate : Draw()

        /** The game ended due to threefold repetition. */
        object ThreefoldRepetition : Draw()

        /** The game ended due to the fifty-move rule. */
        object FiftyMoveRule : Draw()

        /** The game ended due to insufficient material. */
        object InsufficientMaterial : Draw()
    }

    /**
     * Indicates whether the game is over.
     */
    val isOver: Boolean
        get() = this !is Ongoing
}