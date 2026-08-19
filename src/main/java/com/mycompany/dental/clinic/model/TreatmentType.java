package com.mycompany.dental.clinic.model;

import java.math.BigDecimal;

public class TreatmentType {

    private int treatment_id;
    private String treatmentName;
    private String description;
    private BigDecimal baseCost;

    public TreatmentType() {
    }

    public TreatmentType(int treatment_id, String treatmentName, String description, BigDecimal baseCost) {
        this.treatment_id = treatment_id;
        this.treatmentName = treatmentName;
        this.description = description;
        this.baseCost = baseCost;
    }

    public int getTreatmentId() {
        return treatment_id;
    }

    public void setTreatmentId(int treatmentId) {
        this.treatment_id = treatmentId;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(BigDecimal baseCost) {
        this.baseCost = baseCost;
    }

    @Override
    public String toString() {
        return treatmentName;
    }
}
