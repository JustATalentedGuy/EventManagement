package UI;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import eventmanagement.Main;
import events.Competition;
import events.Event;
import events.Seminar;
import events.Workshop;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import system.SystemManager;
import users.Admin;

public class AdminPage {

    private Main app;
    private Admin admin;
    private VBox rootPane;

    public AdminPage(Main app, Admin admin) {
        this.app = app;
        this.admin = admin;
        createAdminPage();
    }

    private void createAdminPage() {
        rootPane = new VBox();
        rootPane.setPadding(new Insets(20));
        rootPane.setSpacing(20);
        rootPane.setStyle("-fx-background-color: #f0f0f0;"); // light gray background

        Label titleLabel = new Label("Admin Dashboard - Event Approval Requests");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.BLACK);
        titleLabel.setBackground(new Background(new BackgroundFill(Color.web("#F68C40"), CornerRadii.EMPTY, Insets.EMPTY)));

        // Simulated event requests
        VBox approvalRequests = createApprovalRequestCards();

        ScrollPane scrollPane = new ScrollPane(approvalRequests);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #3E2723;"); // transparent background

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);
        buttonBox.setPadding(new Insets(10));

        Button manageDepartmentsButton = new Button("Manage Departments");
        manageDepartmentsButton.setOnAction(e -> app.showManageDepartmentsPage(admin));
        manageDepartmentsButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"); // green button

        Button manageVenuesButton = new Button("Manage Venues");
        manageVenuesButton.setOnAction(e -> app.showManageVenuesPage(admin));
        manageVenuesButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"); // green button

        Button goBack = new Button("Log Out");
        goBack.setOnAction(e
                -> {
            app.showLoginPage();
        }
        );
        goBack.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white;"); // red button

        buttonBox.getChildren().addAll(manageDepartmentsButton, manageVenuesButton, goBack);

        rootPane.getChildren().addAll(titleLabel, scrollPane, buttonBox);
    }

    private VBox createApprovalRequestCards() {
        VBox requestsBox = new VBox();
        requestsBox.setSpacing(10);
        ArrayList<Event> requests = SystemManager.getListOfOrganizingRequests();

        // Simulated event approval requests
        for (Event event : requests) {
            VBox eventCard = new VBox();
            eventCard.setPadding(new Insets(15));
            eventCard.setSpacing(10);

            if (event instanceof Workshop) {
                eventCard.setStyle("-fx-background-color: #ffe6b3;");
            } else if (event instanceof Seminar) {
                eventCard.setStyle("-fx-background-color: #b3d9ff;");
            } else if (event instanceof Competition) {
                eventCard.setStyle("-fx-background-color: #ccffcc;");
            }

            HBox titleBox = new HBox();
            Label eventTitle = new Label(event.getName());
            eventTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

            // Registered count
            Label registeredCount = new Label("Registered x" + SystemManager.getListOfUsersFromEvent(event.getEventID()).size());
            registeredCount.setStyle("-fx-font-size: 14px;");
            HBox.setHgrow(registeredCount, Priority.ALWAYS);
            registeredCount.setMaxWidth(Double.MAX_VALUE);

            titleBox.getChildren().addAll(eventTitle, registeredCount);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a");
            String formattedStartTime = event.getStartTimeDate().format(formatter);
            String formattedEndTime = event.getEndTimeDate().format(formatter);

            Label eventDesc = new Label(event.getDescription());
            Label eventDate = new Label("Date: " + formattedStartTime + " - " + formattedEndTime);
            Label eventVenue = new Label("Venue: " + event.getVenue().getName());
            Label eventDept = new Label("Department: " + event.getDepartment().getName());
            Label eventOrganizer = new Label("Organizer: " + event.getOrganizer().getUsername());
            Label maxParticipants = new Label("Max Participants: " + event.getMaxParticipants());
            Label isOnlineLabel = new Label(event.isOnline() ? "Online Event" : "Offline Event");

            eventCard.getChildren().addAll(titleBox, eventDesc, eventDate, eventVenue, eventDept, eventOrganizer, maxParticipants, isOnlineLabel);

            HBox buttonBox = new HBox();
            Button approveButton = new Button("Approve");
            approveButton.setOnAction(e -> {
                admin.acceptOrganizingRequest(event);
                app.showAdminPage(admin);
            });
            approveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

            Button rejectButton = new Button("Reject");
            rejectButton.setOnAction(e -> {
                admin.declineOrganizingRequest(event);
                app.showAdminPage(admin);
            });
            rejectButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white;");

            buttonBox.getChildren().addAll(approveButton, rejectButton);

            eventCard.getChildren().add(buttonBox);
            requestsBox.getChildren().add(eventCard);
        }
        return requestsBox;
    }

    public VBox getRootPane() {
        return rootPane;
    }
}
