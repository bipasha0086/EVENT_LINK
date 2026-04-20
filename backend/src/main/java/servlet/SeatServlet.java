package servlet;

import model.Seat;
import service.SeatService;
import util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/seat")
public class SeatServlet extends HttpServlet {
    private SeatService seatService = new SeatService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        try {
            int eventId = Integer.parseInt(req.getParameter("eventId"));
            List<Seat> seats = seatService.getSeatsByEventId(eventId);
            StringBuilder sb = new StringBuilder("{\"status\":\"success\",\"seats\":[");
            for (int i = 0; i < seats.size(); i++) {
                Seat s = seats.get(i);
                sb.append("{")
                    .append("\"seatId\":").append(s.getSeatId()).append(",")
                    .append("\"eventId\":").append(s.getEventId()).append(",")
                    .append("\"seatNumber\":").append(JsonUtil.string(s.getSeatNumber())).append(",")
                    .append("\"isBooked\":").append(s.isBooked())
                    .append("}");
                if (i < seats.size() - 1) sb.append(",");
            }
            sb.append("]}");
            out.print(sb.toString());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"" + JsonUtil.escape(e.getMessage()) + "\"}");
        }
    }
}
