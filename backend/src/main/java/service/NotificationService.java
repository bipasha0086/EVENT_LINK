package service;

import dao.NotificationDAO;
import model.Notification;

import java.sql.SQLException;
import java.util.List;

public class NotificationService {
    private NotificationDAO notificationDAO = new NotificationDAO();

    public int sendTheatreAlert(int theatreId, String subject, String message) throws SQLException {
        return notificationDAO.sendTheatreAlert(theatreId, subject, message);
    }

    public List<Notification> getNotificationsByUserId(int userId) throws SQLException {
        return notificationDAO.getNotificationsByUserId(userId);
    }
}
