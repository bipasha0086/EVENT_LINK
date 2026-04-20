package dao;

import model.Event;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    public void addEvent(Event event) throws SQLException {
        String sql = "INSERT INTO events (theatre_id, name, event_date, event_time, location, description) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, event.getTheatreId());
            stmt.setString(2, event.getName());
            stmt.setDate(3, new java.sql.Date(event.getEventDate().getTime()));
            stmt.setTime(4, Time.valueOf(normalizeTime(event.getEventTime())));
            stmt.setString(5, event.getLocation());
            stmt.setString(6, event.getDescription());
            stmt.executeUpdate();
        }
    }

    public Event getEventById(int eventId) throws SQLException {
        String sql = "SELECT * FROM events WHERE event_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapEvent(rs);
            }
        }
        return null;
    }

    public List<Event> getEventsByTheatreId(int theatreId) throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events WHERE theatre_id = ? ORDER BY event_date, event_time";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, theatreId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                events.add(mapEvent(rs));
            }
        }
        return events;
    }

    public List<Event> getAllEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY event_date, event_time";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                events.add(mapEvent(rs));
            }
        }
        return events;
    }

    public void updateEvent(Event event) throws SQLException {
        String sql = "UPDATE events SET theatre_id=?, name=?, event_date=?, event_time=?, location=?, description=? WHERE event_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, event.getTheatreId());
            stmt.setString(2, event.getName());
            stmt.setDate(3, new java.sql.Date(event.getEventDate().getTime()));
            stmt.setTime(4, Time.valueOf(normalizeTime(event.getEventTime())));
            stmt.setString(5, event.getLocation());
            stmt.setString(6, event.getDescription());
            stmt.setInt(7, event.getEventId());
            stmt.executeUpdate();
        }
    }

    public void deleteEvent(int eventId) throws SQLException {
        String sql = "DELETE FROM events WHERE event_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.executeUpdate();
        }
    }

    private Event mapEvent(ResultSet rs) throws SQLException {
        Time time = rs.getTime("event_time");
        return new Event(
            rs.getInt("event_id"),
            rs.getInt("theatre_id"),
            rs.getString("name"),
            rs.getDate("event_date"),
            time == null ? "00:00:00" : time.toString(),
            rs.getString("location"),
            rs.getString("description")
        );
    }

    private String normalizeTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "00:00:00";
        }
        String trimmed = value.trim();
        if (trimmed.length() == 5) {
            return trimmed + ":00";
        }
        return trimmed;
    }
}
