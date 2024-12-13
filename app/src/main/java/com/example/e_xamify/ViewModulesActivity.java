package com.example.e_xamify;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ViewModulesActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int user_id;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_modules);

        dbHelper = new DatabaseHelper(this);
        user_id = getIntent().getIntExtra("user_id", -1);

        if (user_id == -1) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tableLayout = findViewById(R.id.moduleTable);

        displayModules();
    }

    private void displayModules() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT module_name, module_description, module_key FROM module WHERE institution_id = ?",
                new String[]{String.valueOf(user_id)});

        if (cursor.moveToFirst()) {
            do {
                String moduleName = cursor.getString(cursor.getColumnIndexOrThrow("module_name"));
                String moduleDescription = cursor.getString(cursor.getColumnIndexOrThrow("module_description"));
                String moduleKey = cursor.getString(cursor.getColumnIndexOrThrow("module_key"));

                TableRow tableRow = new TableRow(this);

                TextView nameTextView = new TextView(this);
                nameTextView.setText(moduleName);
                nameTextView.setPadding(8, 8, 8, 8);
                nameTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                TextView descriptionTextView = new TextView(this);
                descriptionTextView.setText(moduleDescription);
                descriptionTextView.setPadding(8, 8, 8, 8);
                descriptionTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                TextView keyTextView = new TextView(this);
                keyTextView.setText(moduleKey);
                keyTextView.setPadding(8, 8, 8, 8);
                keyTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                keyTextView.setTextIsSelectable(true);
                tableRow.addView(nameTextView);
                tableRow.addView(createVerticalDivider());
                tableRow.addView(descriptionTextView);
                tableRow.addView(createVerticalDivider());
                tableRow.addView(keyTextView);

                // Table dividers
                tableLayout.addView(tableRow);
                tableLayout.addView(createHorizontalDivider());
            } while (cursor.moveToNext());
        } else {
            Toast.makeText(this, "No modules found for this institution.", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    private View createHorizontalDivider() {
        View divider = new View(this);
        divider.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                2)); // height
        divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        return divider;
    }

    private View createVerticalDivider() {
        View divider = new View(this);
        divider.setLayoutParams(new TableRow.LayoutParams(
                1, // width
                TableRow.LayoutParams.MATCH_PARENT));
        divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        return divider;
    }
}
