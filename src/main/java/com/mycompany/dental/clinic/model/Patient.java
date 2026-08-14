package com.mycompany.dental.clinic.model;

public class Patient {

    private int patient_id;
    private String name;
    private String address;
    private String contact_no;

    public Patient() {
    }

    public Patient(int patient_id, String name, String address, String contactNumber) {
        this.patient_id = patient_id;
        this.name = name;
        this.address = address;
        this.contact_no = contactNumber;
    }

    public int getPatientId() {
        return patient_id;
    }

    public void setPatientId(int patientId) {
        this.patient_id = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contact_no;
    }

    public void setContactNumber(String contactNumber) {
        this.contact_no = contactNumber;
    }
}
