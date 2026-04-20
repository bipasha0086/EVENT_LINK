package dao;

import model.Notification;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    public int sendTheatreAlert(int theatreId, String subject, String message) throws SQLException {
        List<Integer> users = new ArrayList<>();
        String userSql = "SELECT DISTINCT b.user_id FROM bookings b JOIN events e ON e.event_id = b.event_id " +
            "WHERE e.theatre_id = ? AND b.status IN ('PENDING_PAYMENT', 'PAID')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(userSql)) {
            stmt.setInt(1, theatreId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(rs.getInt("user_id"));
            }
        }

        if (users.isEmpty()) {
            return 0;
        }

        String insertSql = "INSERT INTO notifications (theatre_id, user_id, subject, message, status) VALUES (?, ?, ?, ?, 'SENT')";
        int count = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            for (Integer userId : users) {
                stmt.setInt(1, theatreId);
                stmt.setInt(2, userId);
                stmt.setString(3, subject);
                stmt.setString(4, message);
                count += stmt.executeUpdate();
            }
        }
        return count;
    }

    public List<Notification> getNotificationsByUserId(int userId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY sent_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notifications.add(new Notification(
                    rs.getInt("notification_id"),
                    rs.getInt("theatre_id"),
                    rs.getInt("user_id"),
                    rs.getString("subject"),
                    rs.getString("message"),
                    rs.getTimestamp("sent_at"),
                    rs.getString("status")
                ));
            }
        }

        return notifications;
    }
}
