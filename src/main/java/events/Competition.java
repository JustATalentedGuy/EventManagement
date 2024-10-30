package events;

import system.SystemManager;
import users.Organizer;
import venue.Department;
import venue.Venue;

public class Competition extends Event {
    private int prizeAmount;
    private String competitionType;

    public Competition(int eventID, String name, String description, Venue venue, Department department, Organizer organizer, String startTime, String endTime, int maxParticipants, boolean online, boolean finished, boolean approved, boolean rejected, String eventType, int prizeAmount, String competitionType) {
        super(eventID, name, description, venue, department, organizer, startTime, endTime, maxParticipants, online, finished, approved, rejected, eventType);
        this.prizeAmount = prizeAmount;
        this.competitionType = competitionType;
    }

    public int getPrizeAmount() {
        return prizeAmount;
    }

    public boolean setPrizeAmount(int prizeAmount) {
        if (SystemManager.updateEvent(this.eventID, "field1", prizeAmount)) {
            this.prizeAmount = prizeAmount;
            return true;
        }
        return false;
    }

    public boolean setCompetitionType(String competitionType) {
        if (SystemManager.updateEvent(this.eventID, "field2", competitionType)) {
            this.competitionType = competitionType;
            return true;
        }
        return false;
    }

    public String getCompetitionType() {
        return competitionType;
    }

    public Seminar toSeminar() {
        Seminar seminar = new Seminar(this.eventID, this.name, this.description, this.venue, this.department, this.organizer, this.startTime, this.endTime, this.maxParticipants, this.online, this.finished, this.approved, this.rejected, this.eventType, "", "");
        return seminar;
    }

    public Competition toCompetition() {
        return this;
    }

    public Workshop toWorkshop() {
        Workshop workshop = new Workshop(this.eventID, this.name, this.description, this.venue, this.department, this.organizer, this.startTime, this.endTime, this.maxParticipants, this.online, this.finished, this.approved, this.rejected, this.eventType, "", "");
        return workshop;
    }
}
