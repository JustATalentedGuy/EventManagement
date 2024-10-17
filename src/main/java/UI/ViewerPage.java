package UI;

import eventmanagement.Main;
import system.SystemManager;
import users.Viewer;
import events.Competition;
import events.Event;
import events.Seminar;
import events.Workshop;

import java.time.format.DateTimeFormatter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;


import java.io.InputStream;
import java.util.ArrayList;

public class ViewerPage {

    private Main app;
    private Viewer viewer;
    private VBox rootPane;

    public ViewerPage(Main app, Viewer viewer) {
        this.app = app;
        this.viewer = viewer;
        createViewerPage();
    }

    private void createViewerPage() {
        rootPane = new VBox();
        rootPane.setPadding(new Insets(10));
        rootPane.setSpacing(10);

        String username = viewer.getUsername() != null ? viewer.getUsername() : "Guest";
        Label titleLabel = new Label("Available Events for " + username);

        HBox userInfoContainer = new HBox();
        userInfoContainer.setAlignment(Pos.TOP_RIGHT);
        userInfoContainer.setSpacing(5);

        Label userNameLabel = new Label(username);

        InputStream inputStream = getClass().getResourceAsStream("/assets/account_icon.jpeg");
        if (inputStream == null) {
            System.out.println("Image not found! Check the path.");
            userInfoContainer.getChildren().add(userNameLabel);
        } else {
            Image personIcon = new Image(inputStream);
            ImageView iconView = new ImageView(personIcon);
            iconView.setFitWidth(20);
            iconView.setFitHeight(20);
            userInfoContainer.getChildren().addAll(iconView, userNameLabel);
        }

        VBox eventsContainer = new VBox();
        eventsContainer.setSpacing(10);

        ArrayList<Event> events = SystemManager.getAllEvents();
        events.forEach(event -> {
            VBox eventCard = createEventCard(event);
            eventsContainer.getChildren().add(eventCard);
        });

        Button goBack = new Button("Log Out");
        goBack.setOnAction(e -> 
            {
                app.showLoginPage();
            }
        );

        ScrollPane scrollPane = new ScrollPane(eventsContainer);
        scrollPane.setFitToWidth(true);

        rootPane.getChildren().addAll(userInfoContainer, titleLabel, scrollPane, goBack);
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
    
        // Check if the user is already registered for the event
        if (SystemManager.userIsRegisteredToEvent(viewer.getUserID(), event.getEventID())) {
            Label registeredLabel = new Label("Registered");
            registeredLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: green;");
            eventCard.getChildren().addAll(titleBox, eventDesc, eventDate, eventVenue, eventDept, eventOrganizer, maxParticipants, isOnlineLabel, field1Label, field2Label, registeredLabel);
        } else {
            Button registerButton = new Button("Register");
            registerButton.setOnAction(e -> registerForEvent(event));
            eventCard.getChildren().addAll(titleBox, eventDesc, eventDate, eventVenue, eventDept, eventOrganizer, maxParticipants, isOnlineLabel, field1Label, field2Label, registerButton);
        }
        return eventCard;
    }


    private void registerForEvent(Event event) {
        if (SystemManager.registerUserToEvent(viewer.getUserID(), event.getEventID())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration");
            alert.setHeaderText("Registration Successful");
            alert.setContentText("You have successfully registered for " + event.getName());
            alert.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Registration");
            alert.setHeaderText("Registration Failed");
            alert.setContentText("You have already registered for this event");
            alert.showAndWait();
        }
    }

    public VBox getRootPane() {
        return rootPane;
    }
}
