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
import java.util.Date;
import java.util.List;


public class StudentDashboardActivity extends AppCompatActivity {
    private static final String TAG = "StudentDashboardActivity";
    private Spinner institutionSpinner;
    private Spinner moduleSpinner;
    private ListView assignmentListView;
    private DatabaseHelper dbHelper;
    private int user_id;
    private Button viewPastResultsButton;
    private List<Assignment> assignmentList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        dbHelper = new DatabaseHelper(this);
        user_id = getIntent().getIntExtra("user_id", -1);

        if (user_id == -1) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        institutionSpinner = findViewById(R.id.institutionSpinner);
        moduleSpinner = findViewById(R.id.moduleSpinner);
        assignmentListView = findViewById(R.id.quizListView);
        viewPastResultsButton = findViewById(R.id.viewPastResultsButton);


        loadInstitutions();

        institutionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Institution selectedInstitution = (Institution) parent.getItemAtPosition(position);
                loadModules(selectedInstitution.getUserId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        moduleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Module selectedModule = (Module) parent.getItemAtPosition(position);
                loadAssignments(selectedModule.getModuleId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                institutions.add(new Institution(id, name, null, null, null, null, null));
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

    private void loadModules(int institutionId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT module.module_id, module.module_name " +
                            "FROM student_module " +
                            "INNER JOIN module ON student_module.module_id = module.module_id " +
                            "WHERE module.institution_id = ? AND student_module.user_id = ?",
                    new String[]{String.valueOf(institutionId), String.valueOf(user_id)}
            );
            List<Module> modules = new ArrayList<>();

            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                modules.add(new Module(id, name, institutionId));
            }

            ArrayAdapter<Module> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modules);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            moduleSpinner.setAdapter(adapter);
        } catch (Exception e) {
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

    private void loadAssignments(long moduleId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT a.assignment_id, a.quiz_id, a.user_id, a.status, a.attempt_number_left, a.mark, a.assignment_start_date, a.assignment_end_date, q.quiz_title " +
                            "FROM assignment a " +
                            "JOIN quiz q ON a.quiz_id = q.quiz_id " +
                            "WHERE a.user_id = ? AND q.module_id = ?",
                    new String[]{String.valueOf(user_id), String.valueOf(moduleId)}
            );
            assignmentList.clear();

            while (cursor.moveToNext()) {
                int assignmentId = cursor.getInt(0);
                int quizId = cursor.getInt(1);
                int userId = cursor.getInt(2);
                String status = cursor.getString(3);
                int attemptNumberLeft = cursor.getInt(4);
                int mark = cursor.getInt(5);
                Date startDate = new Date(cursor.getLong(6));
                Date endDate = new Date(cursor.getLong(7));
                String quizTitle = cursor.getString(8);
                assignmentList.add(new Assignment(assignmentId, quizId, userId, status, attemptNumberLeft, mark, startDate, endDate, quizTitle));
            }

            ArrayAdapter<Assignment> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, assignmentList);
            assignmentListView.setAdapter(adapter);

            assignmentListView.setOnItemClickListener((parent, view, position, id) -> {
                Assignment selectedAssignment = assignmentList.get(position);
                startAssignment(selectedAssignment.getAssignmentId());
            });
        } catch (Exception e) {
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

    private void startAssignment(int assignmentId) {
        Intent intent = new Intent(this, AssignmentDetailsActivity.class);
        intent.putExtra("assignmentId", assignmentId);
        intent.putExtra("user_id", user_id);
        startActivity(intent);
    }

    private void handleDatabaseError(Exception e) {
        Toast.makeText(this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
        finish();
    }
}