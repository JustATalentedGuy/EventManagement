package users;

import java.util.ArrayList;

import events.Competition;
import events.Event;
import events.Seminar;
import events.Workshop;
import system.SystemManager;
import venue.Department;
import venue.Venue;

public class Admin extends User {

    public Admin(int userID, String username, String password) {
        super(userID, username, password, null);
    }

    public boolean createEvent(String name, String description, Venue venue, Department department, Organizer organizer, String startTime, String endTime, int maxParticipants, boolean online, boolean finished, boolean approved, String eventType, String field1, String field2) {
        int id = SystemManager.createEvent(
            name, 
            description, 
            venue.getVenueID(), 
            department.getDepartmentID(), 
            organizer.getUserID(), 
            startTime, 
            endTime, 
            maxParticipants, 
            online, 
            finished, 
            approved, 
            eventType, 
            field1, 
            field2
        );
        return id != -1;
    }

    public boolean updateEventName(Event event, String newName) {
        return event.setName(newName);
    }

    public boolean updateEventDescription(Event event, String newDescription) {
        return event.setDescription(newDescription);
    }

    public boolean updateEventVenue(Event event, Venue newVenue) {
        return event.setVenue(newVenue);
    }

    public boolean updateEventDepartment(Event event, Department newDepartment) {
        return event.setDepartment(newDepartment);
    }

    public boolean updateEventOrganizer(Event event, Organizer newOrganizer) {
        return event.setOrganizer(newOrganizer);
    }

    public boolean updateEventStartTime(Event event, String newStartTime) {
        return event.setStartTime(newStartTime);
    }

    public boolean updateEventEndTime(Event event, String newEndTime) {
        return event.setEndTime(newEndTime);
    }

    public boolean updateEventMaxParticipants(Event event, int newMaxParticipants) {
        return event.setMaxParticipants(newMaxParticipants);
    }

    public boolean updateEventOnline(Event event, boolean newOnline) {
        return event.setOnline(newOnline);
    }

    public boolean updateEventFinished(Event event, boolean newFinished) {
        return event.setFinished(newFinished);
    }

    public boolean updateEventApproved(Event event, boolean newApproved) {
        return event.setApproved(newApproved);
    }

    public boolean updatePrizeAmount(Competition competition, int prizeAmount) {
        return competition.setPrizeAmount(prizeAmount);
    }

    public boolean updateCompetitionType(Competition competition, String competitionType) {
        return competition.setCompetitionType(competitionType);
    }

    public boolean updateTrainerName(Workshop workshop, String newTrainerName) {
        return workshop.setTrainerName(newTrainerName);
    }

    public boolean updateMaterialsProvided(Workshop workshop, String newMaterialsProvided) {
        return workshop.setMaterialsProvided(newMaterialsProvided);
    }

    public boolean updateSpeaker(Seminar seminar, String newSpeaker) {
        return seminar.setSpeaker(newSpeaker);
    }

    public boolean updateTopic(Seminar seminar, String topic) {
        return seminar.setTopic(topic);
    }    

    public boolean createVenue(String venueName) {
        int id = SystemManager.registerVenue(venueName);
        return id != -1;
    }

    public boolean createDepartment(String departmentName) {
        int id = SystemManager.registerDepartment(departmentName);
        return id != -1;
    }

    public boolean acceptOrganizingRequest(Event event) {
        return SystemManager.updateEvent(event.getEventID(), "approved", true);
    }

    public boolean declineOrganizingRequest(Event event) {
        return SystemManager.rejectEvent(event.getEventID());
    }

    public ArrayList<Event> viewOrganizingRequests() {
        return SystemManager.getListOfOrganizingRequests();
    }
}
