package com.mycompany.dental.clinic.view;

import com.mycompany.dental.clinic.controller.UserController;
import com.mycompany.dental.clinic.model.User;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Login screen. Also doubles as the JavaFX entry point (launched from
 * {@link com.mycompany.dental.clinic.DentalClinic#main}); every other screen
 * is just a plain {@link Stage} opened from here or from another view.
 */
public class LoginView extends Application {

    private final UserController userController = new UserController();

    @Override
    public void start(Stage stage) {
        showOn(stage);
    }

    public void showOn(Stage stage) {
        Label title = new Label("Dental Clinic Login");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Sign in");
        loginButton.setDefaultButton(true);
        loginButton.setMaxWidth(Double.MAX_VALUE);

        VBox root = new VBox(12, title, usernameField, passwordField, loginButton);
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.CENTER);
        root.setPrefWidth(320);

        loginButton.setOnAction(event ->
                handleLogin(stage, usernameField.getText().trim(), passwordField.getText()));

        stage.setTitle("Dental Clinic - Login");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void handleLogin(Stage stage, String username, String password) {
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

        new DashboardView(user).showOn(new Stage());
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
