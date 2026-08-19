package com.mycompany.dental.clinic.view;

import com.mycompany.dental.clinic.controller.PatientController;
import com.mycompany.dental.clinic.model.Patient;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/** FXML code-behind for PatientRegisterView.fxml — lists patients; select a row to edit it, "Add new" to create one. */
public class PatientRegisterView {

    private final PatientController patientController = new PatientController();

    private Stage stage;
    private Patient editingPatient;

    @FXML
    private TableView<Patient> patientsTable;
    @FXML
    private VBox formSection;
    @FXML
    private Button registerButton;
    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField contactNumberField;

    public static void open(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(PatientRegisterView.class.getResource("PatientRegisterView.fxml"));
        Parent root = loader.load();
        PatientRegisterView controller = loader.getController();
        controller.stage = stage;
        controller.fetchPatients();

        stage.setTitle("Dental Clinic - Patients");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void initialize() {
        patientsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                startEditing(newSel);
            }
        });
    }

    private void fetchPatients() {
        try {
            patientsTable.setItems(FXCollections.observableArrayList(patientController.listAll()));
        } catch (RuntimeException e) {
            new Alert(Alert.AlertType.ERROR, "Could not load patients.").showAndWait();
        }
    }

    private void startEditing(Patient patient) {
        editingPatient = patient;
        nameField.setText(patient.getName());
        addressField.setText(patient.getAddress());
        contactNumberField.setText(patient.getContactNumber());
        registerButton.setText("Update Patient");
        formSection.setVisible(true);
        formSection.setManaged(true);
    }

    @FXML
    private void handleShowForm() {
        editingPatient = null;
        clearForm();
        registerButton.setText("Register Patient");
        patientsTable.getSelectionModel().clearSelection();
        formSection.setVisible(true);
        formSection.setManaged(true);
    }

    @FXML
    private void handleCancelForm() {
        closeForm();
    }

    @FXML
    private void handleRegister() {
        String name = nameField.getText().trim();
        String address = addressField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();

        if (name.isEmpty() || address.isEmpty() || contactNumber.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing details", "Fill in name, address and contact number.");
            return;
        }

        boolean isUpdate = editingPatient != null;

        try {
            Patient result = isUpdate
                    ? patientController.update(editingPatient.getPatientId(), name, address, contactNumber)
                    : patientController.register(name, address, contactNumber);

            fetchPatients();
            closeForm();

            showAlert(Alert.AlertType.INFORMATION, isUpdate ? "Patient Updated" : "Patient Registered",
                    "Patient ID: " + result.getPatientId());
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to save patient", "Check the details and try again.");
        }
    }

    private void closeForm() {
        clearForm();
        editingPatient = null;
        registerButton.setText("Register Patient");
        patientsTable.getSelectionModel().clearSelection();
        formSection.setVisible(false);
        formSection.setManaged(false);
    }

    private void clearForm() {
        nameField.clear();
        addressField.clear();
        contactNumberField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
