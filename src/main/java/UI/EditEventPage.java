package UI;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.control.Alert;
import javafx.scene.text.Font;
import system.SystemManager;
import users.*;
import venue.*;
import events.*;

public class EditEventPage {

    private Main app;
    private Organizer organizer;
    private Event event;  // Current event being edited
    private VBox rootPane;

    // Fields for general event input
    private TextField eventNameField;
    private TextArea eventDescField;
    private ComboBox<String> eventVenueBox;
    private ComboBox<String> eventDepartmentBox;
    private TextField maxParticipantsField;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private ComboBox<String> startTimeBox;
    private ComboBox<String> endTimeBox;
    private RadioButton onlineRadioButton;
    private RadioButton offlineRadioButton;

    // Fields for event-specific input
    private TextField prizeAmountField;
    private TextField competitionTypeField;
    private TextField speakerNameField;
    private TextField topicNameField;
    private TextField trainerNameField;
    private TextArea materialsProvidedField;
    
    private VBox eventSpecificFields;

    public EditEventPage(Main app, Organizer organizer, Event event) {
        this.app = app;
        this.organizer = organizer;
        this.event = event;
        createEditEventPage();
    }

    private void createEditEventPage() {
        rootPane = new VBox();
        rootPane.setPadding(new Insets(20));
        rootPane.setSpacing(15);
        rootPane.setStyle("-fx-background-color: #FFEBCD;");

        Label titleLabel = new Label("Edit Event Details");
        titleLabel.setFont(Font.font(24));
        titleLabel.setTextFill(Color.web("#3498db"));

        eventNameField = new TextField(event.getName());
        eventNameField.setPromptText("Event Name");

        eventDescField = new TextArea(event.getDescription());
        eventDescField.setPromptText("Event Description");

        eventVenueBox = new ComboBox<>();
        eventVenueBox.getItems().addAll(getAllVenueStrings());
        eventVenueBox.setValue(event.getVenue().getName());

        eventDepartmentBox = new ComboBox<>();
        eventDepartmentBox.getItems().addAll(getAllDepartmentStrings());
        eventDepartmentBox.setValue(event.getDepartment().getName());

        maxParticipantsField = new TextField(String.valueOf(event.getMaxParticipants()));
        maxParticipantsField.setPromptText("Maximum Participants");

        startDatePicker = new DatePicker(event.getStartTimeDate().toLocalDate());
        endDatePicker = new DatePicker(event.getEndTimeDate().toLocalDate());

        startTimeBox = new ComboBox<>();
        startTimeBox.getItems().addAll(getTimeOptions());
        startTimeBox.setValue(event.getStartTimeDate().format(DateTimeFormatter.ofPattern("HH:mm")));

        endTimeBox = new ComboBox<>();
        endTimeBox.getItems().addAll(getTimeOptions());
        endTimeBox.setValue(event.getEndTimeDate().format(DateTimeFormatter.ofPattern("HH:mm")));

        ToggleGroup onlineOfflineToggleGroup = new ToggleGroup();
        onlineRadioButton = new RadioButton("Online");
        offlineRadioButton = new RadioButton("Offline");
        onlineRadioButton.setToggleGroup(onlineOfflineToggleGroup);
        offlineRadioButton.setToggleGroup(onlineOfflineToggleGroup);
        if (event.isOnline()) {
            onlineRadioButton.setSelected(true);
        } else {
            offlineRadioButton.setSelected(true);
        }

        ComboBox<String> eventTypeBox = new ComboBox<>();
        eventTypeBox.getItems().addAll("Competition", "Seminar", "Workshop");
        if (event instanceof Competition) {
            eventTypeBox.setValue("Competition");
        }
        else if (event instanceof Seminar) {
            eventTypeBox.setValue("Seminar");
        }
        else if (event instanceof Workshop) {
            eventTypeBox.setValue("Workshop");
        }
        

        eventSpecificFields = new VBox();
        eventSpecificFields.setSpacing(5);
        if (event instanceof Competition) {
            loadEventSpecificFields("Competition");
        }
        else if (event instanceof Seminar) {
            loadEventSpecificFields("Seminar");
        }
        else if (event instanceof Workshop) {
            loadEventSpecificFields("Workshop");
        }
        
        eventTypeBox.setOnAction(e -> {
            String selectedEventType = eventTypeBox.getValue();
            eventSpecificFields.getChildren().clear();
            loadEventSpecificFields(selectedEventType);
        });

        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(e -> saveChanges(eventTypeBox.getValue()));

        Button goBackButton = new Button("Go Back");
        goBackButton.setOnAction(e -> app.showOrganizerPage(organizer));

        VBox scrollableContent = new VBox();
        scrollableContent.setSpacing(10);
        scrollableContent.getChildren().addAll(titleLabel, eventNameField, eventDescField, eventVenueBox, eventDepartmentBox, 
            maxParticipantsField, startDatePicker, startTimeBox, endDatePicker, endTimeBox, onlineRadioButton, 
            offlineRadioButton, eventTypeBox, eventSpecificFields, saveButton, goBackButton);

        ScrollPane scrollPane = new ScrollPane(scrollableContent);
        scrollPane.setFitToWidth(true);

        rootPane.getChildren().add(scrollPane);
    }

    private void loadEventSpecificFields(String eventType) {
        switch (eventType) {
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
    }

    private VBox createCompetitionFields() {
        VBox competitionBox = new VBox();
        competitionBox.setSpacing(5);
        
        Competition comp = (Competition) event;
        Label prizeAmountLabel = new Label("Prize Amount:");
        prizeAmountField = new TextField(Integer.toString(comp.getPrizeAmount()));
        prizeAmountField.setPromptText("Enter Prize Amount");

        Label competitionTypeLabel = new Label("Competition Type:");
        competitionTypeField = new TextField(comp.getCompetitionType());
        competitionTypeField.setPromptText("Enter Competition Type");

        competitionBox.getChildren().addAll(prizeAmountLabel, prizeAmountField, competitionTypeLabel, competitionTypeField);
        return competitionBox;
    }

    private VBox createSeminarFields() {
        VBox seminarBox = new VBox();
        seminarBox.setSpacing(5);

        Seminar sem = (Seminar) event;

        Label speakerNameLabel = new Label("Speaker Name:");
        speakerNameField = new TextField(sem.getSpeaker());
        speakerNameField.setPromptText("Enter Speaker Name");

        Label topicNameLabel = new Label("Topic Name:");
        topicNameField = new TextField(sem.getTopic());
        topicNameField.setPromptText("Enter Topic Name");

        seminarBox.getChildren().addAll(speakerNameLabel, speakerNameField, topicNameLabel, topicNameField);
        return seminarBox;
    }

    private VBox createWorkshopFields() {
        VBox workshopBox = new VBox();
        workshopBox.setSpacing(5);

        Workshop workshop = (Workshop) event;

        Label trainerNameLabel = new Label("Trainer Name:");
        trainerNameField = new TextField(workshop.getTrainerName());
        trainerNameField.setPromptText("Enter Trainer Name");

        Label materialsProvidedLabel = new Label("Materials Provided:");
        materialsProvidedField = new TextArea(workshop.getMaterialsProvided());
        materialsProvidedField.setPromptText("Enter Materials Provided");

        workshopBox.getChildren().addAll(trainerNameLabel, trainerNameField, materialsProvidedLabel, materialsProvidedField);
        return workshopBox;
    }

    private ObservableList<String> getTimeOptions() {
        ObservableList<String> timeOptions = FXCollections.observableArrayList();
        for (int hour = 0; hour <= 23; hour++) {
            timeOptions.add(String.format("%02d:00", hour));
            timeOptions.add(String.format("%02d:30", hour));
        }
        return timeOptions;
    }

    private void saveChanges(String eventType) {
        switch (eventType) {
            case "Competition":
                if (event instanceof Competition) {
                    event = ((Competition)event).toCompetition();
                }
                else if (event instanceof Seminar) {
                    event = ((Seminar)event).toCompetition();
                }
                else if (event instanceof Workshop) {
                    event = ((Workshop)event).toCompetition();
                }
                ((Competition)event).setPrizeAmount(Integer.parseInt(prizeAmountField.getText()));
                ((Competition)event).setCompetitionType(competitionTypeField.getText());
                break;
            case "Seminar":
                if (event instanceof Competition) {
                    event = ((Competition)event).toSeminar();
                }
                else if (event instanceof Seminar) {
                    event = ((Seminar)event).toSeminar();
                }
                else if (event instanceof Workshop) {
                    event = ((Workshop)event).toSeminar();
                }
                ((Seminar)event).setSpeaker(speakerNameField.getText());
                ((Seminar)event).setTopic(topicNameField.getText());
                break;
            case "Workshop":
                if (event instanceof Competition) {
                    event = ((Competition)event).toWorkshop();
                }
                else if (event instanceof Seminar) {
                    event = ((Seminar)event).toWorkshop();
                }
                else if (event instanceof Workshop) {
                    event = ((Workshop)event).toWorkshop();
                }
                ((Workshop)event).setTrainerName(trainerNameField.getText());
                ((Workshop)event).setMaterialsProvided(materialsProvidedField.getText());
                break;
        }        
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String startTime = startTimeBox.getValue();
        String endTime = endTimeBox.getValue();
        String startDateTime = startDate.toString() + " " + startTime;
        String endDateTime = endDate.toString() + " " + endTime;

        event.setName(eventNameField.getText());
        event.setDescription(eventDescField.getText());
        event.setVenue(SystemManager.getVenue(eventVenueBox.getValue()));
        event.setDepartment(SystemManager.getDepartment(eventDepartmentBox.getValue()));
        event.setMaxParticipants(Integer.parseInt(maxParticipantsField.getText()));
        event.setOnline(onlineRadioButton.isSelected());
        event.setEventType(eventType);
        event.setStartTime(startDateTime);
        event.setEndTime(endDateTime);


        showAlert("Changes Saved", "Event details have been updated successfully.");
        app.showOrganizerPage(organizer);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
