package com.example.e_xamify;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class QuizListActivity extends AppCompatActivity {
    private RecyclerView quizzesRecyclerView;
    private QuizAdapter quizAdapter;
    private List<Quiz> quizzes;
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getReadableDatabase();

        quizzesRecyclerView = findViewById(R.id.quizzesRecyclerView);
        quizzesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load quizzes from the database
        quizzes = loadQuizzesFromDatabase();
        quizAdapter = new QuizAdapter(quizzes);
        quizzesRecyclerView.setAdapter(quizAdapter);

        quizAdapter.setOnItemClickListener(new QuizAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Quiz quiz) {
                Intent intent = new Intent(QuizListActivity.this, EditQuizInterface.class);
                intent.putExtra("quiz_id", (int) quiz.getQuizId()); // Ensure quiz_id is passed as int
                startActivity(intent);
            }
        });
    }

    private List<Quiz> loadQuizzesFromDatabase() {
        List<Quiz> quizList = new ArrayList<>();
        Cursor cursor = db.query("Quiz", null, null, null, null, null, null);

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("quiz_id"));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("quiz_title"));
                    Quiz quiz = new Quiz(id, title);
                    quizList.add(quiz);
                }
            } catch (IllegalArgumentException e) {
                Log.e("QuizListActivity", "Column not found: " + e.getMessage());
            } finally {
                cursor.close();
            }
        }
        return quizList;
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
