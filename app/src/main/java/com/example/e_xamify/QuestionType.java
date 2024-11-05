package com.example.e_xamify;

public class QuestionType {
    private int question_type_id;
    private String type_name;
    private String type_description;

    public QuestionType(int question_type_id, String type_name, String type_description) {
        this.question_type_id = question_type_id;
        this.type_name = type_name;
        this.type_description = type_description;
    }

    public int getQuestionTypeId() {
        return question_type_id;
    }

    public String getTypeName() {
        return type_name;
    }

    public String getTypeDescription() {
        return type_description;
    }

    public String toString() {
        return "QuestionType{" +
                "question_type_id=" + question_type_id +
                '}';
    }
}
