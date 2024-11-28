package com.example.e_xamify;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
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
    private int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getReadableDatabase();
        user_id = getIntent().getIntExtra("user_id", -1);
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
                intent.putExtra("quiz_id", (int) quiz.getQuizId());
                intent.putExtra("user_id", user_id);
                startActivity(intent);
            }
        });

        quizAdapter.setOnDeleteClickListener(new QuizAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(Quiz quiz) {
                if (!isQuizLaunchedAsAssignment(quiz.getQuizId())) {
                    deleteQuiz(quiz.getQuizId());
                    quizzes.remove(quiz);
                    quizAdapter.notifyDataSetChanged();
                    Toast.makeText(QuizListActivity.this, "Quiz deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(QuizListActivity.this, "Quiz has been launched as an assignment and cannot be deleted", Toast.LENGTH_SHORT).show();
                }
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

    private boolean isQuizLaunchedAsAssignment(int quiz_id) {
        String query = "SELECT 1 FROM assignment WHERE quiz_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(quiz_id)});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    private void deleteQuiz(int quiz_id) {
        db.delete("mcq", "question_id IN (SELECT question_id FROM question WHERE quiz_id = ?)", new String[]{String.valueOf(quiz_id)});
        db.delete("question", "quiz_id = ?", new String[]{String.valueOf(quiz_id)});
        db.delete("quiz", "quiz_id = ?", new String[]{String.valueOf(quiz_id)});
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
