package service;

import dao.UserDAO;
import model.User;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    private UserDAO userDAO = new UserDAO();

    private String normalizeRole(String role) {
        if (role == null) {
            return null;
        }

        String normalized = role.trim().toUpperCase().replace(' ', '_');
        switch (normalized) {
            case "ADMIN":
                return "ADMIN";
            case "USER":
                return "USER";
            case "THEATER":
            case "THEATRE":
            case "THEATER_PERSON":
            case "THEATRE_PERSON":
            case "THREATRE":
            case "THREAD_PERSON":
                return "THEATRE";
            default:
                return normalized;
        }
    }

    public void registerUser(User user) throws SQLException {
        if (user.getRole() != null) {
            user.setRole(normalizeRole(user.getRole()));
        }
        if ("THEATRE".equals(user.getRole()) && user.getTheatreId() == null) {
            throw new SQLException("Theatre role requires theatreId");
        }
        if (!"THEATRE".equals(user.getRole())) {
            user.setTheatreId(null);
        }
        userDAO.addUser(user);
    }

    public User loginUser(String username, String password) throws SQLException {
        User user = userDAO.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            user.setRole(normalizeRole(user.getRole()));
            return user;
        }
        return null;
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    public User getUserById(int userId) throws SQLException {
        return userDAO.getUserById(userId);
    }

    public List<User> getUsersByTheatreId(int theatreId) throws SQLException {
        return userDAO.getUsersByTheatreId(theatreId);
    }

    public void updateUser(User user) throws SQLException {
        user.setRole(normalizeRole(user.getRole()));
        if (!"THEATRE".equals(user.getRole())) {
            user.setTheatreId(null);
        }
        userDAO.updateUser(user);
    }

    public void deleteUser(int userId) throws SQLException {
        userDAO.deleteUser(userId);
    }
}
