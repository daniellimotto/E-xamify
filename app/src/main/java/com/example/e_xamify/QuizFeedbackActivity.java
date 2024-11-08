package com.example.e_xamify;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class QuizFeedbackActivity extends AppCompatActivity {

    private LinearLayout questionsContainer;
    private Button toggleFeedbackButton;
    private Button submitFeedbackButton;
    private List<Question> quizData;
    private List<EditText> feedbackInputs;
    private boolean showingFeedback = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_feedback);

        questionsContainer = findViewById(R.id.questionsContainer);
        toggleFeedbackButton = findViewById(R.id.toggleFeedbackButton);
        submitFeedbackButton = findViewById(R.id.submitFeedbackButton);

        // TODO: Replace this with actual quiz data from your database
        quizData = getMockQuizData();
        feedbackInputs = new ArrayList<>();

        displayQuizQuestions();

        toggleFeedbackButton.setOnClickListener(v -> toggleFeedback());
        submitFeedbackButton.setOnClickListener(v -> submitFeedback());
    }

    private void displayQuizQuestions() {
        for (int i = 0; i < quizData.size(); i++) {
            Question question = quizData.get(i);
            View questionView = getLayoutInflater().inflate(R.layout.item_question_feedback, null);

            TextView questionTextView = questionView.findViewById(R.id.questionTextView);
            RadioGroup optionsRadioGroup = questionView.findViewById(R.id.optionsRadioGroup);
            EditText feedbackEditText = questionView.findViewById(R.id.feedbackEditText);

            questionTextView.setText(String.format("Question %d: %s", i + 1, question.getQuestionText()));

            String[] options = {question.getOptionA(), question.getOptionB(), question.getOptionC(), question.getOptionD()};
            for (int j = 0; j < options.length; j++) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(options[j]);
                radioButton.setEnabled(false);
                // Note: We don't have student's answer in this structure, so we'll leave all unchecked
                optionsRadioGroup.addView(radioButton);
            }

            feedbackInputs.add(feedbackEditText);
            questionsContainer.addView(questionView);
        }
    }

    private void toggleFeedback() {
        showingFeedback = !showingFeedback;
        toggleFeedbackButton.setText(showingFeedback ? "Hide Correct Answers" : "Show Correct Answers");
        submitFeedbackButton.setVisibility(showingFeedback ? View.VISIBLE : View.GONE);

        for (int i = 0; i < quizData.size(); i++) {
            Question question = quizData.get(i);
            View questionView = questionsContainer.getChildAt(i);
            RadioGroup optionsRadioGroup = questionView.findViewById(R.id.optionsRadioGroup);
            EditText feedbackEditText = questionView.findViewById(R.id.feedbackEditText);

            for (int j = 0; j < optionsRadioGroup.getChildCount(); j++) {
                RadioButton radioButton = (RadioButton) optionsRadioGroup.getChildAt(j);
                if (showingFeedback) {
                    if (j + 1 == question.getCorrectOption()) {
                        radioButton.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    }
                    // Note: We don't have student's answer in this structure, so we can't highlight wrong answers
                } else {
                    radioButton.setTextColor(getResources().getColor(android.R.color.black));
                }
            }

            feedbackEditText.setVisibility(showingFeedback ? View.VISIBLE : View.GONE);
        }
    }

    private void submitFeedback() {
        List<String> feedbackList = new ArrayList<>();
        for (EditText feedbackInput : feedbackInputs) {
            feedbackList.add(feedbackInput.getText().toString());
        }
        // TODO: Save feedback to database
        // For now, we'll just print it to the console
        for (int i = 0; i < feedbackList.size(); i++) {
            System.out.println("Feedback for question " + (i + 1) + ": " + feedbackList.get(i));
        }
    }

    private List<Question> getMockQuizData() {
        List<Question> quizData = new ArrayList<>();

        // Question 1
        Question q1 = new Question(
                1,  // question_id
                1,  // quiz_id
                "What is the capital of France?",
                1,  // question_type_id (assuming 1 is for MCQ)
                null,  // question_img_url
                "London",
                "Berlin",
                "Paris",
                "Madrid",
                3  // correctOption (3 for C, which is Paris)
        );
        quizData.add(q1);

        // Question 2
        Question q2 = new Question(
                2,  // question_id
                1,  // quiz_id
                "Which planet is known as the Red Planet?",
                1,  // question_type_id (assuming 1 is for MCQ)
                null,  // question_img_url
                "Venus",
                "Mars",
                "Jupiter",
                "Saturn",
                2  // correctOption (2 for B, which is Mars)
        );
        quizData.add(q2);

        return quizData;
    }
}