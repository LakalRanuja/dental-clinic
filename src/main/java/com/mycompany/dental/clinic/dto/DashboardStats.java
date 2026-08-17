package com.mycompany.dental.clinic.dto;

public class DashboardStats {

    private final int todaysAppointments;
    private final int totalAppointments;
    private final int upcomingAppointments;
    private final int totalPatients;

    public DashboardStats(int todaysAppointments, int totalAppointments, int upcomingAppointments,
            int totalPatients) {
        this.todaysAppointments = todaysAppointments;
        this.totalAppointments = totalAppointments;
        this.upcomingAppointments = upcomingAppointments;
        this.totalPatients = totalPatients;
    }

    public int getTodaysAppointments() {
        return todaysAppointments;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public int getUpcomingAppointments() {
        return upcomingAppointments;
    }

    public int getTotalPatients() {
        return totalPatients;
    }
}
