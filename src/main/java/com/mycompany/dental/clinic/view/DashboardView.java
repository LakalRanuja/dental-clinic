package com.mycompany.dental.clinic.view;

import com.mycompany.dental.clinic.controller.AppoinmentController;
import com.mycompany.dental.clinic.controller.DashboardController;
import com.mycompany.dental.clinic.dto.AppointmentSummary;
import com.mycompany.dental.clinic.dto.DashboardStats;
import com.mycompany.dental.clinic.model.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * FXML code-behind for DashboardView.fxml. One shared layout for both roles;
 * admin-only and staff-only buttons are hidden based on {@link User#getRole()}.
 */
public class DashboardView {

    private final DashboardController dashboardController = new DashboardController();
    private final AppoinmentController appointmentController = new AppoinmentController();

    private User user;
    private Stage stage;

    @FXML
    private Label accountLabel;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label todaysAppointmentsValue;
    @FXML
    private Label totalAppointmentsValue;
    @FXML
    private Label upcomingAppointmentsValue;
    @FXML
    private Label totalPatientsValue;
    @FXML
    private TableView<AppointmentSummary> appointmentsTable;
    @FXML
    private Button registerAppointmentButton;
    @FXML
    private Button searchAppointmentButton;
    @FXML
    private Button addPatientButton;
    @FXML
    private Button addDentistButton;
    @FXML
    private Button addUserButton;
    @FXML
    private Button paymentsButton;

    public static void open(Stage stage, User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(DashboardView.class.getResource("DashboardView.fxml"));
        Parent root = loader.load();
        DashboardView controller = loader.getController();
        controller.stage = stage;
        controller.user = user;
        controller.populate();

        stage.setTitle("Dental Clinic - Dashboard");
        stage.setScene(new Scene(root, 1100, 720));
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setMaximized(true);
        stage.show();
    }

    @FXML
    private void initialize() {
        appointmentsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void populate() {
        welcomeLabel.setText("Welcome, " + user.getFullName());
        accountLabel.setText(user.getFullName() + "  (" + user.getRole() + ")");
        applyRoleVisibility();
        refreshStats();
        fetchAppointments();
    }

    private void refreshStats() {
        try {
            DashboardStats stats = dashboardController.getStats();
            todaysAppointmentsValue.setText(String.valueOf(stats.getTodaysAppointments()));
            totalAppointmentsValue.setText(String.valueOf(stats.getTotalAppointments()));
            upcomingAppointmentsValue.setText(String.valueOf(stats.getUpcomingAppointments()));
            totalPatientsValue.setText(String.valueOf(stats.getTotalPatients()));
        } catch (RuntimeException e) {
            new Alert(Alert.AlertType.ERROR, "Could not load dashboard stats. Check the database connection.")
                    .showAndWait();
        }
    }

    /** Reloads the appointments table from the DB. Called on open, and again after a new appointment is registered. */
    private void fetchAppointments() {
        try {
            List<AppointmentSummary> summaries = appointmentController.listAll();
            appointmentsTable.setItems(FXCollections.observableArrayList(summaries));
        } catch (RuntimeException e) {
            new Alert(Alert.AlertType.ERROR, "Could not load the appointments list.").showAndWait();
        }
    }

    private void applyRoleVisibility() {
        boolean isAdmin = "admin".equalsIgnoreCase(user.getRole());

        // admin-only
        setShown(searchAppointmentButton, isAdmin);
        setShown(addUserButton, isAdmin);
        setShown(registerAppointmentButton, !isAdmin);
        setShown(addPatientButton, !isAdmin);

        // staff-only
        setShown(paymentsButton, !isAdmin);

        // both roles: registerAppointmentButton, addPatientButton, addDentistButton stay visible
    }

    private void setShown(javafx.scene.Node node, boolean shown) {
        node.setVisible(shown);
        node.setManaged(shown);
    }

    @FXML
    private void handleLogout() throws IOException {
        stage.close();
        LoginView.open(new Stage());
    }

    @FXML
    private void handleRegisterAppointment() throws IOException {
        AppointmentRegisterView.open(new Stage(), user, this::fetchAppointments);
    }

    @FXML
    private void handleSearchAppointment() throws IOException {
        AppointmentSearchView.open(new Stage());
    }

    @FXML
    private void handleRegisterPatient() throws IOException {
        PatientRegisterView.open(new Stage());
    }

    @FXML
    private void handleRegisterDentist() throws IOException {
        DentistRegisterView.open(new Stage());
    }

    @FXML
    private void handleRegisterUser() throws IOException {
        UserRegisterView.open(new Stage());
    }
}
