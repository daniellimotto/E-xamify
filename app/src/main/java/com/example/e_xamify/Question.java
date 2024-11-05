package com.example.e_xamify;

public class Question {
    private int question_id;
    private int quiz_id;
    private String question_text;
    private int question_type_id;
    private String question_img_url;

    public Question(int question_id, int quiz_id, String question_text, int question_type_id, String question_img_url) {
        this.question_id = question_id;
        this.quiz_id = quiz_id;
        this.question_text = question_text;
        this.question_type_id = question_type_id;
        this.question_img_url = question_img_url;
    }

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

    public String toString() {
        return "Question{" +
                "question_id=" + question_id +
                '}';
    }
}
