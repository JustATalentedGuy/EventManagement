package UI;

import java.time.LocalDate;
import java.util.ArrayList;

import eventmanagement.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import system.SystemManager;
import users.Organizer;
import venue.Department;
import venue.Venue;

public class NewEventRequestPage {

    private Main app;
    private Organizer organizer;
    private VBox rootPane;

    // Fields for event-specific input
    private TextField prizeAmountField;         // For Competition
    private TextField competitionTypeField;     // For Competition
    private TextField speakerNameField;         // For Seminar
    private TextField topicNameField;           // For Seminar
    private TextField trainerNameField;         // For Workshop
    private TextArea materialsProvidedField;    // For Workshop

    // Containers for event-specific fields
    private VBox eventSpecificFields;

    public NewEventRequestPage(Main app, Organizer organizer) {
        this.app = app;
        this.organizer = organizer;
        createNewEventRequestPage();
    }

    private void createNewEventRequestPage() {
        rootPane = new VBox();
        rootPane.setPadding(new Insets(20));
        rootPane.setSpacing(15);
        rootPane.setStyle("-fx-background-color: #FFEBCD;"); // Light gray background

        // Add all components to the rootPane (not the ScrollPane)
        Label titleLabel = new Label("Request to Organize New Event");
        titleLabel.setFont(Font.font(24));
        titleLabel.setTextFill(Color.web("#3498db")); // Blue text

        TextField eventNameField = new TextField();
        eventNameField.setPromptText("Event Name");
        eventNameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        TextArea eventDescField = new TextArea();
        eventDescField.setPromptText("Event Description");
        eventDescField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        ComboBox<String> eventVenueBox = new ComboBox<>();
        eventVenueBox.getItems().addAll(getAllVenueStrings());
        eventVenueBox.setPromptText("Select Venue");
        eventVenueBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        ComboBox<String> eventDepartmentBox = new ComboBox<>();
        eventDepartmentBox.getItems().addAll(getAllDepartmentStrings());
        eventDepartmentBox.setPromptText("Select Department");
        eventDepartmentBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        TextField maxParticipantsField = new TextField();
        maxParticipantsField.setPromptText("Maximum Participants");
        maxParticipantsField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        // DatePicker for Start and End Dates
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Select Start Date");
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("Select End Date");

        // ComboBox for Start and End Times
        ComboBox<String> startTimeBox = new ComboBox<>();
        ComboBox<String> endTimeBox = new ComboBox<>();
        startTimeBox.getItems().addAll(getTimeOptions());
        endTimeBox.getItems().addAll(getTimeOptions());
        startTimeBox.setPromptText("Select Start Time");
        endTimeBox.setPromptText("Select End Time");

        // ToggleGroup for Online or Offline selection
        ToggleGroup onlineOfflineToggleGroup = new ToggleGroup();
        RadioButton onlineRadioButton = new RadioButton("Online");
        onlineRadioButton.setToggleGroup(onlineOfflineToggleGroup);
        onlineRadioButton.setSelected(true);  // Default to Online
        RadioButton offlineRadioButton = new RadioButton("Offline");
        offlineRadioButton.setToggleGroup(onlineOfflineToggleGroup);

        Label onlineOfflineLabel = new Label("Event Type:");
        onlineOfflineLabel.setStyle("-fx-font-weight: bold;");

        // ComboBox for event type selection
        ComboBox<String> eventTypeBox = new ComboBox<>();
        eventTypeBox.getItems().addAll("Competition", "Seminar", "Workshop");
        eventTypeBox.setPromptText("Select Event Type");
        eventTypeBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        // Initialize the VBox for event-specific fields
        eventSpecificFields = new VBox();
        eventSpecificFields.setSpacing(5);
        eventSpecificFields.setVisible(false); // Hide initially

        // Listen for event type changes and show corresponding fields
        eventTypeBox.setOnAction(e -> {
            String selectedEventType = eventTypeBox.getValue();
            eventSpecificFields.getChildren().clear(); // Clear previous fields
            switch (selectedEventType) {
                case "Competition":
                    eventSpecificFields.getChildren().addAll(createCompetitionFields());
                    break;
                case "Seminar":
                    eventSpecificFields.getChildren().addAll(createSeminarFields());
                    break;
                case "Workshop":
                    eventSpecificFields.getChildren().addAll(createWorkshopFields());
                    break;
            }
            eventSpecificFields.setVisible(true); // Show the fields after selection
        });

        Button submitButton = new Button("Submit Request");
        submitButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;");
        submitButton.setOnAction(e -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String startTime = startTimeBox.getValue();
            String endTime = endTimeBox.getValue();
            boolean online = onlineRadioButton.isSelected(); 
            String field1 = null, field2 = null;
    
            if (startDate != null && endDate != null && startTime != null && endTime != null) {
                String startDateTime = startDate.toString() + " " + startTime;
                String endDateTime = endDate.toString() + " " + endTime;

                if ("Competition".equals(eventTypeBox.getValue())) {
                    field1 = prizeAmountField.getText();
                    field2 = competitionTypeField.getText();
                } else if ("Seminar".equals(eventTypeBox.getValue())) {
                    field1 = speakerNameField.getText();
                    field2 = topicNameField.getText();
                } else if ("Workshop".equals(eventTypeBox.getValue())) {
                    field1 = trainerNameField.getText();
                    field2 = materialsProvidedField.getText();
                }

                // Process the event request
                submitEventRequest(eventNameField.getText(), eventDescField.getText(), eventVenueBox.getValue(),
                        eventDepartmentBox.getValue(), organizer.getUserID(), startDateTime, endDateTime, Integer.parseInt(maxParticipantsField.getText()), online, field1, field2, eventTypeBox.getValue());
            } else {
                System.out.println("Please select both start and end dates and times.");
            }
        });

        Button goBack = new Button("Go Back");
        goBack.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 16px; -fx-cursor: hand;");
        goBack.setOnAction(e -> app.showOrganizerPage(organizer));

        // Add all components to the rootPane
        /*rootPane.getChildren().addAll(titleLabel, eventNameField, eventDescField, eventVenueBox, eventDepartmentBox,
                startDatePicker, startTimeBox, endDatePicker, endTimeBox, onlineOfflineLabel, onlineRadioButton, offlineRadioButton,
                eventTypeBox, eventSpecificFields, submitButton, goBack);
        // Add the ScrollPane in the scene layout or root container*/
        // Create a scrollable container for the content
        VBox scrollableContent = new VBox();
        scrollableContent.setSpacing(10);

        // Add all components to the scrollable content container
        scrollableContent.getChildren().addAll(titleLabel, eventNameField, eventDescField, eventVenueBox, eventDepartmentBox, maxParticipantsField,
                startDatePicker, startTimeBox, endDatePicker, endTimeBox, onlineOfflineLabel, onlineRadioButton, offlineRadioButton,
                eventTypeBox, eventSpecificFields, submitButton, goBack);

        // Create the ScrollPane and set the content to the scrollable content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(scrollableContent);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        // Add the ScrollPane to the rootPane, along with any static elements like labels or borders
        rootPane.getChildren().add(scrollPane);

    }

    // Create fields for competition
    private VBox createCompetitionFields() {
        VBox competitionBox = new VBox();
        competitionBox.setSpacing(5);

        Label prizeAmountLabel = new Label("Prize Amount:");
        prizeAmountField = new TextField();
        prizeAmountField.setPromptText("Enter Prize Amount");
        prizeAmountField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        Label competitionTypeLabel = new Label("Competition Type:");
        competitionTypeField = new TextField();
        competitionTypeField.setPromptText("Enter Competition Type");
        competitionTypeField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        competitionBox.getChildren().addAll(prizeAmountLabel, prizeAmountField, competitionTypeLabel, competitionTypeField);
        return competitionBox;
    }

    // Create fields for seminar
    private VBox createSeminarFields() {
        VBox seminarBox = new VBox();
        seminarBox.setSpacing(5);

        Label speakerNameLabel = new Label("Speaker Name:");
        speakerNameField = new TextField();
        speakerNameField.setPromptText("Enter Speaker Name");
        speakerNameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        Label topicNameLabel = new Label("Topic Name:");
        topicNameField = new TextField();
        topicNameField.setPromptText("Enter Topic Name");
        topicNameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        seminarBox.getChildren().addAll(speakerNameLabel, speakerNameField, topicNameLabel, topicNameField);
        return seminarBox;
    }

    // Create fields for workshop
    private VBox createWorkshopFields() {
        VBox workshopBox = new VBox();
        workshopBox.setSpacing(5);

        Label trainerNameLabel = new Label("Trainer Name:");
        trainerNameField = new TextField();
        trainerNameField.setPromptText("Enter Trainer Name");
        trainerNameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        Label materialsProvidedLabel = new Label("Materials Provided:");
        materialsProvidedField = new TextArea();
        materialsProvidedField.setPromptText("Enter Materials Provided");
        materialsProvidedField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        workshopBox.getChildren().addAll(trainerNameLabel, trainerNameField, materialsProvidedLabel, materialsProvidedField);
        return workshopBox;
    }

    // Function to get time options in "HH:MM" format
    private ObservableList<String> getTimeOptions() {
        ObservableList<String> timeOptions = FXCollections.observableArrayList();
        for (int hour = 0; hour <= 23; hour++) {
            timeOptions.add(String.format("%02d:00", hour));
            timeOptions.add(String.format("%02d:30", hour));
        }
        return timeOptions;
    }

    private void submitEventRequest(String eventName, String eventDescription, String venueName, String departmentName, int organizerID, String startTime, String endTime, int maxParticipants, boolean isOnline, String field1, String field2, String eventType) {
        int venueID = SystemManager.getVenue(venueName).getVenueID();
        int departmentID = SystemManager.getDepartment(departmentName).getDepartmentID();
        SystemManager.createEvent(eventName, eventDescription, venueID, departmentID, organizerID, startTime, endTime, maxParticipants, isOnline, false, false, eventType, field1, field2);
    }

    private ArrayList<String> getAllVenueStrings() {
        ArrayList<String> venueStrings = new ArrayList<>();
        for (Venue venue : SystemManager.getAllVenues()) {
            venueStrings.add(venue.getName());
        }
        return venueStrings;
    }

    private ArrayList<String> getAllDepartmentStrings() {
        ArrayList<String> departmentStrings = new ArrayList<>();
        for (Department department : SystemManager.getAllDepartments()) {
            departmentStrings.add(department.getName());
        }
        return departmentStrings;
    }

    public VBox getRootPane() {
        return rootPane;
    }
}