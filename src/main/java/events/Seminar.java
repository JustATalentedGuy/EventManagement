package events;

import system.SystemManager;
import users.Organizer;
import venue.Department;
import venue.Venue;

public class Seminar extends Event {
    private String speaker;
    private String topic;

    public Seminar(int eventID, String name, String description, Venue venue, Department department, Organizer organizer, String startTime, String endTime, int maxParticipants, boolean online, boolean finished, boolean approved, String speaker, String topic) {
        super(eventID, name, description, venue, department, organizer, startTime, endTime, maxParticipants, online, finished, approved);
        this.speaker = speaker;
        this.topic = topic;
    }

    public boolean setSpeaker(String speaker) {
        if (SystemManager.updateEvent(this.eventID, "field1", speaker)) {
            this.speaker = speaker;
            return true;
        }
        return false;
    }

    public String getSpeaker() {
        return speaker;
    }

    public boolean setTopic(String topic) {
        if (SystemManager.updateEvent(this.eventID, "field2", topic)) {
            this.topic = topic;
            return true;
        }
        return false;
    }

    public String getTopic() {
        return topic;
    }
}