package com.example.e_xamify;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AssignmentResultActivity extends AppCompatActivity {
    private int assignmentId;
    private int user_id;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_result);

        assignmentId = getIntent().getIntExtra("assignmentId", -1);
        user_id = getIntent().getIntExtra("user_id", -1);

        if (assignmentId == -1 || user_id == -1) {
            finish();
            return;
        }

        resultText = findViewById(R.id.resultText);
        loadAssignmentResult();
    }

    private void loadAssignmentResult() {
        // Load the result from the database
        // This is a placeholder for actual database logic
        String result = "You scored 85%";
        resultText.setText(result);
    }
}