package com.example.e_xamify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class TeacherActivity extends AppCompatActivity {

    private Button createAssignmentButton;
    private Button createQuizButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

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
                // Navigate to the Quiz Interface
                Intent intent = new Intent(TeacherActivity.this, QuizInterface.class);
                startActivity(intent);
            }
        });
    }
}
