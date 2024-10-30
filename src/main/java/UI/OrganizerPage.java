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

import javafx.scene.control.*;


public class OrganizerPage {

    private Main app;
    private Organizer organizer;
    private VBox rootPane;
    private VBox eventsContainer;
    private VBox myEventsContainer;
    private VBox pendingRequestsContainer;

    private ToggleGroup toggleGroup;

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

        // Toggle buttons for event categories
        toggleGroup = new ToggleGroup();

        ToggleButton allEventsToggle = new ToggleButton("All Events");
        ToggleButton myEventsToggle = new ToggleButton("My Events");
        ToggleButton pendingRequestsToggle = new ToggleButton("Pending Requests");

        allEventsToggle.setToggleGroup(toggleGroup);
        myEventsToggle.setToggleGroup(toggleGroup);
        pendingRequestsToggle.setToggleGroup(toggleGroup);

        HBox toggleButtons = new HBox(10, allEventsToggle, myEventsToggle, pendingRequestsToggle);
        toggleButtons.setPadding(new Insets(10, 0, 10, 0));

        // Set actions for toggle buttons
        allEventsToggle.setOnAction(e -> showAllEvents());
        myEventsToggle.setOnAction(e -> showMyEvents());
        pendingRequestsToggle.setOnAction(e -> showPendingRequests());

        // Container for events
        eventsContainer = new VBox();
        myEventsContainer = new VBox();
        pendingRequestsContainer = new VBox();

        ScrollPane scrollPane = new ScrollPane(eventsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        Button registerButton = new Button("Add Event Request");
        registerButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;");
        registerButton.setOnAction(e -> app.showNewEventRequestPage(organizer));

        Button goBack = new Button("Log Out");
        goBack.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;");
        goBack.setOnAction(e -> app.showLoginPage());

        rootPane.getChildren().addAll(titleLabel, toggleButtons, scrollPane, registerButton, goBack);
        
        // Default view for All Events
        showAllEvents();
    }

    private void showAllEvents() {
        eventsContainer.getChildren().clear();
        ArrayList<Event> events = SystemManager.getAllEvents();  // Simulated events
        events.forEach(event -> {
            VBox eventCard = createEventCard(event, false, false);
            eventsContainer.getChildren().add(eventCard);
        });
    }

    private void showMyEvents() {
        myEventsContainer.getChildren().clear();
        ArrayList<Event> myEvents = SystemManager.getEventsByOrganizer(organizer.getUserID()); // Events by organizer
        myEvents.forEach(event -> {
            VBox eventCard = createEventCard(event, true, false); // Edit button for each event
            myEventsContainer.getChildren().add(eventCard);
        });
        eventsContainer.getChildren().setAll(myEventsContainer.getChildren());
    }

    private void showPendingRequests() {
        pendingRequestsContainer.getChildren().clear();
        ArrayList<Event> pendingRequests = SystemManager.getPendingEventRequests(organizer.getUserID()); // Pending requests
        pendingRequests.forEach(event -> {
            VBox eventCard = createEventCard(event, false, true); // Delete button for each request
            pendingRequestsContainer.getChildren().add(eventCard);
        });
        eventsContainer.getChildren().setAll(pendingRequestsContainer.getChildren());
    }

    private VBox createEventCard(Event event, boolean showEditButton, boolean showDeleteButton) {
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

        // Conditionally add Edit/Delete buttons
        if (showEditButton) {
            Button editButton = new Button("Edit");
            editButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
            editButton.setOnAction(e -> app.showEditEventPage(organizer, event));
            eventCard.getChildren().add(editButton);
        }

        if (showDeleteButton) {
            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
            deleteButton.setOnAction(e -> SystemManager.deleteEvent(event.getEventID()));
            eventCard.getChildren().add(deleteButton);
        }

        return eventCard;
    }

    public VBox getRootPane() {
        return rootPane;
    }
}
