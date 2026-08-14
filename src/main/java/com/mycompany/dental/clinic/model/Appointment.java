package com.mycompany.dental.clinic.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appointment {

    private int appointment_no;
    private int patient_id;
    private int dentist_id;
    private int treatment_id;
    private int user_id;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;

    public Appointment() {
    }

    public Appointment(int appointment_no, int patient_id, int dentist_id, int treatment_id, int user_id,
            LocalDate appointmentDate, LocalTime appointmentTime, String status) {
        this.appointment_no = appointment_no;
        this.patient_id = patient_id;
        this.dentist_id = dentist_id;
        this.treatment_id = treatment_id;
        this.user_id = user_id;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    public int getAppointmentNumber() {
        return appointment_no;
    }

    public void setAppointmentNumber(int appointment_no) {
        this.appointment_no = appointment_no;
    }

    public int getPatientId() {
        return patient_id;
    }

    public void setPatientId(int patientId) {
        this.patient_id = patientId;
    }

    public int getDentistId() {
        return dentist_id;
    }

    public void setDentistId(int dentistId) {
        this.dentist_id = dentistId;
    }

    public int getTreatmentId() {
        return treatment_id;
    }

    public void setTreatmentId(int treatmentId) {
        this.treatment_id = treatmentId;
    }

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int userId) {
        this.user_id = userId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
