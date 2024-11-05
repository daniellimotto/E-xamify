package com.example.e_xamify;

public class Institution {
    private int institution_id;
    private String institution_name;
    private String institution_address;
    private String institution_email;
    private String institution_phone;

    public Institution(int institution_id, String institution_name, String institution_address, String institution_email, String institution_phone) {
        this.institution_id = institution_id;
        this.institution_name = institution_name;
        this.institution_address = institution_address;
        this.institution_email = institution_email;
        this.institution_phone = institution_phone;
    }

    public int getInstitutionId() {
        return institution_id;  
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

    public String toString() {
        return "Institution{" +
                "institution_id=" + institution_id +
                '}';
    }
}
