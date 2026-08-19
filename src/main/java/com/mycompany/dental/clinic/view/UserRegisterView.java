package com.mycompany.dental.clinic.view;

import com.mycompany.dental.clinic.controller.UserController;
import com.mycompany.dental.clinic.model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/** FXML code-behind for UserRegisterView.fxml — lists users; select a row to edit it, "Add new" to create one. */
public class UserRegisterView {

    private final UserController userController = new UserController();

    private Stage stage;
    private User editingUser;

    @FXML
    private TableView<User> usersTable;
    @FXML
    private VBox formSection;
    @FXML
    private Button registerButton;
    @FXML
    private TextField fullNameField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ComboBox<String> roleCombo;

    public static void open(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(UserRegisterView.class.getResource("UserRegisterView.fxml"));
        Parent root = loader.load();
        UserRegisterView controller = loader.getController();
        controller.stage = stage;
        controller.fetchUsers();

        stage.setTitle("Dental Clinic - Users");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void initialize() {
        roleCombo.setItems(FXCollections.observableArrayList("admin", "staff"));

        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                startEditing(newSel);
            }
        });
    }

    private void fetchUsers() {
        try {
            usersTable.setItems(FXCollections.observableArrayList(userController.listAll()));
        } catch (RuntimeException e) {
            new Alert(Alert.AlertType.ERROR, "Could not load users.").showAndWait();
        }
    }

    private void startEditing(User user) {
        editingUser = user;
        fullNameField.setText(user.getFullName());
        usernameField.setText(user.getUsername());
        passwordField.clear();
        passwordField.setPromptText("Leave blank to keep current password");
        roleCombo.setValue(user.getRole());
        registerButton.setText("Update User");
        formSection.setVisible(true);
        formSection.setManaged(true);
    }

    @FXML
    private void handleShowForm() {
        editingUser = null;
        clearForm();
        registerButton.setText("Register User");
        usersTable.getSelectionModel().clearSelection();
        formSection.setVisible(true);
        formSection.setManaged(true);
    }

    @FXML
    private void handleCancelForm() {
        closeForm();
    }

    @FXML
    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String role = roleCombo.getValue();

        boolean isUpdate = editingUser != null;

        if (fullName.isEmpty() || username.isEmpty() || (!isUpdate && password.isEmpty())) {
            showAlert(Alert.AlertType.WARNING, "Missing details",
                    "Fill in full name, username" + (isUpdate ? "." : " and password."));
            return;
        }
        if (role == null) {
            showAlert(Alert.AlertType.WARNING, "Select a role", "Choose whether this account is admin or staff.");
            return;
        }

        try {
            User result = isUpdate
                    ? userController.update(editingUser.getUserId(), username, password, fullName, role)
                    : userController.register(username, password, fullName, role);

            fetchUsers();
            closeForm();

            showAlert(Alert.AlertType.INFORMATION, isUpdate ? "User Updated" : "User Registered",
                    result.getFullName() + " (" + result.getRole() + ") " + (isUpdate ? "updated." : "created."));
        } catch (IllegalStateException e) {
            showAlert(Alert.AlertType.WARNING, "Username taken", "That username is already in use.");
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Failed to save user", "Check the details and try again.");
        }
    }

    private void closeForm() {
        clearForm();
        editingUser = null;
        registerButton.setText("Register User");
        usersTable.getSelectionModel().clearSelection();
        formSection.setVisible(false);
        formSection.setManaged(false);
    }

    private void clearForm() {
        fullNameField.clear();
        usernameField.clear();
        passwordField.clear();
        passwordField.setPromptText(null);
        roleCombo.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
