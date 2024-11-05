package com.example.e_xamify;

import java.util.Date;

public class Assignment {
    private int assignment_id;
    private int quiz_id;
    private int user_id;
    private String status;
    private int attempt_number_left;
    private int mark;
    private Date assignment_start_date;
    private Date assignment_end_date;

    public Assignment(int assignment_id, int quiz_id, int user_id, String status, int attempt_number_left, int mark, Date assignment_start_date, Date assignment_end_date) {
        this.assignment_id = assignment_id;
        this.quiz_id = quiz_id;
        this.user_id = user_id;
        this.status = status;
        this.attempt_number_left = attempt_number_left;
        this.mark = mark;
        this.assignment_start_date = assignment_start_date;
        this.assignment_end_date = assignment_end_date;
    }

    public int getAssignmentId() {
        return assignment_id;
    }   

    public int getQuizId() {
        return quiz_id;
    }

    public int getUserId() {
        return user_id;
    }

    public String getStatus() {
        return status;
    }

    public int getAttemptNumberLeft() {
        return attempt_number_left;
    }

    public int getMark() {
        return mark;
    }

    public Date getAssignmentStartDate() {
        return assignment_start_date;
    }

    public Date getAssignmentEndDate() {
        return assignment_end_date;
    }

    public String toString() {
        return "Assignment{" +
                "assignment_id=" + assignment_id +
                '}';
    }
}
