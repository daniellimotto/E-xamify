package com.example.e_xamify;

import java.util.List;

public class Question {
    private int question_id;
    private int quiz_id;
    private String question_text;
    private int question_type_id;
    private String question_img_url;

    // Fields for options
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    // Field for correct answer (1 for A, 2 for B, 3 for C, 4 for D)
    private int correctOption;

    public Question(int question_id, int quiz_id, String question_text, int question_type_id,
                    String question_img_url, String optionA, String optionB, String optionC, String optionD, int correctOption) {
        this.question_id = question_id;
        this.quiz_id = quiz_id;
        this.question_text = question_text;
        this.question_type_id = question_type_id;
        this.question_img_url = question_img_url;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
    }
    public Question(){
        
    }

    // Getters
    public int getQuestionId() {
        return question_id;
    }

    public int getQuizId() {
        return quiz_id;
    }

    public String getQuestionText() {
        return question_text;
    }

    public int getQuestionTypeId() {
        return question_type_id;
    }

    public String getQuestionImgUrl() {
        return question_img_url;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public int getCorrectOption() {
        return correctOption;
    }

    // Setters
    public void setQuestionText(String questionText) {
        this.question_text = questionText;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public void setCorrectOption(int correctOption) {
        this.correctOption = correctOption;
    }

    // toString() method for easy display of the question details
    @Override
    public String toString() {
        return "Question{" +
                "question_id=" + question_id +
                ", question_text='" + question_text + '\'' +
                ", optionA='" + optionA + '\'' +
                ", optionB='" + optionB + '\'' +
                ", optionC='" + optionC + '\'' +
                ", optionD='" + optionD + '\'' +
                ", correctOption=" + correctOption +
                '}';
    }
}
