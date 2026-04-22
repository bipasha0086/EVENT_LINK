-- MySQL schema for Event Ticket Booking System (Admin/User/Theatre)

DROP DATABASE IF EXISTS event_ticket_booking;
CREATE DATABASE event_ticket_booking;
USE event_ticket_booking;

CREATE TABLE theatres (
    theatre_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    area VARCHAR(100) NOT NULL,
    map_query VARCHAR(255)
);

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(120),
    role ENUM('USER', 'ADMIN', 'SUPER_ADMIN', 'THEATRE') NOT NULL,
    threat_area VARCHAR(100),
    theatre_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_theatre FOREIGN KEY (theatre_id) REFERENCES theatres(theatre_id)
);

CREATE TABLE events (
    event_id INT AUTO_INCREMENT PRIMARY KEY,
    theatre_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    event_date DATE NOT NULL,
    event_time TIME NOT NULL,
    location VARCHAR(100) NOT NULL,
    description TEXT,
    CONSTRAINT fk_event_theatre FOREIGN KEY (theatre_id) REFERENCES theatres(theatre_id)
);

CREATE TABLE seats (
    seat_id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    is_booked BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_seat_event FOREIGN KEY (event_id) REFERENCES events(event_id),
    CONSTRAINT uq_event_seat UNIQUE (event_id, seat_number)
);

CREATE TABLE bookings (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    event_id INT NOT NULL,
    seat_id INT NOT NULL,
    booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_deadline TIMESTAMP NOT NULL,
    status ENUM('PENDING_PAYMENT', 'PAID', 'EXPIRED', 'DEALLOCATED') NOT NULL DEFAULT 'PENDING_PAYMENT',
    allocated_friend_name VARCHAR(100),
    allocated_at TIMESTAMP NULL,
    paid_at TIMESTAMP NULL,
    deallocated_at TIMESTAMP NULL,
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_booking_event FOREIGN KEY (event_id) REFERENCES events(event_id),
    CONSTRAINT fk_booking_seat FOREIGN KEY (seat_id) REFERENCES seats(seat_id)
);

CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    theatre_id INT NOT NULL,
    user_id INT NOT NULL,
    subject VARCHAR(120) NOT NULL,
    message TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('SENT', 'FAILED') NOT NULL DEFAULT 'SENT',
    CONSTRAINT fk_notification_theatre FOREIGN KEY (theatre_id) REFERENCES theatres(theatre_id),
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE INDEX idx_booking_user_status ON bookings(user_id, status);
CREATE INDEX idx_booking_event_status ON bookings(event_id, status);
CREATE INDEX idx_event_theatre ON events(theatre_id);

INSERT INTO theatres (name, area, map_query) VALUES
('CineSphere Downtown', 'Downtown', 'CineSphere Downtown'),
('Nova Screen East', 'East Side', 'Nova Screen East'),
('StarView North', 'North Zone', 'StarView North');

INSERT INTO users (username, password, email, role, threat_area, theatre_id) VALUES
('admin', 'admin123', 'admin@local', 'SUPER_ADMIN', 'ALL_AREAS', NULL),
('theatre1', 'theatre123', 'theatre1@local', 'THEATRE', 'Downtown', 1),
('user1', 'user123', 'user1@mail.local', 'USER', 'Downtown', NULL);

INSERT INTO events (theatre_id, name, event_date, event_time, location, description) VALUES
(1, 'Music Night Live', '2026-04-08', '18:00:00', 'Downtown Hall', 'Local bands and indie artists in one show.'),
(2, 'Anime Fan Fest', '2026-04-09', '17:30:00', 'East Arena', 'Cosplay, screening, and community meetups.'),
(3, 'Classic Retro Night', '2026-04-11', '19:00:00', 'North Theatre', 'Timeless classics back on the big screen.');

INSERT INTO seats (event_id, seat_number) VALUES
(1, 'A1'), (1, 'A2'), (1, 'A3'), (1, 'B1'), (1, 'B2'), (1, 'B3'),
(2, 'A1'), (2, 'A2'), (2, 'A3'), (2, 'B1'), (2, 'B2'), (2, 'B3'),
(3, 'A1'), (3, 'A2'), (3, 'A3'), (3, 'B1'), (3, 'B2'), (3, 'B3');