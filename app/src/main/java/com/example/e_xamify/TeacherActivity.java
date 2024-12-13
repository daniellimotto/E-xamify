package com.example.e_xamify;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TeacherActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    private Button createAssignmentButton;
    private Button createQuizButton;
    private Button viewQuizzesButton;
    private Button enrollButton;

    private int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

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

        createAssignmentButton = findViewById(R.id.createAssignmentButton);
        createAssignmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherActivity.this, AssignmentLauncherActivity.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
            }
        });

        createQuizButton = findViewById(R.id.createQuizButton);
        createQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewQuiz();
            }
        });


        enrollButton = findViewById(R.id.enrollButton);
        enrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherActivity.this, TeacherEnrollmentActivity.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
            }
        });
    }

    private void createNewQuiz() {
        Intent intent = new Intent(TeacherActivity.this, QuizInterface.class);
        intent.putExtra("user_id", user_id);
        startActivity(intent);
    }



    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
