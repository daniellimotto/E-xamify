package com.example.e_xamify;

import java.util.List;

public class Question {
    private int question_id;
    private int quiz_id;
    private String question_text;
    private int question_type_id;
    private String question_img_url;
    private int question_num;

    public Question() {
        // Default constructor
    }

    public Question(int question_id, int quiz_id, int question_num, String question_text, int question_type_id, String question_img_url) {
        this.question_id = question_id;
        this.quiz_id = quiz_id;
        this.question_num = question_num;
        this.question_text = question_text;
        this.question_type_id = question_type_id;
        this.question_img_url = question_img_url;
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

    public int getQuestionNum() {
        return question_num;
    }

    // Setters
    public void setQuestionText(String questionText) {
        this.question_text = questionText;
    }

    public void SetQuestionId(int question_id) {
        this.question_id = question_id;
    }

    public void SetQuizId(int quiz_id) {
        this.quiz_id = quiz_id;
    }

    public void setQuestionNum(int question_num) {
        this.question_num = question_num;
    }

    // toString() method for easy display of the question details
    @Override
    public String toString() {
        return "Question ID: " + question_id + "\n" +
                "Quiz ID: " + quiz_id + "\n" +
                "Question Text: " + question_text + "\n" +
                "Question Type ID: " + question_type_id + "\n" +
                "Question Image URL: " + question_img_url + "\n" +
                "Question Number: " + question_num;
    }
}
