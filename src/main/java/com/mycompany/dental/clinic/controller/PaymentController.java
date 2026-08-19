package com.mycompany.dental.clinic.controller;

import com.mycompany.dental.clinic.dao.AppointmentDao;
import com.mycompany.dental.clinic.dao.PaymentDao;
import com.mycompany.dental.clinic.model.Payment;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentController {

    private final PaymentDao paymentDao = new PaymentDao();
    private final AppointmentDao appointmentDao = new AppointmentDao();

    /** Records the payment and marks the appointment's payment status as Paid. */
    public Payment register(int appointmentNo, BigDecimal consultationFee, BigDecimal treatmentCost,
            BigDecimal totalAmount, String paymentMethod, LocalDate billDate) {
        int paymentId = paymentDao.insert(appointmentNo, consultationFee, treatmentCost, totalAmount, paymentMethod,
                billDate);
        appointmentDao.updatePaymentStatus(appointmentNo, "Paid");
        return new Payment(paymentId, appointmentNo, consultationFee, treatmentCost, totalAmount, paymentMethod,
                billDate);
    }
}
