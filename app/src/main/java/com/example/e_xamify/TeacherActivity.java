package com.example.e_xamify;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TeacherActivity extends AppCompatActivity {
    private RecyclerView quizzesRecyclerView;
    private QuizAdapter quizAdapter;
    private List<Quiz> quizzes;
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    private Button createAssignmentButton;
    private Button createQuizButton;
    private Button viewQuizzesButton;

    private ExecutorService executorService;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        viewQuizzesButton = findViewById(R.id.viewQuizzesButton);
        viewQuizzesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherActivity.this, QuizListActivity.class);
                startActivity(intent);
            }
        });

        // "Create New Assignment" Button
        createAssignmentButton = findViewById(R.id.createAssignmentButton);
        createAssignmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle creating new assignment (existing functionality)
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

        Button deleteAllQuizzesButton = findViewById(R.id.deleteAllQuizzesButton);
        deleteAllQuizzesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllQuizzes();
            }
        });
    }

    private void createNewQuiz() {
        // Navigate to QuizInterface to enter quiz details
        Intent intent = new Intent(TeacherActivity.this, QuizInterface.class);
        startActivity(intent);
    }

    private void deleteAllQuizzes() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deletedRows = db.delete("Quiz", null, null);
        Log.d("TeacherActivity", "Deleted " + deletedRows + " quizzes");
        Toast.makeText(this, "All quizzes deleted", Toast.LENGTH_SHORT).show();

        // Refresh the list if necessary
        quizzes.clear();
        quizAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        db.close();
        executorService.shutdown();
        super.onDestroy();
    }
}
