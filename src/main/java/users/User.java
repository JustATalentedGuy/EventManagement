package users;

import java.util.List;

import events.Event;
import system.SystemManager;

public abstract class User {
    protected int userID;
    protected String username;
    protected String password;
    protected String email;

    public User(int userID, String username, String password, String email) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public int getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public boolean setUserName(String newName) {
        return SystemManager.updateUser(userID, "name", newName);
    }

    public boolean setPassword(String newPassword) {
        return SystemManager.updateUser(userID, "password", newPassword);
    }

    public String getEmail() {
        return email;
    }

    public boolean setEmail(String newEmail) {
        return SystemManager.updateUser(userID, "email", newEmail);
    }

    public List<Event> viewAllEvents(SystemManager systemManager) {
        return SystemManager.getAllEvents();
    }
}