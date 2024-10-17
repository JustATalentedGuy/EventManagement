package events;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import system.SystemManager;
import users.Organizer;
import users.User;
import venue.Department;
import venue.Venue;

public abstract class Event {
    protected int eventID;
    protected String name;
    protected String description;
    protected Venue venue;
    protected Department department;
    protected Organizer organizer;
    protected String startTime;
    protected String endTime;
    protected int maxParticipants;
    protected boolean online;
    protected boolean finished;
    protected boolean approved;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Event(int eventID, String name, String description, Venue venue, Department department, Organizer organizer, String startTime, String endTime, int maxParticipants, boolean online, boolean finished, boolean approved) {
        this.eventID = eventID;
        this.name = name;
        this.description = description;
        this.venue = venue;
        this.department = department;
        this.organizer = organizer;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxParticipants = maxParticipants;
        this.online = online;
        this.finished = finished;
        this.approved = approved;
    }

    public boolean registerUser(User user) {
        return SystemManager.registerUserToEvent(user.getUserID(), this.eventID);
    }

    public boolean unregisterUser(User user) {
        return SystemManager.removeUserRegistrationFromEvent(user.getUserID(), this.eventID);
    }

    public boolean setName(String newName) {
        if (SystemManager.updateEvent(eventID, "name", newName)) {
            this.name = newName;
            return true;
        } else {
            return false;
        }
    }

    public boolean setDescription(String newDescription) {
        if (SystemManager.updateEvent(eventID, "description", newDescription)) {
            this.description = newDescription;
            return true;
        } else {
            return false;
        }
    }

    public boolean setStartTime(String newStartTime) {
        if (SystemManager.updateEvent(eventID, "starttime", newStartTime)) {
            this.startTime = newStartTime;
            return true;
        } else {
            return false;
        }
    }

    public boolean setEndTime(String newEndTime) {
        if (SystemManager.updateEvent(eventID, "endtime", newEndTime)) {
            this.endTime = newEndTime;
            return true;
        } else {
            return false;
        }
    }

    public boolean setMaxParticipants(int newMaxParticipants) {
        if (SystemManager.updateEvent(eventID, "maxparticipants", Integer.toString(newMaxParticipants))) {
            this.maxParticipants = newMaxParticipants;
            return true;
        } else {
            return false;
        }
    }

    public boolean setOnline(boolean newOnline) {
        if (SystemManager.updateEvent(eventID, "online", Boolean.toString(newOnline))) {
            this.online = newOnline;
            return true;
        } else {
            return false;
        }
    }

    public boolean setFinished(boolean newFinished) {
        if (SystemManager.updateEvent(eventID, "finished", Boolean.toString(newFinished))) {
            this.finished = newFinished;
            return true;
        } else {
            return false;
        }
    }

    public boolean setApproved(boolean newApproved) {
        if (SystemManager.updateEvent(eventID, "approved", Boolean.toString(newApproved))) {
            this.approved = newApproved;
            return true;
        } else {
            return false;
        }
    }

    public boolean setVenue(Venue newVenue) {
        if (SystemManager.updateEvent(eventID, "venue", newVenue.getVenueID())) {
            this.venue = newVenue;
            return true;
        } else {
            return false;
        }
    }

    public boolean setDepartment(Department newDepartment) {
        if (SystemManager.updateEvent(eventID, "department", newDepartment.getDepartmentID())) {
            this.department = newDepartment;
            return true;
        } else {
            return false;
        }
    }

    public boolean setOrganizer(Organizer newOrganizer) {
        if (SystemManager.updateEvent(eventID, "organizer", newOrganizer.getUserID())) {
            this.organizer = newOrganizer;
            return true;
        } else {
            return false;
        }
    }

    public LocalDateTime getStartTimeDate() {
        return LocalDateTime.parse(startTime, formatter);
    }

    public LocalDateTime getEndTimeDate() {
        return LocalDateTime.parse(endTime, formatter);
    }

    public boolean isCollision(String startTime, String endTime) {
        LocalDateTime startDate = LocalDateTime.parse(startTime, formatter);
        LocalDateTime endDate = LocalDateTime.parse(endTime, formatter);

        return (
            startDate.isAfter(this.getStartTimeDate()) && startDate.isBefore(this.getEndTimeDate()) ||
            endDate.isAfter(this.getStartTimeDate()) && endDate.isBefore(this.getEndTimeDate())
        );
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Venue getVenue() {
        return venue;
    }

    public Department getDepartment() {
        return department;
    }

    public Organizer getOrganizer() {
        return organizer;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public boolean isOnline() {
        return online;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isApproved() {
        return approved;
    }
}