package com.mycompany.dental.clinic.view;

import com.mycompany.dental.clinic.controller.AppoinmentController;
import com.mycompany.dental.clinic.controller.PaymentController;
import com.mycompany.dental.clinic.dto.AppointmentDetails;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * FXML code-behind for PaymentRegisterView.fxml — collects payment for an appointment that has NOT
 * been saved yet. The appointment row is only created (together with the payment row) if "Complete
 * Payment" succeeds; cancelling this screen leaves no appointment behind.
 */
public class PaymentRegisterView {

    private final AppoinmentController appointmentController = new AppoinmentController();
    private final PaymentController paymentController = new PaymentController();

    private Stage stage;
    private PendingAppointment pending;
    private Runnable onCompleted;

    @FXML
    private Label appointmentNumberLabel;
    @FXML
    private TextField consultationFeeField;
    @FXML
    private TextField treatmentCostField;
    @FXML
    private TextField totalAmountField;
    @FXML
    private ComboBox<String> paymentMethodCombo;
    @FXML
    private DatePicker billDatePicker;

    public static void open(Stage stage, PendingAppointment pending, BigDecimal consultationFee,
            BigDecimal treatmentCost, Runnable onCompleted) throws IOException {
        FXMLLoader loader = new FXMLLoader(PaymentRegisterView.class.getResource("PaymentRegisterView.fxml"));
        Parent root = loader.load();
        PaymentRegisterView controller = loader.getController();
        controller.stage = stage;
        controller.pending = pending;
        controller.onCompleted = onCompleted;

        controller.appointmentNumberLabel.setText("Patient: " + pending.getPatientName());
        controller.consultationFeeField.setText(consultationFee == null ? "0.00" : consultationFee.toPlainString());
        controller.treatmentCostField.setText(treatmentCost == null ? "0.00" : treatmentCost.toPlainString());
        controller.recomputeTotal();
        controller.billDatePicker.setValue(LocalDate.now());

        stage.setTitle("Dental Clinic - Make Payment");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void initialize() {
        paymentMethodCombo.setItems(FXCollections.observableArrayList("Cash", "Card", "Insurance"));
        consultationFeeField.setEditable(false);
        treatmentCostField.setEditable(false);
        billDatePicker.setDisable(true);
        
        consultationFeeField.textProperty().addListener((obs, oldText, newText) -> recomputeTotal());
        treatmentCostField.textProperty().addListener((obs, oldText, newText) -> recomputeTotal());
    }

    private void recomputeTotal() {
        totalAmountField.setText(parseOrZero(consultationFeeField.getText())
                .add(parseOrZero(treatmentCostField.getText()))
                .toPlainString());
    }

    private BigDecimal parseOrZero(String text) {
        try {
            return new BigDecimal(text.trim());
        } catch (RuntimeException e) {
            return BigDecimal.ZERO;
        }
    }

    @FXML
    private void handleCompletePayment() {
        BigDecimal consultationFee;
        BigDecimal treatmentCost;
        try {
            consultationFee = new BigDecimal(consultationFeeField.getText().trim());
            treatmentCost = new BigDecimal(treatmentCostField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid amount", "Consultation fee and treatment cost must be numbers.");
            return;
        }

        String paymentMethod = paymentMethodCombo.getValue();
        if (paymentMethod == null) {
            showAlert(Alert.AlertType.WARNING, "Select a payment method", "Choose how the patient paid.");
            return;
        }
        if (billDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Pick a date", "Choose the bill date.");
            return;
        }

        BigDecimal totalAmount = consultationFee.add(treatmentCost);

        try {
            // Only now does the appointment actually get created — this is the "Complete Payment" moment.
            AppointmentDetails createdAppointment = appointmentController.register(
                    pending.getPatientName(), pending.getAddress(), pending.getContactNumber(),
                    pending.getDentistId(), pending.getTreatmentId(),
                    pending.getAppointmentDate(), pending.getAppointmentTime(),
                    pending.getUserId(), pending.getStatus()
            );
            int appointmentNo = AppointmentDetails.parseAppointmentNumber(createdAppointment.getAppointmentNumber());

            paymentController.register(appointmentNo, consultationFee, treatmentCost, totalAmount, paymentMethod,
                    billDatePicker.getValue());

            if (onCompleted != null) {
                onCompleted.run();
            }

            showAlert(Alert.AlertType.INFORMATION, "Payment Completed",
                    "Appointment " + createdAppointment.getAppointmentNumber() + " created and payment recorded.");
            stage.close();
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to complete payment", "Check the details and try again.");
        }
    }

    @FXML
    private void handleClose() {
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
