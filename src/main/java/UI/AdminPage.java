package UI;

import java.util.ArrayList;

import eventmanagement.Main;
import events.Event;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
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
        for (Event req : requests) {
            VBox requestCard = new VBox();
            requestCard.setPadding(new Insets(10));
            requestCard.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: #E16274;"); // light gray background

            Label requestLabel = new Label(req.getName());
            requestLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            requestLabel.setTextFill(Color.WHITE);

            Label organizerLabel = new Label("Organizer: " + req.getOrganizer().getUsername());
            Label departmentLabel = new Label("Department: " + req.getDepartment().getName());
            Label venueLabel = new Label("Venue: " + req.getVenue().getName());

            HBox buttonBox = new HBox();
            buttonBox.setSpacing(10);

            Button approveButton = new Button("Approve");
            approveButton.setOnAction(e -> {
                admin.acceptOrganizingRequest(req);
                app.showAdminPage(admin);
            });
            approveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

            Button rejectButton = new Button("Reject");
            rejectButton.setOnAction(e -> {
                admin.declineOrganizingRequest(req);
                app.showAdminPage(admin);
            });
            rejectButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white;");

            buttonBox.getChildren().addAll(approveButton, rejectButton);

            requestCard.getChildren().addAll(requestLabel, organizerLabel, departmentLabel, venueLabel, buttonBox);
            requestsBox.getChildren().add(requestCard);
        }
        return requestsBox;
    }

    public VBox getRootPane() {
        return rootPane;
    }
}
