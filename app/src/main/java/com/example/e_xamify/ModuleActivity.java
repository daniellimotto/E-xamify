package com.example.e_xamify;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ModuleActivity extends AppCompatActivity {

    private EditText moduleNameInput, moduleDescriptionInput;
    private Button createModuleButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);

        // Initialize the database helper
        dbHelper = new DatabaseHelper(this);

        moduleNameInput = findViewById(R.id.moduleNameInput);
        moduleDescriptionInput = findViewById(R.id.moduleDescriptionInput);
        createModuleButton = findViewById(R.id.createModuleButton);

        createModuleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createModule();
            }
        });
    }

    private void createModule() {
        String moduleName = moduleNameInput.getText().toString().trim();
        String moduleDescription = moduleDescriptionInput.getText().toString().trim();

        if (moduleName.isEmpty() || moduleDescription.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("module_name", moduleName);
        values.put("module_description", moduleDescription);

        long newModuleId = db.insert("module", null, values);
        if (newModuleId == -1) {
            Toast.makeText(this, "Module creation failed. Please try again.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Module created successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity and return to the previous one
        }
    }
}
