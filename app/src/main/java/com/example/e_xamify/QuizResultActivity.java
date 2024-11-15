package com.example.e_xamify;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class QuizResultActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private TextView scoreText;
    private TextView timeUsedText;
    private TextView totalQuestionsText;
    private TextView correctAnswersText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        int attemptId = getIntent().getIntExtra("attemptId", -1);
        if (attemptId == -1) {
            finish();
            return;
        }

        initializeViews();
        loadQuizResults(attemptId);
    }

    private void initializeViews() {
        scoreText = findViewById(R.id.scoreText);
        timeUsedText = findViewById(R.id.timeUsedText);
        totalQuestionsText = findViewById(R.id.totalQuestionsText);
        correctAnswersText = findViewById(R.id.correctAnswersText);
    }

    private void loadQuizResults(int attemptId) {
        dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Get attempt details
        Cursor attemptCursor = db.rawQuery(
                "SELECT qa.score, qa.start_time, qa.end_time, q.quiz_duration " +
                        "FROM quiz_attempt qa " +
                        "JOIN quiz q ON qa.quiz_id = q.quiz_id " +
                        "WHERE qa.attempt_id = ?",
                new String[]{String.valueOf(attemptId)}
        );

        if (attemptCursor.moveToFirst()) {
            int score = attemptCursor.getInt(0);
            String startTime = attemptCursor.getString(1);
            String endTime = attemptCursor.getString(2);
            int quizDuration = attemptCursor.getInt(3);

            // Calculate time used
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                Date start = sdf.parse(startTime);
                Date end = sdf.parse(endTime);
                long timeUsedMillis = end.getTime() - start.getTime();
                long timeUsedMinutes = timeUsedMillis / (60 * 1000);
                long timeLeftMinutes = quizDuration - timeUsedMinutes;

                timeUsedText.setText(String.format("Time Used: %d minutes\nTime Left: %d minutes",
                        timeUsedMinutes, timeLeftMinutes));
            } catch (ParseException e) {
                timeUsedText.setText("Error calculating time");
            }

            // Get total questions and correct answers
            Cursor questionsCursor = db.rawQuery(
                    "SELECT COUNT(*) as total, SUM(CASE WHEN is_correct = 1 THEN 1 ELSE 0 END) as correct " +
                            "FROM student_answer WHERE attempt_id = ?",
                    new String[]{String.valueOf(attemptId)}
            );

            if (questionsCursor.moveToFirst()) {
                int totalQuestions = questionsCursor.getInt(0);
                int correctAnswers = questionsCursor.getInt(1);

                scoreText.setText(String.format("Score: %d%%", score));
                totalQuestionsText.setText(String.format("Total Questions: %d", totalQuestions));
                correctAnswersText.setText(String.format("Correct Answers: %d", correctAnswers));
            }
            questionsCursor.close();
        }
        attemptCursor.close();
    }
}