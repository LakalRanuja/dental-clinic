package com.mycompany.dental.clinic.view;

/**
 * Appointment details collected on the register form but not yet saved.
 * Used to hand off from {@link AppointmentRegisterView} to {@link PaymentRegisterView}
 * for the "Arrived" flow, where the appointment row must only be created once
 * payment actually completes — never on a cancelled/abandoned payment screen.
 */
final class PendingAppointment {

    private final String patientName;
    private final String address;
    private final String contactNumber;
    private final int dentistId;
    private final int treatmentId;
    private final String appointmentDate;
    private final String appointmentTime;
    private final int userId;
    private final String status;

    PendingAppointment(String patientName, String address, String contactNumber, int dentistId, int treatmentId,
            String appointmentDate, String appointmentTime, int userId, String status) {
        this.patientName = patientName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.dentistId = dentistId;
        this.treatmentId = treatmentId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.userId = userId;
        this.status = status;
    }

    String getPatientName() {
        return patientName;
    }

    String getAddress() {
        return address;
    }

    String getContactNumber() {
        return contactNumber;
    }

    int getDentistId() {
        return dentistId;
    }

    int getTreatmentId() {
        return treatmentId;
    }

    String getAppointmentDate() {
        return appointmentDate;
    }

    String getAppointmentTime() {
        return appointmentTime;
    }

    int getUserId() {
        return userId;
    }

    String getStatus() {
        return status;
    }
}
