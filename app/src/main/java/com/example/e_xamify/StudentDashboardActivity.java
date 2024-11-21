package com.example.e_xamify;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class StudentDashboardActivity extends AppCompatActivity {
    private static final String TAG = "StudentDashboardActivity";
    private Spinner institutionSpinner;
    private Spinner moduleSpinner;
    private ListView quizListView;
    private DatabaseHelper dbHelper;
    private int user_id;
    private Button viewPastResultsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        dbHelper = new DatabaseHelper(this);
        user_id = getIntent().getIntExtra("user_id", -1);

        if (user_id == -1) {
            Log.e(TAG, "Invalid user ID received");
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        institutionSpinner = findViewById(R.id.institutionSpinner);
        moduleSpinner = findViewById(R.id.moduleSpinner);
        quizListView = findViewById(R.id.quizListView);
        viewPastResultsButton = findViewById(R.id.viewPastResultsButton);

        loadInstitutions();

        institutionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Institution selectedInstitution = (Institution) parent.getItemAtPosition(position);
                loadModules(selectedInstitution.id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        moduleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Module selectedModule = (Module) parent.getItemAtPosition(position);
                loadQuizzes(selectedModule.id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set up the "View Past Results" button to navigate to PastResultsActivity
        viewPastResultsButton.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDashboardActivity.this, PastResultsActivity.class);
            intent.putExtra("user_id", user_id);
            startActivity(intent);
        });
    }

    private void loadInstitutions() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT i.user_id, i.institution_name FROM institution i " +
                            "INNER JOIN student_institution si ON i.user_id = si.institution_id " +
                            "WHERE si.student_id = ?",
                    new String[]{String.valueOf(user_id)}
            );
            List<Institution> institutions = new ArrayList<>();

            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String name = cursor.getString(1);
                institutions.add(new Institution(id, name));
            }

            if (institutions.isEmpty()) {
                Log.w(TAG, "No institutions found for user ID: " + user_id);
                Toast.makeText(this, "You are not enrolled in any institutions. Please enroll first.", Toast.LENGTH_LONG).show();
                return;
            }

            ArrayAdapter<Institution> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, institutions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            institutionSpinner.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "Error loading institutions: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading institutions. Please try again.", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    private void loadModules(long institutionId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT module_id, module_name FROM module WHERE institution_id = ?",
                    new String[]{String.valueOf(institutionId)}
            );
            List<Module> modules = new ArrayList<>();

            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String name = cursor.getString(1);
                modules.add(new Module(id, name));
            }

            ArrayAdapter<Module> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modules);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            moduleSpinner.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "Error loading modules: " + e.getMessage(), e);
            handleDatabaseError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    private void loadQuizzes(long moduleId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT quiz_id, quiz_title FROM quiz WHERE module_id = ?",
                    new String[]{String.valueOf(moduleId)}
            );
            List<Quiz> quizzes = new ArrayList<>();

            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                quizzes.add(new Quiz(id, title));
            }

            ArrayAdapter<Quiz> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, quizzes);
            quizListView.setAdapter(adapter);

            quizListView.setOnItemClickListener((parent, view, position, id) -> {
                Quiz selectedQuiz = (Quiz) parent.getItemAtPosition(position);
                startQuiz(selectedQuiz.id);
            });
        } catch (Exception e) {
            Log.e(TAG, "Error loading quizzes: " + e.getMessage(), e);
            handleDatabaseError(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    private void startQuiz(int quizId) {
        Intent intent = new Intent(this, QuizDetailsActivity.class);
        intent.putExtra("quizId", quizId);
        intent.putExtra("user_id", user_id);
        startActivity(intent);
    }

    private void handleDatabaseError(Exception e) {
        Log.e(TAG, "Database error: " + e.getMessage(), e);
        Toast.makeText(this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private static class Institution {
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

    private static class Module {
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

    private static class Quiz {
        int id;
        String title;

        Quiz(int id, String title) {
            this.id = id;
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
