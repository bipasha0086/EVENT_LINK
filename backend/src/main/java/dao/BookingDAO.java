package dao;

import model.Booking;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    public Booking createBooking(int userId, int eventId, int seatId) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement activeStmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM bookings WHERE user_id = ? AND status IN ('PENDING_PAYMENT','PAID')"
            )) {
                activeStmt.setInt(1, userId);
                ResultSet activeRs = activeStmt.executeQuery();
                activeRs.next();
                if (activeRs.getInt(1) > 0) {
                    throw new SQLException("User already has an active seat booking.");
                }
            }

            int seatEventId;
            boolean isBooked;
            try (PreparedStatement seatStmt = conn.prepareStatement(
                "SELECT event_id, is_booked FROM seats WHERE seat_id = ? FOR UPDATE"
            )) {
                seatStmt.setInt(1, seatId);
                ResultSet seatRs = seatStmt.executeQuery();
                if (!seatRs.next()) {
                    throw new SQLException("Seat not found.");
                }
                seatEventId = seatRs.getInt("event_id");
                isBooked = seatRs.getBoolean("is_booked");
            }

            if (seatEventId != eventId) {
                throw new SQLException("Seat does not belong to selected event.");
            }

            if (isBooked) {
                throw new SQLException("Seat is already booked.");
            }

            Timestamp now = new Timestamp(System.currentTimeMillis());
            Timestamp deadline = new Timestamp(System.currentTimeMillis() + 24L * 60 * 60 * 1000);
            int bookingId;

            try (PreparedStatement bookingStmt = conn.prepareStatement(
                "INSERT INTO bookings (user_id, event_id, seat_id, booking_time, payment_deadline, status) VALUES (?, ?, ?, ?, ?, 'PENDING_PAYMENT')",
                Statement.RETURN_GENERATED_KEYS
            )) {
                bookingStmt.setInt(1, userId);
                bookingStmt.setInt(2, eventId);
                bookingStmt.setInt(3, seatId);
                bookingStmt.setTimestamp(4, now);
                bookingStmt.setTimestamp(5, deadline);
                bookingStmt.executeUpdate();

                ResultSet keys = bookingStmt.getGeneratedKeys();
                if (!keys.next()) {
                    throw new SQLException("Failed to create booking.");
                }
                bookingId = keys.getInt(1);
            }

            try (PreparedStatement seatUpdateStmt = conn.prepareStatement(
                "UPDATE seats SET is_booked = TRUE WHERE seat_id = ?"
            )) {
                seatUpdateStmt.setInt(1, seatId);
                seatUpdateStmt.executeUpdate();
            }

            conn.commit();
            return getBookingById(bookingId);
        } catch (SQLException ex) {
            if (conn != null) {
                conn.rollback();
            }
            throw ex;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public Booking getBookingById(int bookingId) throws SQLException {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapBooking(rs);
            }
        }
        return null;
    }

    public List<Booking> getBookingsByUserId(int userId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(mapBooking(rs));
            }
        }
        return bookings;
    }

    public List<Booking> getBookingsByTheatreId(int theatreId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.* FROM bookings b JOIN events e ON e.event_id = b.event_id WHERE e.theatre_id = ? ORDER BY b.booking_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, theatreId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookings.add(mapBooking(rs));
            }
        }
        return bookings;
    }

    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY booking_time DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                bookings.add(mapBooking(rs));
            }
        }
        return bookings;
    }

    public boolean markBookingPaid(int bookingId, int userId) throws SQLException {
        String sql = "UPDATE bookings SET status = 'PAID', paid_at = CURRENT_TIMESTAMP WHERE booking_id = ? AND user_id = ? AND status = 'PENDING_PAYMENT'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean allocateFriend(int bookingId, String friendName) throws SQLException {
        String sql = "UPDATE bookings SET allocated_friend_name = ?, allocated_at = CURRENT_TIMESTAMP WHERE booking_id = ? AND status = 'PENDING_PAYMENT'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, friendName);
            stmt.setInt(2, bookingId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deallocateByAdmin(int bookingId) throws SQLException {
        return transitionToDeallocated(bookingId, "DEALLOCATED");
    }

    public int expirePendingBookings() throws SQLException {
        List<Integer> toExpire = new ArrayList<>();
        String findSql = "SELECT booking_id FROM bookings WHERE status = 'PENDING_PAYMENT' AND payment_deadline <= CURRENT_TIMESTAMP";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(findSql)) {
            while (rs.next()) {
                toExpire.add(rs.getInt("booking_id"));
            }
        }

        int expiredCount = 0;
        for (Integer bookingId : toExpire) {
            if (transitionToDeallocated(bookingId, "EXPIRED")) {
                expiredCount++;
            }
        }
        return expiredCount;
    }

    public void deleteBooking(int bookingId) throws SQLException {
        transitionToDeallocated(bookingId, "DEALLOCATED");
    }

    private boolean transitionToDeallocated(int bookingId, String finalStatus) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int seatId;
            String currentStatus;
            try (PreparedStatement bookingStmt = conn.prepareStatement(
                "SELECT seat_id, status FROM bookings WHERE booking_id = ? FOR UPDATE"
            )) {
                bookingStmt.setInt(1, bookingId);
                ResultSet rs = bookingStmt.executeQuery();
                if (!rs.next()) {
                    conn.rollback();
                    return false;
                }
                seatId = rs.getInt("seat_id");
                currentStatus = rs.getString("status");
            }

            if ("DEALLOCATED".equals(currentStatus) || "EXPIRED".equals(currentStatus)) {
                conn.rollback();
                return false;
            }

            try (PreparedStatement updateBookingStmt = conn.prepareStatement(
                "UPDATE bookings SET status = ?, deallocated_at = CURRENT_TIMESTAMP WHERE booking_id = ?"
            )) {
                updateBookingStmt.setString(1, finalStatus);
                updateBookingStmt.setInt(2, bookingId);
                updateBookingStmt.executeUpdate();
            }

            try (PreparedStatement seatStmt = conn.prepareStatement(
                "UPDATE seats SET is_booked = FALSE WHERE seat_id = ?"
            )) {
                seatStmt.setInt(1, seatId);
                seatStmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException ex) {
            if (conn != null) {
                conn.rollback();
            }
            throw ex;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    private Booking mapBooking(ResultSet rs) throws SQLException {
        return new Booking(
            rs.getInt("booking_id"),
            rs.getInt("user_id"),
            rs.getInt("event_id"),
            rs.getInt("seat_id"),
            rs.getTimestamp("booking_time"),
            rs.getTimestamp("payment_deadline"),
            rs.getString("status"),
            rs.getString("allocated_friend_name"),
            rs.getTimestamp("allocated_at"),
            rs.getTimestamp("paid_at"),
            rs.getTimestamp("deallocated_at")
        );
    }
}
