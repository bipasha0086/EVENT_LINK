package model;

import java.util.Date;

public class Booking {
    private int bookingId;
    private int userId;
    private int eventId;
    private int seatId;
    private Date bookingTime;
    private Date paymentDeadline;
    private String status;
    private String allocatedFriendName;
    private Date allocatedAt;
    private Date paidAt;
    private Date deallocatedAt;

    public Booking() {}

    public Booking(
        int bookingId,
        int userId,
        int eventId,
        int seatId,
        Date bookingTime,
        Date paymentDeadline,
        String status,
        String allocatedFriendName,
        Date allocatedAt,
        Date paidAt,
        Date deallocatedAt
    ) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.eventId = eventId;
        this.seatId = seatId;
        this.bookingTime = bookingTime;
        this.paymentDeadline = paymentDeadline;
        this.status = status;
        this.allocatedFriendName = allocatedFriendName;
        this.allocatedAt = allocatedAt;
        this.paidAt = paidAt;
        this.deallocatedAt = deallocatedAt;
    }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public int getSeatId() { return seatId; }
    public void setSeatId(int seatId) { this.seatId = seatId; }
    public Date getBookingTime() { return bookingTime; }
    public void setBookingTime(Date bookingTime) { this.bookingTime = bookingTime; }
    public Date getPaymentDeadline() { return paymentDeadline; }
    public void setPaymentDeadline(Date paymentDeadline) { this.paymentDeadline = paymentDeadline; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAllocatedFriendName() { return allocatedFriendName; }
    public void setAllocatedFriendName(String allocatedFriendName) { this.allocatedFriendName = allocatedFriendName; }
    public Date getAllocatedAt() { return allocatedAt; }
    public void setAllocatedAt(Date allocatedAt) { this.allocatedAt = allocatedAt; }
    public Date getPaidAt() { return paidAt; }
    public void setPaidAt(Date paidAt) { this.paidAt = paidAt; }
    public Date getDeallocatedAt() { return deallocatedAt; }
    public void setDeallocatedAt(Date deallocatedAt) { this.deallocatedAt = deallocatedAt; }
}
