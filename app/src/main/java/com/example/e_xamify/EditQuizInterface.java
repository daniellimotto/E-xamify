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

public class EditQuizInterface extends AppCompatActivity {

    private EditText titleInput;
    private EditText durationInput;
    private EditText instructionInput;
    private Spinner quizTypeSpinner;
    private Spinner moduleSpinner;
    private Switch navigableSwitch;
    private Switch tabRestrictSwitch;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private int user_id;
    private int quizId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_interface);
        user_id = getIntent().getIntExtra("user_id", -1);
        quizId = getIntent().getIntExtra("quiz_id", -1);
        titleInput = findViewById(R.id.titleInput);
        durationInput = findViewById(R.id.durationInput);
        instructionInput = findViewById(R.id.instructionInput);
        quizTypeSpinner = findViewById(R.id.quizTypeSpinner);
        moduleSpinner = findViewById(R.id.moduleSpinner);
        navigableSwitch = findViewById(R.id.navigableSwitch);
        tabRestrictSwitch = findViewById(R.id.tabRestrictSwitch);
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        if (quizId != -1) {
            loadQuizDetails(quizId);
        }

        Button proceedButton = findViewById(R.id.proceedButton);
        proceedButton.setText("Proceed to Edit MCQ");
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMCQ();
            }
        });
    }

    private void populateQuizTypeSpinner(String quizTypeName) {
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
        if (quizTypeName != null) {
            int position = adapter.getPosition(quizTypeName);
            if (position >= 0) {
                quizTypeSpinner.setSelection(position);
            }
        }
    }

    private void populateModuleSpinner(String moduleName) {
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
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modules);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moduleSpinner.setAdapter(adapter);
        if (moduleName != null) {
            int position = adapter.getPosition(moduleName);
            if (position >= 0) {
                moduleSpinner.setSelection(position);
            } 
        }
    }


    private void loadQuizDetails(int quizId) {
        Cursor cursor = db.query("Quiz", null, "quiz_id = ?", new String[]{String.valueOf(quizId)}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow("quiz_title"));
            int quizTypeId = cursor.getInt(cursor.getColumnIndexOrThrow("quiz_type_id"));
            int moduleId = cursor.getInt(cursor.getColumnIndexOrThrow("module_id"));
            String duration = cursor.getString(cursor.getColumnIndexOrThrow("quiz_duration"));
            String instructions = cursor.getString(cursor.getColumnIndexOrThrow("instructions"));
            boolean navigable = cursor.getInt(cursor.getColumnIndexOrThrow("quiz_navigable")) > 0;
            boolean restrictTab = cursor.getInt(cursor.getColumnIndexOrThrow("quiz_tab_restrictor")) > 0;
            cursor.close();
            String quizTypeName = null;
            Cursor quizTypeCursor = db.rawQuery("SELECT type_name FROM quiz_type WHERE quiz_type_id = ?", new String[]{String.valueOf(quizTypeId)});
            if (quizTypeCursor.moveToFirst()) {
                quizTypeName = quizTypeCursor.getString(0);
            }
            quizTypeCursor.close();
            String moduleName = null;
            Cursor moduleCursor = db.rawQuery("SELECT module_name FROM module WHERE module_id = ?", new String[]{String.valueOf(moduleId)});
            if (moduleCursor.moveToFirst()) {
                moduleName = moduleCursor.getString(0);
            }
            moduleCursor.close();
            titleInput.setText(title);
            durationInput.setText(duration);
            instructionInput.setText(instructions);
            navigableSwitch.setChecked(navigable);
            tabRestrictSwitch.setChecked(restrictTab);
            populateQuizTypeSpinner(quizTypeName);
            populateModuleSpinner(moduleName);
        }
    }


    private void editMCQ() {
        String quizTitle = titleInput.getText().toString();
        String durationStr = durationInput.getText().toString();
        String instructions = instructionInput.getText().toString();
        if (quizTitle.isEmpty() || durationStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        int quizDuration = Integer.parseInt(durationStr);
        int quizNavigable = navigableSwitch.isChecked() ? 1 : 0;
        int quizTabRestrictor = tabRestrictSwitch.isChecked() ? 1 : 0;
        String type_name = quizTypeSpinner.getSelectedItem().toString();
        String moduleName = moduleSpinner.getSelectedItem().toString();
        int quizTypeId = -1;
        try (Cursor cursorQuizType = db.rawQuery("SELECT quiz_type_id FROM quiz_type WHERE type_name = ?", new String[]{type_name})) {
            if (cursorQuizType.moveToFirst()) {
                quizTypeId = cursorQuizType.getInt(0);
            }
        }
        if (quizTypeId == -1) {
            Toast.makeText(this, "Invalid quiz type selected", Toast.LENGTH_SHORT).show();
            return;
        }
        int moduleId = -1;
        try (Cursor cursorModule = db.rawQuery("SELECT module_id FROM module WHERE module_name = ?", new String[]{moduleName})) {
            if (cursorModule.moveToFirst()) {
                moduleId = cursorModule.getInt(0);
            }
        }
        if (moduleId == -1) {
            Toast.makeText(this, "Invalid module selected", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues quizValues = new ContentValues();
        quizValues.put("quiz_title", quizTitle);
        quizValues.put("quiz_duration", quizDuration);
        quizValues.put("instructions", instructions);
        quizValues.put("quiz_navigable", quizNavigable);
        quizValues.put("quiz_tab_restrictor", quizTabRestrictor);
        quizValues.put("quiz_type_id", quizTypeId);
        quizValues.put("module_id", moduleId);
        quizValues.put("user_id", user_id);
        try {
            int rowsAffected = db.update("quiz", quizValues, "quiz_id = ?", new String[]{String.valueOf(quizId)});
            if (rowsAffected == 0) {
                Toast.makeText(this, "Failed to update quiz", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, MCQEditorActivity.class);
            intent.putExtra("quiz_title", quizTitle);
            intent.putExtra("quiz_type_id", quizTypeId);
            intent.putExtra("quiz_id", quizId);
            intent.putExtra("user_id", user_id);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error updating quiz: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}