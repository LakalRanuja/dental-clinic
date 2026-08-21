package com.mycompany.dental.clinic.view;

import com.mycompany.dental.clinic.controller.AppoinmentController;
import com.mycompany.dental.clinic.controller.PaymentController;
import com.mycompany.dental.clinic.dto.AppointmentDetails;
import com.mycompany.dental.clinic.pdf.BillPdfGenerator;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
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
    private AppointmentDetails existingAppointment;
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
        PaymentRegisterView controller = load(stage, "Patient: " + pending.getPatientName(), consultationFee,
                treatmentCost, onCompleted);
        controller.pending = pending;
        stage.show();
    }

    /** Opens the payment screen for an appointment that already exists (e.g. a "Pending" row on the dashboard). */
    public static void open(Stage stage, AppointmentDetails existingAppointment, Runnable onCompleted)
            throws IOException {
        PaymentRegisterView controller = load(stage,
                existingAppointment.getAppointmentNumber() + " — " + existingAppointment.getPatientName(),
                existingAppointment.getConsultationFee(), existingAppointment.getTreatmentCost(), onCompleted);
        controller.existingAppointment = existingAppointment;
        stage.show();
    }

    private static PaymentRegisterView load(Stage stage, String headerText, BigDecimal consultationFee,
            BigDecimal treatmentCost, Runnable onCompleted) throws IOException {
        FXMLLoader loader = new FXMLLoader(PaymentRegisterView.class.getResource("PaymentRegisterView.fxml"));
        Parent root = loader.load();
        PaymentRegisterView controller = loader.getController();
        controller.stage = stage;
        controller.onCompleted = onCompleted;

        controller.appointmentNumberLabel.setText(headerText);
        controller.consultationFeeField.setText(consultationFee == null ? "0.00" : consultationFee.toPlainString());
        controller.treatmentCostField.setText(treatmentCost == null ? "0.00" : treatmentCost.toPlainString());
        controller.recomputeTotal();
        controller.billDatePicker.setValue(LocalDate.now());

        stage.setTitle("Dental Clinic - Make Payment");
        stage.setScene(new Scene(root));
        return controller;
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
            AppointmentDetails billSource;

            if (pending != null) {
                // Only now does the appointment actually get created — this is the "Complete Payment" moment.
                billSource = appointmentController.register(
                        pending.getPatientName(), pending.getAddress(), pending.getContactNumber(),
                        pending.getDentistId(), pending.getTreatmentId(),
                        pending.getAppointmentDate(), pending.getAppointmentTime(),
                        pending.getUserId(), pending.getStatus()
                );
            } else {
                // The appointment already exists (e.g. an "Arrived" row still marked Pending) — just settle it.
                billSource = existingAppointment;
            }
            int appointmentNo = AppointmentDetails.parseAppointmentNumber(billSource.getAppointmentNumber());

            paymentController.register(appointmentNo, consultationFee, treatmentCost, totalAmount, paymentMethod,
                    billDatePicker.getValue());

            if (onCompleted != null) {
                onCompleted.run();
            }

            showAlert(Alert.AlertType.INFORMATION, "Payment Completed",
                    "Appointment " + billSource.getAppointmentNumber() + " payment recorded.");
            stage.close();
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to complete payment", "Check the details and try again.");
        }
    }

    /** Generates and opens the bill as a PDF on demand — only runs when the user clicks "Print Bill". */
    @FXML
    private void handlePrintBill() {
        BigDecimal consultationFee;
        BigDecimal treatmentCost;
        try {
            consultationFee = new BigDecimal(consultationFeeField.getText().trim());
            treatmentCost = new BigDecimal(treatmentCostField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid amount", "Consultation fee and treatment cost must be numbers.");
            return;
        }
        BigDecimal totalAmount = consultationFee.add(treatmentCost);
        String paymentMethod = paymentMethodCombo.getValue();
        String billDate = billDatePicker.getValue() == null ? "-" : billDatePicker.getValue().toString();

        String appointmentNumber;
        String patientName;
        String contactNumber;
        String dentistName;
        String treatmentType;

        if (existingAppointment != null) {
            appointmentNumber = existingAppointment.getAppointmentNumber();
            patientName = existingAppointment.getPatientName();
            contactNumber = existingAppointment.getContactNumber();
            dentistName = existingAppointment.getDentistName();
            treatmentType = existingAppointment.getTreatmentType();
        } else {
            // Appointment isn't saved yet (still on the "Complete Payment" step of a new registration) —
            // there's no appointment number or dentist/treatment names to show until that happens.
            appointmentNumber = existingAppointment.getAppointmentNumber();
            patientName = existingAppointment.getPatientName();
            contactNumber = existingAppointment.getContactNumber();
            dentistName = existingAppointment.getDentistName();
            treatmentType = existingAppointment.getTreatmentType();
        }

        saveAndOpenBill(appointmentNumber, patientName, contactNumber, dentistName, treatmentType,
                consultationFee, treatmentCost, totalAmount, paymentMethod == null ? "-" : paymentMethod, billDate);
    }

    /**
     * Lets the user pick where to save the bill, generates it as a PDF, then opens it in the OS's default
     * PDF viewer (from where it can be printed).
     */
    private void saveAndOpenBill(String appointmentNumber, String patientName, String contactNumber,
            String dentistName, String treatmentType, BigDecimal consultationFee, BigDecimal treatmentCost,
            BigDecimal totalAmount, String paymentMethod, String billDate) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Bill As");
        fileChooser.setInitialFileName("Bill_" + appointmentNumber.replace(" ", "") + ".pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            return;
        }

        try {
            BillPdfGenerator.generate(file, appointmentNumber, patientName, contactNumber, dentistName,
                    treatmentType, consultationFee, treatmentCost, totalAmount, paymentMethod, billDate);
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            showAlert(Alert.AlertType.WARNING, "Bill not created", "The PDF bill could not be created.");
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
