package com.example.e_xamify;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class AssignmentTakingActivity extends AppCompatActivity {
    private int assignmentId;
    private int user_id;
    private int quizId;
    private TextView questionText;
    private RadioGroup optionsGroup;
    private Button nextButton;
    private Button previousButton;
    private Button submitButton;
    private List<Mcq> questions;
    private int currentQuestionIndex = 0;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_taking);

        assignmentId = getIntent().getIntExtra("assignmentId", -1);
        user_id = getIntent().getIntExtra("user_id", -1);

        if (assignmentId == -1 || user_id == -1) {
            Toast.makeText(this, "Error loading assignment", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        quizId = getQuizIdForAssignment(assignmentId);

        initializeViews();
        loadQuestions();
        showQuestion(currentQuestionIndex);
    }

    private void initializeViews() {
        questionText = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);
        nextButton = findViewById(R.id.nextButton);
        previousButton = findViewById(R.id.previousButton);
        submitButton = findViewById(R.id.submitButton);

        nextButton.setOnClickListener(v -> {
            saveSelectedOption();
            showNextQuestion();
        });
        previousButton.setOnClickListener(v -> showPreviousQuestion());
        submitButton.setOnClickListener(v -> {
            saveSelectedOption();
            submitAssignment();
        });
    }

    private int getQuizIdForAssignment(int assignmentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT quiz_id FROM assignment WHERE assignment_id = ?", new String[]{String.valueOf(assignmentId)});
        int quizId = -1;
        if (cursor.moveToFirst()) {
            quizId = cursor.getInt(0);
        }
        cursor.close();
        return quizId;
    }

    private void loadQuestions() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT q.question_id, q.question_text, m.optionA, m.optionB, m.optionC, m.optionD, m.correctOption " +
                        "FROM question q " +
                        "JOIN mcq m ON q.question_id = m.question_id " +
                        "WHERE q.quiz_id = ?",
                new String[]{String.valueOf(quizId)}
        );
        questions = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String text = cursor.getString(1);
            String optionA = cursor.getString(2);
            String optionB = cursor.getString(3);
            String optionC = cursor.getString(4);
            String optionD = cursor.getString(5);
            int correctOption = cursor.getInt(6);
            questions.add(new Mcq(id, quizId, 0, text, 0, null, optionA, optionB, optionC, optionD, correctOption));
        }
        cursor.close();
    }

    private void showQuestion(int index) {
        if (index < 0 || index >= questions.size()) return;

        Mcq question = questions.get(index);
        questionText.setText(question.getQuestionText());

        optionsGroup.removeAllViews();
        RadioButton optionA = new RadioButton(this);
        optionA.setText(question.getOptionA());
        optionsGroup.addView(optionA);

        RadioButton optionB = new RadioButton(this);
        optionB.setText(question.getOptionB());
        optionsGroup.addView(optionB);

        RadioButton optionC = new RadioButton(this);
        optionC.setText(question.getOptionC());
        optionsGroup.addView(optionC);

        RadioButton optionD = new RadioButton(this);
        optionD.setText(question.getOptionD());
        optionsGroup.addView(optionD);

        loadSelectedOption(question.getQuestionId());

        // Show submit button on last question
        if (index == questions.size() - 1) {
            nextButton.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
        }
    }

    private void loadSelectedOption(int questionId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT selected_option_id FROM quiz_submission WHERE assignment_id = ? AND question_id = ?", new String[]{String.valueOf(assignmentId), String.valueOf(questionId)});
        if (cursor.moveToFirst()) {
            int selectedOptionId = cursor.getInt(0) + 1;
            ((RadioButton) optionsGroup.getChildAt(selectedOptionId)).setChecked(true);
        }
        cursor.close();
    }

    private void saveSelectedOption() {
        int selectedOptionId = optionsGroup.indexOfChild(findViewById(optionsGroup.getCheckedRadioButtonId()));
        if (selectedOptionId == -1) return;

        Mcq currentQuestion = questions.get(currentQuestionIndex);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("assignment_id", assignmentId);
        values.put("question_id", currentQuestion.getQuestionId());
        values.put("selected_option_id", selectedOptionId);
        values.put("user_id", user_id);
        if (selectedOptionId == currentQuestion.getCorrectOption()) values.put("is_correct", 1);

        db.insertWithOnConflict("quiz_submission", null, values, SQLiteDatabase.CONFLICT_REPLACE);
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

    private void submitAssignment() {
        // Logic to submit the assignment
        Toast.makeText(this, "Assignment submitted!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, AssignmentResultActivity.class);
        intent.putExtra("assignmentId", assignmentId);
        intent.putExtra("user_id", user_id);
        startActivity(intent);
        finish();
    }
}