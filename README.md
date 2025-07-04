# chess-core

A Kotlin-based chess game logic library for handling chess rules, move generation, and game state management — designed
for flexibility, extensibility, and integration with engines like Stockfish.

## Features

- **Complete Chess Logic:** Implements all standard chess rules, including castling, en passant, promotion, and draw
  conditions (threefold repetition, fifty-move rule, insufficient material).
- **FEN and PGN Support:** Easily load and export games using standard Forsyth-Edwards Notation (FEN) and Portable Game
  Notation (PGN).
- **Move Generation:** Efficiently generates legal moves for any given position.
- **Extensible Design:** The project is structured to be easily extended with new features or integrations.

## Getting Started

### Prerequisites

- Java Development Kit (JDK)
- Gradle
- Stockfish engine (optional, for AI opponent)

### Building the Project

1. Clone the repository:
   ```bash
   git clone https://github.com/alluhemanth/chess-core.git
   ```
2. Build the project using Gradle:
   ```bash
   ./gradlew build
   ```

### Running the Example

The project includes an example `main` function that demonstrates how to use the chess engine to play a game against
Stockfish.

To run the example:

1. Make sure you have the Stockfish engine installed and available in your system's PATH.
2. Run the `main` function in `src/main/kotlin/com/hemanth/chess/core/Example.kt`.

## ChessGame API

The `ChessGame` class provides a complete chess game implementation, including board state management, move generation,
and game logic. Below is an overview of its key methods and usage.

### Creating a Game

```kotlin
val game = ChessGame()
```

### Making Moves

#### Using SAN (Standard Algebraic Notation)

```kotlin
game.makeSanMove("e4")
game.makeSanMove("e5")
```

#### Using UCI (Universal Chess Interface)

```kotlin
game.makeUciMove("e2e4")
game.makeUciMove("e7e5")
```

#### Directly Using Move Objects

```kotlin
val move = game.getLegalMoves().first()
game.makeMove(move)
```

### Undo and Redo Moves

```kotlin
game.undo()
game.redo()
```

### Querying Game State

#### Get Current Board

```kotlin
game.getBoard()
```

#### Get Current Player

```kotlin
game.getCurrentPlayer()
```

#### Check if Game is Over

```kotlin
game.isGameOver()
```

#### Get Game Result

```kotlin
val result = game.getGameResult()
```

### Legal Moves

```kotlin
val legalMoves = game.getLegalMoves()
```

### FEN and PGN Support

#### Get FEN

```kotlin
val fen = game.getFen()
```

#### Load FEN

```kotlin
game.loadFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
```

#### Get PGN

```kotlin
val pgn = game.getPgn()
```

#### Load PGN

```kotlin
game.loadPgn("1. e4 e5 2. Nf3 Nc6")
```

### Example Usage with Stockfish

The `Example.kt` file demonstrates how to integrate `ChessGame` with the Stockfish engine for AI gameplay. Below is a
simplified version:

```kotlin
val game = ChessGame()
val stockfish = Stockfish()
stockfish.start()

Scanner(System.`in`).use { scanner ->
    while (!game.isGameOver()) {
        game.getBoard()
        game.getFen()
        game.getCurrentPlayer()

        if (game.getCurrentPlayer() == PieceColor.WHITE) {
            val input = scanner.nextLine()
            if (input.equals("quit", ignoreCase = true)) break
            game.makeSanMove(input)
        } else {
            val bestMove = stockfish.getBestMove(game.getFen())
            game.makeUciMove(bestMove!!)
        }
    }
}

stockfish.stop()
game.getGameResult()
```

## Project Structure

- `src/main/kotlin`: Contains the core source code for the chess engine.
    - `board`: Classes related to the chessboard, squares, files, and ranks.
    - `exception`: Custom exceptions for handling errors.
    - `game`: Game state management, rules, and results.
    - `move`: Move generation and representation.
    - `piece`: Piece types, colors, and representation.
    - `utils`: Utilities for handling FEN, PGN, and SAN (Standard Algebraic Notation).
- `src/test/kotlin`: Contains unit tests for the project.

## Documentation

Detailed documentation for this project is available
at [chess-core documentation](https://alluhemanth.github.io/chess-core/).

## Contributing

Contributions are welcome! Please see the [CONTRIBUTING](CONTRIBUTING.md) file for detailed guidelines on how to
contribute, report issues, and suggest features.

## Attribution

The rook icon used in this project is attributed to:

Cburnett, [CC BY-SA 3.0](http://creativecommons.org/licenses/by-sa/3.0/), via Wikimedia Commons.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
