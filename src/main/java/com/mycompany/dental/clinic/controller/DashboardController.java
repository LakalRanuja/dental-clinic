package com.mycompany.dental.clinic.controller;

import com.mycompany.dental.clinic.dao.DashboardDao;
import com.mycompany.dental.clinic.dto.DashboardStats;

public class DashboardController {

    private final DashboardDao dashboardDao = new DashboardDao();

    public DashboardStats getStats() {
        return dashboardDao.getStats();
    }
}
