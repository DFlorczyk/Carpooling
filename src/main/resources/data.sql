-- Idempotent seed data. Each statement is safe to re-run on every startup.

INSERT INTO cities (name) SELECT 'Warsaw'  WHERE NOT EXISTS (SELECT 1 FROM cities WHERE name = 'Warsaw');
INSERT INTO cities (name) SELECT 'Krakow'  WHERE NOT EXISTS (SELECT 1 FROM cities WHERE name = 'Krakow');
INSERT INTO cities (name) SELECT 'Gdansk'  WHERE NOT EXISTS (SELECT 1 FROM cities WHERE name = 'Gdansk');
INSERT INTO cities (name) SELECT 'Wroclaw' WHERE NOT EXISTS (SELECT 1 FROM cities WHERE name = 'Wroclaw');
INSERT INTO cities (name) SELECT 'Poznan'  WHERE NOT EXISTS (SELECT 1 FROM cities WHERE name = 'Poznan');

-- password123
INSERT INTO admins (login, hashed_password, is_super_admin)
SELECT 'admin', '$2a$12$DC.3P/V/TzlawOuBdqZyvu0THkKTQXS1vCM2h510MX1FNa4DP4H7a', true
WHERE NOT EXISTS (SELECT 1 FROM admins WHERE login = 'admin');

INSERT INTO users (is_driver, email, hashed_password, is_blocked, is_woman, name, phone_number, surname, city_id)
SELECT true, 'john.doe@gmail.com', '$2a$12$EdFdaVfFKtMQ5AJ4w7v.6Os0NKtkFL8e.9xNHMJqqXMdarxgW2cdu', false, false, 'John', '500100200', 'Doe', (SELECT id FROM cities WHERE name = 'Warsaw')
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'john.doe@gmail.com');

INSERT INTO users (is_driver, email, hashed_password, is_blocked, is_woman, name, phone_number, surname, city_id)
SELECT true, 'jane.smith@gmail.com', '$2a$12$EdFdaVfFKtMQ5AJ4w7v.6Os0NKtkFL8e.9xNHMJqqXMdarxgW2cdu', false, true, 'Jane', '500100201', 'Smith', (SELECT id FROM cities WHERE name = 'Wroclaw')
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'jane.smith@gmail.com');

INSERT INTO users (is_driver, email, hashed_password, is_blocked, is_woman, name, phone_number, surname, city_id)
SELECT false, 'adam.kowal@gmail.com', '$2a$12$EdFdaVfFKtMQ5AJ4w7v.6Os0NKtkFL8e.9xNHMJqqXMdarxgW2cdu', false, false, 'Adam', '500100202', 'Kowal', (SELECT id FROM cities WHERE name = 'Gdansk')
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'adam.kowal@gmail.com');

INSERT INTO users (is_driver, email, hashed_password, is_blocked, is_woman, name, phone_number, surname, city_id)
SELECT false, 'anna.nowak@gmail.com', '$2a$12$EdFdaVfFKtMQ5AJ4w7v.6Os0NKtkFL8e.9xNHMJqqXMdarxgW2cdu', false, true, 'Anna', '500100203', 'Nowak', (SELECT id FROM cities WHERE name = 'Warsaw')
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'anna.nowak@gmail.com');

INSERT INTO users (is_driver, email, hashed_password, is_blocked, is_woman, name, phone_number, surname, city_id)
SELECT false, 'piotr.wis@gmail.com', '$2a$12$EdFdaVfFKtMQ5AJ4w7v.6Os0NKtkFL8e.9xNHMJqqXMdarxgW2cdu', false, false, 'Piotr', '500100204', 'Wis', (SELECT id FROM cities WHERE name = 'Wroclaw')
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'piotr.wis@gmail.com');

INSERT INTO users (is_driver, email, hashed_password, is_blocked, is_woman, name, phone_number, surname, city_id)
SELECT false, 'admin@carpooling.com', '$2a$12$EdFdaVfFKtMQ5AJ4w7v.6Os0NKtkFL8e.9xNHMJqqXMdarxgW2cdu', false, false, 'Admin', '500100205', 'System', (SELECT id FROM cities WHERE name = 'Warsaw')
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@carpooling.com');

INSERT INTO ride_states (name)
SELECT name FROM (VALUES ('not started'), ('active'), ('finished')) AS t(name)
WHERE NOT EXISTS (SELECT 1 FROM ride_states);

INSERT INTO ride_roles (name)
SELECT name FROM (VALUES ('driver'), ('passenger')) AS t(name)
WHERE NOT EXISTS (SELECT 1 FROM ride_roles);

INSERT INTO star_ratings (value)
SELECT v FROM (VALUES (1), (2), (3), (4), (5)) AS t(v)
WHERE NOT EXISTS (SELECT 1 FROM star_ratings);

INSERT INTO car_details (model, seat_count, mileage, color, user_id)
SELECT 'Toyota Corolla', 4, 15000, 'Silver', (SELECT id FROM users WHERE email = 'john.doe@gmail.com')
WHERE NOT EXISTS (SELECT 1 FROM car_details WHERE user_id = (SELECT id FROM users WHERE email = 'john.doe@gmail.com'));

INSERT INTO car_details (model, seat_count, mileage, color, user_id)
SELECT 'Honda Civic', 4, 28500, 'Blue', (SELECT id FROM users WHERE email = 'jane.smith@gmail.com')
WHERE NOT EXISTS (SELECT 1 FROM car_details WHERE user_id = (SELECT id FROM users WHERE email = 'jane.smith@gmail.com'));

-- Ride 1: Warsaw → Kraków
INSERT INTO rides (start_latitude, start_longitude, end_latitude, end_longitude, date, cost, is_payed, state_id)
SELECT 52.2297, 21.0122, 50.0647, 19.9450, '2026-05-06 08:00:00', 35.00, false, (SELECT id FROM ride_states WHERE name = 'not started')
WHERE NOT EXISTS (SELECT 1 FROM rides WHERE start_latitude = 52.2297 AND end_latitude = 50.0647);

-- Ride 2: Wrocław → Poznań
INSERT INTO rides (start_latitude, start_longitude, end_latitude, end_longitude, date, cost, is_payed, state_id)
SELECT 51.1079, 17.0385, 52.4064, 16.9252, '2026-05-07 10:00:00', 25.00, false, (SELECT id FROM ride_states WHERE name = 'not started')
WHERE NOT EXISTS (SELECT 1 FROM rides WHERE start_latitude = 51.1079 AND end_latitude = 52.4064);

INSERT INTO ride_participants (user_id, ride_id, role_id)
SELECT (SELECT id FROM users WHERE email = 'john.doe@gmail.com'), (SELECT id FROM rides WHERE start_latitude = 52.2297), (SELECT id FROM ride_roles WHERE name = 'driver')
WHERE NOT EXISTS (SELECT 1 FROM ride_participants WHERE user_id = (SELECT id FROM users WHERE email = 'john.doe@gmail.com') AND ride_id = (SELECT id FROM rides WHERE start_latitude = 52.2297));

INSERT INTO ride_participants (user_id, ride_id, role_id)
SELECT (SELECT id FROM users WHERE email = 'jane.smith@gmail.com'), (SELECT id FROM rides WHERE start_latitude = 51.1079), (SELECT id FROM ride_roles WHERE name = 'driver')
WHERE NOT EXISTS (SELECT 1 FROM ride_participants WHERE user_id = (SELECT id FROM users WHERE email = 'jane.smith@gmail.com') AND ride_id = (SELECT id FROM rides WHERE start_latitude = 51.1079));

INSERT INTO ride_requests (user_id, ride_id, is_accepted, pickup_latitude, pickup_longitude, dropoff_latitude, dropoff_longitude)
SELECT (SELECT id FROM users WHERE email = 'piotr.wis@gmail.com'), (SELECT id FROM rides WHERE start_latitude = 52.2297), true, 52.2207, 20.9857, 50.0614, 19.9374
WHERE NOT EXISTS (SELECT 1 FROM ride_requests WHERE user_id = (SELECT id FROM users WHERE email = 'piotr.wis@gmail.com') AND ride_id = (SELECT id FROM rides WHERE start_latitude = 52.2297));

INSERT INTO ride_requests (user_id, ride_id, is_accepted, pickup_latitude, pickup_longitude, dropoff_latitude, dropoff_longitude)
SELECT (SELECT id FROM users WHERE email = 'anna.nowak@gmail.com'), (SELECT id FROM rides WHERE start_latitude = 52.2297), true, 52.2551, 21.0358, 50.0780, 19.9340
WHERE NOT EXISTS (SELECT 1 FROM ride_requests WHERE user_id = (SELECT id FROM users WHERE email = 'anna.nowak@gmail.com') AND ride_id = (SELECT id FROM rides WHERE start_latitude = 52.2297));

INSERT INTO ride_requests (user_id, ride_id, is_accepted, pickup_latitude, pickup_longitude, dropoff_latitude, dropoff_longitude)
SELECT (SELECT id FROM users WHERE email = 'adam.kowal@gmail.com'), (SELECT id FROM rides WHERE start_latitude = 51.1079), true, 51.1200, 17.0480, 52.3900, 16.9100
WHERE NOT EXISTS (SELECT 1 FROM ride_requests WHERE user_id = (SELECT id FROM users WHERE email = 'adam.kowal@gmail.com') AND ride_id = (SELECT id FROM rides WHERE start_latitude = 51.1079));

INSERT INTO ride_participants (user_id, ride_id, role_id)
SELECT (SELECT id FROM users WHERE email = 'piotr.wis@gmail.com'), (SELECT id FROM rides WHERE start_latitude = 52.2297), (SELECT id FROM ride_roles WHERE name = 'passenger')
WHERE NOT EXISTS (SELECT 1 FROM ride_participants WHERE user_id = (SELECT id FROM users WHERE email = 'piotr.wis@gmail.com') AND ride_id = (SELECT id FROM rides WHERE start_latitude = 52.2297));

INSERT INTO ride_participants (user_id, ride_id, role_id)
SELECT (SELECT id FROM users WHERE email = 'anna.nowak@gmail.com'), (SELECT id FROM rides WHERE start_latitude = 52.2297), (SELECT id FROM ride_roles WHERE name = 'passenger')
WHERE NOT EXISTS (SELECT 1 FROM ride_participants WHERE user_id = (SELECT id FROM users WHERE email = 'anna.nowak@gmail.com') AND ride_id = (SELECT id FROM rides WHERE start_latitude = 52.2297));

INSERT INTO ride_participants (user_id, ride_id, role_id)
SELECT (SELECT id FROM users WHERE email = 'adam.kowal@gmail.com'), (SELECT id FROM rides WHERE start_latitude = 51.1079), (SELECT id FROM ride_roles WHERE name = 'passenger')
WHERE NOT EXISTS (SELECT 1 FROM ride_participants WHERE user_id = (SELECT id FROM users WHERE email = 'adam.kowal@gmail.com') AND ride_id = (SELECT id FROM rides WHERE start_latitude = 51.1079));

-- Waypoints: Ride 1 (Warsaw→Kraków) — Piotr pickup first (closer to center), then Anna, then dropoffs toward Kraków
INSERT INTO ride_waypoints (latitude, longitude, type, sequence_order, ride_id, passenger_id)
SELECT 52.2207, 20.9857, 'PICKUP', 1, (SELECT id FROM rides WHERE start_latitude = 52.2297), (SELECT id FROM users WHERE email = 'piotr.wis@gmail.com')
WHERE NOT EXISTS (SELECT 1 FROM ride_waypoints WHERE ride_id = (SELECT id FROM rides WHERE start_latitude = 52.2297) AND passenger_id = (SELECT id FROM users WHERE email = 'piotr.wis@gmail.com') AND type = 'PICKUP');

INSERT INTO ride_waypoints (latitude, longitude, type, sequence_order, ride_id, passenger_id)
SELECT 52.2551, 21.0358, 'PICKUP', 2, (SELECT id FROM rides WHERE start_latitude = 52.2297), (SELECT id FROM users WHERE email = 'anna.nowak@gmail.com')
WHERE NOT EXISTS (SELECT 1 FROM ride_waypoints WHERE ride_id = (SELECT id FROM rides WHERE start_latitude = 52.2297) AND passenger_id = (SELECT id FROM users WHERE email = 'anna.nowak@gmail.com') AND type = 'PICKUP');

INSERT INTO ride_waypoints (latitude, longitude, type, sequence_order, ride_id, passenger_id)
SELECT 50.0780, 19.9340, 'DROPOFF', 3, (SELECT id FROM rides WHERE start_latitude = 52.2297), (SELECT id FROM users WHERE email = 'anna.nowak@gmail.com')
WHERE NOT EXISTS (SELECT 1 FROM ride_waypoints WHERE ride_id = (SELECT id FROM rides WHERE start_latitude = 52.2297) AND passenger_id = (SELECT id FROM users WHERE email = 'anna.nowak@gmail.com') AND type = 'DROPOFF');

INSERT INTO ride_waypoints (latitude, longitude, type, sequence_order, ride_id, passenger_id)
SELECT 50.0614, 19.9374, 'DROPOFF', 4, (SELECT id FROM rides WHERE start_latitude = 52.2297), (SELECT id FROM users WHERE email = 'piotr.wis@gmail.com')
WHERE NOT EXISTS (SELECT 1 FROM ride_waypoints WHERE ride_id = (SELECT id FROM rides WHERE start_latitude = 52.2297) AND passenger_id = (SELECT id FROM users WHERE email = 'piotr.wis@gmail.com') AND type = 'DROPOFF');

-- Waypoints: Ride 2 (Wrocław→Poznań)
INSERT INTO ride_waypoints (latitude, longitude, type, sequence_order, ride_id, passenger_id)
SELECT 51.1200, 17.0480, 'PICKUP', 1, (SELECT id FROM rides WHERE start_latitude = 51.1079), (SELECT id FROM users WHERE email = 'adam.kowal@gmail.com')
WHERE NOT EXISTS (SELECT 1 FROM ride_waypoints WHERE ride_id = (SELECT id FROM rides WHERE start_latitude = 51.1079) AND passenger_id = (SELECT id FROM users WHERE email = 'adam.kowal@gmail.com') AND type = 'PICKUP');

INSERT INTO ride_waypoints (latitude, longitude, type, sequence_order, ride_id, passenger_id)
SELECT 52.3900, 16.9100, 'DROPOFF', 2, (SELECT id FROM rides WHERE start_latitude = 51.1079), (SELECT id FROM users WHERE email = 'adam.kowal@gmail.com')
WHERE NOT EXISTS (SELECT 1 FROM ride_waypoints WHERE ride_id = (SELECT id FROM rides WHERE start_latitude = 51.1079) AND passenger_id = (SELECT id FROM users WHERE email = 'adam.kowal@gmail.com') AND type = 'DROPOFF');
