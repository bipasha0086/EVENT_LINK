package service;

import dao.UserDAO;
import model.User;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    private UserDAO userDAO = new UserDAO();

    public void registerUser(User user) throws SQLException {
        if (user.getRole() != null) {
            user.setRole(user.getRole().toUpperCase());
        }
        userDAO.addUser(user);
    }

    public User loginUser(String username, String password) throws SQLException {
        User user = userDAO.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
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
        userDAO.updateUser(user);
    }

    public void deleteUser(int userId) throws SQLException {
        userDAO.deleteUser(userId);
    }
}
