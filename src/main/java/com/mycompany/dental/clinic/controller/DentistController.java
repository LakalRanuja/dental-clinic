package com.mycompany.dental.clinic.controller;

import com.mycompany.dental.clinic.dao.DentistDao;
import com.mycompany.dental.clinic.model.Dentist;

import java.math.BigDecimal;
import java.util.List;

public class DentistController {

    private final DentistDao dentistDao = new DentistDao();

    public List<Dentist> search(String query, int treatmentId) {
        return dentistDao.searchByNameAndTreatment(query == null ? "" : query, treatmentId);
    }

    public List<Dentist> listAll() {
        return dentistDao.findAll();
    }

    public Dentist register(String name, String specialization, String contactNumber, BigDecimal consultationFee,
            List<Integer> treatmentIds) {
        int dentistId = dentistDao.insert(name, specialization, contactNumber, consultationFee);
        dentistDao.linkTreatments(dentistId, treatmentIds);
        return new Dentist(dentistId, name, specialization, contactNumber, consultationFee);
    }

    public Dentist update(int dentistId, String name, String specialization, String contactNumber,
            BigDecimal consultationFee, List<Integer> treatmentIds) {
        dentistDao.update(dentistId, name, specialization, contactNumber, consultationFee);
        dentistDao.linkTreatments(dentistId, treatmentIds);
        return new Dentist(dentistId, name, specialization, contactNumber, consultationFee);
    }

    public List<Integer> getTreatmentIdsForDentist(int dentistId) {
        return dentistDao.findTreatmentIdsForDentist(dentistId);
    }
}
