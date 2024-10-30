package events;

import system.SystemManager;
import users.Organizer;
import venue.Department;
import venue.Venue;

public class Workshop extends Event {
    private String trainerName;
    private String materialsProvided;

    public Workshop(int eventID, String name, String description, Venue venue, Department department, Organizer organizer, String startTime, String endTime, int maxParticipants, boolean online, boolean finished, boolean approved, boolean rejected, String eventType, String trainerName, String materialsProvided) {
        super(eventID, name, description, venue, department, organizer, startTime, endTime, maxParticipants, online, finished, approved, rejected, eventType);
        this.trainerName = trainerName;
        this.materialsProvided = materialsProvided;
    }

    public boolean setTrainerName(String trainerName) {
        if (SystemManager.updateEvent(this.eventID, "field1", trainerName)) {
            this.trainerName = trainerName;
            return true;
        }
        return false;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public boolean setMaterialsProvided(String materialsProvided) {
        if (SystemManager.updateEvent(this.eventID, "field1", materialsProvided)) {
            this.materialsProvided = materialsProvided;
            return true;
        }
        return false;
    }

    public String getMaterialsProvided() {
        return materialsProvided;
    }

    public Seminar toSeminar() {
        Seminar seminar = new Seminar(this.eventID, this.name, this.description, this.venue, this.department, this.organizer, this.startTime, this.endTime, this.maxParticipants, this.online, this.finished, this.approved, this.rejected, this.eventType, "", "");
        return seminar;
    }

    public Competition toCompetition() {
        Competition competition = new Competition(this.eventID, this.name, this.description, this.venue, this.department, this.organizer, this.startTime, this.endTime, this.maxParticipants, this.online, this.finished, this.approved, this.rejected, this.eventType, 0, "");
        return competition;
    }

    public Workshop toWorkshop() {
        return this;
    }
}