# Chess Game

A simple chess game for Android developed in Java. Allows two players to play on the same device.

## Description

A fully functional chess game with classic rules, including all essential chess mechanics:

- **All chess pieces**: pawns, rooks, bishops, knights, queen, and king
- **Special moves**: castling, en passant capture, pawn promotion
- **Check and checkmate detection**: automatic threat detection for the king
- **Move validation**: only legal moves according to chess rules
- **Undo move**: ability to undo the last move

## Features

### Game Mechanics
- Turn-based gameplay for two players (white and black)
- Visual highlighting of possible moves
- Selected piece highlighting
- Check indication with red color around the king
- Automatic checkmate and draw detection

### Rules
- Standard FIDE chess rules
- Castling (kingside and queenside)
- En passant capture
- Pawn promotion when reaching the last rank
- Automatic draw for insufficient material

### Interface
- Beautiful 8x8 chess board
- Classic chess piece designs
- Intuitive touch controls
- Current turn information display
- Undo buttons for each player

## How to Play

1. **Game Start**: White moves first
2. **Select Piece**: Tap the piece you want to move
3. **View Moves**: Possible moves are highlighted in green
4. **Make Move**: Tap the square where you want to move the piece
5. **Undo Move**: Use button on the other side to undo the last move
6. **Pawn Promotion**: When a pawn reaches the last rank, choose the piece for promotion

## Technical Features

### Architecture
- **ChessEngine**: Core game logic, move validation, check/checkmate detection
- **MainActivity**: User interface and interaction handling
- Uses byte arrays for efficient board state storage

### Algorithms
- Generation of all possible moves for the current position
- Legal move checking considering check situations
- Checkmate and stalemate detection
- Special rules handling (castling, en passant)

## System Requirements

- Android 6.0 (API level 23) and above
- Touch screen support
- Minimum 6 MB free space

## Installation

1. Download the APK file
2. Allow installation from unknown sources
3. Install the application
4. Enjoy playing!

## Development

The project is written in Java using Android SDK. Main components:

- `ChessEngine.java` - game logic
- `MainActivity.java` - user interface
- Resources with chess piece images

### Debug

To debug board (full synchronizing with real matrix) replace all updateCell(byte, byte) (in ChessEngine) with updateCell() and add this code to ChessEngine:

```java
/// To debug board
private void updateCell() {
    for (byte y = 0; y < 8; y++) {
        for (byte x = 0; x < 8; x++) {
            callback.updateCell(y, x);
        }
    }
}
```

## License

The project is freely distributed for personal use.

## Support

If you have questions or suggestions for improving the game, please create an issue in the project repository.

---

*Enjoy playing chess!* ♟️