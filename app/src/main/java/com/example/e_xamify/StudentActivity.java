package com.example.e_xamify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class StudentActivity extends AppCompatActivity {

    private Button enrollButton;
    private TextView welcomeText;
    private long userId; // User ID retrieved from Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        // Retrieve the user ID passed from MainActivity
        userId = getIntent().getLongExtra("userId", -1); // Default value of -1 if not found
        if (userId == -1) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if user ID is not valid
            return;
        }

        welcomeText = findViewById(R.id.welcomeText);
        enrollButton = findViewById(R.id.enrollButton);

        // Display a welcome message
        welcomeText.setText("Welcome, Student!");

        // Set up the enroll button click listener
        enrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to navigate to EnrollmentActivity
                Intent intent = new Intent(StudentActivity.this, StudentEnrollmentActivity.class);
                intent.putExtra("userId", userId); // Pass the user ID to EnrollmentActivity
                startActivity(intent);
            }
        });
    }
}
