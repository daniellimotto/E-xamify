package com.example.e_xamify;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StudentEnrollmentActivity extends AppCompatActivity {

    private EditText enrollmentKeyInput;
    private Button enrollButton;
    private DatabaseHelper dbHelper;
    private int userId; // User ID passed from StudentActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Retrieve user ID from the Intent
        userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        enrollmentKeyInput = findViewById(R.id.enrollmentKeyInput);
        enrollButton = findViewById(R.id.enrollButton);

        enrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enrollStudent();
            }
        });
    }

    private void enrollStudent() {
        String enrollmentKey = enrollmentKeyInput.getText().toString().trim();

        if (enrollmentKey.isEmpty()) {
            Toast.makeText(this, "Please enter an enrollment key.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Query to find the institution ID based on the enrollment key
        Cursor cursor = db.rawQuery("SELECT user_id FROM institution WHERE institution_enrolment_key = ?", new String[]{enrollmentKey});

        if (cursor.moveToFirst()) {
            long institutionUserId = cursor.getLong(0);

            // Check if the user is already enrolled in this institution
            Cursor checkCursor = db.rawQuery("SELECT * FROM student_institution WHERE student_id = ? AND institution_id = ?",
                    new String[]{String.valueOf(userId), String.valueOf(institutionUserId)});

            if (checkCursor.getCount() > 0) {
                Toast.makeText(this, "You are already enrolled in this institution.", Toast.LENGTH_SHORT).show();
                checkCursor.close();
                cursor.close();
                return;
            }
            checkCursor.close();

            // Insert the enrollment record
            ContentValues values = new ContentValues();

            values.put("institution_user_id", institutionUserId);
            values.put("user_id", userId);

            values.put("enrollment_date", getCurrentDate());

            long newEnrollmentId = db.insert("student_institution", null, values);
            if (newEnrollmentId == -1) {
                Toast.makeText(this, "Enrollment failed. Please try again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Successfully enrolled!", Toast.LENGTH_SHORT).show();
                finish(); // Close this activity and return to StudentActivity
            }
        } else {
            Toast.makeText(this, "Invalid enrollment key. Institution not found.", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}
