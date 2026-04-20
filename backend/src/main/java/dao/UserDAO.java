package dao;

import model.User;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, email, role, threat_area, theatre_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getThreatArea());
            if (user.getTheatreId() == null) {
                stmt.setNull(6, Types.INTEGER);
            } else {
                stmt.setInt(6, user.getTheatreId());
            }
            stmt.executeUpdate();
        }
    }

    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        }
        return null;
    }

    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
        }
        return null;
    }

    public List<User> getUsersByTheatreId(int theatreId) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT DISTINCT u.* FROM users u JOIN bookings b ON b.user_id = u.user_id JOIN events e ON e.event_id = b.event_id " +
            "WHERE e.theatre_id = ? AND b.status IN ('PENDING_PAYMENT','PAID')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, theatreId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }
        return users;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }
        return users;
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username=?, password=?, email=?, role=?, threat_area=?, theatre_id=? WHERE user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getThreatArea());
            if (user.getTheatreId() == null) {
                stmt.setNull(6, Types.INTEGER);
            } else {
                stmt.setInt(6, user.getTheatreId());
            }
            stmt.setInt(7, user.getUserId());
            stmt.executeUpdate();
        }
    }

    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        int theatreIdValue = rs.getInt("theatre_id");
        Integer theatreId = rs.wasNull() ? null : theatreIdValue;

        return new User(
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("email"),
            rs.getString("role"),
            rs.getString("threat_area"),
            theatreId
        );
    }
}
