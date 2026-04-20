package dao;

import model.Seat;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {
    public void addSeat(Seat seat) throws SQLException {
        String sql = "INSERT INTO seats (event_id, seat_number, is_booked) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, seat.getEventId());
            stmt.setString(2, seat.getSeatNumber());
            stmt.setBoolean(3, seat.isBooked());
            stmt.executeUpdate();
        }
    }

    public Seat getSeatById(int seatId) throws SQLException {
        String sql = "SELECT * FROM seats WHERE seat_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, seatId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Seat(
                    rs.getInt("seat_id"),
                    rs.getInt("event_id"),
                    rs.getString("seat_number"),
                    rs.getBoolean("is_booked")
                );
            }
        }
        return null;
    }

    public List<Seat> getSeatsByEventId(int eventId) throws SQLException {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT * FROM seats WHERE event_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                seats.add(new Seat(
                    rs.getInt("seat_id"),
                    rs.getInt("event_id"),
                    rs.getString("seat_number"),
                    rs.getBoolean("is_booked")
                ));
            }
        }
        return seats;
    }

    public void updateSeat(Seat seat) throws SQLException {
        String sql = "UPDATE seats SET event_id=?, seat_number=?, is_booked=? WHERE seat_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, seat.getEventId());
            stmt.setString(2, seat.getSeatNumber());
            stmt.setBoolean(3, seat.isBooked());
            stmt.setInt(4, seat.getSeatId());
            stmt.executeUpdate();
        }
    }

    public void deleteSeat(int seatId) throws SQLException {
        String sql = "DELETE FROM seats WHERE seat_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, seatId);
            stmt.executeUpdate();
        }
    }

    public void setSeatBooked(int seatId, boolean booked) throws SQLException {
        String sql = "UPDATE seats SET is_booked = ? WHERE seat_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, booked);
            stmt.setInt(2, seatId);
            stmt.executeUpdate();
        }
    }
}
