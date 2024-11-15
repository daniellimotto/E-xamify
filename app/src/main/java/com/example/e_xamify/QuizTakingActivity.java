package com.example.e_xamify;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuizTakingActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private int quizId;
    private int userId;
    private List<Mcq> questions;
    private int currentQuestionIndex = 0;
    private CountDownTimer timer;

    private TextView questionText;
    private RadioGroup optionsGroup;
    private Button nextButton;
    private Button previousButton;
    private TextView timerText;
    private TextView questionNumberText;
    private Button submitButton;

    private int attemptId;
    private List<String> userAnswers;
    private boolean isQuizNavigable;
    private int quizDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_taking);

        quizId = getIntent().getIntExtra("quizId", -1);
        userId = getIntent().getIntExtra("userId", -1);

        if (quizId == -1 || userId == -1) {
            Toast.makeText(this, "Error loading quiz", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        loadQuizQuestions();
        startQuizTimer();
    }

    private void initializeViews() {
        questionText = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);
        nextButton = findViewById(R.id.nextButton);
        previousButton = findViewById(R.id.previousButton);
        timerText = findViewById(R.id.timerText);
        questionNumberText = findViewById(R.id.questionNumberText);

        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> submitQuiz());

        nextButton.setOnClickListener(v -> showNextQuestion());
        previousButton.setOnClickListener(v -> showPreviousQuestion());
    }

    private void loadQuizQuestions() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // First get quiz settings
        Cursor quizCursor = db.rawQuery(
                "SELECT quiz_navigable, quiz_duration FROM quiz WHERE quiz_id = ?",
                new String[]{String.valueOf(quizId)}
        );

        if (quizCursor.moveToFirst()) {
            isQuizNavigable = quizCursor.getInt(0) == 1;
            quizDuration = quizCursor.getInt(1);
        }
        quizCursor.close();

        // Then load questions
        Cursor cursor = db.rawQuery(
                "SELECT q.question_id, q.quiz_id, q.question_num, q.question_text, " +
                        "q.question_type_id, q.question_img_url, m.option_a, m.option_b, " +
                        "m.option_c, m.option_d, m.correct_option " +
                        "FROM question q " +
                        "JOIN mcq m ON q.question_id = m.question_id " +
                        "WHERE q.quiz_id = ? " +
                        "ORDER BY q.question_number",
                new String[]{String.valueOf(quizId)}
        );

        questions = new ArrayList<>();
        userAnswers = new ArrayList<>();

        while (cursor.moveToNext()) {
            Mcq question = new Mcq(
                    cursor.getInt(0),  // question_id
                    cursor.getInt(1),     // quiz_id
                    cursor.getInt(2),     // question_num
                    cursor.getString(3),  // question_text
                    cursor.getInt(4),     // question_type_id
                    cursor.getString(5),  // question_img_url
                    cursor.getString(6),  // option_a
                    cursor.getString(7),  // option_b
                    cursor.getString(8),  // option_c
                    cursor.getString(9),  // option_d
                    cursor.getInt(10)     // correct_option
            );
            questions.add(question);
            userAnswers.add(null); // Initialize with no answer
        }
        cursor.close();

        // Create quiz attempt record
        createQuizAttempt();

        // Show first question
        if (!questions.isEmpty()) {
            showQuestion(0);
        }

        // Configure navigation buttons based on quiz settings
        previousButton.setEnabled(isQuizNavigable);
    }

    private void createQuizAttempt() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quiz_id", quizId);
        values.put("user_id", userId);
        values.put("start_time", getCurrentDateTime());
        values.put("status", "in_progress");

        attemptId = (int) db.insert("quiz_attempt", null, values);
    }

    private void showQuestion(int index) {
        Mcq question = questions.get(index);
        questionNumberText.setText(String.format("Question %d of %d", index + 1, questions.size()));
        questionText.setText(question.getQuestionText());

        optionsGroup.removeAllViews();
        String[] options = {
                question.getOptionA(),
                question.getOptionB(),
                question.getOptionC(),
                question.getOptionD()
        };

        for (int i = 0; i < options.length; i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText(options[i]);
            rb.setId(i);
            optionsGroup.addView(rb);

            // Set previously selected answer if any
            if (userAnswers.get(index) != null &&
                    userAnswers.get(index).equals(options[i])) {
                rb.setChecked(true);
            }
        }

        // Show submit button on last question
        submitButton.setVisibility(
                index == questions.size() - 1 ? View.VISIBLE : View.GONE
        );

        optionsGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                RadioButton selectedButton = findViewById(checkedId);
                userAnswers.set(index, selectedButton.getText().toString());
                saveAnswer(question.getQuestionId(), selectedButton.getText().toString());
            }
        });
    }

    private void saveAnswer(int questionId, String selectedOption) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("attempt_id", attemptId);
        values.put("question_id", questionId);
        values.put("selected_option", selectedOption);

        // Check if answer is correct
        Cursor cursor = db.rawQuery(
                "SELECT correct_option FROM mcq WHERE question_id = ?",
                new String[]{String.valueOf(questionId)}
        );

        if (cursor.moveToFirst()) {
            int correctOption = cursor.getInt(0);
            // Convert selected option to number (1 for A, 2 for B, etc.)
            int selectedOptionNum = getOptionNumber(selectedOption);
            values.put("is_correct", selectedOptionNum == correctOption ? 1 : 0);
        }
        cursor.close();

        db.insert("student_answer", null, values);
    }

    private int getOptionNumber(String option) {
        for (int i = 0; i < questions.get(currentQuestionIndex).getOptionA().length(); i++) {
            if (option.equals(questions.get(currentQuestionIndex).getOptionA())) return 1;
            if (option.equals(questions.get(currentQuestionIndex).getOptionB())) return 2;
            if (option.equals(questions.get(currentQuestionIndex).getOptionC())) return 3;
            if (option.equals(questions.get(currentQuestionIndex).getOptionD())) return 4;
        }
        return -1;
    }

    private void startQuizTimer() {
        timer = new CountDownTimer(quizDuration * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = millisUntilFinished / 60000;
                long seconds = (millisUntilFinished % 60000) / 1000;
                timerText.setText(String.format("Time remaining: %02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                submitQuiz();
            }
        }.start();
    }

    private void submitQuiz() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Calculate score
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM student_answer WHERE attempt_id = ? AND is_correct = 1",
                new String[]{String.valueOf(attemptId)}
        );

        int score = 0;
        if (cursor.moveToFirst()) {
            score = cursor.getInt(0);
        }
        cursor.close();

        // Update quiz attempt
        ContentValues values = new ContentValues();
        values.put("end_time", getCurrentDateTime());
        values.put("score", score);
        values.put("status", "completed");

        db.update("quiz_attempt", values, "attempt_id = ?",
                new String[]{String.valueOf(attemptId)});

        // Show results
        Intent intent = new Intent(this, QuizResultActivity.class);
        intent.putExtra("attemptId", attemptId);
        startActivity(intent);
        finish();
    }

    private String getCurrentDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }

    private void showNextQuestion() {
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            showQuestion(currentQuestionIndex);
        }
    }

    private void showPreviousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            showQuestion(currentQuestionIndex);
        }
    }
}