package com.example.e_xamify;

public class Quiz {
    private long quiz_id;
    private int quiz_type_id;
    private String quiz_title;
    private int quiz_duration;
    private String instructions;
    private int quiz_attempts;
    private int user_id;  // Now refers specifically to a teacher
    private int quiz_navigable;
    private int quiz_tab_restrictor;
    private int question_randomize;
    private int module_id;  // Added to align with the schema
    private int num_questions;  // New field added as per schema

    public Quiz(long quiz_id, int quiz_type_id, String quiz_title, int quiz_duration, String instructions, int quiz_attempts, int user_id, int quiz_navigable, int quiz_tab_restrictor, int question_randomize, int module_id, int num_questions) {
        this.quiz_id = quiz_id;
        this.quiz_type_id = quiz_type_id;
        this.quiz_title = quiz_title;
        this.quiz_duration = quiz_duration;
        this.instructions = instructions;
        this.quiz_attempts = quiz_attempts;
        this.user_id = user_id;
        this.quiz_navigable = quiz_navigable;
        this.quiz_tab_restrictor = quiz_tab_restrictor;
        this.question_randomize = question_randomize;
        this.module_id = module_id;
        this.num_questions = num_questions;
    }

    public Quiz(long id, String title) {
        this.quiz_id = id;
        this.quiz_title = title;
    }

    public Quiz(int quizId, int moduleId, int quizAttempts) {
        this.quiz_id = quizId;
        this.module_id = moduleId;
        this.quiz_attempts = quizAttempts;
    }

    // Getters
    public long getQuizId() {
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

    public int getModuleId() {
        return module_id;
    }

    public int getNumQuestions() {
        return num_questions;
    }

    // toString() method for easy display
    @Override
    public String toString() {
        return "Quiz{" +
                "quiz_id=" + quiz_id +
                ", quiz_title='" + quiz_title + '\'' +
                '}';
    }
}
