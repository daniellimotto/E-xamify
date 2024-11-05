package com.example.e_xamify;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TQuestionActivity extends AppCompatActivity {
    private EditText questionInput;
    private EditText optionAInput;
    private EditText optionBInput;
    private EditText optionCInput;
    private EditText optionDInput;
    DatabaseHelper dbHelper = new DatabaseHelper(this);
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tquestion);

        questionInput = findViewById(R.id.questionInput);
        optionAInput = findViewById(R.id.optionAInput);
        optionBInput = findViewById(R.id.optionBInput);
        optionCInput = findViewById(R.id.optionCInput);
        optionDInput = findViewById(R.id.optionDInput);

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQuestion();
            }
        });
    }

    private void saveQuestion() {
        String question = questionInput.getText().toString();
        String optionA = optionAInput.getText().toString();
        String optionB = optionBInput.getText().toString();
        String optionC = optionCInput.getText().toString();
        String optionD = optionDInput.getText().toString();

        if (question.isEmpty() || optionA.isEmpty() || optionB.isEmpty() || optionC.isEmpty() || optionD.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            // TODO: Save the question and options to a database or send to a server
            Toast.makeText(this, "Question saved", Toast.LENGTH_SHORT).show();
        }
    }
}
