package com.example.e_xamify;

public class Teacher {
    private String teacher_email;
    private String teacher_password;
    private String teacher_name;
    private String teacher_joined_date;
    private int teacher_role_id;
    private int institution_id;

    public Teacher(String teacher_email, String teacher_password, String teacher_name, String teacher_joined_date, int teacher_role_id, int institution_id) {
        this.teacher_email = teacher_email;
        this.teacher_password = teacher_password;
        this.teacher_name = teacher_name;
        this.teacher_joined_date = teacher_joined_date;
        this.teacher_role_id = teacher_role_id;
        this.institution_id = institution_id;
    }

    public String getTeacherEmail() {
        return teacher_email;
    }

    public String getTeacherPassword() {
        return teacher_password;
    }      

    public String getTeacherName() {
        return teacher_name;
    }

    public String getTeacherJoinedDate() {
        return teacher_joined_date;
    }

    public int getTeacherRoleId() {
        return teacher_role_id;
    }

    public int getInstitutionId() {
        return institution_id;
    }

    public String toString() {
        return "Teacher{" +
                "teacher_email='" + teacher_email + '\'' +
                '}';
    }
   
}
