package com.hemanth.chess.core.move

/**
 * Represents a relative offset in file and rank for chess piece movement.
 *
 * @property fileDelta The change in file (horizontal movement).
 * @property rankDelta The change in rank (vertical movement).
 */
data class Offset(
    val fileDelta: Int,
    val rankDelta: Int
)
