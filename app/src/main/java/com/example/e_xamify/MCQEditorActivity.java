package com.example.e_xamify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
    private List<Question> questions;
    private int quiz_id;
    private String quizTitle;
    private int quiz_type_id;
    private int currentQuestionIndex = 0;
    private int question_type_id = 1; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcq_editor);

        // Retrieve quiz ID and title from Intent
        quiz_id = getIntent().getIntExtra("quiz_id", -1);
        Log.d("MCQEditorActivity", "Received quiz_id: " + getIntent().getIntExtra("quiz_id", -1));
        Log.d("MCQEditorActivity", "Received quiz_title: " + getIntent().getStringExtra("quiz_title"));
        Log.d("MCQEditorActivity", "Received quiz_type_id: " + getIntent().getIntExtra("quiz_type_id", -1));

        String quizTitle = getIntent().getStringExtra("quiz_title");

//        Cursor cursor = db.rawQuery("SELECT quiz_id FROM quiz WHERE quiz_id = ?", new String[]{String.valueOf(quiz_id)});
//        if (cursor.getCount() == 0) {
//            Log.e("DatabaseHelper", "quiz_id " + quiz_id + " does not exist in quiz table");
//        }


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
//        prevButton.setOnClickListener(v -> navigateToPreviousQuestion());
//        nextButton.setOnClickListener(v -> navigateToNextQuestion());
//        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());
//
//        // Display the first question
//        displayQuestion(currentQuestionIndex);

    }

    private int saveQuestion() {
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

            }
            try {
                // Check if the quiz_id exists in the quiz table
                Cursor cursor = db.rawQuery("SELECT quiz_id FROM quiz WHERE quiz_id = ?", new String[]{String.valueOf(quiz_id)});
                if (!cursor.moveToFirst()) {
                    Toast.makeText(this, "Quiz ID not found. Please create a quiz first.", Toast.LENGTH_LONG).show();
                    cursor.close();
                    return -1;
                }
                cursor.close();

                // Insert question into the question table
            ContentValues questionValues = new ContentValues();
            questionValues.put("quiz_id", quiz_id);
            questionValues.put("question_text", questionText);
            questionValues.put("question_type_id", question_type_id); // Assuming 1 is the ID for MCQ type
            long questionId = db.insert("question", null, questionValues);

                if (questionId == -1) {
                    throw new Exception("Failed to insert question");
                }

            // Insert options into the mcq table
            ContentValues mcqValues = new ContentValues();
            mcqValues.put("question_id", questionId);
            mcqValues.put("optionA", optionA);
            mcqValues.put("optionB", optionB);
            mcqValues.put("optionC", optionC);
            mcqValues.put("optionD", optionD);
            mcqValues.put("correctOption", selectedCorrectOptionId);
            long mcqId = db.insert("mcq", null, mcqValues);

            if (mcqId == -1) {
                throw new Exception("Failed to insert MCQ options");
            }

            Toast.makeText(this, "Question saved", Toast.LENGTH_SHORT).show();

            // Update the current question with the saved data
//            if (currentQuestionIndex < questions.size()) {
//                Mcq currentQuestion = (Mcq) questions.get(currentQuestionIndex);
//                currentQuestion.setQuestionText(questionText);
//                currentQuestion.setOptionA(optionA);
//                currentQuestion.setOptionB(optionB);
//                currentQuestion.setOptionC(optionC);
//                currentQuestion.setOptionD(optionD);
//                currentQuestion.setCorrectOption(getCorrectOption());
//                currentQuestion.setQuestionNum(currentQuestionIndex + 1); // Update question number
//            }
//
//            // Navigate to the next question or create a new one
//            if (currentQuestionIndex < questions.size() - 1) {
//                currentQuestionIndex++;
//                displayQuestion(currentQuestionIndex);
//            } else {
//                // Create a new question
//                currentQuestionIndex = questions.size();
//                Mcq newQuestion = new Mcq();
//                currentQuestionIndex++;
//                newQuestion.setQuestionNum(currentQuestionIndex); // Set question number for new question
//                questions.add(newQuestion);
//                clearInputs();
//                //updateQuestionNumber();
//            }
        } catch (Exception e) {
                Log.e("SaveQuestionError", "Error saving question: ", e);
                Toast.makeText(this, "Error saving question: " + e.getMessage(), Toast.LENGTH_LONG).show();
                return -1;
        }
        return 1;
    }
    return -1;
}

    private int getCorrectOption() {
        if (selectedCorrectOptionId == R.id.optionARadio) {
            return 1;
        } else if (selectedCorrectOptionId == R.id.optionBRadio) {
            return 2;
        } else if (selectedCorrectOptionId == R.id.optionCRadio) {
            return 3;
        } else if (selectedCorrectOptionId == R.id.optionDRadio) {
            return 4;
        } else {
            return -1; // No option selected
        }
    }

    private void displayQuestion(int index) {
        if (index >= 0 && index < questions.size()) {
            Mcq question = (Mcq) questions.get(index);
            questionInput.setText(question.getQuestionText());
            optionAInput.setText(question.getOptionA());
            optionBInput.setText(question.getOptionB());
            optionCInput.setText(question.getOptionC());
            optionDInput.setText(question.getOptionD());
            int correctOption = question.getCorrectOption();
            if (correctOption == 1) {
                correctOptionGroup.check(R.id.optionARadio);
            } else if (correctOption == 2) {
                correctOptionGroup.check(R.id.optionBRadio);
            } else if (correctOption == 3) {
                correctOptionGroup.check(R.id.optionCRadio);
            } else if (correctOption == 4) {
                correctOptionGroup.check(R.id.optionDRadio);
            } else {
                correctOptionGroup.clearCheck();
            }
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

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Question")
                .setMessage("Are you sure you want to delete this question?")
                .setPositiveButton("Yes", (dialog, which) -> deleteCurrentQuestion())
                .setNegativeButton("No", null)
                .show();
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
        if (currentQuestionIndex < questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex);
            questionNumber.setText("Question " + currentQuestion.getQuestionNum());
        }
    }

    @Override
    protected void onDestroy() {
        // Close the database when the activity is destroyed
        db.close();
        super.onDestroy();
    }
}
