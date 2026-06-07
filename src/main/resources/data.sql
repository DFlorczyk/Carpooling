INSERT INTO cities (id, name) VALUES
(1, 'Warszawa'),
(2, 'Kraków'),
(3, 'Wrocław'),
(4, 'Poznań'),
(5, 'Gdańsk')
ON CONFLICT DO NOTHING;

INSERT INTO ride_states (id, name) VALUES
(1, 'not started'),
(2, 'active'),
(3, 'finished')
ON CONFLICT DO NOTHING;

INSERT INTO ride_roles (id, name) VALUES
(1, 'driver'),
(2, 'passenger')
ON CONFLICT DO NOTHING;

INSERT INTO star_ratings (id, value) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5)
ON CONFLICT DO NOTHING;

INSERT INTO report_states (id, name) VALUES
(1, 'pending'),
(2, 'resolved'),
(3, 'rejected')
ON CONFLICT DO NOTHING;

INSERT INTO special_rides (id, name) VALUES
(1, 'standard'),
(2, 'women-only')
ON CONFLICT DO NOTHING;

INSERT INTO user_credentials (id, email, hashed_password, is_blocked, role) VALUES
(1, 'admin@carpooling.pl',      '$2a$12$Mqxgow7B35ClPnAeS9qmFOetLxuBtDdKmfHWr2YDS4p96a3Y7kDLm', false, 'ADMIN'),
(2, 'jan.kowalski@example.com', '$2a$12$Mqxgow7B35ClPnAeS9qmFOetLxuBtDdKmfHWr2YDS4p96a3Y7kDLm', false, 'USER'),
(3, 'anna.nowak@example.com',   '$2a$12$Mqxgow7B35ClPnAeS9qmFOetLxuBtDdKmfHWr2YDS4p96a3Y7kDLm', false, 'USER'),
(4, 'piotr.w@example.com',      '$2a$12$Mqxgow7B35ClPnAeS9qmFOetLxuBtDdKmfHWr2YDS4p96a3Y7kDLm', false, 'USER'),
(5, 'maria.z@example.com',      '$2a$12$Mqxgow7B35ClPnAeS9qmFOetLxuBtDdKmfHWr2YDS4p96a3Y7kDLm', false, 'USER')
ON CONFLICT DO NOTHING;

INSERT INTO user_profiles (id, credential_id, name, surname, phone_number, is_woman, is_driver, city_id) VALUES
(1, 1, 'Admin', 'Admin',      null,          false, false, 1),
(2, 2, 'Jan',   'Kowalski',   '123456789',   false, true,  1),
(3, 3, 'Anna',  'Nowak',      '987654321',   true,  false, 2),
(4, 4, 'Piotr', 'Wisniewski', '555444333',   false, true,  3),
(5, 5, 'Maria', 'Zielinska',  '111222333',   true,  false, 2)
ON CONFLICT DO NOTHING;

INSERT INTO car_details (id, user_id, model, seat_count, mileage, color) VALUES
(1, 2, 'Toyota Corolla', 4, 50, 'Srebrny'),
(2, 4, 'BMW 3',          4, 80, 'Czarny')
ON CONFLICT DO NOTHING;

INSERT INTO rides (id, start_latitude, start_longitude, end_latitude, end_longitude, date, cost, is_payed, state_id) VALUES
(1, 52.2297, 21.0122, 50.0647, 19.9450, '2026-05-10 08:00:00', 25.00, false, 1),
(2, 51.1079, 17.0385, 52.4064, 16.9252, '2026-05-11 14:00:00', 30.00, false, 1),
(3, 52.2297, 21.0122, 54.3520, 18.6466, '2026-04-01 07:00:00', 50.00, true,  3)
ON CONFLICT DO NOTHING;

INSERT INTO ride_participants (id, user_id, ride_id, role_id) VALUES
(1, 2, 1, 1),
(2, 3, 1, 2),
(3, 4, 2, 1),
(4, 2, 3, 1),
(5, 5, 3, 2)
ON CONFLICT DO NOTHING;

INSERT INTO user_reviews (id, sender_id, receiver_id, star_rating_id) VALUES
(1, 3, 2, 5),
(2, 5, 2, 4)
ON CONFLICT DO NOTHING;

SELECT setval(pg_get_serial_sequence('cities',         'id'), COALESCE((SELECT MAX(id) FROM cities), 1));
SELECT setval(pg_get_serial_sequence('ride_states',      'id'), COALESCE((SELECT MAX(id) FROM ride_states), 1));
SELECT setval(pg_get_serial_sequence('ride_roles',       'id'), COALESCE((SELECT MAX(id) FROM ride_roles), 1));
SELECT setval(pg_get_serial_sequence('star_ratings',     'id'), COALESCE((SELECT MAX(id) FROM star_ratings), 1));
SELECT setval(pg_get_serial_sequence('report_states',    'id'), COALESCE((SELECT MAX(id) FROM report_states), 1));
SELECT setval(pg_get_serial_sequence('special_rides',    'id'), COALESCE((SELECT MAX(id) FROM special_rides), 1));
SELECT setval(pg_get_serial_sequence('user_credentials', 'id'), COALESCE((SELECT MAX(id) FROM user_credentials), 1));
SELECT setval(pg_get_serial_sequence('user_profiles',    'id'), COALESCE((SELECT MAX(id) FROM user_profiles), 1));
SELECT setval(pg_get_serial_sequence('car_details',      'id'), COALESCE((SELECT MAX(id) FROM car_details), 1));
SELECT setval(pg_get_serial_sequence('rides',            'id'), COALESCE((SELECT MAX(id) FROM rides), 1));
SELECT setval(pg_get_serial_sequence('ride_participants','id'), COALESCE((SELECT MAX(id) FROM ride_participants), 1));
SELECT setval(pg_get_serial_sequence('user_reviews',     'id'), COALESCE((SELECT MAX(id) FROM user_reviews), 1));
