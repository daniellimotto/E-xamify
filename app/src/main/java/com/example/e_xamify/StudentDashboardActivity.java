package com.example.e_xamify;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class StudentDashboardActivity extends AppCompatActivity {

    private Spinner institutionSpinner;
    private Spinner moduleSpinner;
    private EditText enrollmentKeyInput;
    private Button enrollButton;
    private ListView quizListView;
    private DatabaseHelper dbHelper;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        dbHelper = new DatabaseHelper(this);
        userId = getIntent().getLongExtra("userId", -1);

        institutionSpinner = findViewById(R.id.institutionSpinner);
        moduleSpinner = findViewById(R.id.moduleSpinner);
        enrollmentKeyInput = findViewById(R.id.enrollmentKeyInput);
        enrollButton = findViewById(R.id.enrollButton);
        quizListView = findViewById(R.id.quizListView);

        loadInstitutions();

        institutionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadModules((long) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        moduleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadQuizzes((long) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        enrollButton.setOnClickListener(v -> enrollWithKey());
    }

    private void loadInstitutions() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT user_id, institution_name FROM institution", null);
        List<Institution> institutions = new ArrayList<>();

        while (cursor.moveToNext()) {
            long id = cursor.getLong(0);
            String name = cursor.getString(1);
            institutions.add(new Institution(id, name));
        }
        cursor.close();

        ArrayAdapter<Institution> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, institutions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        institutionSpinner.setAdapter(adapter);
    }

    private void loadModules(long institutionId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT module_id, module_name FROM module WHERE institution_id = ?", new String[]{String.valueOf(institutionId)});
        List<Module> modules = new ArrayList<>();

        while (cursor.moveToNext()) {
            long id = cursor.getLong(0);
            String name = cursor.getString(1);
            modules.add(new Module(id, name));
        }
        cursor.close();

        ArrayAdapter<Module> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modules);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moduleSpinner.setAdapter(adapter);
    }

    private void loadQuizzes(long moduleId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT quiz_id, quiz_title FROM quiz WHERE module_id = ?", new String[]{String.valueOf(moduleId)});
        List<Quiz> quizzes = new ArrayList<>();

        while (cursor.moveToNext()) {
            long id = cursor.getLong(0);
            String title = cursor.getString(1);
            quizzes.add(new Quiz(id, title));
        }
        cursor.close();

        ArrayAdapter<Quiz> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, quizzes);
        quizListView.setAdapter(adapter);
    }

    private void enrollWithKey() {
        String enrollmentKey = enrollmentKeyInput.getText().toString().trim();
        if (enrollmentKey.isEmpty()) {
            Toast.makeText(this, "Please enter an enrollment key", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT user_id FROM institution WHERE institution_enrolment_key = ?", new String[]{enrollmentKey});

        if (cursor.moveToFirst()) {
            long institutionId = cursor.getLong(0);
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            values.put("institution_id", institutionId);
            values.put("enrollment_date", System.currentTimeMillis());

            long result = db.insert("student_institution", null, values);
            if (result != -1) {
                Toast.makeText(this, "Successfully enrolled!", Toast.LENGTH_SHORT).show();
                loadInstitutions(); // Refresh the institutions list
            } else {
                Toast.makeText(this, "Enrollment failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Invalid enrollment key", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    // Inner classes for spinner items
    private class Institution {
        long id;
        String name;

        Institution(long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private class Module {
        long id;
        String name;

        Module(long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}