package com.anready.chess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChessEngine {
    public byte[][] board = {
            /*0*/{-5, -2, -3, -9, -8, -3, -2, -5},
            /*1*/{-1, -1, -1, -1, -1, -1, -1, -1},
            /*2*/{+0, +0, +0, +0, +0, +0, +0, +0},
            /*3*/{+0, +0, +0, +0, +0, +0, +0, +0},
            /*4*/{+0, +0, +0, +0, +0, +0, +0, +0},
            /*5*/{+0, +0, +0, +0, +0, +0, +0, +0},
            /*6*/{+1, +1, +1, +1, +1, +1, +1, +1},
            /*7*/{+5, +2, +3, +9, +8, +3, +2, +5}
            //_____0___1___2___3___4___5___6___7__\\
    };

//    public byte[][] board = {
//            /*0*/{-5, +0, +0, -8, +0, -3, -2, -5},
//            /*1*/{-1, +5, -1, -2, -1, -9, -1, -1},
//            /*2*/{+0, +0, +0, +0, +0, +0, +0, +0},
//            /*3*/{+0, +0, +0, +0, +0, +0, +3, +0},
//            /*4*/{+0, +0, +0, +1, +0, +0, +0, +0},
//            /*5*/{+0, +0, +0, +0, +1, +0, +1, +3},
//            /*6*/{+1, +0, +0, +0, +8, +1, +0, +1},
//            /*7*/{+0, +0, +0, +0, +0, +0, +0, +0}
//            //_____0___1___2___3___4___5___6___7__\\
//    };

    List<byte[]> history = new ArrayList<>();
    byte[] lastMove = new byte[6];
    boolean isWhiteMove = true;

    byte[] whiteKing = {7,4}; // Y; X
    byte[] blackKing = {0,4}; // Y; X;

    public Map<byte[], List<byte[]>> allMoves;

    public ChessEngine() {
        allMoves = getAllPossibleMoves();
    }

//    public ChessEngine(byte[][] board, List<byte[]> history, byte[] lastMove,
//                       boolean isWhiteMove, byte[] whiteKing, byte[] blackKing, Map<byte[], List<byte[]>> allMoves) {
//        this.board = board;
//        this.history = history;
//        this.lastMove = lastMove;
//        this.isWhiteMove = isWhiteMove;
//        this.whiteKing = whiteKing;
//        this.blackKing = blackKing;
//        this.allMoves = allMoves;
//    }

    public boolean isMate() {
        Map<byte[], List<byte[]>> moves = getAllPossibleMoves();

        for (List<byte[]> moveList : moves.values()) {
            if (!moveList.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public boolean isAllowedToMove(byte y, byte x, byte y1, byte x1) {
        for (Map.Entry<byte[], List<byte[]>> entry : allMoves.entrySet()) {
            if (entry.getKey()[0] == y && entry.getKey()[1] == x) {
                for (byte[] value : entry.getValue()) {
                    if (value[0] == y1 && value[1] == x1) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public Map<byte[], List<byte[]>> getAllPossibleMoves() {
        Map<byte[], List<byte[]>> possibleMoves = new HashMap<>();
        for (byte y = 0; y < 8; y++) {
            for (byte x = 0; x < 8; x++) {
                byte rawPiece = board[y][x];
                byte piece = (byte) Math.abs(rawPiece);
                if ((isWhiteMove && rawPiece < 0) || (!isWhiteMove && rawPiece > 0) || (piece == 0)) {
                    continue;
                }

                if (piece == 5) {
                    Map<byte[], Byte> allPossibleMoves = yLineMoves(y, x, (byte) 7);
                    allPossibleMoves.putAll(yLineMoves(y, x, (byte) 0));
                    allPossibleMoves.putAll(xLineMoves(y, x, (byte) 0));
                    allPossibleMoves.putAll(xLineMoves(y, x, (byte) 7));

                    possibleMoves.put(new byte[]{y,x}, getValidMoves(allPossibleMoves, rawPiece, y, x));
                } else if (piece == 3) {
                    possibleMoves.put(new byte[]{y,x}, getValidMoves(getDiagonalsMoves(y, x), rawPiece, y, x));
                } else if (piece == 9) {
                    Map<byte[], Byte> allPossibleMoves = yLineMoves(y, x, (byte) 7);
                    allPossibleMoves.putAll(yLineMoves(y, x, (byte) 0));
                    allPossibleMoves.putAll(xLineMoves(y, x, (byte) 0));
                    allPossibleMoves.putAll(xLineMoves(y, x, (byte) 7));
                    allPossibleMoves.putAll(getDiagonalsMoves(y, x));

                    possibleMoves.put(new byte[]{y,x}, getValidMoves(allPossibleMoves, rawPiece, y, x));
                } else if (piece == 2) {
                    Map<byte[], Byte> allPossibleMoves = new HashMap<>();

                    byte[][] knightOffsets = {
                            {+1, +2}, {+1, -2}, {-1, +2}, {-1, -2},
                            {+2, +1}, {+2, -1}, {-2, +1}, {-2, -1}
                    };

                    for (byte[] offset : knightOffsets) {
                        byte newY = (byte)(y + offset[0]);
                        byte newX = (byte)(x + offset[1]);
                        if (newY >= 0 && newY <= 7 && newX >= 0 && newX <= 7) {
                            allPossibleMoves.put(new byte[]{newY,newX}, board[newY][newX]);
                        }
                    }

                    possibleMoves.put(new byte[]{y, x}, getValidMoves(allPossibleMoves, rawPiece, y, x));

                } else if (piece == 8) {
                    Map<byte[], Byte> allPossibleMoves = new HashMap<>(getRMoves(rawPiece, y, x));

                    byte[][] kingOffsets = {
                            {+1, +1}, {+1, -1}, {+1, 0},
                            {-1, +1}, {-1, -1}, {-1, 0},
                            {0, +1}, {0, -1}
                    };

                    for (byte[] offset : kingOffsets) {
                        byte newY = (byte)(y + offset[0]);
                        byte newX = (byte)(x + offset[1]);
                        if (newY >= 0 && newY <= 7 && newX >= 0 && newX <= 7) {
                            allPossibleMoves.put(new byte[]{newY,newX}, board[newY][newX]);
                        }
                    }

                    possibleMoves.put(new byte[]{y, x}, getValidMoves(allPossibleMoves, rawPiece, y, x));
                } else if (piece == 1) {
                    Map<byte[], Byte> allPossibleMoves = new HashMap<>();

                    byte pawn = deAbs(rawPiece);

                    byte[][] offsets = {
                            {pawn, 0}, {(byte) (2 * pawn), 0}, {pawn, pawn},
                            {pawn, (byte) -pawn}
                    };

                    for (byte[] offset : offsets) {
                        byte newY = (byte)(y + offset[0]);
                        byte newX = (byte)(x + offset[1]);
                        if (newY >= 0 && newY <= 7 && newX >= 0 && newX <= 7) {
                            allPossibleMoves.put(new byte[]{newY,newX}, board[newY][newX]);
                        }
                    }

                    List<byte[]> validMoves = new ArrayList<>();
                    for (Map.Entry<byte[], Byte> entry : allPossibleMoves.entrySet()) {
                        byte[] key = entry.getKey();
                        if (Math.abs(y - key[0]) == 1 && Math.abs(x - key[1]) == 0 && board[key[0]][x] == 0) {
                            if (isPawnMoveValid(key, entry.getValue(), rawPiece, y, x)) {
                                validMoves.add(key);
                            }
                            continue;
                        }

                        if (Math.abs(y - key[0]) == 2 && Math.abs(x - key[1]) == 0 && board[key[0]][x] == 0) {
                            if ((rawPiece == 1 && y == 6) || (rawPiece == -1 && y == 1)) {
                                if (isPawnMoveValid(key, entry.getValue(), rawPiece, y, x)) {
                                    if (board[y - isNeg(board[y][x])][x] == 0) {
                                        validMoves.add(key);
                                    }
                                }
                            }

                            continue;
                        }

                        if (Math.abs(y - key[0]) == 1 && Math.abs(x - key[1]) == 1) {
                            if (board[key[0]][key[1]] * isNeg(rawPiece) == 0) {
                                if (lastMove[0] == rawPiece * -1 && lastMove[3] == y && lastMove[4] == key[1] && Math.abs(lastMove[1] - lastMove[3]) == 2) {
                                    if (isPawnMoveValid(key, entry.getValue(), rawPiece, y, x)) {
                                        validMoves.add(key);
                                    }
                                }
                            } else if (board[key[0]][key[1]] * isNeg(rawPiece) < 0) {
                                if (isPawnMoveValid(key, entry.getValue(), rawPiece, y, x)) {
                                    validMoves.add(key);
                                }
                            }
                        }
                    }

                    possibleMoves.put(new byte[]{y, x}, validMoves);
                }
            }
        }
        return possibleMoves;
    }

    private List<byte[]> getValidMoves(Map<byte[], Byte> allPossibleMoves, byte rawPiece, byte y, byte x) {
        List<byte[]> allValidMoves = new ArrayList<>();
        for (Map.Entry<byte[], Byte> value : allPossibleMoves.entrySet()) {
            if (value.getValue() * rawPiece <= 0) {
                board[value.getKey()[0]][value.getKey()[1]] = rawPiece;
                board[y][x] = 0;

                byte kingY = isWhiteMove ? whiteKing[0] : blackKing[0];
                byte kingX = isWhiteMove ? whiteKing[1] : blackKing[1];

                if (Math.abs(rawPiece) == 8) {
                    kingY = value.getKey()[0];
                    kingX = value.getKey()[1];

                    if (isWhiteMove) {
                        if (Math.abs(blackKing[0] - kingY) <= 1 && Math.abs(blackKing[1] - kingX) <=1) {
                            board[value.getKey()[0]][value.getKey()[1]] = value.getValue();
                            board[y][x] = rawPiece;
                            continue;
                        }
                    } else {
                        if (Math.abs(whiteKing[0] - kingY) <= 1 && Math.abs(whiteKing[1] - kingX) <=1) {
                            board[value.getKey()[0]][value.getKey()[1]] = value.getValue();
                            board[y][x] = rawPiece;
                            continue;
                        }
                    }
                }
                if (!isCheck(kingY, kingX)) {
                    board[value.getKey()[0]][value.getKey()[1]] = value.getValue();
                    board[y][x] = rawPiece;
                    allValidMoves.add(value.getKey());
                } else {
                    board[y][x] = rawPiece;
                    board[value.getKey()[0]][value.getKey()[1]] = value.getValue();
                }
            }
        }

        return allValidMoves;
    }

    public boolean isCheck(byte kingY, byte kingX) {
        Map<byte[], Byte> xLine7 = xLineMoves(kingY, kingX, (byte) 7);
        byte k = deAbs(board[kingY][kingX]);
        if (xLine7.containsValue((byte)(5*k)) || xLine7.containsValue((byte)(9*k))) {
            return true;
        }

        Map<byte[], Byte> xLine0 = xLineMoves(kingY, kingX, (byte) 0);
        if (xLine0.containsValue((byte)(5*k)) || xLine0.containsValue((byte)(9*k))) {
            return true;
        }

        Map<byte[], Byte> yLine7 = yLineMoves(kingY, kingX, (byte) 7);
        if (yLine7.containsValue((byte)(5*k)) || yLine7.containsValue((byte)(9*k))) {
            return true;
        }

        Map<byte[], Byte> yLine0 = yLineMoves(kingY, kingX, (byte) 0);
        if (yLine0.containsValue((byte)(5*k)) || yLine0.containsValue((byte)(9*k))) {
            return true;
        }

        if (!isDiagonalsFree(kingY, kingX)) {
            return true;
        }

        if ((kingX < 6 && kingY < 7 && board[kingY + 1][kingX + 2]*isNeg(board[kingY][kingX]) == -2) ||
                (kingX -2 >= 0 && kingY < 7 && board[kingY + 1][kingX - 2]*isNeg(board[kingY][kingX]) == -2) ||
                (kingY -1 >= 0 && kingX < 6 && board[kingY - 1][kingX + 2]*isNeg(board[kingY][kingX]) == -2)||
                (kingX -2  >= 0 && kingY -1 >= 0 && board[kingY - 1][kingX - 2]*isNeg(board[kingY][kingX]) == -2) ||
                (kingX < 7 && kingY < 6 && board[kingY + 2][kingX + 1]*isNeg(board[kingY][kingX]) == -2) ||
                (kingY -2 >= 0 && kingX < 7 && board[kingY - 2][kingX + 1]*isNeg(board[kingY][kingX]) == -2) ||
                (kingX -1 >= 0 && kingY < 6 && board[kingY + 2][kingX - 1]*isNeg(board[kingY][kingX]) == -2) ||
                (kingX -1  >= 0 && kingY -2 >= 0 && board[kingY - 2][kingX - 1]*isNeg(board[kingY][kingX]) == -2)
        ) {
            return true;
        }

        boolean p1 = false;
        boolean p2 = false;

        try {
            p1 = board[kingY + deAbs(board[kingY][kingX])][kingX + 1] * isNeg(board[kingY][kingX]) == -1;
        } catch (Exception ignore) {}

        try {
            p2 = board[kingY + deAbs(board[kingY][kingX])][kingX - 1] * isNeg(board[kingY][kingX]) == -1;
        } catch (Exception ignore){}

        return p1 || p2;
    }

    private Map<byte[], Byte> getDiagonalsMoves(byte y, byte x) {
        Map<byte[], Byte> allPossibleMoves = new HashMap<>();

        for (int i = 1; y + i <= 7 && x + i <= 7; i++) {
            byte t = board[y+i][x+i];
            if (t != 0) {
                allPossibleMoves.put(new byte[]{(byte)(y+i),(byte)(x+i)}, t);
                break;
            } else {
                allPossibleMoves.put(new byte[]{(byte)(y+i),(byte)(x+i)}, t);
            }
        }
        for (int i = 1; y + i <= 7 && x - i >= 0; i++) {
            byte t = board[y+i][x-i];
            if (t != 0) {
                allPossibleMoves.put(new byte[]{(byte)(y+i),(byte)(x-i)}, t);
                break;
            } else {
                allPossibleMoves.put(new byte[]{(byte)(y+i),(byte)(x-i)}, t);
            }
        }
        for (int i = 1; y - i >= 0 && x - i >= 0; i++) {
            byte t = board[y-i][x-i];
            if (t != 0) {
                allPossibleMoves.put(new byte[]{(byte)(y-i),(byte)(x-i)}, t);
                break;
            } else {
                allPossibleMoves.put(new byte[]{(byte)(y-i),(byte)(x-i)}, t);
            }
        }
        for (int i = 1; y - i >= 0 && x + i <= 7; i++) {
            byte t = board[y-i][x+i];
            if (t != 0) {
                allPossibleMoves.put(new byte[]{(byte)(y-i),(byte)(x+i)}, t);
                break;
            } else {
                allPossibleMoves.put(new byte[]{(byte)(y-i),(byte)(x+i)}, t);
            }
        }

        return allPossibleMoves;
    }

    private Map<byte[], Byte> yLineMoves(byte y, byte x, byte y1){
        Map<byte[], Byte> possibleMoves = new HashMap<>();
        for (byte i = 1; i <= Math.abs(y-y1); i++) {
            byte tempPiece = board[y - isNeg((byte)(y-y1))*i][x];
            if(tempPiece != 0) {
                possibleMoves.put(new byte[]{(byte)(y - isNeg((byte) (y - y1)) * i), x}, tempPiece);
                break;
            } else {
                possibleMoves.put(new byte[]{(byte)(y - isNeg((byte) (y - y1)) * i), x}, tempPiece);
            }
        }

        return possibleMoves;
    }

    private Map<byte[], Byte> xLineMoves(byte y, byte x, byte x1){
        Map<byte[], Byte> possibleMoves = new HashMap<>();
        for (byte i = 1; i <= Math.abs(x-x1); i++) {
            byte tempPiece = board[y][x - isNeg((byte)(x-x1))*i];
            if (tempPiece != 0) {
                possibleMoves.put(new byte[]{y, (byte)(x - isNeg((byte)(x-x1))*i)}, tempPiece);
                break;
            } else {
                possibleMoves.put(new byte[]{y, (byte)(x - isNeg((byte)(x-x1))*i)}, tempPiece);
            }
        }

        return possibleMoves;
    }

    private Map<byte[], Byte> getRMoves(byte rawPiece, byte y, byte x) {
        Map<byte[], Byte> allPossibleMoves = new HashMap<>();

        if (!isFigureInHistory(rawPiece)) {
            if (!isFigureInHistory((byte) (5 * isNeg(rawPiece)), y, (byte) 0)) {
                allPossibleMoves.putAll(xLineMoves(y, x, (byte) 2));
            }

            if (!isFigureInHistory((byte) (5 * isNeg(rawPiece)), y, (byte) 7)) {
                allPossibleMoves.putAll(xLineMoves(y, x, (byte) 6));
            }

            Iterator<Map.Entry<byte[], Byte>> iterator = allPossibleMoves.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<byte[], Byte> entry = iterator.next();
                byte[] key = entry.getKey();
                byte oldValue = entry.getValue();

                if (board[key[0]][key[1]] != 0) {
                    iterator.remove();
                    continue;
                }

                byte kingY = key[0];
                byte kingX = key[1];

                if (kingX == 2 && board[kingY][1] != 0) {
                    board[y][x] = rawPiece;
                    board[key[0]][key[1]] = oldValue;
                    iterator.remove();
                    continue;
                }

                if (kingX == 6 || kingX == 2) {
                    byte tempX = (byte) (kingX == 2 ? kingX + 1 : kingX - 1);

                    board[key[0]][tempX] = rawPiece;
                    board[y][x] = 0;

                    if (isCheck(kingY, tempX)) {
                        board[y][x] = rawPiece;
                        board[key[0]][tempX] = oldValue;
                        iterator.remove();
                        continue;
                    } else {
                        board[key[0]][tempX] = oldValue;
                        board[y][x] = rawPiece;
                    }
                }

                board[key[0]][key[1]] = rawPiece;
                board[y][x] = 0;

                if (isCheck(kingY, kingX)) {
                    board[y][x] = rawPiece;
                    board[key[0]][key[1]] = oldValue;
                    iterator.remove();
                } else {
                    board[key[0]][key[1]] = oldValue;
                    board[y][x] = rawPiece;
                }
            }
        }

        return allPossibleMoves;
    }

    private boolean isFigureInHistory(byte rawPiece) {
        return isFigureInHistory(rawPiece, (byte) -1, (byte) -1);
    }

    private boolean isFigureInHistory(byte rawPiece, byte y, byte x) {
        for (byte[] record : history) {
            if (y != -1 && x != -1) {
                if (record[0] == rawPiece && record[1] == y && record[2] == x) {
                    return true;
                }
            } else if (record[0] == rawPiece) {
                return true;
            }
        }

        return false;
    }

    private boolean isDiagonalsFree(byte a, byte b){
        for (int i = 1; a + i <= 7 && b + i <= 7; i++) {
            byte t = (byte) (board[a+i][b+i] * isNeg(board[a][b]));
            if (t == -3 || t == -9) {
                return false;
            } else if (t != 0) {
                break;
            }
        }

        for (int i = 1; a + i <= 7 && b - i >= 0; i++) {
            byte t = (byte) (board[a+i][b-i] * isNeg(board[a][b]));
            if (t == -3 || t == -9) {
                return false;
            } else if (t != 0) {
                break;
            }
        }

        for (int i = 1; a - i >= 0 && b - i >= 0; i++) {
            byte t = (byte) (board[a-i][b-i] * isNeg(board[a][b]));
            if (t == -3 || t == -9) {
                return false;
            } else if (t != 0) {
                break;
            }
        }

        for (int i = 1; a - i >= 0 && b + i <= 7; i++) {
            byte t = (byte) (board[a-i][b+i] * isNeg(board[a][b]));
            if (t == -3 || t == -9) {
                return false;
            } else if (t != 0) {
                break;
            }
        }

        return true;
    }

    private boolean isPawnMoveValid(byte[] key, byte value, byte rawPiece, byte y, byte x) {
        board[key[0]][key[1]] = rawPiece;
        board[y][x] = 0;

        byte kingY = isWhiteMove ? whiteKing[0] : blackKing[0];
        byte kingX = isWhiteMove ? whiteKing[1] : blackKing[1];

        if (!isCheck(kingY, kingX)) {
            board[key[0]][key[1]] = value;
            board[y][x] = rawPiece;
            return true;
        } else {
            board[y][x] = rawPiece;
            board[key[0]][key[1]] = value;
        }

        return false;
    }

    private byte deAbs(byte a){
        return (byte) (a < 0 ? 1 : -1);
    }

    public byte isNeg(byte a){
        return (byte) (a < 0 ? -1 : 1);
    }
}
