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

public class QuizResultActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private LinearLayout resultLayout;
    private TextView feedbackText;
    private int quizId;
    private int studentId;
    private boolean showCorrectAnswers = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        dbHelper = new DatabaseHelper(this);
        resultLayout = findViewById(R.id.resultLayout);
        feedbackText = findViewById(R.id.feedbackText);

        Button toggleCorrectAnswersButton = findViewById(R.id.toggleCorrectAnswersButton);

        quizId = getIntent().getIntExtra("quizId", -1);
        studentId = getIntent().getIntExtra("studentId", -1);

        if (quizId == -1 || studentId == -1) {
            Toast.makeText(this, "Error loading quiz results.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        loadQuizResults();

        // Toggle button for showing/hiding correct answers
        toggleCorrectAnswersButton.setOnClickListener(v -> {
            showCorrectAnswers = !showCorrectAnswers;
            toggleCorrectAnswersButton.setText(showCorrectAnswers ? "Hide Correct Answers" : "Show Correct Answers");
            loadQuizResults(); // Reload results with the updated state
        });
    }

    private void loadQuizResults() {
        resultLayout.removeAllViews(); // Clear existing content
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Fetch questions, answers, and results
        Cursor cursor = db.rawQuery(
                "SELECT q.question_text, m.optionA, m.optionB, m.optionC, m.optionD, m.correctOption, " +
                        "s.selected_option_id, s.is_correct " +
                        "FROM question q " +
                        "INNER JOIN mcq m ON q.question_id = m.question_id " +
                        "INNER JOIN quiz_submission s ON q.question_id = s.question_id " +
                        "WHERE s.user_id = ? AND s.assignment_id = ?",
                new String[]{String.valueOf(studentId), String.valueOf(quizId)}
        );

        if (!cursor.moveToFirst()) {
            Toast.makeText(this, "No results found for the selected quiz.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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
        }

        cursor.close();

        // Fetch feedback
        Cursor feedbackCursor = db.rawQuery(
                "SELECT feedback_text FROM feedback WHERE assignment_id = ? AND user_id = ?",
                new String[]{String.valueOf(quizId), String.valueOf(studentId)}
        );

        if (feedbackCursor.moveToFirst()) {
            String feedback = feedbackCursor.getString(0);
            feedbackText.setText("Feedback: " + feedback);
        } else {
            feedbackText.setText("No feedback available.");
        }

        feedbackCursor.close();
    }

    private void addQuestionToLayout(String questionText, String optionA, String optionB, String optionC,
                                     String optionD, int correctOption, int selectedOption, boolean isCorrect) {
        TextView questionView = new TextView(this);
        questionView.setText(questionText);
        resultLayout.addView(questionView);

        // Display options with conditional coloring
        String[] options = {optionA, optionB, optionC, optionD};
        for (int i = 0; i < options.length; i++) {
            TextView optionView = new TextView(this);
            optionView.setText((i + 1) + ". " + options[i]);

            if ((i + 1) == correctOption && showCorrectAnswers) {
                optionView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else if ((i + 1) == selectedOption && !isCorrect) {
                optionView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }

            resultLayout.addView(optionView);
        }
    }
}
