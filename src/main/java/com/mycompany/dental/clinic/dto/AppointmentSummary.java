package com.mycompany.dental.clinic.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Row shape for the appointments listing table: raw patient_id (not joined
 * to a patient name), plus dentist/treatment/booked-by resolved to readable
 * names via their foreign keys.
 */
public class AppointmentSummary {

    private final int appointmentNo;
    private final int patientId;
    private final String dentistName;
    private final String treatmentType;
    private final LocalDate appointmentDate;
    private final LocalTime appointmentTime;
    private final String bookedBy;
    private final String status;
    private final String paymentStatus;

    public AppointmentSummary(int appointmentNo, int patientId, String dentistName, String treatmentType,
            LocalDate appointmentDate, LocalTime appointmentTime, String bookedBy, String status,
            String paymentStatus) {
        this.appointmentNo = appointmentNo;
        this.patientId = patientId;
        this.dentistName = dentistName;
        this.treatmentType = treatmentType;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.bookedBy = bookedBy;
        this.status = status;
        this.paymentStatus = paymentStatus;
    }

    public String getAppointmentNumber() {
        return AppointmentDetails.formatAppointmentNumber(appointmentNo);
    }

    public int getPatientId() {
        return patientId;
    }

    public String getDentistName() {
        return dentistName;
    }

    public String getTreatmentType() {
        return treatmentType;
    }

    public String getAppointmentDate() {
        return appointmentDate.toString();
    }

    public String getAppointmentTime() {
        return String.format("%02d:%02d", appointmentTime.getHour(), appointmentTime.getMinute());
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }
}
