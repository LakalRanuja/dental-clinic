package com.mycompany.dental.clinic.controller;

import com.mycompany.dental.clinic.dao.PatientDao;
import com.mycompany.dental.clinic.model.Patient;

import java.util.List;

public class PatientController {

    private final PatientDao patientDao = new PatientDao();

    public Patient register(String name, String address, String contactNumber) {
        int patientId = patientDao.findOrCreate(name, address, contactNumber);
        return new Patient(patientId, name, address, contactNumber);
    }

    public Patient update(int patientId, String name, String address, String contactNumber) {
        patientDao.update(patientId, name, address, contactNumber);
        return new Patient(patientId, name, address, contactNumber);
    }

    public List<Patient> listAll() {
        return patientDao.findAll();
    }
}
