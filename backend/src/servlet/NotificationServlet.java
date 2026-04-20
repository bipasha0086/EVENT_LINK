package servlet;

import model.Notification;
import service.NotificationService;
import util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.List;

@WebServlet("/notification")
public class NotificationServlet extends HttpServlet {
    private NotificationService notificationService = new NotificationService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        String action = req.getParameter("action");

        try {
            if ("theatre-alert".equals(action)) {
                int theatreId = Integer.parseInt(req.getParameter("theatreId"));
                String subject = req.getParameter("subject");
                String message = req.getParameter("message");

                int sentCount = notificationService.sendTheatreAlert(theatreId, subject, message);
                out.print("{\"status\":\"success\",\"message\":\"Alerts processed\",\"sentCount\":" + sentCount + "}");
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\",\"message\":\"Unsupported action\"}");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"" + JsonUtil.escape(e.getMessage()) + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        try {
            int userId = Integer.parseInt(req.getParameter("userId"));
            List<Notification> notifications = notificationService.getNotificationsByUserId(userId);

            StringBuilder sb = new StringBuilder("{\"status\":\"success\",\"notifications\":[");
            for (int i = 0; i < notifications.size(); i++) {
                Notification n = notifications.get(i);
                sb.append("{")
                    .append("\"notificationId\":").append(n.getNotificationId()).append(",")
                    .append("\"theatreId\":").append(n.getTheatreId()).append(",")
                    .append("\"userId\":").append(n.getUserId()).append(",")
                    .append("\"subject\":").append(JsonUtil.string(n.getSubject())).append(",")
                    .append("\"message\":").append(JsonUtil.string(n.getMessage())).append(",")
                    .append("\"sentAt\":").append(JsonUtil.string(new Timestamp(n.getSentAt().getTime()).toString())).append(",")
                    .append("\"statusValue\":").append(JsonUtil.string(n.getStatus()))
                    .append("}");
                if (i < notifications.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("]}");
            out.print(sb.toString());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"" + JsonUtil.escape(e.getMessage()) + "\"}");
        }
    }
}
