package com.example.e_xamify;

public class Mcq {
    private int option_id;
    private int question_id;
    private String option_text;
    private int is_correct;

    public Mcq(int option_id, int question_id, String option_text, int is_correct) {
        this.option_id = option_id;
        this.question_id = question_id;
        this.option_text = option_text;
        this.is_correct = is_correct;
    }

    public int getOptionId() {
        return option_id;
    }

    public int getQuestionId() {
        return question_id;
    }

    public String getOptionText() {
        return option_text;
    }

    public int getIsCorrect() {
        return is_correct;
    }
    
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put("question_id", question_id);
        values.put("option_text", option_text);
        values.put("is_correct", is_correct);
        return values;
    }

    public String toString() {
        return "Mcq{" +
                "option_id=" + option_id +
                '}';
    }
}
