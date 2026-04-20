package servlet;

import model.Booking;
import service.BookingService;
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

@WebServlet("/booking")
public class BookingServlet extends HttpServlet {
    private BookingService bookingService = new BookingService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            bookingService.expirePendingBookings();

            if ("create".equals(action)) {
                int userId = Integer.parseInt(req.getParameter("userId"));
                int eventId = Integer.parseInt(req.getParameter("eventId"));
                int seatId = Integer.parseInt(req.getParameter("seatId"));

                Booking booking = bookingService.addBooking(userId, eventId, seatId);
                out.print("{\"status\":\"success\",\"message\":\"Booking created\",\"bookingId\":" + booking.getBookingId() + "}");
            } else if ("pay".equals(action)) {
                int bookingId = Integer.parseInt(req.getParameter("bookingId"));
                int userId = Integer.parseInt(req.getParameter("userId"));
                boolean paid = bookingService.payBooking(bookingId, userId);
                if (!paid) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"status\":\"fail\",\"message\":\"Payment failed. Booking may be expired or not pending.\"}");
                    return;
                }
                out.print("{\"status\":\"success\",\"message\":\"Payment successful\"}");
            } else if ("allocate".equals(action)) {
                int bookingId = Integer.parseInt(req.getParameter("bookingId"));
                String friendName = req.getParameter("friendName");
                boolean allocated = bookingService.allocateFriend(bookingId, friendName);
                if (!allocated) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"status\":\"fail\",\"message\":\"Could not allocate seat. Booking may not be pending.\"}");
                    return;
                }
                out.print("{\"status\":\"success\",\"message\":\"Seat allocated for friend\"}");
            } else if ("deallocate".equals(action)) {
                int bookingId = Integer.parseInt(req.getParameter("bookingId"));
                boolean deallocated = bookingService.deallocateBookingByAdmin(bookingId);
                if (!deallocated) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"status\":\"fail\",\"message\":\"Booking already deallocated/expired or not found\"}");
                    return;
                }
                out.print("{\"status\":\"success\",\"message\":\"Booking deallocated\"}");
            } else if ("expire-now".equals(action)) {
                int count = bookingService.expirePendingBookings();
                out.print("{\"status\":\"success\",\"message\":\"Expiry executed\",\"expiredCount\":" + count + "}");
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
            bookingService.expirePendingBookings();
            String userIdRaw = req.getParameter("userId");
            String theatreIdRaw = req.getParameter("theatreId");

            List<Booking> bookings;
            if (userIdRaw != null && !userIdRaw.trim().isEmpty()) {
                bookings = bookingService.getBookingsByUserId(Integer.parseInt(userIdRaw));
            } else if (theatreIdRaw != null && !theatreIdRaw.trim().isEmpty()) {
                bookings = bookingService.getBookingsByTheatreId(Integer.parseInt(theatreIdRaw));
            } else {
                bookings = bookingService.getAllBookings();
            }

            StringBuilder sb = new StringBuilder("{\"status\":\"success\",\"bookings\":[");
            for (int i = 0; i < bookings.size(); i++) {
                Booking b = bookings.get(i);
                sb.append("{")
                    .append("\"bookingId\":").append(b.getBookingId()).append(",")
                    .append("\"userId\":").append(b.getUserId()).append(",")
                    .append("\"eventId\":").append(b.getEventId()).append(",")
                    .append("\"seatId\":").append(b.getSeatId()).append(",")
                    .append("\"bookingTime\":").append(JsonUtil.string(new Timestamp(b.getBookingTime().getTime()).toString())).append(",")
                    .append("\"paymentDeadline\":").append(JsonUtil.string(new Timestamp(b.getPaymentDeadline().getTime()).toString())).append(",")
                    .append("\"status\":").append(JsonUtil.string(b.getStatus())).append(",")
                    .append("\"allocatedFriendName\":").append(JsonUtil.string(b.getAllocatedFriendName())).append(",")
                    .append("\"allocatedAt\":").append(b.getAllocatedAt() == null ? "null" : JsonUtil.string(new Timestamp(b.getAllocatedAt().getTime()).toString())).append(",")
                    .append("\"paidAt\":").append(b.getPaidAt() == null ? "null" : JsonUtil.string(new Timestamp(b.getPaidAt().getTime()).toString())).append(",")
                    .append("\"deallocatedAt\":").append(b.getDeallocatedAt() == null ? "null" : JsonUtil.string(new Timestamp(b.getDeallocatedAt().getTime()).toString()))
                    .append("}");
                if (i < bookings.size() - 1) sb.append(",");
            }
            sb.append("]}");
            out.print(sb.toString());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"" + JsonUtil.escape(e.getMessage()) + "\"}");
        }
    }
}
