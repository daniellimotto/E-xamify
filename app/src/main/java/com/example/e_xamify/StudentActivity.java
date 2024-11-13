package com.example.e_xamify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class StudentActivity extends AppCompatActivity {

    private TextView welcomeText;
    private Button dashboardButton;
    private Button enrollButton;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        // Initialize views
        welcomeText = findViewById(R.id.welcomeText);
        dashboardButton = findViewById(R.id.dashboardButton);
        enrollButton = findViewById(R.id.enrollButton);

        // Get user ID from intent
        userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set welcome message
        welcomeText.setText("Welcome, Student!");

        // Set up button click listeners
        dashboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentActivity.this, StudentDashboardActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

        enrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentActivity.this, StudentEnrollmentActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data if needed when returning to this activity
    }
}