# Module chess-core

Welcome to `chess-core`! This Kotlin library is designed to handle all the core logic and rules of chess, making it easy
to build chess apps, tools, or analysis utilities. It focuses on move generation, rule enforcement, and game state
management. If you want to connect to a chess engine like Stockfish for analysis or computer play, this library provides
a simple wrapper for that—but it does not include a chess engine or AI itself.

## Features

- **Comprehensive Chess Rules:** Handles all standard chess rules, including castling, en passant, pawn promotion, and
  draw conditions (like threefold repetition and the fifty-move rule).
- **FEN and PGN Support:** Easily load and export games using Forsyth-Edwards Notation (FEN) and Portable Game
  Notation (PGN) for saving, sharing, and analyzing games.
- **Fast Move Generation:** Quickly generate all legal moves for any position—ideal for building analysis tools, GUIs,
  or integrating with chess engines.
- **Modular and Extensible:** The code is organized for easy extension, so you can add features or adapt it for your own
  chess-related projects.
- **Engine Communication:** Includes a wrapper to communicate with external chess engines like Stockfish for advanced
  analysis or computer play.

# Package com.hemanth.chess.core

This is the main package, bringing together all the core features for working with chess games. Here you’ll find the
primary classes for setting up and managing a chess game.

# Package com.hemanth.chess.core.board

Handles everything related to the chessboard: board representation, squares, files (columns), and ranks (rows).
Responsible for tracking piece positions, validating moves, and setting up positions (including from FEN strings).

# Package com.hemanth.chess.core.exception

Custom exceptions for error handling, such as invalid moves, bad FEN strings, or out-of-bounds board coordinates.

# Package com.hemanth.chess.core.game

Manages the flow of the chess game: turn management, special moves (castling, en passant), and game end conditions (
checkmate, stalemate, draw, etc.).

# Package com.hemanth.chess.core.move

Focuses on move generation and validation. Generates legal moves, checks move validity, and applies moves to the board.
`PseudoLegalMoveGenerator.kt`: Generates moves that are legal except for leaving the king in check.

# Package com.hemanth.chess.core.move.piece

Defines how each chess piece moves. Implements the movement rules for pawns, knights, bishops, rooks, queens, and kings.

# Package com.hemanth.chess.core.piece

Defines chess pieces, their types, and colors.

# Package com.hemanth.chess.core.utils

Utility functions for working with chess data, including FEN and PGN parsing/generation and standard algebraic
notation (SAN).
