package com.example.e_xamify;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button signInButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        dbHelper = new DatabaseHelper(this);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signInButton = findViewById(R.id.signInButton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });
    }

    private void signInUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT user_role_id, user_id FROM user WHERE user_email = ? AND user_password = ?",
                new String[]{email, password});
        if (cursor.moveToFirst()) {
            int userRoleId = cursor.getInt(0);
            int user_id = cursor.getInt(1);
            navigateToRoleActivity(userRoleId, user_id);
        } else {
            Toast.makeText(this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    private void navigateToRoleActivity(int userRoleId, int user_id) {
        Intent intent;
        switch (userRoleId) {
            case 1: // Institution
                intent = new Intent(SignInActivity.this, InstitutionActivity.class);
                break;
            case 2: // Teacher
                intent = new Intent(SignInActivity.this, TeacherActivity.class);
                break;
            case 3: // Student
                intent = new Intent(SignInActivity.this, StudentActivity.class);
                break;
            default:
                Toast.makeText(this, "Unknown role.", Toast.LENGTH_SHORT).show();
                return;
        }
        intent.putExtra("user_id", user_id);
        startActivity(intent);
        finish(); // Close SignInActivity
    }
}
