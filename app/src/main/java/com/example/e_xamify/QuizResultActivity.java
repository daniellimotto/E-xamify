package com.example.e_xamify;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
    private LinearLayout resultLayout;
    private Button toggleCorrectAnswersButton;
    private boolean showCorrectAnswers = false;
    private int attemptId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        attemptId = getIntent().getIntExtra("attemptId", -1);
        if (attemptId == -1) {
            finish();
            return;
        }

        initializeViews();
        loadQuizSummary();
        loadQuizResults();

        toggleCorrectAnswersButton.setOnClickListener(v -> {
            showCorrectAnswers = !showCorrectAnswers;
            toggleCorrectAnswersButton.setText(showCorrectAnswers ? "Hide Correct Answers" : "Show Correct Answers");
            loadQuizResults(); // Reload results to update view
        });
    }

    private void initializeViews() {
        scoreText = findViewById(R.id.scoreText);
        timeUsedText = findViewById(R.id.timeUsedText);
        totalQuestionsText = findViewById(R.id.totalQuestionsText);
        correctAnswersText = findViewById(R.id.correctAnswersText);
        resultLayout = findViewById(R.id.resultLayout);
        toggleCorrectAnswersButton = findViewById(R.id.toggleCorrectAnswersButton);
    }

    private void loadQuizSummary() {
        dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

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

    private void loadQuizResults() {
        resultLayout.removeAllViews();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT q.question_text, m.optionA, m.optionB, m.optionC, m.optionD, m.correctOption, " +
                        "s.selected_option_id, s.is_correct " +
                        "FROM question q " +
                        "INNER JOIN mcq m ON q.question_id = m.question_id " +
                        "INNER JOIN quiz_submission s ON q.question_id = s.question_id " +
                        "WHERE s.attempt_id = ?",
                new String[]{String.valueOf(attemptId)}
        );

        if (cursor.moveToFirst()) {
            do {
                String questionText = cursor.getString(0);
                String optionA = cursor.getString(1);
                String optionB = cursor.getString(2);
                String optionC = cursor.getString(3);
                String optionD = cursor.getString(4);
                int correctOption = cursor.getInt(5);
                int selectedOption = cursor.getInt(6);
                boolean isCorrect = cursor.getInt(7) == 1;

                addQuestionToLayout(questionText, optionA, optionB, optionC, optionD, correctOption, selectedOption, isCorrect);
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "No quiz results found.", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
    }

    private void addQuestionToLayout(String questionText, String optionA, String optionB, String optionC,
                                     String optionD, int correctOption, int selectedOption, boolean isCorrect) {
        TextView questionView = new TextView(this);
        questionView.setText(questionText);
        resultLayout.addView(questionView);

        String[] options = {optionA, optionB, optionC, optionD};
        for (int i = 0; i < options.length; i++) {
            TextView optionView = new TextView(this);
            optionView.setText((i + 1) + ". " + options[i]);

            if (showCorrectAnswers && (i + 1) == correctOption) {
                optionView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else if ((i + 1) == selectedOption && !isCorrect) {
                optionView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }

            resultLayout.addView(optionView);
        }
    }
}
