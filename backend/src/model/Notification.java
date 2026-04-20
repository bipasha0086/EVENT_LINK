package model;

import java.util.Date;

public class Notification {
    private int notificationId;
    private int theatreId;
    private int userId;
    private String subject;
    private String message;
    private Date sentAt;
    private String status;

    public Notification() {
    }

    public Notification(int notificationId, int theatreId, int userId, String subject, String message, Date sentAt, String status) {
        this.notificationId = notificationId;
        this.theatreId = theatreId;
        this.userId = userId;
        this.subject = subject;
        this.message = message;
        this.sentAt = sentAt;
        this.status = status;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getTheatreId() {
        return theatreId;
    }

    public void setTheatreId(int theatreId) {
        this.theatreId = theatreId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
