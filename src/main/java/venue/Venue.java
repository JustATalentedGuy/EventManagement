package venue;

import java.util.ArrayList;

import events.*;
import system.SystemManager;

public class Venue {
    private int venueID;
    private String name;

    public Venue(int venueID, String name) {
        this.venueID = venueID;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isAvailable(String startTime, String endTime) {
        ArrayList<Event> events = SystemManager.getEventsFromVenue(venueID);
        for (Event event : events) {
            if (event.isCollision(startTime, endTime) && !event.isOnline()) {
                return false;
            }
        }
        return true;
    }

    public void setVenueID(int venueID) {
        this.venueID = venueID;
    }

    public int getVenueID() {
        return venueID;
    }

    public void setName(String name) {
        SystemManager.updateVenue(venueID, name);
    }
}