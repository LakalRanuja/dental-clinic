package com.mycompany.dental.clinic.view;

import com.mycompany.dental.clinic.controller.AppoinmentController;
import com.mycompany.dental.clinic.dto.AppointmentDetails;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

/** FXML code-behind for AppointmentSearchView.fxml. */
public class AppointmentSearchView {

    private final AppoinmentController appointmentController = new AppoinmentController();

    @FXML
    private TextField queryField;
    @FXML
    private Label statusLabel;
    @FXML
    private GridPane resultGrid;
    @FXML
    private Label appointmentNumberValue;
    @FXML
    private Label patientNameValue;
    @FXML
    private Label addressValue;
    @FXML
    private Label contactNumberValue;
    @FXML
    private Label dentistNameValue;
    @FXML
    private Label treatmentTypeValue;
    @FXML
    private Label appointmentDateValue;
    @FXML
    private Label appointmentTimeValue;
    @FXML
    private Label statusValue;
    @FXML
    private Label paymentStatusValue;

    public static void open(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(AppointmentSearchView.class.getResource("AppointmentSearchView.fxml"));
        Parent root = loader.load();

        stage.setTitle("Dental Clinic - Find Appointment");
        stage.setScene(new Scene(root, 400, 800));
        stage.show();
    }

    @FXML
    private void handleSearch() {
        String appointmentNumber = queryField.getText().trim();
        hideStatus();
        hideResult();

        if (appointmentNumber.isEmpty()) {
            return;
        }

        try {
            AppointmentDetails found = appointmentController.findByAppointmentNumber(appointmentNumber);
            if (found == null) {
                showStatus("No appointment found for \"" + appointmentNumber + "\".");
            } else {
                showResult(found);
            }
        } catch (IllegalArgumentException e) {
            showStatus("That doesn't look like a valid appointment number.");
        } catch (RuntimeException e) {
            new Alert(Alert.AlertType.ERROR, "Search failed. Check the database connection.").showAndWait();
        }
    }

    private void showResult(AppointmentDetails details) {
        appointmentNumberValue.setText(details.getAppointmentNumber());
        patientNameValue.setText(details.getPatientName());
        addressValue.setText(details.getAddress());
        contactNumberValue.setText(details.getContactNumber());
        dentistNameValue.setText(details.getDentistName());
        treatmentTypeValue.setText(details.getTreatmentType());
        appointmentDateValue.setText(details.getAppointmentDate());
        appointmentTimeValue.setText(details.getAppointmentTime());
        statusValue.setText(details.getStatus());
        paymentStatusValue.setText(details.getPaymentStatus());

        resultGrid.setVisible(true);
        resultGrid.setManaged(true);
    }

    private void hideResult() {
        resultGrid.setVisible(false);
        resultGrid.setManaged(false);
    }

    private void showStatus(String message) {
        statusLabel.setText(message);
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }

    private void hideStatus() {
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
    }
}
