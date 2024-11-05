package com.example.e_xamify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class QuizInterface extends AppCompatActivity {

    private Button createMCQButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_interface);

        // "Create MCQ" Button
        createMCQButton = findViewById(R.id.createMCQButton);
        createMCQButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the Quiz Interface
                Intent intent = new Intent(QuizInterface.this, MCQEditorActivity.class);
                startActivity(intent);
            }
        });
    }
}
