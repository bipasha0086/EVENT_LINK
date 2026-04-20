package servlet;

import model.User;
import service.UserService;
import util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/user")
public class UserServlet extends HttpServlet {
    private UserService userService = new UserService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            if ("register".equals(action)) {
                String username = req.getParameter("username");
                String password = req.getParameter("password");
                String role = req.getParameter("role");
                String email = req.getParameter("email");
                String threatArea = req.getParameter("threatArea");
                String theatreIdRaw = req.getParameter("theatreId");
                Integer theatreId = (theatreIdRaw == null || theatreIdRaw.trim().isEmpty()) ? null : Integer.parseInt(theatreIdRaw);

                User user = new User(0, username, password, email, role, threatArea, theatreId);
                userService.registerUser(user);
                out.print("{\"status\":\"success\",\"message\":\"User registered\"}");
            } else if ("login".equals(action)) {
                String username = req.getParameter("username");
                String password = req.getParameter("password");
                User user = userService.loginUser(username, password);
                if (user != null) {
                    String theatreId = user.getTheatreId() == null ? "null" : String.valueOf(user.getTheatreId());
                    out.print("{\"status\":\"success\",\"user\":{" +
                        "\"userId\":" + user.getUserId() + "," +
                        "\"username\":" + JsonUtil.string(user.getUsername()) + "," +
                        "\"email\":" + JsonUtil.string(user.getEmail()) + "," +
                        "\"role\":" + JsonUtil.string(user.getRole()) + "," +
                        "\"threatArea\":" + JsonUtil.string(user.getThreatArea()) + "," +
                        "\"theatreId\":" + theatreId +
                    "}}"
                    );
                } else {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.print("{\"status\":\"fail\",\"message\":\"Invalid credentials\"}");
                }
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
            String userIdRaw = req.getParameter("userId");
            if (userIdRaw != null && !userIdRaw.trim().isEmpty()) {
                User user = userService.getUserById(Integer.parseInt(userIdRaw));
                if (user == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"status\":\"fail\",\"message\":\"User not found\"}");
                    return;
                }

                String theatreId = user.getTheatreId() == null ? "null" : String.valueOf(user.getTheatreId());
                out.print("{\"status\":\"success\",\"user\":{" +
                    "\"userId\":" + user.getUserId() + "," +
                    "\"username\":" + JsonUtil.string(user.getUsername()) + "," +
                    "\"email\":" + JsonUtil.string(user.getEmail()) + "," +
                    "\"role\":" + JsonUtil.string(user.getRole()) + "," +
                    "\"threatArea\":" + JsonUtil.string(user.getThreatArea()) + "," +
                    "\"theatreId\":" + theatreId +
                "}}"
                );
                return;
            }

            List<User> users = userService.getAllUsers();
            StringBuilder sb = new StringBuilder("{\"status\":\"success\",\"users\":[");
            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                String theatreId = user.getTheatreId() == null ? "null" : String.valueOf(user.getTheatreId());
                sb.append("{")
                    .append("\"userId\":").append(user.getUserId()).append(",")
                    .append("\"username\":").append(JsonUtil.string(user.getUsername())).append(",")
                    .append("\"email\":").append(JsonUtil.string(user.getEmail())).append(",")
                    .append("\"role\":").append(JsonUtil.string(user.getRole())).append(",")
                    .append("\"threatArea\":").append(JsonUtil.string(user.getThreatArea())).append(",")
                    .append("\"theatreId\":").append(theatreId)
                    .append("}");
                if (i < users.size() - 1) {
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
