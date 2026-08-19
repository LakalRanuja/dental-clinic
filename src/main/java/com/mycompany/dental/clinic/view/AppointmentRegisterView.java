package com.mycompany.dental.clinic.view;

import com.mycompany.dental.clinic.controller.AppoinmentController;
import com.mycompany.dental.clinic.controller.DentistController;
import com.mycompany.dental.clinic.dto.AppointmentDetails;
import com.mycompany.dental.clinic.model.Dentist;
import com.mycompany.dental.clinic.model.TreatmentType;
import com.mycompany.dental.clinic.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalTime;

/** FXML code-behind for AppointmentRegisterView.fxml. */
public class AppointmentRegisterView {

    private final AppoinmentController appointmentController = new AppoinmentController();
    private final DentistController dentistController = new DentistController();

    private User user;
    private Stage stage;
    private Runnable onRegistered;

    @FXML
    private TextField patientNameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField contactNumberField;
    @FXML
    private ComboBox<TreatmentType> treatmentCombo;
    @FXML
    private ComboBox<Dentist> dentistCombo;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Spinner<Integer> hourSpinner;
    @FXML
    private Spinner<Integer> minuteSpinner;

    public static void open(Stage stage, User user, Runnable onRegistered) throws IOException {
        FXMLLoader loader = new FXMLLoader(AppointmentRegisterView.class.getResource("AppointmentRegisterView.fxml"));
        Parent root = loader.load();
        AppointmentRegisterView controller = loader.getController();
        controller.stage = stage;
        controller.user = user;
        controller.onRegistered = onRegistered;

        stage.setTitle("Dental Clinic - Register Appointment");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void initialize() {
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 9));
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 55, 0, 5));
        setUpTreatmentCombo();
        setUpDentistCombo();
    }

    private void setUpDentistCombo() {
        dentistCombo.setDisable(true);

        dentistCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Dentist dentist) {
                return dentist == null ? "" : dentist.getName();
            }

            @Override
            public Dentist fromString(String string) {
                // JavaFX calls fromString() to "commit" the editor text on focus loss (e.g. Tab
                // to the next field). If we always returned null here, that commit would wipe out
                // a real selection every time the field lost focus. Keep the current selection when
                // the text still matches it; only free-typed, never-picked text resolves to null.
                Dentist current = dentistCombo.getValue();
                if (current != null && current.getName().equals(string)) {
                    return current;
                }
                return null;
            }
        });

        dentistCombo.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Dentist dentist, boolean empty) {
                super.updateItem(dentist, empty);
                if (empty || dentist == null) {
                    setText(null);
                } else {
                    String specialization = dentist.getSpecialization();
                    setText(specialization == null || specialization.isBlank()
                            ? dentist.getName()
                            : dentist.getName() + "  —  " + specialization);
                }
            }
        });

        dentistCombo.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            TreatmentType treatment = treatmentCombo.getValue();
            if (treatment == null) {
                return;
            }

            Dentist selected = dentistCombo.getValue();
            if (selected != null && selected.getName().equals(newText)) {
                return;
            }
            if (selected != null) {
                dentistCombo.setValue(null);
            }

            var matches = dentistController.search(newText == null ? "" : newText, treatment.getTreatmentId());
            dentistCombo.getItems().setAll(matches);
            if (matches.isEmpty()) {
                dentistCombo.hide();
            } else {
                dentistCombo.show();
            }
        });

        dentistCombo.getEditor().focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            TreatmentType treatment = treatmentCombo.getValue();
            if (isFocused && treatment != null && dentistCombo.getItems().isEmpty()) {
                dentistCombo.getItems().setAll(dentistController.search("", treatment.getTreatmentId()));
                if (!dentistCombo.getItems().isEmpty()) {
                    dentistCombo.show();
                }
            }
        });
    }

    private void setUpTreatmentCombo() {
        treatmentCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(TreatmentType type) {
                return type == null ? "" : type.getTreatmentName();
            }

            @Override
            public TreatmentType fromString(String string) {
                return null;
            }
        });

        treatmentCombo.valueProperty().addListener((obs, oldTreatment, newTreatment) -> {
            dentistCombo.setValue(null);
            dentistCombo.getEditor().clear();
            dentistCombo.getItems().clear();
            dentistCombo.setDisable(newTreatment == null);
            dentistCombo.setPromptText(newTreatment == null ? "Select a treatment first" : "Search dentist by name");
        });

        try {
            treatmentCombo.getItems().setAll(appointmentController.listTreatmentTypes());
        } catch (RuntimeException e) {
            new Alert(Alert.AlertType.ERROR, "Could not load treatment types.").showAndWait();
        }
    }

    @FXML
    private void handleRegister() {
        String patientName = patientNameField.getText().trim();
        String address = addressField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();
        Dentist dentist = dentistCombo.getValue();
        TreatmentType treatment = treatmentCombo.getValue();

        if (patientName.isEmpty() || address.isEmpty() || contactNumber.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing details", "Fill in patient name, address and contact number.");
            return;
        }
        if (treatment == null) {
            showAlert(Alert.AlertType.WARNING, "Select a treatment", "Choose a treatment type.");
            return;
        }
        if (dentist == null) {
            showAlert(Alert.AlertType.WARNING, "Select a dentist", "Search and pick a dentist from the list.");
            return;
        }
        if (datePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Pick a date", "Choose an appointment date.");
            return;
        }

        LocalTime time = LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue());

        try {
            AppointmentDetails created = appointmentController.register(
                    patientName, address, contactNumber,
                    dentist.getDentistId(), treatment.getTreatmentId(),
                    datePicker.getValue().toString(),
                    String.format("%02d:%02d", time.getHour(), time.getMinute()),
                    user.getUserId()
            );

            if (onRegistered != null) {
                onRegistered.run();
            }

            showAlert(Alert.AlertType.INFORMATION, "Appointment Registered",
                    "Appointment number: " + created.getAppointmentNumber());

            patientNameField.clear();
            addressField.clear();
            contactNumberField.clear();
            dentistCombo.getEditor().clear();
            dentistCombo.setValue(null);
            treatmentCombo.setValue(null);
            datePicker.setValue(null);
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to register appointment", "Check the details and try again.");
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
