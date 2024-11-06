package com.example.e_xamify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MCQEditorActivity extends AppCompatActivity {
    private EditText questionInput;
    private EditText optionAInput;
    private EditText optionBInput;
    private EditText optionCInput;
    private EditText optionDInput;
    private RadioGroup correctOptionGroup;
    private TextView quizTitleTextView;
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private int selectedCorrectOptionId = -1;
    private TextView questionNumber;
    private Button prevButton, nextButton, deleteButton;
    private int currentQuestionIndex = 1;
    private List<Question> questions;
    private int quiz_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcq_editor);

        // Retrieve quiz ID and title from Intent
        quiz_id = getIntent().getIntExtra("quiz_id", -1);
        String quizTitle = getIntent().getStringExtra("quiz_title");

        // Initialize UI components
        quizTitleTextView = findViewById(R.id.quizTitleTextView);
        questionNumber = findViewById(R.id.questionNumber);

        // Set quiz title
        if (quizTitle != null) {
            quizTitleTextView.setText(quizTitle);
        }

        // Initialize the database
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // Initialize UI components
        questionInput = findViewById(R.id.questionInput);
        optionAInput = findViewById(R.id.optionAInput);
        optionBInput = findViewById(R.id.optionBInput);
        optionCInput = findViewById(R.id.optionCInput);
        optionDInput = findViewById(R.id.optionDInput);
        correctOptionGroup = findViewById(R.id.correctOptionGroup);
        correctOptionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedCorrectOptionId = checkedId; // Update the selected option ID
            }
        });

        // Set up save button click listener
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQuestion();
            }
        });

        // Load questions from the database
        questions = loadQuestionsFromDatabase();

        // If no questions exist, initialize with a default question
        if (questions.isEmpty()) {
            questions.add(new Question());
        }

        // Set up button listeners
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        deleteButton = findViewById(R.id.deleteButton);
        prevButton.setOnClickListener(v -> navigateToPreviousQuestion());
        nextButton.setOnClickListener(v -> navigateToNextQuestion());
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());

        // Display the first question
        displayQuestion(currentQuestionIndex);
    }

    private void saveQuestion() {
        // Get values from input fields
        String questionText = questionInput.getText().toString();
        String optionA = optionAInput.getText().toString();
        String optionB = optionBInput.getText().toString();
        String optionC = optionCInput.getText().toString();
        String optionD = optionDInput.getText().toString();

        // Validate inputs
        if (questionText.isEmpty() || optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            // Check if a correct option is selected
            if (selectedCorrectOptionId == -1) {
                Toast.makeText(this, "Please select the correct option", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert question into the database
            try {
                ContentValues questionValues = new ContentValues();
                questionValues.put("quiz_id", quiz_id);
                questionValues.put("question_text", questionText);
                questionValues.put("question_type_id", 1); // Assuming 1 is the ID for MCQ type
                long questionId = db.insert("question", null, questionValues);

                if (questionId == -1) {
                    throw new Exception("Failed to insert question");
                }

                // Insert options into the database
                insertOption(questionId, optionA, selectedCorrectOptionId == R.id.optionARadio ? 1 : 0);
                insertOption(questionId, optionB, selectedCorrectOptionId == R.id.optionBRadio ? 1 : 0);
                insertOption(questionId, optionC, selectedCorrectOptionId == R.id.optionCRadio ? 1 : 0);
                insertOption(questionId, optionD, selectedCorrectOptionId == R.id.optionDRadio ? 1 : 0);

                Toast.makeText(this, "Question saved", Toast.LENGTH_SHORT).show();

                // Update the current question with the saved data
                if (currentQuestionIndex < questions.size()) {
                    Question currentQuestion = questions.get(currentQuestionIndex);
                    currentQuestion.setQuestionText(questionText);
                    currentQuestion.setOptionA(optionA);
                    currentQuestion.setOptionB(optionB);
                    currentQuestion.setOptionC(optionC);
                    currentQuestion.setOptionD(optionD);
                    currentQuestion.setCorrectOption(selectedCorrectOptionId);
                }

                // Navigate to the next question or create a new one
                if (currentQuestionIndex < questions.size() - 1) {
                    currentQuestionIndex++;
                    displayQuestion(currentQuestionIndex);
                } else {
                    // Create a new question
                    currentQuestionIndex = questions.size();
                    questions.add(new Question());
                    clearInputs();
                    updateQuestionNumber();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error saving question: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void insertOption(long questionId, String optionText, int isCorrect) {
        ContentValues optionValues = new ContentValues();
        optionValues.put("question_id", questionId);
        optionValues.put("option_text", optionText);
        optionValues.put("is_correct", isCorrect);
        db.insert("mcq", null, optionValues);
    }

    private void displayQuestion(int index) {
        if (index >= 0 && index < questions.size()) {
            Question question = questions.get(index);
            questionInput.setText(question.getQuestionText());
            optionAInput.setText(question.getOptionA());
            optionBInput.setText(question.getOptionB());
            optionCInput.setText(question.getOptionC());
            optionDInput.setText(question.getOptionD());
            correctOptionGroup.check(question.getCorrectOption());
            updateQuestionNumber();
        }
    }

    private void navigateToPreviousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            displayQuestion(currentQuestionIndex);
        }
    }

    private void navigateToNextQuestion() {
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            displayQuestion(currentQuestionIndex);
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Delete Question")
            .setMessage("Are you sure you want to delete this question?")
            .setPositiveButton("Yes", (dialog, which) -> deleteCurrentQuestion())
            .setNegativeButton("No", null)
            .show();
    }

    private void deleteCurrentQuestion() {
        if (questions.size() > 0) {
            questions.remove(currentQuestionIndex);
            // Update database to remove the question
            // db.delete("question", "id=?", new String[]{String.valueOf(question.getId())});
            if (currentQuestionIndex >= questions.size()) {
                currentQuestionIndex = questions.size() - 1;
            }
            displayQuestion(currentQuestionIndex);
        }
    }

    private List<Question> loadQuestionsFromDatabase() {
        // Load questions from the database and return as a list
        // This is a placeholder for actual database loading logic
        return new ArrayList<>();
    }

    private void clearInputs() {
        questionInput.setText("");
        optionAInput.setText("");
        optionBInput.setText("");
        optionCInput.setText("");
        optionDInput.setText("");
        correctOptionGroup.clearCheck();
    }

    private void updateQuestionNumber() {
        questionNumber.setText("Question " + (currentQuestionIndex + 1));
    }

    @Override
    protected void onDestroy() {
        // Close the database when the activity is destroyed
        db.close();
        super.onDestroy();
    }
}
