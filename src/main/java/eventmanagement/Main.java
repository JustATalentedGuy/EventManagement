package eventmanagement;

import users.*;

import UI.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

// public class Main {
//     public static void main(String[] args) {
//         // Create a new database connection
//         DatabaseController db = new DatabaseController();

//         // Create a new department
//         int departmentID = db.createDepartment("Computer Science");
//         System.out.println("Department ID: " + departmentID);

//         // Create a new venue
//         int venueID = db.createVenue("Main Auditorium");
//         System.out.println("Venue ID: " + venueID);

//         // Create a new event
//         int eventID = db.createEvent(
//                 "Test Event",
//                 "This is a test event",
//                 venueID,
//                 departmentID,
//                 1,
//                 "2024-01-01 10:00:00",
//                 "2024-01-01 12:00:00",
//                 100,
//                 false,
//                 false,
//                 true,
//                 "Competition",
//                 "1000",
//                 "Individual"
//         );
//         System.out.println("Event ID: " + eventID);

//         // Get the event by ID
//         Event event = db.getEvent(eventID);
//         System.out.println("Event Name: " + event.getName());
//         System.out.println("Event Description: " + event.getDescription());
//         System.out.println("Event Venue: " + event.getVenue().getName());
//         System.out.println("Event Department: " + event.getDepartment().getName());
//         System.out.println("Event Organizer: " + event.getOrganizer().getUsername());
//         System.out.println("Event Start Time: " + event.getStartTime());
//         System.out.println("Event End Time: " + event.getEndTime());
//         System.out.println("Event Max Participants: " + event.getMaxParticipants());
//         System.out.println("Event Online: " + event.isOnline());
//         System.out.println("Event Finished: " + event.isFinished());
//         System.out.println("Event Approved: " + event.isApproved());

//         // Get all events
//         ArrayList<Event> events = db.getAllEvents();
//         for (Event e : events) {
//             System.out.println("Event Name: " + e.getName());
//             System.out.println("Event Description: " + e.getDescription());
//             System.out.println("Event Venue: " + e.getVenue().getName());
//             System.out.println("Event Department: " + e.getDepartment().getName());
//             System.out.println("Event Organizer: " + e.getOrganizer().getUsername());
//             System.out.println("Event Start Time: " + e.getStartTime());
//             System.out.println("Event End Time: " + e.getEndTime());
//             System.out.println("Event Max Participants: " + e.getMaxParticipants());
//             System.out.println("Event Online: " + e.isOnline());
//             System.out.println("Event Finished: " + e.isFinished());
//             System.out.println("Event Approved: " + e.isApproved());
//         }

//         // Delete the event
//         boolean deleted = db.deleteEvent(eventID);
//         System.out.println("Event Deleted: " + deleted);
//     }
// }

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showLoginPage();  // Start with login page
    }

    public void showLoginPage() {
        LoginPage loginPage = new LoginPage(this);
        Scene scene = new Scene(loginPage.getRootPane(), 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login Page");
        primaryStage.show();
    }

    public void showViewerPage(Viewer viewer) {
        ViewerPage viewerPage = new ViewerPage(this, viewer);
        Scene scene = new Scene(viewerPage.getRootPane(), 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Viewer - Event List");
        primaryStage.show();
    }

    public void showOrganizerPage(Organizer organizer) {
        OrganizerPage organizerPage = new OrganizerPage(this, organizer);
        Scene scene = new Scene(organizerPage.getRootPane(), 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Organizer - Event List");
        primaryStage.show();
    }

    public void showAdminPage(Admin admin) {
        AdminPage adminPage = new AdminPage(this, admin);
        Scene scene = new Scene(adminPage.getRootPane(), 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Dashboard");
        primaryStage.show();
    }

    public void showManageDepartmentsPage(Admin admin) {
        AdminDepartmentsPage adminDepartmentsPage = new AdminDepartmentsPage(this, admin);
        Scene scene = new Scene(adminDepartmentsPage.getRootPane(), 600, 400);
        primaryStage.setScene(scene);
    }

    public void showManageVenuesPage(Admin admin) {
        AdminVenuesPage adminVenuesPage = new AdminVenuesPage(this, admin);
        Scene scene = new Scene(adminVenuesPage.getRootPane(), 600, 400);
        primaryStage.setScene(scene);
    }

    public void showNewEventRequestPage(Organizer organizer) {
        NewEventRequestPage newEventRequestPage = new NewEventRequestPage(this, organizer);
        Scene scene = new Scene(newEventRequestPage.getRootPane(), 600, 400);
        primaryStage.setScene(scene);
    }

    
    public static void main(String[] args) {
        
        launch(args);
    }
}
