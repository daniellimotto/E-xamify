package com.example.e_xamify;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class QuizDetailsActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private int quizId;
    private int userId;
    private TextView quizTitleText;
    private TextView instructionsText;
    private TextView durationText;
    private TextView attemptsText;
    private Button startQuizButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_details);

        // Initialize views
        quizTitleText = findViewById(R.id.quizTitleText);
        instructionsText = findViewById(R.id.instructionsText);
        durationText = findViewById(R.id.durationText);
        attemptsText = findViewById(R.id.attemptsText);
        startQuizButton = findViewById(R.id.startQuizButton);

        // Get quiz ID and user ID from intent
        quizId = getIntent().getIntExtra("quizId", -1);
        userId = getIntent().getIntExtra("userId", -1);

        if (quizId == -1 || userId == -1) {
            Toast.makeText(this, "Error loading quiz details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        loadQuizDetails();

        startQuizButton.setOnClickListener(v -> startQuiz());
    }

    private void loadQuizDetails() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT quiz_title, instructions, quiz_duration, quiz_attempts FROM quiz WHERE quiz_id = ?",
                new String[]{String.valueOf(quizId)}
        );

        if (cursor.moveToFirst()) {
            String title = cursor.getString(0);
            String instructions = cursor.getString(1);
            int duration = cursor.getInt(2);
            int attempts = cursor.getInt(3);

            quizTitleText.setText(title);
            instructionsText.setText(instructions);
            durationText.setText(String.format("Duration: %d minutes", duration));
            attemptsText.setText(attempts == -1 ? "Attempts: Unlimited" :
                    String.format("Attempts: %d", attempts));
        }
        cursor.close();
    }

    private void startQuiz() {
        Intent intent = new Intent(this, QuizTakingActivity.class);
        intent.putExtra("quizId", quizId);
        intent.putExtra("userId", userId);
        startActivity(intent);
        finish();
    }
}