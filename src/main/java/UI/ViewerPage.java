package UI;

import eventmanagement.Main;
import system.SystemManager;
import users.Viewer;
import venue.Department;
import venue.Venue;
import events.Competition;
import events.Event;
import events.Seminar;
import events.Workshop;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.GridPane;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.InputStream;

public class ViewerPage {

    private Main app;
    private Viewer viewer;
    private VBox rootPane;
    private ComboBox<String> eventTypeFilter;
    private ComboBox<String> departmentFilter;
    private ComboBox<String> venueFilter;
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

        // Calendar View Tab
        Tab calendarTab = new Tab("Calendar View");
        CalendarView calendarView = new CalendarView(SystemManager.getAllEvents());
        calendarTab.setContent(calendarView);

        tabPane.getTabs().addAll(availableEventsTab, registeredEventsTab, favoriteEventsTab, calendarTab);

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

        // Department Filter
        departmentFilter = new ComboBox<>();
        ArrayList<Department> availableDepartments = SystemManager.getAllDepartments();
        departmentFilter.getItems().add("All Departments"); // Add actual departments dynamically if available
        availableDepartments.forEach(department -> departmentFilter.getItems().add(department.getName()));
        departmentFilter.setValue("All Departments");
        departmentFilter.setOnAction(e -> applyFilters());

        // Venue Filter
        venueFilter = new ComboBox<>();
        ArrayList<Venue> availableVenues = SystemManager.getAllVenues();
        venueFilter.getItems().addAll("All Venues"); // Add actual venues dynamically if available
        availableVenues.forEach(venue -> venueFilter.getItems().add(venue.getName()));
        venueFilter.setValue("All Venues");
        venueFilter.setOnAction(e -> applyFilters());

        // Date Filters
        afterDateFilter = new DatePicker();
        afterDateFilter.setPromptText("After Date");
        afterDateFilter.setOnAction(e -> applyFilters());

        beforeDateFilter = new DatePicker();
        beforeDateFilter.setPromptText("Before Date");
        beforeDateFilter.setOnAction(e -> applyFilters());

        filterPane.getChildren().addAll(
            new Label("Event Type:"), eventTypeFilter,
            new Label("Department:"), departmentFilter,
            new Label("Venue:"), venueFilter,
            new Label("After:"), afterDateFilter,
            new Label("Before:"), beforeDateFilter
        );

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

        // Filter by department
        String selectedDepartment = departmentFilter.getValue();
        if (!selectedDepartment.equals("All Departments")) {
            filteredEvents = (ArrayList<Event>) filteredEvents.stream()
                    .filter(event -> event.getDepartment().getName().equals(selectedDepartment))
                    .collect(Collectors.toList());
        }

        // Filter by venue
        String selectedVenue = venueFilter.getValue();
        if (!selectedVenue.equals("All Venues")) {
            filteredEvents = (ArrayList<Event>) filteredEvents.stream()
                    .filter(event -> event.getVenue().getName().equals(selectedVenue))
                    .collect(Collectors.toList());
        }

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
            eventCard.setStyle("-fx-background-color: #ffe6b3;");
            Workshop workshopEvent = (Workshop) event;
            field1Label = new Label("Trainer Name: " + workshopEvent.getTrainerName());
            field2Label = new Label("Materials Provided: " + workshopEvent.getMaterialsProvided());
        } else if (event instanceof Seminar) {
            eventCard.setStyle("-fx-background-color: #b3d9ff;");
            Seminar seminarEvent = (Seminar) event;
            field1Label = new Label("Speaker: " + seminarEvent.getSpeaker());
            field2Label = new Label("Topic: " + seminarEvent.getTopic());
        } else if (event instanceof Competition) {
            eventCard.setStyle("-fx-background-color: #ccffcc;");
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
        favoriteButton.setOnAction(e -> {
            if (isFavourite) {
                SystemManager.removeFromFavourites(viewer.getUserID(), event.getEventID());
                favoriteButton.setText("Add to Favorites");
            } else {
                SystemManager.addToFavourites(viewer.getUserID(), event.getEventID());
                favoriteButton.setText("Remove from Favorites");
            }
            app.showViewerPage(viewer);
        });

        // Register button
        Button registerButton = new Button("Register");
        Button waitingListButton = new Button("Join Waiting List");

        // Check if event is full and viewer is not already registered
        boolean isFull = SystemManager.getListOfUsersFromEvent(event.getEventID()).size() >= event.getMaxParticipants();
        boolean isRegistered = SystemManager.userIsRegisteredToEvent(viewer.getUserID(), event.getEventID());

        if (isRegistered) {
            int position = SystemManager.userPositionInWaitingList(viewer.getUserID(), event.getEventID());
            if (position > event.getMaxParticipants()) {
                registerButton.setText("In Waiting List (Position " + (position-event.getMaxParticipants()) + ")\n\tClick to Unregister");
                registerButton.setDisable(false);
            }
            else {
                registerButton.setText("Unregister");
                registerButton.setDisable(false);
            }

            registerButton.setOnAction(e -> {
                SystemManager.removeUserRegistrationFromEvent(viewer.getUserID(), event.getEventID());
                app.showViewerPage(viewer);
            });
        } else if (isFull) {
            registerButton.setText("Event Full");
            registerButton.setDisable(true);

            // Show waiting list button if event is full and user is not registered
            int queueLength = SystemManager.getWaitingList(event.getEventID(), event.getMaxParticipants()).size();
            waitingListButton.setText("Join Waiting List (" + queueLength + " in queue)");
            waitingListButton.setOnAction(e -> {
                SystemManager.registerUserToEvent(viewer.getUserID(), event.getEventID());
                app.showViewerPage(viewer);
            });
        } else {
            registerButton.setOnAction(e -> {
                SystemManager.registerUserToEvent(viewer.getUserID(), event.getEventID());
                registerButton.setText("Unregister");
                registerButton.setDisable(false);

                registerButton.setOnAction(unregisterEvent -> {
                    SystemManager.removeUserRegistrationFromEvent(viewer.getUserID(), event.getEventID());
                    registerButton.setText("Register");
                    registerButton.setDisable(false);
                    app.showViewerPage(viewer);
                });
                app.showViewerPage(viewer);
            });
        }

        HBox buttonContainer = new HBox(10);
        buttonContainer.getChildren().addAll(favoriteButton, registerButton);

        if (isFull && !isRegistered) {
            buttonContainer.getChildren().add(waitingListButton);
        }

        eventCard.getChildren().addAll(
            titleBox, eventDesc, eventDate, eventVenue, eventDept, eventOrganizer, 
            isOnlineLabel, maxParticipants, field1Label, field2Label, buttonContainer
        );

        return eventCard;
    }

    public VBox getRootPane() {
        return rootPane;
    }
}










class CalendarView extends VBox {

    private YearMonth currentMonth;
    private List<Event> events;

    public CalendarView(List<Event> events) {
        this.events = events;
        this.currentMonth = YearMonth.now();
        updateCalendar();
    }

    private void updateCalendar() {
        this.getChildren().clear();

        // Header for the month
        Label monthLabel = new Label(currentMonth.getMonth() + " " + currentMonth.getYear());
        monthLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        monthLabel.setAlignment(Pos.CENTER);

        // Grid for days of the month
        GridPane calendarGrid = new GridPane();
        calendarGrid.setHgap(10);
        calendarGrid.setVgap(10);
        calendarGrid.setPadding(new Insets(10));

        // Days of the week header
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < daysOfWeek.length; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            dayLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            calendarGrid.add(dayLabel, i, 0);
        }

        // Get events for the current month
        Map<LocalDate, List<Event>> eventsByDate = events.stream()
                .filter(event -> event.getStartTimeDate().getYear() == currentMonth.getYear() &&
                        event.getStartTimeDate().getMonth() == currentMonth.getMonth())
                .collect(Collectors.groupingBy(event -> event.getStartTimeDate().toLocalDate()));

        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7;

        // Fill in days of the month
        int dayCounter = 1;
        for (int row = 1; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                if (row == 1 && col < firstDayOfWeek) continue;
                if (dayCounter > currentMonth.lengthOfMonth()) break;

                LocalDate date = firstDayOfMonth.withDayOfMonth(dayCounter);
                VBox dayBox = new VBox();
                dayBox.setAlignment(Pos.TOP_LEFT);
                dayBox.setPadding(new Insets(5));
                dayBox.setStyle("-fx-border-color: lightgray;");
                
                Label dayLabel = new Label(String.valueOf(dayCounter));
                dayBox.getChildren().add(dayLabel);

                // Highlight event dates
                if (eventsByDate.containsKey(date)) {
                    dayBox.setStyle("-fx-background-color: lightblue;");
                    List<Event> dayEvents = eventsByDate.get(date);
                    
                    // Add Tooltip to show event details on hover
                    Tooltip tooltip = new Tooltip(getEventDetails(dayEvents));
                    Tooltip.install(dayBox, tooltip);

                    // Add event list popover on click
                    dayBox.setOnMouseClicked(event -> showEventPopover(dayEvents, dayBox));
                }

                calendarGrid.add(dayBox, col, row);
                dayCounter++;
            }
        }

        // Navigation buttons for month
        Label prevButton = new Label("<");
        prevButton.setOnMouseClicked(e -> navigateMonth(-1));
        Label nextButton = new Label(">");
        nextButton.setOnMouseClicked(e -> navigateMonth(1));

        // Arrange header and grid
        VBox headerBox = new VBox(new Label("Calendar View"), prevButton, monthLabel, nextButton);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setSpacing(10);

        this.getChildren().addAll(headerBox, calendarGrid);
    }

    // Method to navigate between months
    private void navigateMonth(int direction) {
        currentMonth = currentMonth.plusMonths(direction);
        updateCalendar();
    }

    // Method to format event details as a string for the tooltip
    private String getEventDetails(List<Event> events) {
        StringBuilder details = new StringBuilder();
        for (Event event : events) {
            details.append(event.getName()).append(" - ").append(event.getStartTime()).append("\n");
        }
        return details.toString();
    }

    // Method to show a popover with event details when clicked
    private void showEventPopover(List<Event> events, VBox dayBox) {
        Stage popover = new Stage();
        popover.initModality(Modality.APPLICATION_MODAL);
        VBox popoverContent = new VBox();
        popoverContent.setPadding(new Insets(10));
        popoverContent.setStyle("-fx-background-color: white; -fx-border-color: lightgray;");
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a");
        Label title = new Label("Events on " + events.get(0).getStartTimeDate().toLocalDate());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        for (Event event : events) {
            Label eventLabel = new Label(event.getName() + " at " + event.getStartTimeDate().format(formatter));
            eventLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            popoverContent.getChildren().add(eventLabel);
        }

        popover.setScene(new Scene(popoverContent));
        popover.show();
    }
}
