package model;

import java.util.Date;

public class Event {
    private int eventId;
    private int theatreId;
    private String name;
    private Date eventDate;
    private String eventTime;
    private String location;
    private String description;

    public Event() {}

    public Event(int eventId, int theatreId, String name, Date eventDate, String eventTime, String location, String description) {
        this.eventId = eventId;
        this.theatreId = theatreId;
        this.name = name;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.location = location;
        this.description = description;
    }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public int getTheatreId() { return theatreId; }
    public void setTheatreId(int theatreId) { this.theatreId = theatreId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }
    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
