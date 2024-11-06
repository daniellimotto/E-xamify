package com.example.e_xamify;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class QuizInterface extends AppCompatActivity {

    private Button createMCQButton;
    private EditText titleInput;
    private EditText durationInput;
    private EditText instructionInput;
    private EditText attemptsInput;
    private Switch navigableSwitch;
    private Switch tabRestrictSwitch;
    private Switch randomizeSwitch;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_interface);

        titleInput = findViewById(R.id.titleInput);
        durationInput = findViewById(R.id.durationInput);
        instructionInput = findViewById(R.id.instructionInput);
        attemptsInput = findViewById(R.id.attemptsInput);
        navigableSwitch = findViewById(R.id.navigableSwitch);
        tabRestrictSwitch = findViewById(R.id.tabRestrictSwitch);
        randomizeSwitch = findViewById(R.id.randomizeSwitch);
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // "Create MCQ" Button
        createMCQButton = findViewById(R.id.createMCQButton);
        createMCQButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMCQ();
            }
        });
    }
    private void createMCQ() {
        String quizTitle = titleInput.getText().toString();
        String durationStr = durationInput.getText().toString();
        String instructions = instructionInput.getText().toString();
        String attemptsStr = attemptsInput.getText().toString();

        if (quizTitle.isEmpty() || durationStr.isEmpty() || attemptsStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quizDuration = Integer.parseInt(durationStr);
        int quizAttempts = Integer.parseInt(attemptsStr);
        int quizNavigable = navigableSwitch.isChecked() ? 1 : 0;
        int quizTabRestrictor = tabRestrictSwitch.isChecked() ? 1 : 0;
        int questionRandomize = randomizeSwitch.isChecked() ? 1 : 0;


        ContentValues quizValues = new ContentValues();
        quizValues.put("quiz_title", quizTitle);
        quizValues.put("quiz_duration", quizDuration);
        quizValues.put("instructions", instructions);
        quizValues.put("quiz_attempts", quizAttempts);
        quizValues.put("quiz_navigable", quizNavigable);
        quizValues.put("quiz_tab_restrictor", quizTabRestrictor);
        quizValues.put("question_randomize", questionRandomize);

        long quiz_id = db.insert("quiz", null, quizValues);
        if (quiz_id == -1) {
            Toast.makeText(this, "Failed to create quiz", Toast.LENGTH_SHORT).show();
            return;
        }
        // Pass the quiz ID and title to MCQEditorActivity
        Intent intent = new Intent(QuizInterface.this, MCQEditorActivity.class);
        intent.putExtra("quiz_id", quiz_id);
        intent.putExtra("quiz_title", quizTitle);
        startActivity(intent);
    }
}
