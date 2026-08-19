package com.mycompany.dental.clinic.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Flat, frontend-shaped view of an appointment (joins patient, dentist and
 * treatment type into a single record). Distinct from the normalized
 * {@link com.mycompany.dental.clinic.model.Appointment} entity, which is
 * ID-based.
 */
public class AppointmentDetails {

    private final int appointmentNo;
    private final String patientName;
    private final String address;
    private final String contactNumber;
    private final String dentistName;
    private final String treatmentType;
    private final LocalDate appointmentDate;
    private final LocalTime appointmentTime;
    private final String status;
    private final String paymentStatus;

    public AppointmentDetails(int appointmentNo, String patientName, String address, String contactNumber,
            String dentistName, String treatmentType, LocalDate appointmentDate, LocalTime appointmentTime,
            String status, String paymentStatus) {
        this.appointmentNo = appointmentNo;
        this.patientName = patientName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.dentistName = dentistName;
        this.treatmentType = treatmentType;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.paymentStatus = paymentStatus;
    }

    public String getAppointmentNumber() {
        return formatAppointmentNumber(appointmentNo);
    }

    public String getPatientName() {
        return patientName;
    }

    public String getAddress() {
        return address;
    }

    public String getContactNumber() {
        return contactNumber;
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

    public String getStatus() {
        return status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public static String formatAppointmentNumber(int appointmentNo) {
        return String.format("APT-%04d", appointmentNo);
    }

    public static int parseAppointmentNumber(String formatted) {
        String digits = formatted.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            throw new IllegalArgumentException("Invalid appointment number: " + formatted);
        }
        return Integer.parseInt(digits);
    }
}
