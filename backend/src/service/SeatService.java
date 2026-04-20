package service;

import dao.SeatDAO;
import model.Seat;

import java.sql.SQLException;
import java.util.List;

public class SeatService {
    private SeatDAO seatDAO = new SeatDAO();

    public void addSeat(Seat seat) throws SQLException {
        seatDAO.addSeat(seat);
    }

    public Seat getSeatById(int seatId) throws SQLException {
        return seatDAO.getSeatById(seatId);
    }

    public List<Seat> getSeatsByEventId(int eventId) throws SQLException {
        return seatDAO.getSeatsByEventId(eventId);
    }

    public void updateSeat(Seat seat) throws SQLException {
        seatDAO.updateSeat(seat);
    }

    public void deleteSeat(int seatId) throws SQLException {
        seatDAO.deleteSeat(seatId);
    }
}
