package com.mycompany.dental.clinic.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Payment {

    private int payment_id;
    private int appointment_no;
    private BigDecimal consultationFee;
    private BigDecimal treatmentCost;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private LocalDate billDate;

    public Payment() {
    }

    public Payment(int payment_id, int appointment_no, BigDecimal consultationFee, BigDecimal treatmentCost,
            BigDecimal totalAmount, String paymentMethod, LocalDate billDate) {
        this.payment_id = payment_id;
        this.appointment_no = appointment_no;
        this.consultationFee = consultationFee;
        this.treatmentCost = treatmentCost;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.billDate = billDate;
    }

    public int getPaymentId() {
        return payment_id;
    }

    public void setPaymentId(int payment_id) {
        this.payment_id = payment_id;
    }

    public int getAppointmentNumber() {
        return appointment_no;
    }

    public void setAppointmentNumber(int appointment_no) {
        this.appointment_no = appointment_no;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public BigDecimal getTreatmentCost() {
        return treatmentCost;
    }

    public void setTreatmentCost(BigDecimal treatmentCost) {
        this.treatmentCost = treatmentCost;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }
}
