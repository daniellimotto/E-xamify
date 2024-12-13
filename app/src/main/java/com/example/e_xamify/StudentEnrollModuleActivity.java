package com.example.e_xamify;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class StudentEnrollModuleActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText moduleKeyInput;
    private Button enrollButton;
    private int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_enroll_module);

        dbHelper = new DatabaseHelper(this);
        user_id = getIntent().getIntExtra("user_id", -1);

        if (user_id == -1) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        moduleKeyInput = findViewById(R.id.moduleKeyInput);
        enrollButton = findViewById(R.id.enrollButton);

        enrollButton.setOnClickListener(v -> enrollToModule());
    }

    private void enrollToModule() {
        String moduleKey = moduleKeyInput.getText().toString().trim();

        if (moduleKey.isEmpty()) {
            Toast.makeText(this, "Please enter a module key.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor moduleCursor = db.rawQuery("SELECT module_id, institution_id FROM module WHERE module_key = ?",
                new String[]{moduleKey});

        if (moduleCursor.moveToFirst()) {
            int moduleId = moduleCursor.getInt(0);
            int institutionId = moduleCursor.getInt(1);

            Cursor institutionCursor = db.rawQuery("SELECT * FROM student_institution WHERE student_id = ? AND institution_id = ?",
                    new String[]{String.valueOf(user_id), String.valueOf(institutionId)});

            if (!institutionCursor.moveToFirst()) {
                Toast.makeText(this, "You are not enrolled in this institution.", Toast.LENGTH_SHORT).show();
                institutionCursor.close();
                moduleCursor.close();
                return;
            }
            institutionCursor.close();

            Cursor moduleCheckCursor = db.rawQuery("SELECT * FROM student_module WHERE user_id = ? AND module_id = ?",
                    new String[]{String.valueOf(user_id), String.valueOf(moduleId)});

            if (moduleCheckCursor.moveToFirst()) {
                Toast.makeText(this, "You are already enrolled in this module.", Toast.LENGTH_SHORT).show();
                moduleCheckCursor.close();
                moduleCursor.close();
                return;
            }
            moduleCheckCursor.close();

            db.execSQL("INSERT INTO student_module (user_id, module_id, enrollment_date) VALUES (?, ?, datetime('now'))",
                    new Object[]{user_id, moduleId});

            Toast.makeText(this, "Successfully enrolled in module!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Invalid module key.", Toast.LENGTH_SHORT).show();
        }
        moduleCursor.close();
    }
}
