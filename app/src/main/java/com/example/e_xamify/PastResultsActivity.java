package com.example.e_xamify;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PastResultsActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ListView pastResultsListView;
    private int user_id;
    private ArrayList<String> pastResultsList;
    private ArrayList<Integer> quizIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_results);

        dbHelper = new DatabaseHelper(this);
        pastResultsListView = findViewById(R.id.pastResultsListView);
        user_id = getIntent().getIntExtra("user_id", -1);

        if (user_id == -1) {
            Toast.makeText(this, "Error loading past results.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        pastResultsList = new ArrayList<>();
        quizIds = new ArrayList<>();

        loadPastResults();

        pastResultsListView.setOnItemClickListener((parent, view, position, id) -> {
            if (position < quizIds.size()) {
                int selectedQuizId = quizIds.get(position);
                Intent intent = new Intent(PastResultsActivity.this, QuizResultActivity.class);
                intent.putExtra("assignmentId", selectedQuizId);
                intent.putExtra("studentId", user_id);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Invalid selection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPastResults() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT q.quiz_id, q.quiz_title, qa.score " +
                        "FROM quiz q " +
                        "INNER JOIN quiz_attempt qa ON q.quiz_id = qa.quiz_id " +
                        "WHERE qa.user_id = ? AND qa.status = 'completed'",
                new String[]{String.valueOf(user_id)}
        );

        if (cursor.moveToFirst()) {
            do {
                int quizId = cursor.getInt(0);
                String quizTitle = cursor.getString(1);
                int score = cursor.getInt(2);

                quizIds.add(quizId);
                pastResultsList.add(quizTitle + " - Score: " + score);
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "No past results found.", Toast.LENGTH_SHORT).show();
        }

        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pastResultsList);
        pastResultsListView.setAdapter(adapter);
    }
}
