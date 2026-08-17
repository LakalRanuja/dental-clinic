package com.mycompany.dental.clinic.controller;

import com.mycompany.dental.clinic.dao.DentistDao;
import com.mycompany.dental.clinic.model.Dentist;

import java.util.List;

public class DentistController {

    private final DentistDao dentistDao = new DentistDao();

    public List<Dentist> search(String query) {
        return dentistDao.searchByName(query == null ? "" : query);
    }
}
