package servlet;

import model.Event;
import service.EventService;
import util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/event")
public class EventServlet extends HttpServlet {
    private EventService eventService = new EventService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String theatreIdRaw = req.getParameter("theatreId");
            List<Event> events;
            if (theatreIdRaw != null && !theatreIdRaw.trim().isEmpty()) {
                events = eventService.getEventsByTheatreId(Integer.parseInt(theatreIdRaw));
            } else {
                events = eventService.getAllEvents();
            }

            StringBuilder sb = new StringBuilder("{\"status\":\"success\",\"events\":[");
            for (int i = 0; i < events.size(); i++) {
                Event e = events.get(i);
                sb.append("{")
                    .append("\"eventId\":").append(e.getEventId()).append(",")
                    .append("\"theatreId\":").append(e.getTheatreId()).append(",")
                    .append("\"name\":").append(JsonUtil.string(e.getName())).append(",")
                    .append("\"eventDate\":").append(JsonUtil.string(sdf.format(e.getEventDate()))).append(",")
                    .append("\"eventTime\":").append(JsonUtil.string(e.getEventTime())).append(",")
                    .append("\"location\":").append(JsonUtil.string(e.getLocation())).append(",")
                    .append("\"description\":").append(JsonUtil.string(e.getDescription()))
                    .append("}");
                if (i < events.size() - 1) sb.append(",");
            }
            sb.append("]}");
            out.print(sb.toString());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"" + JsonUtil.escape(e.getMessage()) + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if ("add".equals(action)) {
                int theatreId = Integer.parseInt(req.getParameter("theatreId"));
                String name = req.getParameter("name");
                String eventDate = req.getParameter("eventDate");
                String eventTime = req.getParameter("eventTime");
                String location = req.getParameter("location");
                String description = req.getParameter("description");
                Event event = new Event(0, theatreId, name, sdf.parse(eventDate), eventTime, location, description);
                eventService.addEvent(event);
                out.print("{\"status\":\"success\",\"message\":\"Event added\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\",\"message\":\"Unsupported action\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"" + JsonUtil.escape(e.getMessage()) + "\"}");
        }
    }
}
