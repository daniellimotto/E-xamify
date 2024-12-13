package com.example.e_xamify;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class QuizInterface extends AppCompatActivity {

    private EditText titleInput;
    private EditText durationInput;
    private EditText instructionInput;
    private Spinner quizTypeSpinner;
    private Spinner moduleSpinner;
    private Switch navigableSwitch;
    private Switch tabRestrictSwitch;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private int user_id ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_interface);
        user_id = getIntent().getIntExtra("user_id", -1);
        titleInput = findViewById(R.id.titleInput);
        durationInput = findViewById(R.id.durationInput);
        instructionInput = findViewById(R.id.instructionInput);
        quizTypeSpinner = findViewById(R.id.quizTypeSpinner);
        moduleSpinner = findViewById(R.id.moduleSpinner);
        navigableSwitch = findViewById(R.id.navigableSwitch);
        tabRestrictSwitch = findViewById(R.id.tabRestrictSwitch);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // Spinner to show available quiz type from quiz_type table and module from institution_module table
        populateQuizTypeSpinner();
        populateModuleSpinner();


        Button proceedButton = findViewById(R.id.proceedButton);
        proceedButton.setText("Proceed to Create MCQ");
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMCQ();
            }
        });

        Intent intent = getIntent();
        int quizId = intent.getIntExtra("quiz_id", -1);

    }

    private void populateQuizTypeSpinner() {
        Cursor cursor = db.rawQuery("SELECT type_name FROM quiz_type", null);
        ArrayList<String> quizTypes = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                quizTypes.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, quizTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quizTypeSpinner.setAdapter(adapter);
    }

    private void populateModuleSpinner() {
        String query = "SELECT m.module_name " +
                "FROM module m " +
                "INNER JOIN teacher_institution ti ON m.institution_id = ti.institution_id " +
                "WHERE ti.teacher_id = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(user_id)});
        ArrayList<String> modules = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                modules.add(cursor.getString(cursor.getColumnIndexOrThrow("module_name")));
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "No modules found for the teacher's institution.", Toast.LENGTH_SHORT).show();
        }
        cursor.close();


        ArrayAdapter<String> moduleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modules);
        moduleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moduleSpinner.setAdapter(moduleAdapter);
    }

    private void createMCQ() {
        String quizTitle = titleInput.getText().toString();
        String durationStr = durationInput.getText().toString();
        String instructions = instructionInput.getText().toString();

        if (quizTitle.isEmpty() || durationStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quizDuration = Integer.parseInt(durationStr);
        int quizAttempts = 1;
        int quizNavigable = navigableSwitch.isChecked() ? 1 : 0;
        int quizTabRestrictor = tabRestrictSwitch.isChecked() ? 1 : 0;
        String type_name = quizTypeSpinner.getSelectedItem().toString();
        String moduleName = (String)moduleSpinner.getSelectedItem();

        int quizTypeId = -1;
        try (Cursor cursorQuizType = db.rawQuery("SELECT quiz_type_id FROM quiz_type WHERE type_name = ?", new String[]{type_name})) {
            if (cursorQuizType.moveToFirst()) {
                quizTypeId = cursorQuizType.getInt(0);
            }
        }

        int moduleId = -1;
        try (Cursor cursorModule = db.rawQuery("SELECT module_id FROM module WHERE module_name = ?", new String[]{moduleName})) {
            if (cursorModule.moveToFirst()) {
                moduleId = cursorModule.getInt(0);
            }
        }

        // Insert quiz to the database
        ContentValues quizValues = new ContentValues();
        quizValues.put("quiz_title", quizTitle);
        quizValues.put("quiz_duration", quizDuration);
        quizValues.put("instructions", instructions);
        quizValues.put("quiz_attempts", quizAttempts);
        quizValues.put("quiz_navigable", quizNavigable);
        quizValues.put("quiz_tab_restrictor", quizTabRestrictor);
        quizValues.put("quiz_type_id", quizTypeId);
        quizValues.put("module_id", moduleId);
        quizValues.put("user_id", user_id);



        int quiz_id = (int) db.insert("quiz", null, quizValues);
        if (quiz_id == -1) {
            Toast.makeText(this, "Failed to create quiz", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Quiz Created Successfully with ID: " + quiz_id, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(QuizInterface.this, MCQEditorActivity.class);

        intent.putExtra("quiz_title", quizTitle);
        intent.putExtra("quiz_type_id", quizTypeId);
        intent.putExtra("quiz_id", quiz_id);
        intent.putExtra("user_id", user_id);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

}
