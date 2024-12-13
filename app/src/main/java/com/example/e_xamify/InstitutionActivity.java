package com.example.e_xamify;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class InstitutionActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int user_id;
    private Button showEnrollmentKeyButton, createModuleButton, viewModulesButton;
    private LinearLayout enrollmentKeyLayout;
    private boolean isEnrollmentKeyVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institution);
        dbHelper = new DatabaseHelper(this);
        user_id = getIntent().getIntExtra("user_id", -1);
        if (user_id == -1) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        showEnrollmentKeyButton = findViewById(R.id.showEnrollmentKeyButton);
        createModuleButton = findViewById(R.id.createModuleButton);
        viewModulesButton = findViewById(R.id.viewModulesButton);

        showEnrollmentKeyButton.setOnClickListener(v -> toggleEnrollmentKeyVisibility());
        createModuleButton.setOnClickListener(v -> {
            Intent intent = new Intent(InstitutionActivity.this, ModuleActivity.class);
            intent.putExtra("user_id", user_id);
            startActivity(intent);
        });
        viewModulesButton.setOnClickListener(v -> {
            Intent intent = new Intent(InstitutionActivity.this, ViewModulesActivity.class);
            intent.putExtra("user_id", user_id);
            startActivity(intent);
        });
    }

    private void toggleEnrollmentKeyVisibility() {
        if (enrollmentKeyLayout == null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            enrollmentKeyLayout = (LinearLayout) inflater.inflate(R.layout.enrollment_key_layout, null);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT institution_enrolment_key FROM institution WHERE user_id = ?", new String[]{String.valueOf(user_id)});
            if (cursor.moveToFirst()) {
                String enrollmentKey = cursor.getString(0);
                TextView enrollmentKeyTextView = enrollmentKeyLayout.findViewById(R.id.enrollmentKeyTextView);
                enrollmentKeyTextView.setText(enrollmentKey);
                enrollmentKeyTextView.setTextIsSelectable(true);
            } else {
                Toast.makeText(this, "No enrollment key found for this institution.", Toast.LENGTH_SHORT).show();
                cursor.close();
                return;
            }
            cursor.close();
            LinearLayout mainLayout = findViewById(R.id.main_layout);
            mainLayout.addView(enrollmentKeyLayout);
        }
        if (isEnrollmentKeyVisible) {
            enrollmentKeyLayout.setVisibility(View.GONE);
        } else {
            enrollmentKeyLayout.setVisibility(View.VISIBLE);
        }
        isEnrollmentKeyVisible = !isEnrollmentKeyVisible;
    }
}
