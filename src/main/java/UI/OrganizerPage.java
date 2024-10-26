package UI;

import java.util.ArrayList;

import eventmanagement.Main;
import events.Competition;
import events.Event;
import events.Seminar;
import events.Workshop;

import java.time.format.DateTimeFormatter;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
        eventCard.setPadding(new Insets(15));
        eventCard.setSpacing(10);
        Label field1Label = null, field2Label = null;
        
        if (event instanceof Workshop) {
            eventCard.setStyle(eventCard.getStyle() + "-fx-background-color: #ffe6b3;");
            Workshop workshop_event = (Workshop) event;
            field1Label = new Label("Trainer Name: " + workshop_event.getTrainerName());
            field2Label = new Label("Materials Provided: " + workshop_event.getMaterialsProvided());
        } else if (event instanceof Seminar) {
            eventCard.setStyle(eventCard.getStyle() + "-fx-background-color: #b3d9ff;");
            Seminar seminar_event = (Seminar) event;
            field1Label = new Label("Speaker: " + seminar_event.getSpeaker());
            field2Label = new Label("Topic: " + seminar_event.getTopic());
        } else if (event instanceof Competition) {
            eventCard.setStyle(eventCard.getStyle() + "-fx-background-color: #ccffcc;");
            Competition competition_event = (Competition) event;
            field1Label = new Label("Prize Amount: " + competition_event.getPrizeAmount());
            field2Label = new Label("Type of Competition: " + competition_event.getCompetitionType());
        }        
    
        HBox titleBox = new HBox();
        Label eventTitle = new Label(event.getName());
        eventTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
    
        // Add registered count at the right end of event title
        Label registeredCount = new Label("Registered x" + SystemManager.getListOfUsersFromEvent(event.getEventID()).size());
        registeredCount.setStyle("-fx-alignment: center-right; -fx-font-size: 14px;");
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
    
        eventCard.getChildren().addAll(titleBox, eventDesc, eventDate, eventVenue, eventDept, eventOrganizer, maxParticipants, isOnlineLabel, field1Label, field2Label);
        return eventCard;
    }

    public VBox getRootPane() {
        return rootPane;
    }
}
