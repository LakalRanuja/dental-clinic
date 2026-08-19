package com.mycompany.dental.clinic.view;

import com.mycompany.dental.clinic.controller.UserController;
import com.mycompany.dental.clinic.model.User;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * FXML code-behind for LoginView.fxml; also the JavaFX Application entry
 * point. Not related to the business-logic classes in the {@code controller}
 * package.
 */
public class LoginView extends Application {

    private final UserController userController = new UserController();

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        open(stage);
    }

    public static void open(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(LoginView.class.getResource("LoginView.fxml"));
        Parent root = loader.load();
        LoginView controller = loader.getController();
        controller.stage = stage;

        stage.setTitle("Dental Clinic - Login");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing details", "Enter both username and password.");
            return;
        }

        User user;
        try {
            user = userController.login(username, password);
        } catch (RuntimeException e) {
            showAlert(Alert.AlertType.ERROR, "Connection error",
                    "Could not reach the database. Check that MySQL is running and try again.");
            return;
        }

        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Login failed", "Invalid username or password.");
            return;
        }

        try {
            DashboardView.open(new Stage(), user);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "UI error", "Could not open the dashboard.");
            return;
        }
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
