package UI;

import java.util.ArrayList;

import eventmanagement.Main;
import users.Admin;
import venue.Venue;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import system.SystemManager;

public class AdminVenuesPage {

    private Main app;
    private Admin admin;
    private VBox rootPane;

    public AdminVenuesPage(Main app, Admin admin) {
        this.app = app;
        this.admin = admin;
        createAdminVenuesPage();
    }

    private void createAdminVenuesPage() {
        rootPane = new VBox();
        rootPane.setPadding(new Insets(10));
        rootPane.setSpacing(10);

        Label titleLabel = new Label("Manage Venues");

        TextField venueField = new TextField();
        venueField.setPromptText("Enter new venue name");

        Button addVenueButton = new Button("Add Venue");

        // Add new venue and reload the page
        addVenueButton.setOnAction(e -> {
            addNewVenue(venueField.getText());
            app.showManageVenuesPage(admin);
        });

        VBox venueList = new VBox();
        venueList.setSpacing(10);
        ArrayList<Venue> venues = SystemManager.getAllVenues();
        
        for (Venue venue : venues) {
            HBox venueItem = new HBox();
            venueItem.setSpacing(10);

            Label venueLabel = new Label(venue.getName());
            Button deleteButton = new Button("Delete");

            HBox.setHgrow(venueLabel, Priority.ALWAYS);
            venueLabel.setMaxWidth(Double.MAX_VALUE);

            // Delete button action
            deleteButton.setOnAction(e -> {
                deleteVenue(venue);
                app.showManageVenuesPage(admin); 
            });

            venueItem.getChildren().addAll(venueLabel, deleteButton);
            venueList.getChildren().add(venueItem);
        }

        ScrollPane scrollPane = new ScrollPane(venueList);
        scrollPane.setFitToWidth(true);

        Button goBack = new Button("Go Back");
        goBack.setOnAction(e -> app.showAdminPage(admin));

        rootPane.getChildren().addAll(titleLabel, venueField, addVenueButton, scrollPane, goBack);
    }

    private void addNewVenue(String venueName) {
        SystemManager.registerVenue(venueName);
    }

    private void deleteVenue(Venue venue) {
        SystemManager.deleteEvent(venue.getVenueID());
    }

    public VBox getRootPane() {
        return rootPane;
    }
}
