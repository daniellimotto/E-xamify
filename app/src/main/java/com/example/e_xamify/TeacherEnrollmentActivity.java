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

public class TeacherEnrollmentActivity extends AppCompatActivity {

    private EditText enrollmentKeyInput;
    private Button enrollButton;
    private DatabaseHelper dbHelper;
    private int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment); 

        dbHelper = new DatabaseHelper(this);

        user_id = getIntent().getIntExtra("user_id", -1);
        if (user_id == -1) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        enrollmentKeyInput = findViewById(R.id.enrollmentKeyInput);
        enrollButton = findViewById(R.id.enrollButton);

        enrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enrollTeacher();
            }
        });
    }

    private void enrollTeacher() {
        String enrollmentKey = enrollmentKeyInput.getText().toString().trim();

        if (enrollmentKey.isEmpty()) {
            Toast.makeText(this, "Please enter an enrollment key.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT user_id FROM institution WHERE institution_enrolment_key = ?", new String[]{enrollmentKey});

        if (cursor.moveToFirst()) {
            long institutionuser_id = cursor.getLong(0);
            Cursor checkCursor = db.rawQuery("SELECT * FROM teacher_institution WHERE teacher_id = ? AND institution_id = ?",
                    new String[]{String.valueOf(user_id), String.valueOf(institutionuser_id)});

            if (checkCursor.getCount() > 0) {
                Toast.makeText(this, "You are already enrolled in this institution.", Toast.LENGTH_SHORT).show();
                checkCursor.close();
                cursor.close();
                return;
            }
            checkCursor.close();

            ContentValues values = new ContentValues();
            values.put("teacher_id", user_id);
            values.put("institution_id", institutionuser_id);
            values.put("enrollment_date", getCurrentDate());

            long newEnrollmentId = db.insert("teacher_institution", null, values);
            if (newEnrollmentId == -1) {
                Toast.makeText(this, "Enrollment failed. Please check the enrollment key.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Successfully enrolled!", Toast.LENGTH_SHORT).show();
                finish();
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
