package com.example.e_xamify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
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

import android.content.Intent;

public class MCQEditorActivity extends AppCompatActivity {

    private EditText questionInput;
    private TextView questionNumberTextView;
    private EditText optionAInput;
    private EditText optionBInput;
    private EditText optionCInput;
    private EditText optionDInput;
    private RadioGroup correctOptionGroup;
    private TextView quizTitleTextView;
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;
    private int selectedCorrectOptionId = -1;
    private Button prevButton, nextButton, deleteButton, completeButton;
    private List<Question> questions;
    private int quiz_id;
    private int questionNum = 1; // Initialize question number to 1
    private int question_type_id = 1; 
    private int user_id; // Add a variable to store the user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcq_editor);

        quiz_id = getIntent().getIntExtra("quiz_id", -1);
        user_id = getIntent().getIntExtra("user_id", -1); // Retrieve the user ID from the intent



        String quizTitle = getIntent().getStringExtra("quiz_title");

        // Initialize UI components
        quizTitleTextView = findViewById(R.id.quizTitleTextView); // Ensure this is initialized
        questionNumberTextView = findViewById(R.id.questionNumber);

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

        questions = new ArrayList<>(); // Initialize the questions list

        // If no questions exist, initialize with a default question
        if (questions.isEmpty()) {
            questions.add(new Question());
        }

        // Set up button listeners
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        deleteButton = findViewById(R.id.deleteButton);
        completeButton = findViewById(R.id.completeButton); // Initialize the complete button
        prevButton.setOnClickListener(v -> navigateToPreviousQuestion());
        nextButton.setOnClickListener(v -> navigateToNextQuestion());
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());
        completeButton.setOnClickListener(v -> completeQuiz()); // Set up click listener for complete button

        // Display the first question
        //displayQuestion(questionNum);
        questionNumberTextView.setText("Question " + questionNum); // Update question number // Ensure question number is displayed initially

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
            return -1;
        } else if (optionA.equals(optionB) || optionA.equals(optionC) || optionA.equals(optionD) ||
                optionB.equals(optionC) || optionB.equals(optionD) || optionC.equals(optionD)) {
            Toast.makeText(this, "Options A to D must be unique", Toast.LENGTH_SHORT).show();
            return -1;
        } else if (selectedCorrectOptionId == -1) {
            Toast.makeText(this, "Please select the correct option", Toast.LENGTH_SHORT).show();
            return -1;
        }

        int questionId = -1;  // Initialize questionId
        try {
            // Insert or update question in the question table
            ContentValues questionValues = new ContentValues();
            questionValues.put("quiz_id", quiz_id);
            questionValues.put("question_text", questionText);
            questionValues.put("question_type_id", question_type_id);
            questionValues.put("question_number", questionNum);

            Cursor cursor = db.rawQuery("SELECT question_id FROM question WHERE question_number = ?", new String[]{String.valueOf(questionNum)});
            if (cursor.moveToFirst()) {
                // If the question exists, get its question_id
                questionId = cursor.getInt(cursor.getColumnIndex("question_id"));
                db.update("question", questionValues, "question_id = ?", new String[]{String.valueOf(questionId)});
            } else {
                // Insert new question and retrieve its ID
                long insertedQuestionId = db.insert("question", null, questionValues);
                if (insertedQuestionId == -1) {
                    throw new Exception("Failed to insert question into the question table");
                }
                questionId = (int) insertedQuestionId;
            }
            cursor.close();

            if (questionId == -1) {
                throw new Exception("Question ID is invalid after insertion.");
            }

            // Proceed with inserting or updating options in mcq table
            ContentValues mcqValues = new ContentValues();
            mcqValues.put("question_id", questionId);
            mcqValues.put("optionA", optionA);
            mcqValues.put("optionB", optionB);
            mcqValues.put("optionC", optionC);
            mcqValues.put("optionD", optionD);
            mcqValues.put("correctOption", getCorrectOption());

            cursor = db.rawQuery("SELECT * FROM mcq WHERE question_id = ?", new String[]{String.valueOf(questionId)});
            if (cursor.moveToFirst()) {
                db.update("mcq", mcqValues, "question_id = ?", new String[]{String.valueOf(questionId)});
                Log.d("SaveQuestion", "MCQ options updated for question_id: " + questionId);
            } else {
                db.insert("mcq", null, mcqValues);
                Log.d("SaveQuestion", "MCQ options inserted for question_id: " + questionId);
            }
            cursor.close();

            Toast.makeText(this, "Question and options saved successfully with Question ID: " + questionId, Toast.LENGTH_SHORT).show();

            // Update the current question in the list and set the questionId for reference
            Question currentQuestion;
            if (questionNum <= questions.size()) {
                currentQuestion = questions.get(questionNum - 1);
            } else {
                currentQuestion = new Question();
                questions.add(currentQuestion);
            }

            currentQuestion.setQuestionText(questionText);
            currentQuestion.setQuestionNum(questionNum);
            currentQuestion.setQuestionId(questionId);
            clearInputs();

            if (questionNum < questions.size()) {
                questionNum++;
                displayQuestion(questionNum);
            } else {
                Question newQuestion = new Question();
                newQuestion.setQuestionNum(questionNum + 1);
                questions.add(newQuestion);
                questionNum++;
                questionNumberTextView.setText("Question " + questionNum);
            }
        } catch (Exception e) {
            Log.e("SaveQuestionError", "Error saving question: ", e);
            Toast.makeText(this, "Error saving question: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return -1;
        }
        return 1;
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

private void displayQuestion(int questionNum) {
    if (questionNum > 0 && questionNum <= questions.size()) {
        Question question = questions.get(questionNum-1);
        questionInput.setText(question.getQuestionText());
        questionNumberTextView.setText("Question " + question.getQuestionNum()); // Update question number

        // Retrieve the relevant MCQ fields
        Cursor cursor = db.rawQuery("SELECT * FROM mcq WHERE question_id = ?", new String[]{String.valueOf(question.getQuestionId())});
        if (cursor.moveToFirst()) {
            String optionA = cursor.getString(cursor.getColumnIndex("optionA"));
            String optionB = cursor.getString(cursor.getColumnIndex("optionB"));
            String optionC = cursor.getString(cursor.getColumnIndex("optionC"));
            String optionD = cursor.getString(cursor.getColumnIndex("optionD"));
            int correctOption = cursor.getInt(cursor.getColumnIndex("correctOption"));

            optionAInput.setText(optionA);
            optionBInput.setText(optionB);
            optionCInput.setText(optionC);
            optionDInput.setText(optionD);

            Log.d("DisplayQuestion", "Option A: " + optionA);
            Log.d("DisplayQuestion", "Option B: " + optionB);
            Log.d("DisplayQuestion", "Option C: " + optionC);
            Log.d("DisplayQuestion", "Option D: " + optionD);
            Log.d("DisplayQuestion", "Correct Option: " + correctOption);

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
        } else {
            Log.e("DisplayQuestionError", "No MCQ found for question_id: " + question.getQuestionId());
        }
        cursor.close();
    } else {
        Log.e("DisplayQuestionError", "Invalid question number: " + questionNum);
    }
}

    private void navigateToPreviousQuestion() {
        if (questionNum > 1) {
            questionNum--;
            displayQuestion(questionNum);
        }
    }

    private void navigateToNextQuestion() {
        if (questionNum < questions.size()) {
            questionNum++;
            //clearInputs();
            displayQuestion(questionNum);
        }
    }

    private void deleteCurrentQuestion() {
        if (questions.size() > 0) {
            Question questionToDelete = questions.get(questionNum - 1);
            int deletedQuestionNum = questionToDelete.getQuestionNum();
            try {
                // Delete from mcq table first to remove dependent records
                int mcqRowsDeleted = db.delete("mcq", "question_id=?", new String[]{String.valueOf(questionToDelete.getQuestionId())});
                // Now delete from question table
                int questionRowsDeleted = db.delete("question", "question_id=?", new String[]{String.valueOf(questionToDelete.getQuestionId())});
                questions.remove(questionNum - 1);

                for (int i = deletedQuestionNum -1; i < questions.size(); i++) {
                    Question q = questions.get(i);
                    q.setQuestionNum(i + 1);
                    ContentValues values = new ContentValues();
                    values.put("question_number", i + 1);
                    db.update("question", values, "question_id=?", new String[]{String.valueOf(q.getQuestionId())});
                }
                if (questions.size() > 0) {
                    displayQuestion(questionNum);
                } else {
                    clearInputs();
                    questionNumberTextView.setText("No questions available");
                }
                Toast.makeText(this, "Question deleted successfully", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("DeleteError", "Error deleting question: ", e);
                Toast.makeText(this, "Error deleting question: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
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


    private void clearInputs() {
        questionInput.setText("");
        optionAInput.setText("");
        optionBInput.setText("");
        optionCInput.setText("");
        optionDInput.setText("");
        correctOptionGroup.clearCheck();
    }

    private void completeQuiz() {
        // Logic to complete the quiz and navigate back to the teacher's dashboard
        Intent intent = new Intent(MCQEditorActivity.this, TeacherActivity.class);
        intent.putExtra("userId", user_id); // Pass the user ID to the TeacherActivity
        startActivity(intent);
        finish(); 
    }

    @Override
    protected void onDestroy() {
        // Close the database when the activity is destroyed
        db.close();
        super.onDestroy();
    }
}
