package users;

import java.util.ArrayList;

import events.Event;
import system.SystemManager;

public class Organizer extends User {

    public Organizer(int userID, String username, String password, String email) {
        super(userID, username, password, email);
    }

    public ArrayList<Event> viewOrganizedEvents() {
        return SystemManager.getListOfOrganizedEventsForOrganizer(userID);
    }

    public void sendOrganizingRequest(String name, String description, int venue, int department, String startTime, String endTime, int maxParticipants, boolean online, String eventType, String field1, String field2) {
        SystemManager.createEvent(name, description, venue, department, userID, startTime, endTime, maxParticipants, online, false, false, eventType, field1, field2);
    }

    public void deleteEvent(int eventID) {
        SystemManager.deleteEvent(eventID);
    }

    public void viewPendingRequests() {
        ArrayList<Event> pendingEvents = SystemManager.getPendingEventRequests(this.userID);
        for (Event event : pendingEvents) {
            System.out.println(event.getEventID() + " " + event.getName());
        }
    }
}