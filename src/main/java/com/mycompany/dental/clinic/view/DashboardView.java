package com.mycompany.dental.clinic.view;

import com.mycompany.dental.clinic.controller.AppoinmentController;
import com.mycompany.dental.clinic.controller.DashboardController;
import com.mycompany.dental.clinic.dto.AppointmentSummary;
import com.mycompany.dental.clinic.dto.DashboardStats;
import com.mycompany.dental.clinic.model.User;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class DashboardView {

    private final User user;
    private final DashboardController dashboardController = new DashboardController();
    private final AppoinmentController appointmentController = new AppoinmentController();

    public DashboardView(User user) {
        this.user = user;
    }

    public void showOn(Stage stage) {
        BorderPane root = new BorderPane();
        root.setTop(buildHeader(stage));
        root.setCenter(buildContent());

        stage.setTitle("Dental Clinic - Dashboard");
        stage.setScene(new Scene(root, 1100, 720));
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setMaximized(true);
        stage.show();
    }

    private HBox buildHeader(Stage stage) {
        Label brand = new Label("Dental Clinic");
        brand.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label spacer = new Label();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label account = new Label(user.getFullName() + "  (" + user.getRole() + ")");
        account.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

        Button logoutButton = new Button("Log out");
        logoutButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        logoutButton.setAlignment(Pos.CENTER_RIGHT);
        logoutButton.setOnAction(event -> {
            stage.close();
            new LoginView().showOn(new Stage());
        });

        HBox header = new HBox(16, brand, spacer, account, logoutButton);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 24, 14, 24));
        header.setStyle("-fx-background-color: white; -fx-border-color: #e2e2e2; -fx-border-width: 0 0 1 0;");
        return header;
    }

    private VBox buildContent() {
        Label welcome = new Label("Welcome, " + user.getFullName());
        welcome.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        HBox statsRow = buildStatsRow();

        Button registerAppointmentButton = new Button("Register Appointment");
        registerAppointmentButton.setDisable(true); // wired up in a later step
        styleActionButton(registerAppointmentButton, "#0d9488");

        Button searchAppointmentButton = new Button("Search Appointment");
        searchAppointmentButton.setDisable(true); // wired up in a later step
        styleActionButton(searchAppointmentButton, "#2563eb");

        HBox actionsRow = new HBox(20, registerAppointmentButton, searchAppointmentButton);
        actionsRow.setAlignment(Pos.CENTER);

        VBox appointmentsSection = buildAppointmentsTable();

        VBox content = new VBox(24, welcome, statsRow, actionsRow, appointmentsSection);
        content.setPadding(new Insets(28, 32, 28, 32));
        content.setFillWidth(true);
        VBox.setVgrow(appointmentsSection, Priority.ALWAYS);
        return content;
    }

    private void styleActionButton(Button button, String accentColor) {
        button.setPrefSize(240, 60);
        button.setStyle(
                "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-color: " + accentColor + ";"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 8;"
        );
    }

    private VBox buildAppointmentsTable() {
        Label heading = new Label("All Appointments");
        heading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TableView<AppointmentSummary> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<AppointmentSummary, String> numberCol = new TableColumn<>("Appointment No");
        numberCol.setCellValueFactory(new PropertyValueFactory<>("appointmentNumber"));

        TableColumn<AppointmentSummary, Integer> patientCol = new TableColumn<>("Patient ID");
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientId"));

        TableColumn<AppointmentSummary, String> dentistCol = new TableColumn<>("Dentist");
        dentistCol.setCellValueFactory(new PropertyValueFactory<>("dentistName"));

        TableColumn<AppointmentSummary, String> treatmentCol = new TableColumn<>("Treatment");
        treatmentCol.setCellValueFactory(new PropertyValueFactory<>("treatmentType"));

        TableColumn<AppointmentSummary, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));

        TableColumn<AppointmentSummary, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));

        TableColumn<AppointmentSummary, String> bookedByCol = new TableColumn<>("Booked By");
        bookedByCol.setCellValueFactory(new PropertyValueFactory<>("bookedBy"));

        table.getColumns().addAll(
                numberCol, patientCol, dentistCol, treatmentCol, dateCol, timeCol, bookedByCol
        );

        try {
            List<AppointmentSummary> summaries = appointmentController.listAll();
            table.setItems(FXCollections.observableArrayList(summaries));
        } catch (RuntimeException e) {
            new Alert(Alert.AlertType.ERROR, "Could not load the appointments list.").showAndWait();
        }

        VBox section = new VBox(10, heading, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return section;
    }

    private HBox buildStatsRow() {
        HBox row = new HBox(20);

        DashboardStats stats;
        try {
            stats = dashboardController.getStats();
        } catch (RuntimeException e) {
            new Alert(Alert.AlertType.ERROR, "Could not load dashboard stats. Check the database connection.")
                    .showAndWait();
            stats = new DashboardStats(0, 0, 0, 0);
        }

        row.getChildren().addAll(
                statCard("Today's Appointments", stats.getTodaysAppointments(), "#2563eb"),
                statCard("Total Appointments", stats.getTotalAppointments(), "#0d9488"),
                statCard("Upcoming Appointments", stats.getUpcomingAppointments(), "#7c3aed"),
                statCard("Total Patients", stats.getTotalPatients(), "#ea580c")
        );

        for (var child : row.getChildren()) {
            HBox.setHgrow(child, Priority.ALWAYS);
        }

        return row;
    }

    private VBox statCard(String title, int value, String accentColor) {
        Label valueLabel = new Label(String.valueOf(value));
        valueLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + accentColor + ";");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");

        VBox card = new VBox(6, valueLabel, titleLabel);
        card.setPadding(new Insets(20));
        card.setMinWidth(200);
        card.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 10;"
                + "-fx-border-radius: 10;"
                + "-fx-border-width: 1 1 1 4;"
                + "-fx-border-color: #e2e2e2 #e2e2e2 #e2e2e2 " + accentColor + ";"
        );
        return card;
    }
}
