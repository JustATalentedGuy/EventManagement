package eventmanagement;

import users.*;

import UI.*;
import events.Event;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoginPage();
    }

    public void showLoginPage() {
        LoginPage loginPage = new LoginPage(this);
        Scene scene = new Scene(loginPage.getRootPane(), 1290, 650);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login Page");
        primaryStage.show();
    }

    public void showRegisterPage() {
        RegisterPage registerPage = new RegisterPage(this);
        Scene scene = new Scene(registerPage.getRootPane(), 1290, 650);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Register Page");
        primaryStage.show();
    }

    public void showAdminPage(Admin admin) {
        AdminPage adminPage = new AdminPage(this, admin);
        Scene scene = new Scene(adminPage.getRootPane(), 1290, 650);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Dashboard");
        primaryStage.show();
    }

    public void showViewerPage(Viewer viewer) {
        ViewerPage viewerPage = new ViewerPage(this, viewer);
        Scene scene = new Scene(viewerPage.getRootPane(), 1290, 650);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Viewer - Event List");
        primaryStage.show();
    }

    public void showOrganizerPage(Organizer organizer) {
        OrganizerPage organizerPage = new OrganizerPage(this, organizer);
        Scene scene = new Scene(organizerPage.getRootPane(), 1290, 650);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Organizer - Event List");
        primaryStage.show();
    }

    public void showEditEventPage(Organizer organizer, Event event) {
        EditEventPage editEventPage = new EditEventPage(this, organizer, event);
        Scene scene = new Scene(editEventPage.getRootPane(), 1290, 650);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Edit Event");
        primaryStage.show();
    }

    // public void showAdminPage(Admin admin) {
    //     AdminPage adminPage = new AdminPage(this, admin);
    //     Scene scene = new Scene(adminPage.getRootPane(), 600, 400);
    //     primaryStage.setScene(scene);
    //     primaryStage.setTitle("Admin Dashboard");
    //     primaryStage.show();
    // }

    public void showManageDepartmentsPage(Admin admin) {
        AdminDepartmentsPage adminDepartmentsPage = new AdminDepartmentsPage(this, admin);
        Scene scene = new Scene(adminDepartmentsPage.getRootPane(), 1290, 650);
        primaryStage.setScene(scene);
    }

    public void showManageVenuesPage(Admin admin) {
        AdminVenuesPage adminVenuesPage = new AdminVenuesPage(this, admin);
        Scene scene = new Scene(adminVenuesPage.getRootPane(), 1290, 650);
        primaryStage.setScene(scene);
    }

    public void showNewEventRequestPage(Organizer organizer) {
        NewEventRequestPage newEventRequestPage = new NewEventRequestPage(this, organizer);
        Scene scene = new Scene(newEventRequestPage.getRootPane(), 1290, 650);
        primaryStage.setScene(scene);
    }

    
    public static void main(String[] args) {
        
        launch(args);
    }
}
