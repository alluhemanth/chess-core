# Contributing to chess-core

Thank you for considering contributing to chess-core! Contributions are welcome and greatly appreciated. This document
outlines the guidelines for contributing to the project.

## How to Contribute

### Reporting Issues

If you encounter a bug, have a feature request, or notice something that can be improved, please create an issue in the
GitHub repository. When reporting an issue, make sure to include:

1. **Description**: Provide a clear and concise description of the issue.
2. **Steps to Reproduce**: Include the steps needed to reproduce the issue.
3. **FEN**: If the issue is related to a specific position, include the FEN string representing the position.
4. **PGN**: If the issue is related to a sequence of moves, include the PGN string representing the game.
5. **Expected Behavior**: Describe what you expected to happen.
6. **Actual Behavior**: Describe what actually happened.

### Suggesting Features

If you have an idea for a new feature, please create an issue with the following details:

1. **Feature Description**: Explain the feature and its purpose.
2. **Use Case**: Provide examples of how the feature would be used.
3. **Additional Context**: Include any relevant details or references.

### Submitting Pull Requests

If you'd like to contribute code, follow these steps:

1. Fork the repository.
2. Create a new branch for your changes.
3. Make your changes and commit them with clear and descriptive commit messages.
4. Push your branch to your forked repository.
5. Submit a pull request to the main repository.

### Code Style

Please follow the existing code style and conventions used in the project. Ensure your code is well-documented and
includes unit tests where applicable.

### Testing

Before submitting a pull request, ensure that all tests pass and that your changes do not introduce any regressions. You
can run the tests using Gradle:

```bash
./gradlew test
```

## Examples of Issues to Report

- **Move Generation Bugs**: Incorrect legal moves generated for a given position.
- **Game State Errors**: Incorrect handling of game rules, such as castling, en passant, or draw conditions.
- **FEN/PGN Parsing Issues**: Errors when loading or exporting positions using FEN or PGN.
- **Performance Problems**: Slow move generation or other performance bottlenecks.
- **Documentation Improvements**: Missing or unclear documentation.

## Communication

Feel free to ask questions or discuss ideas by opening an issue or joining the discussion in the repository.

## License

By contributing to chess-core, you agree that your contributions will be licensed under the MIT License.

Thank you for contributing!

