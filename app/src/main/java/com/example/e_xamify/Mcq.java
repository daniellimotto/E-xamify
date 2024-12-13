package com.example.e_xamify;

public class Mcq extends Question {
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private int correctOption;
    private int question_id;

    public Mcq(int question_id, int quiz_id, int question_num, String question_text, int question_type_id, String question_img_url, String optionA, String optionB, String optionC, String optionD, int correctOption) {
        super(question_id, quiz_id, question_num, question_text, question_type_id, question_img_url);
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.question_id = question_id;
    }

    public Mcq() {
        super(); 
    }

    public int getQuestionId() {
        return question_id;
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

    public void setQuestionId(int question_id) {
        this.question_id = question_id;
    }

    @Override
    public String toString() {
        return "Mcq{" +
                "question_id=" + question_id +
                ", optionA='" + optionA + '\'' +
                ", optionB='" + optionB + '\'' +
                ", optionC='" + optionC + '\'' +
                ", optionD='" + optionD + '\'' +
                ", correctOption=" + correctOption +
                '}';
    }
}
