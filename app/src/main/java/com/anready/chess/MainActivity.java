package com.anready.chess;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ChessEngine chessEngine;

    private TextView infoTop;
    private TextView infoBottom;

    boolean isGameFinished = false;
    private byte[] currentSelection = {-1, -1};

    ImageView[][] squares = new ImageView[8][8];

    private final Map<Byte, Integer> pieceMap = new HashMap<>() {{
        put((byte) 1, R.drawable.white_pawn);
        put((byte) 2, R.drawable.white_knight);
        put((byte) 3, R.drawable.white_bishop);
        put((byte) 5, R.drawable.white_rook);
        put((byte) 8, R.drawable.white_king);
        put((byte) 9, R.drawable.white_queen);

        put((byte) -1, R.drawable.black_pawn);
        put((byte) -2, R.drawable.black_knight);
        put((byte) -3, R.drawable.black_bishop);
        put((byte) -5, R.drawable.black_rook);
        put((byte) -8, R.drawable.black_king);
        put((byte) -9, R.drawable.black_queen);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        chessEngine = new ChessEngine();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        infoTop = findViewById(R.id.textView);
        infoBottom = findViewById(R.id.textView2);

        Button whiteButton = findViewById(R.id.button);
        Button blackButton = findViewById(R.id.button2);

        whiteButton.setOnClickListener(v -> cancelMove(whiteButton, blackButton));
        blackButton.setOnClickListener(v -> cancelMove(whiteButton, blackButton));

        toggleButtons(whiteButton, blackButton);

        infoTop.setText(chessEngine.isWhiteMove ? "White Move" : "Black Move");
        infoBottom.setText(chessEngine.isWhiteMove ? "White Move" : "Black Move");

        GridLayout boardGrid = findViewById(R.id.boardGrid);
        int cellSize = getResources().getDisplayMetrics().widthPixels / 8;
        for (byte y = 0; y < 8; y++) {
            for (byte x = 0; x < 8; x++) {
                ImageView square = new ImageView(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cellSize;
                params.height = cellSize;
                params.rowSpec = GridLayout.spec(y);
                params.columnSpec = GridLayout.spec(x);
                square.setLayoutParams(params);

                if ((x + y) % 2 == 0) {
                    square.setBackgroundColor(Color.rgb(240, 217, 181));
                } else {
                    square.setBackgroundColor(Color.rgb(181, 136, 99));
                }

                squares[y][x] = square;

                byte piece = chessEngine.board[y][x];
                if (piece != 0) {
                    Integer name = pieceMap.get(piece);
                    if (name != null) {
                        square.setImageResource(name);
                        square.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    }
                }

                square.setTag(new byte[]{y, x});

                square.setOnClickListener(v -> {
                    byte[] pos = (byte[]) v.getTag();
                    byte clickedY = pos[0];
                    byte clickedX = pos[1];

                    if (isGameFinished) {
                        return;
                    }

                    if (((chessEngine.isWhiteMove && chessEngine.board[clickedY][clickedX] > 0) || (!chessEngine.isWhiteMove && chessEngine.board[clickedY][clickedX] < 0))) {
                        if (currentSelection[0] != -1 && currentSelection[1] != -1) {
                            removeDotForAllPossibleMoves(currentSelection[0], currentSelection[1]);
                        }

                        if (clickedY == currentSelection[0] && clickedX == currentSelection[1]) {
                            currentSelection = new byte[]{-1, -1};
                            return;
                        }

                        currentSelection = pos;
                        setDotForAllPossibleMoves(clickedY, clickedX);
                    } else if (currentSelection[0] != -1 && currentSelection[1] != -1) {
                        if (chessEngine.isAllowedToMove(currentSelection[0], currentSelection[1], clickedY, clickedX)) {
                            updateCell(chessEngine.whiteKing[0], chessEngine.whiteKing[1], -2, Color.RED);
                            updateCell(chessEngine.blackKing[0], chessEngine.blackKing[1], -2, Color.RED);

                            removeDotForAllPossibleMoves(currentSelection[0], currentSelection[1]);

                            byte[] historyRecord = new byte[6];
                            historyRecord[0] = chessEngine.board[currentSelection[0]][currentSelection[1]];
                            historyRecord[1] = currentSelection[0];
                            historyRecord[2] = currentSelection[1];
                            historyRecord[3] = clickedY;
                            historyRecord[4] = clickedX;
                            historyRecord[5] = chessEngine.board[clickedY][clickedX];

                            chessEngine.history.add(historyRecord);
                            chessEngine.lastMove = historyRecord;

                            if (Math.abs(chessEngine.board[currentSelection[0]][currentSelection[1]]) == 8 && Math.abs(clickedX - currentSelection[1]) == 2) {
                                if (clickedX == 2) {
                                    chessEngine.board[clickedY][0] = 0;
                                    chessEngine.board[clickedY][3] = (byte)(5 * chessEngine.isNeg(chessEngine.board[currentSelection[0]][currentSelection[1]]));
                                    updateCell(clickedY, (byte) 0);
                                    updateCell(clickedY, (byte) 3);
                                } else if (clickedX == 6) {
                                    chessEngine.board[clickedY][7] = 0;
                                    chessEngine.board[clickedY][5] = (byte)(5 * chessEngine.isNeg(chessEngine.board[currentSelection[0]][currentSelection[1]]));
                                    updateCell(clickedY, (byte) 7);
                                    updateCell(clickedY, (byte) 5);
                                }
                            }

                            if (Math.abs(chessEngine.board[currentSelection[0]][currentSelection[1]]) == 1 && Math.abs(currentSelection[1] - clickedX) == 1) {
                                if (chessEngine.board[clickedY][clickedX] == 0) {
                                    byte tempY = (byte) (clickedY + chessEngine.isNeg(chessEngine.board[currentSelection[0]][currentSelection[1]]));
                                    chessEngine.board[tempY][clickedX] = 0;
                                    updateCell(tempY, clickedX);
                                }
                            }

                            chessEngine.board[clickedY][clickedX] = chessEngine.board[currentSelection[0]][currentSelection[1]];
                            chessEngine.board[currentSelection[0]][currentSelection[1]] = 0;

                            updateCell(currentSelection[0], currentSelection[1]);
                            updateCell(clickedY, clickedX);

                            if (Math.abs(chessEngine.board[clickedY][clickedX]) == 8) {
                                if (chessEngine.isWhiteMove) {
                                    chessEngine.whiteKing[0] = clickedY;
                                    chessEngine.whiteKing[1] = clickedX;
                                } else {
                                    chessEngine.blackKing[0] = clickedY;
                                    chessEngine.blackKing[1] = clickedX;
                                }
                            }

                            if (chessEngine.board[clickedY][clickedX] == 1 && clickedY == 0) {
                                callChooseDialog(clickedY, clickedX);
                            } else if (chessEngine.board[clickedY][clickedX] == -1 && clickedY == 7) {
                                callChooseDialog(clickedY, clickedX);
                            }

                            chessEngine.isWhiteMove = !chessEngine.isWhiteMove;
                            toggleButtons(whiteButton, blackButton);

                            infoTop.setText(chessEngine.isWhiteMove ? "White Move" : "Black Move");
                            infoBottom.setText(chessEngine.isWhiteMove ? "White Move" : "Black Move");

                            if (checkForFigures()) {
                                endGame("Draw");
                            }

                            drawOrMateCheck();
                            currentSelection = new byte[]{-1, -1};
                        }
                    }
                });

                boardGrid.addView(square);
            }
        }
    }

    private void cancelMove(Button whiteButton, Button blackButton) {
        if (chessEngine.history.isEmpty()) {
            return;
        }

        byte[] lastMove = chessEngine.history.remove(chessEngine.history.size() - 1);
        chessEngine.board[lastMove[1]][lastMove[2]] = lastMove[0];
        chessEngine.board[lastMove[3]][lastMove[4]] = lastMove[5];

        if (currentSelection[0] != -1 && currentSelection[1] != -1) {
            removeDotForAllPossibleMoves(currentSelection[0], currentSelection[1]);
        }

        if (lastMove[0] == 8) {
            chessEngine.whiteKing[0] = lastMove[1];
            chessEngine.whiteKing[1] = lastMove[2];
        } else if (lastMove[0] == -8) {
            chessEngine.blackKing[0] = lastMove[1];
            chessEngine.blackKing[1] = lastMove[2];
        }

        updateCell(lastMove[1], lastMove[2]);
        updateCell(lastMove[3], lastMove[4]);

        chessEngine.isWhiteMove = !chessEngine.isWhiteMove;
        toggleButtons(whiteButton, blackButton);

        infoTop.setText(chessEngine.isWhiteMove ? "White Move" : "Black Move");
        infoBottom.setText(chessEngine.isWhiteMove ? "White Move" : "Black Move");

        updateCell(chessEngine.whiteKing[0], chessEngine.whiteKing[1], -2, Color.RED);
        updateCell(chessEngine.blackKing[0], chessEngine.blackKing[1], -2, Color.RED);
        drawOrMateCheck();
    }

    private void toggleButtons(Button whiteButton, Button blackButton) {
        if (chessEngine.history.isEmpty()) {
            whiteButton.setVisibility(View.INVISIBLE);
            blackButton.setVisibility(View.INVISIBLE);
            return;
        }

        if (chessEngine.isWhiteMove) {
            whiteButton.setVisibility(View.VISIBLE);
            blackButton.setVisibility(View.INVISIBLE);
        } else {
            whiteButton.setVisibility(View.INVISIBLE);
            blackButton.setVisibility(View.VISIBLE);
        }
    }

    private void callChooseDialog(byte y1, byte x1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        Object[][] options = {
                {R.string.queen, 9},
                {R.string.knight, 2},
                {R.string.bishop, 3},
                {R.string.rook, 5}
        };

        AlertDialog dialog = builder.setTitle("Choose figure:").create();

        for (Object[] opt : options) {
            Button btn = new Button(this);
            btn.setText(getResources().getText((int) opt[0]));
            btn.setTag(dialog);
            int multiplier = (int) opt[1];

            btn.setOnClickListener(v -> {
                chessEngine.board[y1][x1] = (byte) (multiplier * chessEngine.isNeg(chessEngine.board[y1][x1]));
                updateCell(y1, x1);
                drawOrMateCheck();
                ((AlertDialog) v.getTag()).dismiss();
            });

            layout.addView(btn);
        }

        builder.setView(layout);
        dialog.setView(layout);
        dialog.show();
    }

    private void drawOrMateCheck() {
        byte kingY = chessEngine.isWhiteMove ? chessEngine.whiteKing[0] : chessEngine.blackKing[0];
        byte kingX = chessEngine.isWhiteMove ? chessEngine.whiteKing[1] : chessEngine.blackKing[1];

        String figure = chessEngine.isWhiteMove ? "White" : "Black";
        String oppositeFigure = !chessEngine.isWhiteMove ? "White" : "Black";

        if (chessEngine.isCheck(kingY, kingX)) {
            if (chessEngine.isMate()) {
                endGame(oppositeFigure + " won");
            } else {
                updateCell(kingY, kingX, -1, Color.RED);
                Toast.makeText(this, "Check for " + figure + "!", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (chessEngine.isMate()) {
                endGame("Draw");
            }
        }

        chessEngine.allMoves = chessEngine.getAllPossibleMoves();
    }

    private void endGame(String text) {
        isGameFinished = true;
        infoTop.setText(text);
        infoBottom.setText(text);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void setDotForAllPossibleMoves(byte clickedY, byte clickedX) {
        handleDotForAllPossibleMoves(clickedY, clickedX, true);
    }

    private void removeDotForAllPossibleMoves(byte clickedY, byte clickedX) {
        handleDotForAllPossibleMoves(clickedY, clickedX, false);
    }

    private void handleDotForAllPossibleMoves(byte clickedY, byte clickedX, boolean isSet) {
        for (Map.Entry<byte[], List<byte[]>> entry : chessEngine.allMoves.entrySet()) {
            if (entry.getKey()[0] == clickedY && entry.getKey()[1] == clickedX) {
                for (byte[] value : entry.getValue()) {
                    if (isSet) {
                        updateCell(value[0], value[1], -1, Color.GREEN);
                    } else {
                        updateCell(value[0], value[1], -2, Color.GREEN);
                    }
                }
            }
        }

        if (isSet) {
            updateCell(clickedY, clickedX, -1, Color.YELLOW);
        } else {
            updateCell(clickedY, clickedX, -2, Color.YELLOW);
        }
    }

    private void updateCell(byte y, byte x) {
        updateCell(y, x, 0, Color.GREEN);
    }

    private void updateCell(int y, int x, int img, int color) {
        byte piece = chessEngine.board[y][x];
        ImageView square = squares[y][x];

        if (piece == 0 && img == 0) {
            square.setImageDrawable(null);
            return;
        }

        Integer resId = pieceMap.get(piece);
        if (img == -1) {
            if (resId != null) {
                square.setImageResource(resId);
                square.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                GradientDrawable border = new GradientDrawable();
                border.setColor(Color.TRANSPARENT);
                border.setStroke(5, color);
                square.setForeground(border);
                return;
            }

            resId = R.drawable.dote;
        } else if (img == -2) {
            square.setForeground(null);

            if (resId == null) {
                square.setImageDrawable(null);
                return;
            }

            square.setImageResource(resId);
            square.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            return;
        }

        if (resId != null) {
            square.setImageResource(resId);
            square.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            square.setForeground(null);
        }
    }

    private boolean checkForFigures() {
        List<Byte> figures = new ArrayList<>();
        for (byte y = 0; y < 8; y++) {
            for (byte x = 0; x < 8; x++) {
                if (chessEngine.board[y][x] != 0) {
                    figures.add(chessEngine.board[y][x]);
                }
            }
        }

        if (figures.size() == 2) {
            return true;
        }

        if (figures.size() == 3 ) {
            if (figures.contains((byte) 8) && figures.contains((byte) -8) && figures.contains((byte) 2)) {
                return true;
            }

            return figures.contains((byte) 8) && figures.contains((byte) -8) && figures.contains((byte) 3);
        }

        return false;
    }
}
