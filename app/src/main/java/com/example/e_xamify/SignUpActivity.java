package com.example.e_xamify;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput, confirmPasswordInput, usernameInput;
    private EditText institutionNameInput, institutionPhoneInput, institutionAddressInput;
    private EditText teacherNameInput, teacherFieldInput;
    private EditText studentNameInput;
    private RadioGroup userRoleLayout;
    private Button signUpButton;
    private DatabaseHelper dbHelper;
    private HashMap<String, Integer> roleIdMap; // To store role names and IDs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        dbHelper = new DatabaseHelper(this);
        roleIdMap = new HashMap<>(); // Initialize the map

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        usernameInput = findViewById(R.id.usernameInput);
        userRoleLayout = findViewById(R.id.userRoleLayout);
        signUpButton = findViewById(R.id.signUpButton);
        institutionNameInput = findViewById(R.id.institutionNameInput);
        institutionPhoneInput = findViewById(R.id.institutionPhoneInput);
        institutionAddressInput = findViewById(R.id.institutionAddressInput);
        teacherNameInput = findViewById(R.id.teacherNameInput);
        teacherFieldInput = findViewById(R.id.teacherFieldInput);
        studentNameInput = findViewById(R.id.studentNameInput);

        // Load user roles from the database
        loadUserRoles();

        // Handle user role selection
        userRoleLayout.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            if (selectedRadioButton != null) {
                String userRole = selectedRadioButton.getText().toString();
                handleUserRoleSelection(userRole);
            }
        });

        signUpButton.setOnClickListener(v -> registerUser());
    }

    private void loadUserRoles() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT role_name, user_role_id FROM user_role", null);

        while (cursor.moveToNext()) {
            String roleName = cursor.getString(0);
            int roleId = cursor.getInt(1);
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(roleName);
            userRoleLayout.addView(radioButton); // Add RadioButton to RadioGroup
            roleIdMap.put(roleName, roleId); // Store the role ID in the map
        }
        cursor.close();
    }

    private void handleUserRoleSelection(String userRole) {
        // Show/hide fields based on user role
        if ("Institution".equals(userRole)) {
            institutionNameInput.setVisibility(View.VISIBLE);
            institutionPhoneInput.setVisibility(View.VISIBLE);
            institutionAddressInput.setVisibility(View.VISIBLE);
            teacherNameInput.setVisibility(View.GONE);
            teacherFieldInput.setVisibility(View.GONE);
            studentNameInput.setVisibility(View.GONE);
        } else if ("Teacher".equals(userRole)) {
            teacherNameInput.setVisibility(View.VISIBLE);
            teacherFieldInput.setVisibility(View.VISIBLE);
            institutionNameInput.setVisibility(View.GONE);
            institutionPhoneInput.setVisibility(View.GONE);
            institutionAddressInput.setVisibility(View.GONE);
            studentNameInput.setVisibility(View.GONE);
        } else if ("Student".equals(userRole)) {
            studentNameInput.setVisibility(View.VISIBLE);
            institutionNameInput.setVisibility(View.GONE);
            institutionPhoneInput.setVisibility(View.GONE);
            institutionAddressInput.setVisibility(View.GONE);
            teacherNameInput.setVisibility(View.GONE);
            teacherFieldInput.setVisibility(View.GONE);
        }
    }

    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected user role
        String userRole = getSelectedUserRole();
        if (userRole == null) {
            Toast.makeText(this, "Please select a user role.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues userValues = new ContentValues();
        userValues.put("user_email", email);
        userValues.put("user_password", password);
        userValues.put("user_name", username);
        userValues.put("joined_date", getCurrentDate());
        userValues.put("user_role_id", roleIdMap.get(userRole)); // Use the role ID from the map

        // Insert into the user table and get the new user ID
        long newUserId = db.insert("user", null, userValues);
        if (newUserId == -1) {
            Toast.makeText(this, "Sign up failed. Please try again.", Toast.LENGTH_SHORT).show();
            return; // Exit if user insertion fails
        }

        // Include details based on the selected role
        ContentValues roleValues = new ContentValues();
        if ("Institution".equals(userRole)) {
            roleValues.put("user_id", newUserId); // Foreign key
            roleValues.put("institution_name", institutionNameInput.getText().toString().trim());
            roleValues.put("institution_phone", institutionPhoneInput.getText().toString().trim());
            roleValues.put("institution_address", institutionAddressInput.getText().toString().trim());
            roleValues.put("institution_enrolment_key", generateEnrollmentKey());
            long institutionId = db.insert("institution", null, roleValues);
            if (institutionId == -1) {
                Toast.makeText(this, "Institution details insertion failed.", Toast.LENGTH_SHORT).show();
            }
        } else if ("Teacher".equals(userRole)) {
            roleValues.put("user_id", newUserId); // Foreign key
            roleValues.put("teacher_name", teacherNameInput.getText().toString().trim());
            roleValues.put("teacher_field", teacherFieldInput.getText().toString().trim());
            long teacherId = db.insert("teacher", null, roleValues);
            if (teacherId == -1) {
                Toast.makeText(this, "Teacher details insertion failed.", Toast.LENGTH_SHORT).show();
            }
        } else if ("Student".equals(userRole)) {
            roleValues.put("user_id", newUserId); // Foreign key
            roleValues.put("student_name", studentNameInput.getText().toString().trim());
            long studentId = db.insert("student", null, roleValues);
            if (studentId == -1) {
                Toast.makeText(this, "Student details insertion failed.", Toast.LENGTH_SHORT).show();
            }
        }

        Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show();
        finish(); // Close this activity
    }


    private String generateEnrollmentKey() {
        StringBuilder enrollmentKey = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 25; i++) {
            enrollmentKey.append((char) (random.nextInt(26) + 'A')); // Generate random uppercase letters
        }
        return enrollmentKey.toString();
    }

    private String getSelectedUserRole() {
        int selectedId = userRoleLayout.getCheckedRadioButtonId(); // Get selected RadioButton ID
        if (selectedId != -1) {
            RadioButton radioButton = findViewById(selectedId); // Find the selected RadioButton
            return radioButton.getText().toString(); // Return the text of the selected RadioButton
        }
        return null;
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}
