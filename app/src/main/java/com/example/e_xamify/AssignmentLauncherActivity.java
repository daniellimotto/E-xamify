package com.example.e_xamify;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssignmentLauncherActivity extends AppCompatActivity {

    private Spinner quizSpinner;
    private EditText startDateEditText, endDateEditText;
    private Button launchAssignmentButton;
    private int user_id;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_launcher);
        quizSpinner = findViewById(R.id.spinner_quiz);
        startDateEditText = findViewById(R.id.editText_start_date);
        endDateEditText = findViewById(R.id.editText_end_date);
        launchAssignmentButton = findViewById(R.id.btn_launch_assignment);
        user_id = getIntent().getIntExtra("user_id", -1);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        populateQuizSpinner();
        startDateEditText.setOnClickListener(v -> showDatePickerDialog(startDateEditText));
        endDateEditText.setOnClickListener(v -> showDatePickerDialog(endDateEditText));
        launchAssignmentButton.setOnClickListener(v -> launchAssignment());
    }

    private void populateQuizSpinner() {
        List<String> quizTitles = new ArrayList<>();
        String query = "SELECT quiz_title FROM quiz WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(user_id)});

        if (cursor.moveToFirst()) {
            do {
                String quizTitle = cursor.getString(cursor.getColumnIndexOrThrow("quiz_title"));
                quizTitles.add(quizTitle);
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "No quizzes found for this teacher.", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, quizTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quizSpinner.setAdapter(adapter);
    }

    private void launchAssignment() {
        String selectedQuizTitle = (String) quizSpinner.getSelectedItem();
        String startDate = startDateEditText.getText().toString();
        String endDate = endDateEditText.getText().toString();

        if (selectedQuizTitle == null) {
            Toast.makeText(this, "Please select a quiz", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate dates
        if (!isEndDateValid(startDate, endDate)) {
            Toast.makeText(this, "End date must be greater than the start date.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve quizId and moduleId for the selected quiz title
        Quiz selectedQuiz = getQuizByTitle(selectedQuizTitle);
        if (selectedQuiz == null) {
            Toast.makeText(this, "Quiz not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        int moduleId = selectedQuiz.getModuleId();
        long quizId = selectedQuiz.getQuizId();
        int attemptNumber = selectedQuiz.getQuizAttempts();

        // Fetch students in the selected module
        List<Student> students = getStudentsInModule(moduleId);

        // Insert assignment for each student, checking for existing assignments
        int assignedCount = 0;
        for (Student student : students) {
            if (!isAlreadyAssigned(student.getUserId(), quizId)) {
                insertAssignment(student.getUserId(), quizId, startDate, endDate, attemptNumber);
                assignedCount++;
            }
        }
        Toast.makeText(this, "Assignments launched for " + assignedCount + " students!", Toast.LENGTH_SHORT).show();
        finish();
    }


    private Quiz getQuizByTitle(String quizTitle) {
        String query = "SELECT quiz_id, module_id, quiz_attempts FROM quiz WHERE quiz_title = ? AND user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{quizTitle, String.valueOf(user_id)});
        Quiz quiz = null;
        if (cursor.moveToFirst()) {
            int quizId = cursor.getInt(cursor.getColumnIndexOrThrow("quiz_id"));
            int moduleId = cursor.getInt(cursor.getColumnIndexOrThrow("module_id"));
            int quizAttempts = cursor.getInt(cursor.getColumnIndexOrThrow("quiz_attempts"));
            quiz = new Quiz(quizId, moduleId, quizAttempts);
        }
        cursor.close();
        return quiz;
    }

    private boolean isAlreadyAssigned(int studentuser_id, long quizId) {
        String query = "SELECT 1 FROM assignment WHERE user_id = ? AND quiz_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentuser_id), String.valueOf(quizId)});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    private void insertAssignment(int studentuser_id, long quizId, String startDate, String endDate, int attemptNumber) {
        ContentValues values = new ContentValues();
        values.put("quiz_id", quizId);
        values.put("user_id", studentuser_id);
        values.put("status", "pending");
        values.put("attempt_number_left", attemptNumber);
        values.put("assignment_start_date", startDate);
        values.put("assignment_end_date", endDate);
        db.insert("assignment", null, values);
    }
    private List<Student> getStudentsInModule(int moduleId) {
        List<Student> students = new ArrayList<>();
        String query = "SELECT user_id FROM student_module WHERE module_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(moduleId)});
        if (cursor.moveToFirst()) {
            do {
                int studentuser_id = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
                students.add(new Student(studentuser_id)); // Assuming Student class constructor takes user_id
            } while (cursor.moveToNext());
        }
        cursor.close();
        return students;
    }
    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            // Format and set selected date to the EditText
            String date = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
            editText.setText(date);
        }, year, month, day);
        datePickerDialog.show();
    }
    private boolean isEndDateValid(String startDate, String endDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            return end != null && start != null && end.after(start);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid date format. Use YYYY-MM-DD.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

}
