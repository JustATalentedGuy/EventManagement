package users;

import java.util.ArrayList;

import events.Event;
import system.SystemManager;

public class Viewer extends User {

    public Viewer(int userID, String username, String password, String email) {
        super(userID, username, password, email);
    }

    public ArrayList<Event> getListOfRegisteredEvents() {
        return SystemManager.getListOfRegisteredEvents(userID);
    }

    public ArrayList<Event> getListOfFavouriteEvents() {
        return SystemManager.getListOfFavouriteEvents(userID);
    }
}