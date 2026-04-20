package service;

import dao.TheatreDAO;
import model.Theatre;

import java.sql.SQLException;
import java.util.List;

public class TheatreService {
    private TheatreDAO theatreDAO = new TheatreDAO();

    public List<Theatre> getAllTheatres() throws SQLException {
        return theatreDAO.getAllTheatres();
    }

    public Theatre getTheatreById(int theatreId) throws SQLException {
        return theatreDAO.getTheatreById(theatreId);
    }
}
