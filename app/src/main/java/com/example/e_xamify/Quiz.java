package com.example.e_xamify;

public class Quiz {
    private int quiz_id;
    private int quiz_type_id;
    private String quiz_title;
    private int quiz_duration;
    private String instructions;
    private int quiz_attempts;
    private int user_id;
    private int quiz_navigable;
    private int quiz_tab_restrictor;
    private int question_randomize;

    public Quiz(int quiz_id, int quiz_type_id, String quiz_title, int quiz_duration, String instructions, int quiz_attempts, int user_id, int quiz_navigable, int quiz_tab_restrictor, int question_randomize) {
        this.quiz_id = quiz_id;
        this.quiz_type_id = quiz_type_id;
    }

    public int getQuizId() {
        return quiz_id;
    }

    public int getQuizTypeId() {
        return quiz_type_id;
    }

    public String getQuizTitle() {
        return quiz_title;
    }

    public int getQuizDuration() {
        return quiz_duration;
    }

    public String getInstructions() {
        return instructions;
    }

    public int getQuizAttempts() {
        return quiz_attempts;
    }

    public int getUserId() {
        return user_id;
    }

    public int getQuizNavigable() {
        return quiz_navigable;
    }

    public int getQuizTabRestrictor() {
        return quiz_tab_restrictor;
    }

    public int getQuestionRandomize() {
        return question_randomize;
    }

    public String toString() {
        return "Quiz{" +
                "quiz_id=" + quiz_id +
                '}';
    }
}
