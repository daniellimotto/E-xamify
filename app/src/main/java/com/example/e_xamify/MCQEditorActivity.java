package com.example.e_xamify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MCQEditorActivity extends AppCompatActivity {
    private EditText questionInput;
    private EditText optionAInput;
    private EditText optionBInput;
    private EditText optionCInput;
    private EditText optionDInput;
    private RadioGroup correctOptionGroup;
    DatabaseHelper dbHelper = new DatabaseHelper(this);
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcq_editor);

        questionInput = findViewById(R.id.questionInput);
        optionAInput = findViewById(R.id.optionAInput);
        optionBInput = findViewById(R.id.optionBInput);
        optionCInput = findViewById(R.id.optionCInput);
        optionDInput = findViewById(R.id.optionDInput);
        correctOptionGroup = findViewById(R.id.correctOptionGroup);

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQuestion();
            }
        });
    }

    private void saveQuestion() {
        String question = questionInput.getText().toString();
        String optionA = optionAInput.getText().toString();
        String optionB = optionBInput.getText().toString();
        String optionC = optionCInput.getText().toString();
        String optionD = optionDInput.getText().toString();

        if (question.isEmpty() || optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            int selectedId = correctOptionGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select the correct option", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues questionValues = new ContentValues();
            questionValues.put("question_text", question);
            long questionId = db.insert("question", null, questionValues);
            insertOption(questionId, optionA, selectedId == R.id.optionARadio ? 1 : 0);
            insertOption(questionId, optionB, selectedId == R.id.optionBRadio ? 1 : 0);
            insertOption(questionId, optionC, selectedId == R.id.optionCRadio ? 1 : 0);
            insertOption(questionId, optionD, selectedId == R.id.optionDRadio ? 1 : 0);
            Toast.makeText(this, "Question saved", Toast.LENGTH_SHORT).show();
            db.close();
        }
    }
    private void insertOption(long questionId, String optionText, int isCorrect) {
        ContentValues optionValues = new ContentValues();
        optionValues.put("question_id", questionId);
        optionValues.put("option_text", optionText);
        optionValues.put("is_correct", isCorrect);
        db.insert("mcq", null, optionValues);
    }
}
