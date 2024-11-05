package com.example.e_xamify;

public class Student {
    private String student_email;
    private String student_password;
    private String student_name;
    private String student_joined_date;
    private int student_role_id;
    private int institution_id;

    public Student(String student_email, String student_password, String student_name, String student_joined_date, int student_role_id, int institution_id) {
        this.student_email = student_email;
        this.student_password = student_password;
        this.student_name = student_name;
        this.student_joined_date = student_joined_date;
        this.student_role_id = student_role_id;
        this.institution_id = institution_id;
    }

    public String getStudentEmail() {
        return student_email;
    }

    public String getStudentPassword() {
        return student_password;
    }

    public String getStudentName() {
        return student_name;
    }

    public String getStudentJoinedDate() {
        return student_joined_date;
    }

    public int getStudentRoleId() {
        return student_role_id;
    }

    public int getInstitutionId() {
        return institution_id;
    }

    public void setStudentEmail(String student_email) {
        this.student_email = student_email;
    }   

    public void setStudentPassword(String student_password) {
        this.student_password = student_password;
    }

    public void setStudentName(String student_name) {
        this.student_name = student_name;
    }

    public void setStudentJoinedDate(String student_joined_date) {
        this.student_joined_date = student_joined_date;
    }

    public void setStudentRoleId(int student_role_id) {
        this.student_role_id = student_role_id;
    }

    public void setInstitutionId(int institution_id) {
        this.institution_id = institution_id;
    }

    public String toString() {
        return "Student{" +
                "student_email='" + student_email + '\'' +
                ", student_password='" + student_password + '\'' +
                '}';
    }   


}
