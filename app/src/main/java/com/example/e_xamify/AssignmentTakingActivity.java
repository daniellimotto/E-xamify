package com.example.e_xamify;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import java.util.ArrayList;
import java.util.List;



public class AssignmentTakingActivity extends AppCompatActivity {
    private int assignmentId;
    private int user_id;
    private int quizId;
    private TextView questionText;
    private TextView questionNumberText;
    private RadioGroup optionsGroup;
    private Button nextButton;
    private Button previousButton;
    private Button submitButton;
    private List<Mcq> questions;
    private int currentQuestionIndex = 0;
    private DatabaseHelper dbHelper;
    private long timeLeftInMillis;
    private CountDownTimer countDownTimer;
    private TextView timerText;
    private boolean isTabRestrictorEnabled;
    private int tabSwitchCount = 0; // Track how many times the user switches tabs
    private boolean isPenaltyApplied = false; // Whether the penalty has been applied
    private CountDownTimer penaltyTimer; // Single instance for penalty timer
    private boolean isSubmitting = false; // Track if the user is submitting
    private boolean isTimerRunning = false; // Track if the penalty timer is running
    private OnBackInvokedCallback onBackInvokedCallback;


    private boolean isNavigable; // To control backward navigation

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void showBackConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Submit Assignment")
                .setMessage("Do you want to submit the assignment?")
                .setPositiveButton("Submit", (dialog, which) -> {
                    saveSelectedOption(); // Save the current selection
                    submitAssignment();   // Submit the assignment
                })
                .setNegativeButton("Continue", (dialog, which) -> {
                    // Do nothing, just dismiss the dialog
                    dialog.dismiss();
                })
                .setCancelable(true)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_taking);

        createNotificationChannel();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedCallback = () -> showBackConfirmationDialog();
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT, onBackInvokedCallback);
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        assignmentId = getIntent().getIntExtra("assignmentId", -1);
        user_id = getIntent().getIntExtra("user_id", -1);

        if (assignmentId == -1 || user_id == -1) {
            Toast.makeText(this, "Error loading assignment", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        quizId = getQuizIdForAssignment(assignmentId);

        // Initialize isTabRestrictorEnabled AFTER dbHelper initialization
        isTabRestrictorEnabled = getQuizTabRestrictor(quizId);

        isNavigable = getQuizNavigable(quizId); // Fetch whether backward navigation is allowed

        initializeViews();
        loadQuestions();
        showQuestion(currentQuestionIndex);
        startTimer();
    }


    private void initializeViews() {
        questionText = findViewById(R.id.questionText);
        optionsGroup = findViewById(R.id.optionsGroup);
        nextButton = findViewById(R.id.nextButton);
        previousButton = findViewById(R.id.previousButton);
        submitButton = findViewById(R.id.submitButton);
        timerText = findViewById(R.id.timerText); // Ensure this line is present
        questionNumberText = findViewById(R.id.questionNumberText);

        if (!isNavigable) {
            previousButton.setVisibility(View.GONE); // Hide Previous button if backward navigation is disabled
        }

        nextButton.setOnClickListener(v -> {
            saveSelectedOption();
            showNextQuestion();
        });
        previousButton.setOnClickListener(v -> {
            saveSelectedOption();
            showPreviousQuestion();
        });
        submitButton.setOnClickListener(v -> {
            saveSelectedOption();
            submitAssignment();
        });
    }

    private boolean getQuizNavigable(int quizId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT quiz_navigable FROM quiz WHERE quiz_id = ?", new String[]{String.valueOf(quizId)});
        boolean navigable = false;
        if (cursor.moveToFirst()) {
            navigable = cursor.getInt(0) == 1;
        }
        cursor.close();
        return navigable;
    }

    private int getQuizIdForAssignment(int assignmentId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT quiz_id FROM assignment WHERE assignment_id = ?", new String[]{String.valueOf(assignmentId)});
        int quizId = -1;
        if (cursor.moveToFirst()) {
            quizId = cursor.getInt(0);
        }
        cursor.close();
        return quizId;
    }

    private void loadQuestions() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT q.question_id, q.question_text, m.optionA, m.optionB, m.optionC, m.optionD, m.correctOption, q.question_number " +
                        "FROM question q " +
                        "JOIN mcq m ON q.question_id = m.question_id " +
                        "WHERE q.quiz_id = ?",
                new String[]{String.valueOf(quizId)}
        );
        questions = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String text = cursor.getString(1);
            String optionA = cursor.getString(2);
            String optionB = cursor.getString(3);
            String optionC = cursor.getString(4);
            String optionD = cursor.getString(5);
            int correctOption = cursor.getInt(6);
            int question_number = cursor.getInt(7);
            questions.add(new Mcq(id, quizId, question_number, text, 0, null, optionA, optionB, optionC, optionD, correctOption));
        }
        cursor.close();
    }

    private void showQuestion(int index) {
        if (index < 0 || index >= questions.size()) return;

        Mcq question = questions.get(index);
        questionText.setText(question.getQuestionText());
        questionNumberText.setText("Question " + String.valueOf(question.getQuestionNum()));

        optionsGroup.removeAllViews();
        RadioButton optionA = new RadioButton(this);
        optionA.setText(question.getOptionA());
        optionsGroup.addView(optionA);

        RadioButton optionB = new RadioButton(this);
        optionB.setText(question.getOptionB());
        optionsGroup.addView(optionB);

        RadioButton optionC = new RadioButton(this);
        optionC.setText(question.getOptionC());
        optionsGroup.addView(optionC);

        RadioButton optionD = new RadioButton(this);
        optionD.setText(question.getOptionD());
        optionsGroup.addView(optionD);

        loadSelectedOption(question.getQuestionId());

        if (index == questions.size() - 1) {
            nextButton.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
        }
    }

    private void loadSelectedOption(int questionId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT selected_option_id FROM quiz_submission WHERE assignment_id = ? AND question_id = ?", new String[]{String.valueOf(assignmentId), String.valueOf(questionId)});
        if (cursor.moveToFirst()) {
            int selectedOptionId = cursor.getInt(0);
            ((RadioButton) optionsGroup.getChildAt(selectedOptionId)).setChecked(true);
        }
        cursor.close();
    }

    private void saveSelectedOption() {
        int selectedOptionId = optionsGroup.indexOfChild(findViewById(optionsGroup.getCheckedRadioButtonId()));
        if (selectedOptionId == -1) return;

        Mcq currentQuestion = questions.get(currentQuestionIndex);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("selected_option_id", selectedOptionId);

        values.put("is_correct", selectedOptionId == (currentQuestion.getCorrectOption()) - 1 ? 1 : 0);


        int rowsAffected = db.update("quiz_submission", values, "assignment_id = ? AND question_id = ? AND user_id = ?",
                new String[]{String.valueOf(assignmentId), String.valueOf(currentQuestion.getQuestionId()), String.valueOf(user_id)});

        if (rowsAffected == 0) {
            values.put("assignment_id", assignmentId);
            values.put("question_id", currentQuestion.getQuestionId());
            values.put("user_id", user_id);
            db.insert("quiz_submission", null, values);
        }
    }

    private void showNextQuestion() {
        if (!isNavigable) {
            // Check if an option is selected
            int selectedOptionId = optionsGroup.indexOfChild(findViewById(optionsGroup.getCheckedRadioButtonId())) + 1;
            if (selectedOptionId == 0) {
                // No option selected, show a toast message
                Toast.makeText(this, "Please fill in an answer as you are not allowed to go back.", Toast.LENGTH_SHORT).show();
                return; // Prevent navigation
            }
        }

        // Proceed to the next question if conditions are met
        if (currentQuestionIndex < questions.size() - 1) {
            saveSelectedOption(); // Save the current answer before moving
            currentQuestionIndex++;
            showQuestion(currentQuestionIndex);
        }
    }


    private void showPreviousQuestion() {
        if (isNavigable && currentQuestionIndex > 0) { // Check if navigation is allowed
            currentQuestionIndex--;
            showQuestion(currentQuestionIndex);
        }
    }

    private void startTimer() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT quiz_duration FROM quiz WHERE quiz_id = ?", new String[]{String.valueOf(quizId)});
        int quizDuration = 0;
        if (cursor.moveToFirst()) {
            quizDuration = cursor.getInt(0);
        }
        cursor.close();
        timeLeftInMillis = quizDuration * 60 * 1000; // Convert minutes to milliseconds

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("TIMER", "onTick: " + millisUntilFinished);
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                saveSelectedOption();
                submitAssignment();
            }
        }.start();
    }

    private void updateTimer() {
        int hours = (int) (timeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((timeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timerText.setText(timeFormatted);
    }

    private void submitAssignment() {
        isSubmitting = true; // Set flag to prevent penalties
        if (penaltyTimer != null) {
            penaltyTimer.cancel(); // Cancel any running timers
            penaltyTimer = null;
        }
        isTimerRunning = false; // Update flag
        saveSelectedOption();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Iterate over all questions
        for (Mcq question : questions) {
            Cursor cursor = db.rawQuery(
                    "SELECT selected_option_id FROM quiz_submission WHERE assignment_id = ? AND question_id = ? AND user_id = ?",
                    new String[]{String.valueOf(assignmentId), String.valueOf(question.getQuestionId()), String.valueOf(user_id)}
            );

            // If no entry exists, insert a new entry with NULL for selected_option_id
            if (!cursor.moveToFirst()) {
                ContentValues values = new ContentValues();
                values.put("assignment_id", assignmentId);
                values.put("question_id", question.getQuestionId());
                values.put("user_id", user_id);
                values.put("selected_option_id", (Integer) null); // Set selected_option_id to NULL
                values.put("is_correct", 0); // Assume unanswered questions are incorrect
                db.insert("quiz_submission", null, values);
            }
            cursor.close();
        }

        Toast.makeText(this, "Assignment submitted!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, AssignmentResultActivity.class);
        intent.putExtra("assignmentId", assignmentId);
        intent.putExtra("user_id", user_id);
        startActivity(intent);
        finish(); // End current activity
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "penalty_channel";
            String channelName = "Penalty Notifications";
            String channelDescription = "Notifications for penalties when switching away from the quiz";
            int importance = android.app.NotificationManager.IMPORTANCE_HIGH;

            android.app.NotificationChannel channel = new android.app.NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            android.app.NotificationManager manager = getSystemService(android.app.NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    private void showPenaltyNotification(long secondsRemaining) {
        String channelId = "penalty_channel"; // Use the same ID from createNotificationChannel()

        android.app.NotificationManager manager = (android.app.NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Penalty Warning")
                .setContentText("Return to the quiz within " + secondsRemaining + " seconds, or your assignment will be submitted with penalty!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify(1, builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) { // 101 is the request code we used in Step 2
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Notification permission denied. Notifications won't work.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean getQuizTabRestrictor(int quizId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT quiz_tab_restrictor FROM quiz WHERE quiz_id = ?", new String[]{String.valueOf(quizId)});
        boolean restrictor = false;
        if (cursor.moveToFirst()) {
            restrictor = cursor.getInt(0) == 1;
        }
        cursor.close();
        return restrictor;
    }
    private void applyPenalty() {
        if (isPenaltyApplied) return; // Prevent multiple penalties
        isPenaltyApplied = true; // Mark penalty as applied
        Toast.makeText(this, "You exceeded the allowed tab switches. Assignment submitted.", Toast.LENGTH_SHORT).show();
        submitAssignment(); // Automatically submit assignment
    }


    private void startPenaltyTimer(long seconds) {
        if (penaltyTimer != null) {
            penaltyTimer.cancel(); // Cancel any existing timer
        }

        isTimerRunning = true;
        penaltyTimer = new CountDownTimer(seconds * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("PENALTY_TIMER", "Seconds remaining: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                if (!isSubmitting && (tabSwitchCount == 1 || tabSwitchCount == 2)) {
                    applyPenalty(); // Apply penalty if user didn't return in time
                }
            }
        }.start();
    }



    @Override
    protected void onPause() {
        super.onPause();
        if (isSubmitting) {
            return; // Ignore penalties if user is submitting
        }

        if (isTabRestrictorEnabled && !isPenaltyApplied) {
            tabSwitchCount++; // Increment tab switch count
            if (tabSwitchCount == 1 || tabSwitchCount == 2) {
                // Show warning notification for the first two switches
                showPenaltyNotification(10); // 10-second penalty warning
                startPenaltyTimer(10); // Start 10-second penalty timer
            } else if (tabSwitchCount >= 3) {
                // Apply penalty and submit assignment immediately on the third switch
                applyPenalty();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isPenaltyApplied) {
            finish(); // Close activity if penalty is applied
        } else if (isTimerRunning && (tabSwitchCount == 1 || tabSwitchCount == 2)) {
            // User returned within time, cancel the penalty timer
            if (penaltyTimer != null) {
                penaltyTimer.cancel();
                penaltyTimer = null; // Clear timer instance
            }
            isTimerRunning = false; // Update flag
            Toast.makeText(this, "You returned in time. Be cautious!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("assignmentId", assignmentId);
        outState.putInt("user_id", user_id);
        outState.putInt("quizId", quizId);
        outState.putInt("currentQuestionIndex", currentQuestionIndex);
        outState.putLong("timeLeftInMillis", timeLeftInMillis);
        outState.putBoolean("isTabRestrictorEnabled", isTabRestrictorEnabled);
        outState.putInt("tabSwitchCount", tabSwitchCount);
        outState.putBoolean("isPenaltyApplied", isPenaltyApplied);
        outState.putBoolean("isSubmitting", isSubmitting);
        outState.putBoolean("isTimerRunning", isTimerRunning);
        outState.putBoolean("isNavigable", isNavigable);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        assignmentId = savedInstanceState.getInt("assignmentId");
        user_id = savedInstanceState.getInt("user_id");
        quizId = savedInstanceState.getInt("quizId");
        currentQuestionIndex = savedInstanceState.getInt("currentQuestionIndex");
        timeLeftInMillis = savedInstanceState.getLong("timeLeftInMillis");
        isTabRestrictorEnabled = savedInstanceState.getBoolean("isTabRestrictorEnabled");
        tabSwitchCount = savedInstanceState.getInt("tabSwitchCount");
        isPenaltyApplied = savedInstanceState.getBoolean("isPenaltyApplied");
        isSubmitting = savedInstanceState.getBoolean("isSubmitting");
        isTimerRunning = savedInstanceState.getBoolean("isTimerRunning");
        isNavigable = savedInstanceState.getBoolean("isNavigable");

        // Restore the timer
        if (isTimerRunning) {
            startTimer();
        }
        showQuestion(currentQuestionIndex);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Handle the orientation change
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Do nothing, just prevent the activity from being recreated
        }
    }






}
