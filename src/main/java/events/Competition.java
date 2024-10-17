package events;

import system.SystemManager;
import users.Organizer;
import venue.Department;
import venue.Venue;

public class Competition extends Event {
    private int prizeAmount;
    private String competitionType;

    public Competition(int eventID, String name, String description, Venue venue, Department department, Organizer organizer, String startTime, String endTime, int maxParticipants, boolean online, boolean finished, boolean approved, int prizeAmount, String competitionType) {
        super(eventID, name, description, venue, department, organizer, startTime, endTime, maxParticipants, online, finished, approved);
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
}
