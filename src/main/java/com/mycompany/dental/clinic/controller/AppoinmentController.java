package com.mycompany.dental.clinic.controller;

import com.mycompany.dental.clinic.dao.AppointmentDao;
import com.mycompany.dental.clinic.dao.PatientDao;
import com.mycompany.dental.clinic.dao.TreatmentTypeDao;
import com.mycompany.dental.clinic.dto.AppointmentDetails;
import com.mycompany.dental.clinic.dto.AppointmentSummary;
import com.mycompany.dental.clinic.model.TreatmentType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppoinmentController {

    private final PatientDao patientDao = new PatientDao();
    private final AppointmentDao appointmentDao = new AppointmentDao();
    private final TreatmentTypeDao treatmentTypeDao = new TreatmentTypeDao();

    public AppointmentDetails register(String patientName, String address, String contactNumber,
            int dentistId, int treatmentId, String appointmentDate, String appointmentTime, int userId,
            String status) {

        int patientId = patientDao.findOrCreate(patientName, address, contactNumber);

        int appointmentNo = appointmentDao.insert(
                patientId, dentistId, treatmentId, userId,
                LocalDate.parse(appointmentDate), LocalTime.parse(appointmentTime), status
        );

        return appointmentDao.findByAppointmentNo(appointmentNo);
    }

    public AppointmentDetails findByAppointmentNumber(String appointmentNumber) {
        int appointmentNo = AppointmentDetails.parseAppointmentNumber(appointmentNumber);
        return appointmentDao.findByAppointmentNo(appointmentNo);
    }

    public List<TreatmentType> listTreatmentTypes() {
        return treatmentTypeDao.findAll();
    }

    public List<AppointmentSummary> listAll() {
        return appointmentDao.findAllSummaries();
    }
}
