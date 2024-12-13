package com.example.e_xamify;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class ModuleActivity extends AppCompatActivity {

    private EditText moduleNameInput, moduleDescriptionInput;
    private Button createModuleButton;
    private DatabaseHelper dbHelper;
    private int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module);

        dbHelper = new DatabaseHelper(this);
        user_id = getIntent().getIntExtra("user_id", -1);

        if (user_id == -1) {
            Toast.makeText(this, "Error: Institution ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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

        if (!isModuleNameUnique(moduleName)) {
            Toast.makeText(this, "Module Exist. Please choose a different module.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uniqueModuleKey = generateUniqueModuleKey();
        if (uniqueModuleKey == null) {
            Toast.makeText(this, "Failed to generate unique module key. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("module_name", moduleName);
        values.put("module_description", moduleDescription);
        values.put("institution_id", user_id);
        values.put("module_key", uniqueModuleKey);

        long newModuleId = db.insert("module", null, values);
        if (newModuleId == -1) {
            Toast.makeText(this, "Module creation failed. Please try again.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Module created successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Close activity
        }
    }

    private boolean isModuleNameUnique(String moduleName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM module WHERE module_name = ? AND institution_id = ?",
                new String[]{moduleName, String.valueOf(user_id)});
        boolean isUnique = !cursor.moveToFirst();
        cursor.close();
        return isUnique;
    }

    private String generateUniqueModuleKey() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String moduleKey;
        boolean isUnique;

        do {
            moduleKey = generateRandomKey(10);
            Cursor cursor = db.rawQuery("SELECT 1 FROM module WHERE module_key = ?", new String[]{moduleKey});
            isUnique = !cursor.moveToFirst();
            cursor.close();
        } while (!isUnique);

        return moduleKey;
    }

    private String generateRandomKey(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder keyBuilder = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            keyBuilder.append(characters.charAt(random.nextInt(characters.length())));
        }

        return keyBuilder.toString();
    }
}
