package service;

import dao.EventDAO;
import model.Event;

import java.sql.SQLException;
import java.util.List;

public class EventService {
    private EventDAO eventDAO = new EventDAO();

    public void addEvent(Event event) throws SQLException {
        eventDAO.addEvent(event);
    }

    public Event getEventById(int eventId) throws SQLException {
        return eventDAO.getEventById(eventId);
    }

    public List<Event> getAllEvents() throws SQLException {
        return eventDAO.getAllEvents();
    }

    public List<Event> getEventsByTheatreId(int theatreId) throws SQLException {
        return eventDAO.getEventsByTheatreId(theatreId);
    }

    public void updateEvent(Event event) throws SQLException {
        eventDAO.updateEvent(event);
    }

    public void deleteEvent(int eventId) throws SQLException {
        eventDAO.deleteEvent(eventId);
    }
}
