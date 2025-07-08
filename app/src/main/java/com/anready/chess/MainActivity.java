package com.anready.chess;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupUI();
    }

    private void setupUI() {
        Button startGameButton = findViewById(R.id.startGameButton);
        startGameButton.setOnClickListener(v -> showTimeSelectionDialog());
    }

    private void showTimeSelectionDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_time_selection, null);
        Spinner timeSpinner = dialogView.findViewById(R.id.timeSpinner);
        Spinner incrementSpinner = dialogView.findViewById(R.id.incrementSpinner);

        String[] timeOptions = {"No Time", "30 seconds", "1 minute", "2 minutes", "5 minutes", "10 minutes",
                "15 minutes", "20 minutes", "25 minutes", "30 minutes", "45 minutes", "60 minutes"};
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, timeOptions);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpinner.setAdapter(timeAdapter);

        String[] incrementOptions = {"+0", "+1s", "+2s", "+5s",
                "+10s", "+20s", "+30s", "+60s"};
        ArrayAdapter<String> incrementAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, incrementOptions);
        incrementAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        incrementSpinner.setAdapter(incrementAdapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Choose playing time")
                .setView(dialogView)
                .setPositiveButton("Start game", (dialogInterface, i) -> {
                    String selectedTime = timeSpinner.getSelectedItem().toString();
                    String selectedIncrement = incrementSpinner.getSelectedItem().toString();

                    long timeInMs = convertTimeToMilliseconds(selectedTime);
                    long incrementInMs = convertIncrementToMilliseconds(selectedIncrement);

                    startGame(timeInMs, incrementInMs);
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    private long convertTimeToMilliseconds(String timeString) {
        switch (timeString) {
            case "No Time":
                return 0;
            case "30 seconds":
                return 30 * 1000L;
            case "1 minute":
                return 60 * 1000L;
            case "2 minutes":
                return 2 * 60 * 1000L;
            case "5 minutes":
                return 5 * 60 * 1000L;
            case "15 minutes":
                return 15 * 60 * 1000L;
            case "20 minutes":
                return 20 * 60 * 1000L;
            case "25 minutes":
                return 25 * 60 * 1000L;
            case "30 minutes":
                return 30 * 60 * 1000L;
            case "45 minutes":
                return 45 * 60 * 1000L;
            case "60 minutes":
                return 60 * 60 * 1000L;
            default:
                return 10 * 60 * 1000L;
        }
    }

    private long convertIncrementToMilliseconds(String incrementString) {
        switch (incrementString) {
            case "+1s":
                return 1000L;
            case "+2s":
                return 2 * 1000L;
            case "+5s":
                return 5 * 1000L;
            case "+10s":
                return 10 * 1000L;
            case "+20s":
                return 20 * 1000L;
            case "+30s":
                return 30 * 1000L;
            case "+60s":
                return 60 * 1000L;
            default:
                return 0;
        }
    }

    private void startGame(long timeInMs, long incrementInMs) {
        String timeDisplay = formatTime(timeInMs);
        String incrementDisplay = formatIncrement(incrementInMs);

        String message = "Game starting!\nTime: " + timeDisplay + "\nIncrement: " + incrementDisplay;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, ChessBoard.class);
        intent.putExtra("time", timeInMs);
        intent.putExtra("increment", incrementInMs);
        startActivity(intent);
    }

    private String formatTime(long timeInMs) {
        long seconds = timeInMs / 1000;
        if (seconds < 60) {
            return seconds + " seconds";
        } else {
            long minutes = seconds / 60;
            return minutes + " minute" + (minutes == 1 ? "" : "s");
        }
    }

    private String formatIncrement(long incrementInMs) {
        if (incrementInMs == 0) {
            return "+0";
        } else {
            long seconds = incrementInMs / 1000;
            return "+" + seconds + "s";
        }
    }
}