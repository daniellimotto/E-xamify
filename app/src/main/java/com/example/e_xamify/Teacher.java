package com.example.e_xamify;

public class Teacher {
    private int user_id;
    private String teacher_email;
    private String teacher_password;
    private String teacher_name;
    private String teacher_joined_date;
    private int teacher_role_id;
    private int institution_id;

    public Teacher(int user_id, String teacher_email, String teacher_password, String teacher_name, String teacher_joined_date, int teacher_role_id, int institution_id) {
        this.user_id = user_id;
        this.teacher_email = teacher_email;
        this.teacher_password = teacher_password;
        this.teacher_name = teacher_name;
        this.teacher_joined_date = teacher_joined_date;
        this.teacher_role_id = teacher_role_id;
        this.institution_id = institution_id;
    }

    public int getUserId() {
        return user_id;
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

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public void setTeacherEmail(String teacher_email) {
        this.teacher_email = teacher_email;
    }

    public void setTeacherPassword(String teacher_password) {
        this.teacher_password = teacher_password;
    }

    public void setTeacherName(String teacher_name) {
        this.teacher_name = teacher_name;
    }

    public void setTeacherJoinedDate(String teacher_joined_date) {
        this.teacher_joined_date = teacher_joined_date;
    }

    public void setTeacherRoleId(int teacher_role_id) {
        this.teacher_role_id = teacher_role_id;
    }

    public void setInstitutionId(int institution_id) {
        this.institution_id = institution_id;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "user_id=" + user_id +
                ", teacher_email='" + teacher_email + '\'' +
                ", teacher_name='" + teacher_name + '\'' +
                '}';
    }
}
