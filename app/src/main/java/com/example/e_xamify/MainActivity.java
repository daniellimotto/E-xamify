package com.example.e_xamify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button teacherButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the database helper (this will create the database)
        dbHelper = new DatabaseHelper(this);

        // Log the database path to ensure it's created
        Log.d("Database Path", getDatabasePath("examify.db").getAbsolutePath());

        // Sample database operation to trigger creation (optional)
        dbHelper.addStudent("test@example.com", "password", "Test User", 1, "2024-11-06");

        // Find the teacher button and set up the click listener
        teacherButton = findViewById(R.id.teacherButton);
        teacherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to TeacherActivity
                Intent intent = new Intent(MainActivity.this, TeacherActivity.class);
                startActivity(intent);
            }
        });
    }
}
