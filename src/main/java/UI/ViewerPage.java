package UI;

import eventmanagement.Main;
import system.SystemManager;
import users.Viewer;
import events.Competition;
import events.Event;
import events.Seminar;
import events.Workshop;

import java.time.LocalDate;
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
import java.util.stream.Collectors;

import javafx.scene.control.Alert.AlertType;

public class ViewerPage {

    private Main app;
    private Viewer viewer;
    private VBox rootPane;
    private ComboBox<String> eventTypeFilter;
    private DatePicker afterDateFilter, beforeDateFilter;
    private VBox eventsContainer;

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

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab availableEventsTab = new Tab("Available Events");
        availableEventsTab.setContent(createAvailableEventsView());

        Tab registeredEventsTab = new Tab("Registered Events");
        registeredEventsTab.setContent(createRegisteredEventsView());

        Tab favoriteEventsTab = new Tab("Favourite Events");
        favoriteEventsTab.setContent(createFavoriteEventsView());

        tabPane.getTabs().addAll(availableEventsTab, registeredEventsTab, favoriteEventsTab);

        Button goBack = new Button("Log Out");
        goBack.setOnAction(e -> app.showLoginPage());

        rootPane.getChildren().addAll(userInfoContainer, titleLabel, createFilterPane(), tabPane, goBack);
    }

    private HBox createFilterPane() {
        HBox filterPane = new HBox(10);
        filterPane.setPadding(new Insets(10));
        filterPane.setSpacing(10);

        // Event Type Filter
        eventTypeFilter = new ComboBox<>();
        eventTypeFilter.getItems().addAll("All", "Competition", "Workshop", "Seminar");
        eventTypeFilter.setValue("All");
        
        eventTypeFilter.setOnAction(e -> applyFilters());

        // Date Filters
        afterDateFilter = new DatePicker();
        afterDateFilter.setPromptText("After Date");
        afterDateFilter.setOnAction(e -> applyFilters());

        beforeDateFilter = new DatePicker();
        beforeDateFilter.setPromptText("Before Date");
        beforeDateFilter.setOnAction(e -> applyFilters());

        filterPane.getChildren().addAll(new Label("Event Type:"), eventTypeFilter, new Label("After:"), afterDateFilter, new Label("Before:"), beforeDateFilter);
        
        return filterPane;
    }

    private VBox createAvailableEventsView() {
        eventsContainer = new VBox();
        eventsContainer.setSpacing(10);

        ScrollPane scrollPane = new ScrollPane(eventsContainer);
        scrollPane.setFitToWidth(true);

        displayEvents(SystemManager.getAllEvents());

        return new VBox(scrollPane);
    }

    private void displayEvents(ArrayList<Event> events) {
        eventsContainer.getChildren().clear();
        events.forEach(event -> {
            VBox eventCard = createEventCard(event);
            eventsContainer.getChildren().add(eventCard);
        });
    }

    private void applyFilters() {
        ArrayList<Event> allEvents = SystemManager.getAllEvents();

        // Filter by event type
        String selectedType = eventTypeFilter.getValue();
        ArrayList<Event> filteredEvents = (ArrayList<Event>) allEvents.stream()
                .filter(event -> selectedType.equals("All") || (selectedType.equals("Competition") && event instanceof Competition)
                        || (selectedType.equals("Workshop") && event instanceof Workshop)
                        || (selectedType.equals("Seminar") && event instanceof Seminar))
                .collect(Collectors.toList());

        // Filter by date range
        LocalDate afterDate = afterDateFilter.getValue();
        LocalDate beforeDate = beforeDateFilter.getValue();

        if (afterDate != null) {
            filteredEvents = (ArrayList<Event>) filteredEvents.stream()
                    .filter(event -> !event.getStartTimeDate().toLocalDate().isBefore(afterDate))
                    .collect(Collectors.toList());
        }

        if (beforeDate != null) {
            filteredEvents = (ArrayList<Event>) filteredEvents.stream()
                    .filter(event -> !event.getStartTimeDate().toLocalDate().isAfter(beforeDate))
                    .collect(Collectors.toList());
        }

        displayEvents(filteredEvents);
    }

    private VBox createRegisteredEventsView() {
        VBox registeredContainer = new VBox();
        registeredContainer.setSpacing(10);

        ArrayList<Event> registeredEvents = SystemManager.getListOfRegisteredEvents(viewer.getUserID());
        registeredEvents.forEach(event -> {
            VBox eventCard = createEventCard(event);
            registeredContainer.getChildren().add(eventCard);
        });

        ScrollPane scrollPane = new ScrollPane(registeredContainer);
        scrollPane.setFitToWidth(true);

        return new VBox(scrollPane);
    }

    private VBox createFavoriteEventsView() {
        VBox favoritesContainer = new VBox();
        favoritesContainer.setSpacing(10);

        ArrayList<Event> favoriteEvents = SystemManager.getListOfFavouriteEvents(viewer.getUserID());
        favoriteEvents.forEach(event -> {
            VBox eventCard = createEventCard(event);
            favoritesContainer.getChildren().add(eventCard);
        });

        ScrollPane scrollPane = new ScrollPane(favoritesContainer);
        scrollPane.setFitToWidth(true);

        return new VBox(scrollPane);
    }

    private VBox createEventCard(Event event) {
        VBox eventCard = new VBox();
        eventCard.setPadding(new Insets(15));
        eventCard.setSpacing(10);

        Label field1Label = null, field2Label = null;
        
        if (event instanceof Workshop) {
            eventCard.setStyle(eventCard.getStyle() + "-fx-background-color: #ffe6b3;");
            Workshop workshopEvent = (Workshop) event;
            field1Label = new Label("Trainer Name: " + workshopEvent.getTrainerName());
            field2Label = new Label("Materials Provided: " + workshopEvent.getMaterialsProvided());
        } else if (event instanceof Seminar) {
            eventCard.setStyle(eventCard.getStyle() + "-fx-background-color: #b3d9ff;");
            Seminar seminarEvent = (Seminar) event;
            field1Label = new Label("Speaker: " + seminarEvent.getSpeaker());
            field2Label = new Label("Topic: " + seminarEvent.getTopic());
        } else if (event instanceof Competition) {
            eventCard.setStyle(eventCard.getStyle() + "-fx-background-color: #ccffcc;");
            Competition competitionEvent = (Competition) event;
            field1Label = new Label("Prize Amount: " + competitionEvent.getPrizeAmount());
            field2Label = new Label("Type of Competition: " + competitionEvent.getCompetitionType());
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

        boolean isFavourite = SystemManager.isFavourite(viewer.getUserID(), event.getEventID());

        // Create favorite/unfavorite button
        Button favoriteButton = new Button(isFavourite ? "Remove from Favorites" : "Add to Favorites");
        favoriteButton.setStyle(isFavourite ? "-fx-text-fill: red;" : "-fx-text-fill: black;");

        favoriteButton.setOnAction(e -> {
            if (isFavourite) {
                SystemManager.removeFromFavourites(viewer.getUserID(), event.getEventID());
                favoriteButton.setText("Add to Favorites");
                favoriteButton.setStyle("-fx-text-fill: black;");
            } else {
                SystemManager.addToFavourites(viewer.getUserID(), event.getEventID());
                favoriteButton.setText("Remove from Favorites");
                favoriteButton.setStyle("-fx-text-fill: red;");
            }
            app.showViewerPage(viewer);
        });

        // Register/Unregister button based on registration status
        if (SystemManager.userIsRegisteredToEvent(viewer.getUserID(), event.getEventID())) {
            Button unregisterButton = new Button("Unregister");
            unregisterButton.setOnAction(e -> {
                SystemManager.removeUserRegistrationFromEvent(viewer.getUserID(), event.getEventID());
                app.showViewerPage(viewer);
            });
            unregisterButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: green;");
            eventCard.getChildren().addAll(titleBox, eventDesc, eventDate, eventVenue, eventDept, eventOrganizer, maxParticipants, isOnlineLabel, field1Label, field2Label, unregisterButton, favoriteButton);
        } else {
            Button registerButton = new Button("Register");
            registerButton.setOnAction(e -> {
                registerForEvent(event);
                app.showViewerPage(viewer);
            });
            eventCard.getChildren().addAll(titleBox, eventDesc, eventDate, eventVenue, eventDept, eventOrganizer, maxParticipants, isOnlineLabel, field1Label, field2Label, registerButton, favoriteButton);
        }

        return eventCard;
    }

    private void registerForEvent(Event event) {
        if (SystemManager.registerUserToEvent(viewer.getUserID(), event.getEventID())) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Registration");
            alert.setHeaderText("Registration Successful");
            alert.setContentText("You have successfully registered for " + event.getName());
            alert.showAndWait();
        } else {
            Alert alert = new Alert(AlertType.ERROR);
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