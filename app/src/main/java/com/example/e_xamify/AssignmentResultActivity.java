package com.example.e_xamify;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.content.Intent;
import android.widget.Button;


public class AssignmentResultActivity extends AppCompatActivity {
    private int assignmentId;
    private int user_id;
    private TextView resultText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_result);
        assignmentId = getIntent().getIntExtra("assignmentId", -1);
        user_id = getIntent().getIntExtra("user_id", -1);
        if (assignmentId == -1 || user_id == -1) {
            finish();
            return;
        }
        dbHelper = new DatabaseHelper(this);
        resultText = findViewById(R.id.resultText);
        calculateAndDisplayScore();
        Button returnToDashboardButton = findViewById(R.id.returnToDashboardButton);
        returnToDashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(AssignmentResultActivity.this, StudentDashboardActivity.class);
            intent.putExtra("user_id", user_id);
            startActivity(intent);
            finish();
        });
    }

    private void calculateAndDisplayScore() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor correctCursor = db.rawQuery(
                "SELECT COUNT(*) FROM quiz_submission WHERE assignment_id = ? AND is_correct = 1",
                new String[]{String.valueOf(assignmentId)}
        );
        int correctCount = 0;
        if (correctCursor.moveToFirst()) {
            correctCount = correctCursor.getInt(0);
        }
        correctCursor.close();
        Cursor quizCursor = db.rawQuery(
                "SELECT quiz_id FROM assignment WHERE assignment_id = ?",
                new String[]{String.valueOf(assignmentId)}
        );
        int quizId = -1;
        if (quizCursor.moveToFirst()) {
            quizId = quizCursor.getInt(0);
        }
        quizCursor.close();
        Cursor questionCursor = db.rawQuery(
                "SELECT COUNT(*) FROM question WHERE quiz_id = ?",
                new String[]{String.valueOf(quizId)}
        );
        int totalQuestions = 0;
        if (questionCursor.moveToFirst()) {
            totalQuestions = questionCursor.getInt(0);
        }
        questionCursor.close();
        int score = (int) (((double) correctCount / totalQuestions) * 100);
        resultText.setText("You scored " + score + "%");
        ContentValues values = new ContentValues();
        values.put("end_time", getCurrentDateTime());
        values.put("score", score);
        values.put("status", "completed");
        db.update("quiz_attempt", values, "quiz_id = ? AND user_id = ? AND status = 'pending'",
                new String[]{String.valueOf(quizId), String.valueOf(user_id)});
    }
    private String getCurrentDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}