package com.example.e_xamify;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AssignmentDetailsActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private int assignmentId;
    private int user_id;
    private TextView quizTitleText;
    private TextView instructionsText;
    private TextView attemptsText;
    private Button startAssignmentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_details);

        quizTitleText = findViewById(R.id.quizTitleText);
        instructionsText = findViewById(R.id.instructionsText);
        attemptsText = findViewById(R.id.attemptsText);
        startAssignmentButton = findViewById(R.id.startAssignmentButton);

        assignmentId = getIntent().getIntExtra("assignmentId", -1);
        user_id = getIntent().getIntExtra("user_id", -1);

        if (assignmentId == -1 || user_id == -1) {
            Toast.makeText(this, "Error loading assignment details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        loadAssignmentDetails();

        startAssignmentButton.setOnClickListener(v -> startAssignment());
    }

    private void loadAssignmentDetails() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT q.quiz_title, q.instructions, a.attempt_number_left FROM quiz q " +
                        "JOIN assignment a ON q.quiz_id = a.quiz_id WHERE a.assignment_id = ?",
                new String[]{String.valueOf(assignmentId)}
        );

        if (cursor.moveToFirst()) {
            String title = cursor.getString(0);
            String instructions = cursor.getString(1);
            int attempts = cursor.getInt(2);

            quizTitleText.setText(title);
            instructionsText.setText(instructions);
            attemptsText.setText("Attempts left: " + attempts);
        }
        cursor.close();
    }

    private void startAssignment() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Check if there are attempts left
        Cursor cursor = db.rawQuery("SELECT attempt_number_left FROM assignment WHERE assignment_id = ?", new String[]{String.valueOf(assignmentId)});
        if (cursor.moveToFirst()) {
            int attemptsLeft = cursor.getInt(0);
            if (attemptsLeft <= 0) {
                Toast.makeText(this, "No attempts left", Toast.LENGTH_SHORT).show();
                cursor.close();
                return;
            }
        }
        cursor.close();

        // Decrement the attempt_number_left
        db.execSQL("UPDATE assignment SET attempt_number_left = attempt_number_left - 1 WHERE assignment_id = ?",
                new String[]{String.valueOf(assignmentId)});

        // Retrieve the quiz_id directly from the assignment table
        int quizId = getQuizIdForAssignment(assignmentId);

        // Insert a new entry into the quiz_attempt table
        ContentValues values = new ContentValues();
        values.put("quiz_id", quizId);
        values.put("user_id", user_id);
        values.put("start_time", getCurrentDateTime());
        values.put("status", "pending");

        long attemptId = db.insert("quiz_attempt", null, values);

        if (attemptId != -1) {
            // Pass the intent to AssignmentTakingActivity
            Intent intent = new Intent(this, AssignmentTakingActivity.class);
            intent.putExtra("assignmentId", assignmentId);
            intent.putExtra("user_id", user_id);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Error starting assignment", Toast.LENGTH_SHORT).show();
        }
    }

    private int getQuizIdForAssignment(int assignmentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT quiz_id FROM assignment WHERE assignment_id = ?", new String[]{String.valueOf(assignmentId)});
        int quizId = -1;
        if (cursor.moveToFirst()) {
            quizId = cursor.getInt(0);
        }
        cursor.close();
        return quizId;
    }

    private String getCurrentDateTime() {
        // Return the current date and time as a string
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}
