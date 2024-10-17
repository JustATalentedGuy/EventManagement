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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import system.SystemManager;
import users.Organizer;

public class OrganizerPage {

    private Main app;
    private Organizer organizer;
    private VBox rootPane;

    public OrganizerPage(Main app, Organizer organizer) {
        this.app = app;
        this.organizer = organizer;
        createOrganizerPage();
    }

    private void createOrganizerPage() {
        rootPane = new VBox();
        rootPane.setPadding(new Insets(20));
        rootPane.setSpacing(20);
        rootPane.setStyle("-fx-background-color: #00FFFF;");

        String username = organizer.getUsername() != null ? organizer.getUsername() : "Guest";
        Label titleLabel = new Label("Available Events for " + username);
        titleLabel.setFont(Font.font(20));
        titleLabel.setStyle("-fx-text-fill: #FFFFFF;");
        titleLabel.setBackground(new Background(new BackgroundFill(Color.web("#0000FF"), CornerRadii.EMPTY, Insets.EMPTY)));

        VBox eventsContainer = new VBox();  // New VBox for events
        eventsContainer.setSpacing(10);      // Space between event cards
        eventsContainer.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-width: 1;");

        ArrayList<Event> events = SystemManager.getAllEvents();  // Simulated events
        events.forEach(event -> {
            VBox eventCard = createEventCard(event);
            eventsContainer.getChildren().add(eventCard); // Add to new VBox
        });

        ScrollPane scrollPane = new ScrollPane(eventsContainer); // Set ScrollPane to use eventsContainer
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        Button registerButton = new Button("Add Event Request");
        registerButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;");
        registerButton.setOnAction(e -> app.showNewEventRequestPage(organizer));

        Button goBack = new Button("Log Out");
        goBack.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;");
        goBack.setOnAction(e
                -> {
            app.showLoginPage();
        }
        );

        rootPane.getChildren().addAll(titleLabel, scrollPane, registerButton, goBack);
    }

    private VBox createEventCard(Event event) {
        VBox eventCard = new VBox();
        eventCard.setPadding(new Insets(10));
        eventCard.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: #ffffff;");
        Label eventTitle = new Label(event.getName());
        eventTitle.setFont(Font.font(18));
        eventTitle.setStyle("-fx-text-fill: #3498db;");
        Label eventDesc = new Label(event.getDescription());
        eventDesc.setFont(Font.font(14));
        eventDesc.setStyle("-fx-text-fill: #666666;");
        Label eventDate = new Label("Date: " + event.getStartTime() + " - " + event.getEndTime());
        eventDate.setFont(Font.font(14));
        eventDate.setStyle("-fx-text-fill: #666666;");

        eventCard.getChildren().addAll(eventTitle, eventDesc, eventDate);
        return eventCard;
    }

    public VBox getRootPane() {
        return rootPane;
    }
}
