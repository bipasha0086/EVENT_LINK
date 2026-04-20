# Backend API Guide

## 1. Database Setup

Run [database/schema.sql](../database/schema.sql) in MySQL.

- Database: `event_ticket_booking`
- User: `root`
- Password: `Bipasha0086@`

The schema creates:
- roles: ADMIN, USER, THEATRE
- theatre, event, seat, booking lifecycle tables
- notification logs for theatre alerts

## 2. Core Endpoints

### User
- `POST /user?action=register`
  - fields: `username`, `password`, `email`, `role`, `threatArea`, `theatreId`
- `POST /user?action=login`
  - fields: `username`, `password`
- `GET /user?userId=1`
- `GET /user`

### Theatre
- `GET /theatre`
- `GET /theatre?theatreId=1`

### Event
- `GET /event`
- `GET /event?theatreId=1`
- `POST /event?action=add`
  - fields: `theatreId`, `name`, `eventDate` (`yyyy-MM-dd`), `eventTime` (`HH:mm` or `HH:mm:ss`), `location`, `description`

### Seat
- `GET /seat?eventId=1`

### Booking
- `POST /booking?action=create`
  - fields: `userId`, `eventId`, `seatId`
- `POST /booking?action=pay`
  - fields: `bookingId`, `userId`
- `POST /booking?action=allocate`
  - fields: `bookingId`, `friendName`
- `POST /booking?action=deallocate`
  - fields: `bookingId`
- `POST /booking?action=expire-now`
- `GET /booking?userId=1`
- `GET /booking?theatreId=1`
- `GET /booking`

### Notification (mail log simulation)
- `POST /notification?action=theatre-alert`
  - fields: `theatreId`, `subject`, `message`
- `GET /notification?userId=1`

### Chatbot
- `POST /chatbot`
  - JSON body: `message`, optional `role`, optional `context`
  - backend environment: `XAI_API_KEY` required
  - optional backend env: `XAI_MODEL` (defaults to `grok-2-latest`)

## 3. Business Rules Enforced

- One active seat per user (`PENDING_PAYMENT` or `PAID`).
- Payment deadline is 24 hours from booking creation.
- Expired unpaid seats auto-transition to `EXPIRED` and seat is released.
- Admin can allocate to friend while pending.
- Admin deallocate changes status to `DEALLOCATED` and releases seat.
- Theatre alerts are saved per active user booking in `notifications` table.
- The chatbot endpoint proxies requests to xAI/Grok from the backend so the API key stays out of the browser.

## 4. Java Files Updated

- Config: [backend/src/util/DBConnection.java](src/util/DBConnection.java)
- Booking lifecycle: [backend/src/dao/BookingDAO.java](src/dao/BookingDAO.java)
- Theatre and notification data: [backend/src/dao/TheatreDAO.java](src/dao/TheatreDAO.java), [backend/src/dao/NotificationDAO.java](src/dao/NotificationDAO.java)
- Role-aware auth/user profile: [backend/src/servlet/UserServlet.java](src/servlet/UserServlet.java)
- Booking actions: [backend/src/servlet/BookingServlet.java](src/servlet/BookingServlet.java)
- Theatre schedules: [backend/src/servlet/EventServlet.java](src/servlet/EventServlet.java), [backend/src/servlet/TheatreServlet.java](src/servlet/TheatreServlet.java)
- User alert feed: [backend/src/servlet/NotificationServlet.java](src/servlet/NotificationServlet.java)
