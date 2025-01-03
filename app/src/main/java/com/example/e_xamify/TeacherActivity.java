package com.example.e_xamify;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TeacherActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    private Button createAssignmentButton;
    private Button createQuizButton;
    private Button viewQuizzesButton;
    private Button enrollButton; // Button to go to enrollment page

    private int user_id; // User ID received from intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // Retrieve the user ID passed from SignInActivity
        user_id = getIntent().getIntExtra("user_id", -1);
        if (user_id == -1) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewQuizzesButton = findViewById(R.id.viewQuizzesButton);
        viewQuizzesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherActivity.this, QuizListActivity.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
            }
        });

        // "Create New Assignment" Button
        createAssignmentButton = findViewById(R.id.createAssignmentButton);
        createAssignmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherActivity.this, AssignmentLauncherActivity.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
            }
        });

        // "Create New Quiz" Button
        createQuizButton = findViewById(R.id.createQuizButton);
        createQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewQuiz();
            }
        });


        // "Enroll" Button to navigate to enrollment activity
        enrollButton = findViewById(R.id.enrollButton);
        enrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherActivity.this, TeacherEnrollmentActivity.class);
                intent.putExtra("user_id", user_id); // Pass user ID to the enrollment activity
                startActivity(intent);
            }
        });
    }

    private void createNewQuiz() {
        // Navigate to QuizInterface to enter quiz details
        Intent intent = new Intent(TeacherActivity.this, QuizInterface.class);
        intent.putExtra("user_id", user_id); // Pass user ID to the QuizInterface
        startActivity(intent);
    }



    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
