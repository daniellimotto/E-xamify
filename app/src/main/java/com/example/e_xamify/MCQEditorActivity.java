package com.example.e_xamify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MCQEditorActivity extends AppCompatActivity {
    private EditText questionInput;
    private EditText optionAInput;
    private EditText optionBInput;
    private EditText optionCInput;
    private EditText optionDInput;
    private RadioGroup correctOptionGroup;
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcq_editor);

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

        // Set up save button click listener
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQuestion();
            }
        });
    }

    private void saveQuestion() {
        // Get values from input fields
        String question = questionInput.getText().toString();
        String optionA = optionAInput.getText().toString();
        String optionB = optionBInput.getText().toString();
        String optionC = optionCInput.getText().toString();
        String optionD = optionDInput.getText().toString();

        // Validate inputs
        if (question.isEmpty() || optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            // Get selected correct answer
            int selectedId = correctOptionGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select the correct option", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert question into the database
            try {
                ContentValues questionValues = new ContentValues();
                questionValues.put("question_text", question);
                long questionId = db.insert("question", null, questionValues);

                // Insert options into the database
                insertOption(questionId, optionA, selectedId == R.id.optionARadio ? 1 : 0);
                insertOption(questionId, optionB, selectedId == R.id.optionBRadio ? 1 : 0);
                insertOption(questionId, optionC, selectedId == R.id.optionCRadio ? 1 : 0);
                insertOption(questionId, optionD, selectedId == R.id.optionDRadio ? 1 : 0);

                Toast.makeText(this, "Question saved", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onDestroy() {
        // Close the database when the activity is destroyed
        db.close();
        super.onDestroy();
    }
}
