package servlet;

import model.Theatre;
import service.TheatreService;
import util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/theatre")
public class TheatreServlet extends HttpServlet {
    private TheatreService theatreService = new TheatreService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            String theatreIdRaw = req.getParameter("theatreId");
            if (theatreIdRaw != null && !theatreIdRaw.trim().isEmpty()) {
                Theatre theatre = theatreService.getTheatreById(Integer.parseInt(theatreIdRaw));
                if (theatre == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"status\":\"fail\",\"message\":\"Theatre not found\"}");
                    return;
                }

                out.print("{\"status\":\"success\",\"theatre\":{" +
                    "\"theatreId\":" + theatre.getTheatreId() + "," +
                    "\"name\":" + JsonUtil.string(theatre.getName()) + "," +
                    "\"area\":" + JsonUtil.string(theatre.getArea()) + "," +
                    "\"mapQuery\":" + JsonUtil.string(theatre.getMapQuery()) +
                    "}}"
                );
                return;
            }

            List<Theatre> theatres = theatreService.getAllTheatres();
            StringBuilder sb = new StringBuilder("{\"status\":\"success\",\"theatres\":[");
            for (int i = 0; i < theatres.size(); i++) {
                Theatre t = theatres.get(i);
                sb.append("{")
                    .append("\"theatreId\":").append(t.getTheatreId()).append(",")
                    .append("\"name\":").append(JsonUtil.string(t.getName())).append(",")
                    .append("\"area\":").append(JsonUtil.string(t.getArea())).append(",")
                    .append("\"mapQuery\":").append(JsonUtil.string(t.getMapQuery()))
                    .append("}");
                if (i < theatres.size() - 1) {
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
