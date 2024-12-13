package com.example.e_xamify;

public class Institution {
    private int user_id;
    private String institution_name;
    private String institution_address;
    private String institution_email;
    private String institution_phone;
    private String institution_enrolment_key;
    private String institution_date_joined;

    public Institution(int user_id, String institution_name, String institution_address, String institution_email, String institution_phone, String institution_enrolment_key, String institution_date_joined) {
        this.user_id = user_id;
        this.institution_name = institution_name;
        this.institution_address = institution_address;
        this.institution_email = institution_email;
        this.institution_phone = institution_phone;
        this.institution_enrolment_key = institution_enrolment_key;
        this.institution_date_joined = institution_date_joined;
    }

    public int getUserId() {
        return user_id;
    }

    public String getInstitutionName() {
        return institution_name;
    }

    public String getInstitutionAddress() {
        return institution_address;
    }

    public String getInstitutionEmail() {
        return institution_email;
    }

    public String getInstitutionPhone() {
        return institution_phone;
    }

    public String getInstitutionEnrolmentKey() {
        return institution_enrolment_key;
    }

    public String getInstitutionDateJoined() {
        return institution_date_joined;
    }

    @Override
    public String toString() {
        return institution_name;
    }
}
