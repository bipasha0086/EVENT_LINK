package model;

public class Seat {
    private int seatId;
    private int eventId;
    private String seatNumber;
    private boolean isBooked;

    public Seat() {}

    public Seat(int seatId, int eventId, String seatNumber, boolean isBooked) {
        this.seatId = seatId;
        this.eventId = eventId;
        this.seatNumber = seatNumber;
        this.isBooked = isBooked;
    }

    public int getSeatId() { return seatId; }
    public void setSeatId(int seatId) { this.seatId = seatId; }
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public boolean isBooked() { return isBooked; }
    public void setBooked(boolean booked) { isBooked = booked; }
}
