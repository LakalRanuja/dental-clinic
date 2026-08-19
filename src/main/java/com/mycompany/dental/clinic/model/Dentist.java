package com.mycompany.dental.clinic.model;

public class Dentist {

    private int dentist_id;
    private String name;
    private String specialization;
    private String contact_no;

    public Dentist() {
    }

    public Dentist(int dentist_id, String name, String specialization, String contact_no) {
        this.dentist_id = dentist_id;
        this.name = name;
        this.specialization = specialization;
        this.contact_no = contact_no;
    }

    public int getDentistId() {
        return dentist_id;
    }

    public void setDentistId(int dentistId) {
        this.dentist_id = dentistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getContactNumber() {
        return contact_no;
    }

    public void setContactNumber(String contact_no) {
        this.contact_no = contact_no;
    }

    @Override
    public String toString() {
        return name;
    }
}
