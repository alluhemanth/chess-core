package com.hemanth.chess.core.move

// Can be inside PerftTest.kt or in a separate file if used elsewhere
data class PerftDetailedResults(
    var nodes: Long = 0L,
    var captures: Long = 0L,
    var enPassant: Long = 0L,
    var castles: Long = 0L,
    var promotions: Long = 0L,
    var checks: Long = 0L,
    var checkmates: Long = 0L
    // Discovery checks and double checks are more complex to isolate cleanly
    // for standard Perft and are often omitted unless specifically needed.
    // We'll stick to the common ones for now.
) {
    operator fun plusAssign(other: PerftDetailedResults) {
        nodes += other.nodes
        captures += other.captures
        enPassant += other.enPassant
        castles += other.castles
        promotions += other.promotions
        checks += other.checks
        checkmates += other.checkmates
    }

    override fun toString(): String {
        return "Nodes: $nodes, Captures: $captures, EP: $enPassant, Castles: $castles, Promotions: $promotions, Checks: $checks, Checkmates: $checkmates"
    }
}