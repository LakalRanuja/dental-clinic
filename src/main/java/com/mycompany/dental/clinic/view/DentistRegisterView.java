package com.mycompany.dental.clinic.view;

import com.mycompany.dental.clinic.controller.AppoinmentController;
import com.mycompany.dental.clinic.controller.DentistController;
import com.mycompany.dental.clinic.model.Dentist;
import com.mycompany.dental.clinic.model.TreatmentType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/** FXML code-behind for DentistRegisterView.fxml — lists dentists; select a row to edit it, "Add new" to create one. */
public class DentistRegisterView {

    private final DentistController dentistController = new DentistController();
    private final AppoinmentController appointmentController = new AppoinmentController();

    private Stage stage;
    private Dentist editingDentist;

    @FXML
    private TableView<Dentist> dentistsTable;
    @FXML
    private VBox formSection;
    @FXML
    private Button registerButton;
    @FXML
    private TextField nameField;
    @FXML
    private TextField specializationField;
    @FXML
    private TextField contactNumberField;
    @FXML
    private TextField consultationFeeField;
    @FXML
    private ListView<TreatmentType> treatmentsList;

    public static void open(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(DentistRegisterView.class.getResource("DentistRegisterView.fxml"));
        Parent root = loader.load();
        DentistRegisterView controller = loader.getController();
        controller.stage = stage;
        controller.fetchDentists();

        stage.setTitle("Dental Clinic - Dentists");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void initialize() {
        treatmentsList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(TreatmentType type, boolean empty) {
                super.updateItem(type, empty);
                setText(empty || type == null ? null : type.getTreatmentName());
            }
        });
        treatmentsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        try {
            treatmentsList.setItems(FXCollections.observableArrayList(appointmentController.listTreatmentTypes()));
        } catch (RuntimeException e) {
            new Alert(Alert.AlertType.ERROR, "Could not load treatment types.").showAndWait();
        }

        dentistsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                startEditing(newSel);
            }
        });
    }

    private void fetchDentists() {
        try {
            dentistsTable.setItems(FXCollections.observableArrayList(dentistController.listAll()));
        } catch (RuntimeException e) {
            new Alert(Alert.AlertType.ERROR, "Could not load dentists.").showAndWait();
        }
    }

    private void startEditing(Dentist dentist) {
        editingDentist = dentist;
        nameField.setText(dentist.getName());
        specializationField.setText(dentist.getSpecialization());
        contactNumberField.setText(dentist.getContactNumber());
        consultationFeeField.setText(dentist.getConsultationFee() == null
                ? "" : dentist.getConsultationFee().toPlainString());

        treatmentsList.getSelectionModel().clearSelection();
        try {
            List<Integer> linkedTreatmentIds = dentistController.getTreatmentIdsForDentist(dentist.getDentistId());
            List<TreatmentType> items = treatmentsList.getItems();
            for (int i = 0; i < items.size(); i++) {
                if (linkedTreatmentIds.contains(items.get(i).getTreatmentId())) {
                    treatmentsList.getSelectionModel().select(i);
                }
            }
        } catch (RuntimeException e) {
            new Alert(Alert.AlertType.ERROR, "Could not load this dentist's current treatments.").showAndWait();
        }

        registerButton.setText("Update Dentist");
        formSection.setVisible(true);
        formSection.setManaged(true);
    }

    @FXML
    private void handleShowForm() {
        editingDentist = null;
        clearForm();
        registerButton.setText("Register Dentist");
        dentistsTable.getSelectionModel().clearSelection();
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
        String specialization = specializationField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();
        String feeText = consultationFeeField.getText().trim();

        if (name.isEmpty() || contactNumber.isEmpty() || feeText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing details",
                    "Fill in at least name, contact number and consultation fee.");
            return;
        }

        BigDecimal consultationFee;
        try {
            consultationFee = new BigDecimal(feeText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid fee", "Consultation fee must be a number, e.g. 2500.00.");
            return;
        }

        List<Integer> treatmentIds = treatmentsList.getSelectionModel().getSelectedItems().stream()
                .map(TreatmentType::getTreatmentId)
                .collect(Collectors.toList());

        boolean isUpdate = editingDentist != null;

        try {
            Dentist result = isUpdate
                    ? dentistController.update(editingDentist.getDentistId(), name, specialization, contactNumber,
                            consultationFee, treatmentIds)
                    : dentistController.register(name, specialization, contactNumber, consultationFee, treatmentIds);

            fetchDentists();
            closeForm();

            showAlert(Alert.AlertType.INFORMATION, isUpdate ? "Dentist Updated" : "Dentist Registered",
                    result.getName() + (isUpdate ? " updated" : " added") + " with " + treatmentIds.size()
                            + " treatment(s) linked.");
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to save dentist", "Check the details and try again.");
        }
    }

    private void closeForm() {
        clearForm();
        editingDentist = null;
        registerButton.setText("Register Dentist");
        dentistsTable.getSelectionModel().clearSelection();
        formSection.setVisible(false);
        formSection.setManaged(false);
    }

    private void clearForm() {
        nameField.clear();
        specializationField.clear();
        contactNumberField.clear();
        consultationFeeField.clear();
        treatmentsList.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
