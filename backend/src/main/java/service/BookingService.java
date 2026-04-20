package service;

import dao.BookingDAO;
import model.Booking;

import java.sql.SQLException;
import java.util.List;

public class BookingService {
    private BookingDAO bookingDAO = new BookingDAO();

    public Booking addBooking(int userId, int eventId, int seatId) throws SQLException {
        bookingDAO.expirePendingBookings();
        return bookingDAO.createBooking(userId, eventId, seatId);
    }

    public Booking getBookingById(int bookingId) throws SQLException {
        return bookingDAO.getBookingById(bookingId);
    }

    public List<Booking> getBookingsByUserId(int userId) throws SQLException {
        bookingDAO.expirePendingBookings();
        return bookingDAO.getBookingsByUserId(userId);
    }

    public List<Booking> getBookingsByTheatreId(int theatreId) throws SQLException {
        bookingDAO.expirePendingBookings();
        return bookingDAO.getBookingsByTheatreId(theatreId);
    }

    public List<Booking> getAllBookings() throws SQLException {
        bookingDAO.expirePendingBookings();
        return bookingDAO.getAllBookings();
    }

    public boolean payBooking(int bookingId, int userId) throws SQLException {
        bookingDAO.expirePendingBookings();
        return bookingDAO.markBookingPaid(bookingId, userId);
    }

    public boolean allocateFriend(int bookingId, String friendName) throws SQLException {
        bookingDAO.expirePendingBookings();
        return bookingDAO.allocateFriend(bookingId, friendName);
    }

    public boolean deallocateBookingByAdmin(int bookingId) throws SQLException {
        bookingDAO.expirePendingBookings();
        return bookingDAO.deallocateByAdmin(bookingId);
    }

    public int expirePendingBookings() throws SQLException {
        return bookingDAO.expirePendingBookings();
    }

    public void deleteBooking(int bookingId) throws SQLException {
        bookingDAO.deleteBooking(bookingId);
    }
}
