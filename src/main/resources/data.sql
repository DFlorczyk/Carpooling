ALTER TABLE ride_requests ALTER COLUMN is_accepted DROP NOT NULL;

ALTER TABLE ride_requests    DROP CONSTRAINT IF EXISTS fk41cbu7ct1o25egxpt4wk9uf0t;
ALTER TABLE ride_waypoints   DROP CONSTRAINT IF EXISTS fk23gkm4nibuvgxhf1ut56ukogl;
ALTER TABLE ride_waypoints   DROP CONSTRAINT IF EXISTS fk_ride_waypoints_passenger;
ALTER TABLE ride_waypoints   ADD  CONSTRAINT fk_ride_waypoints_passenger  FOREIGN KEY (passenger_id) REFERENCES user_profiles(id);
ALTER TABLE ride_participants DROP CONSTRAINT IF EXISTS fkg2tw36nkhf97l5rfdx52qisl9;
ALTER TABLE ride_participants DROP CONSTRAINT IF EXISTS fk_ride_participants_user;
ALTER TABLE ride_participants ADD  CONSTRAINT fk_ride_participants_user    FOREIGN KEY (user_id)     REFERENCES user_profiles(id);
ALTER TABLE ride_requests    DROP CONSTRAINT IF EXISTS fk_ride_requests_user_profile;
ALTER TABLE ride_requests    ADD  CONSTRAINT fk_ride_requests_user_profile FOREIGN KEY (user_id)    REFERENCES user_profiles(id);
ALTER TABLE car_details      DROP CONSTRAINT IF EXISTS fkshfa3ldww6tbmgcxsemum6t8o;
ALTER TABLE car_details      DROP CONSTRAINT IF EXISTS fk_car_details_user;
ALTER TABLE car_details      ADD  CONSTRAINT fk_car_details_user           FOREIGN KEY (user_id)    REFERENCES user_profiles(id);

TRUNCATE TABLE
    ride_waypoints,
    ride_requests,
    ride_participants,
    rides,
    car_details,
    user_profiles,
    user_credentials,
    cities,
    ride_states,
    ride_roles,
    star_ratings
RESTART IDENTITY CASCADE;

INSERT INTO cities (name) VALUES ('Warsaw'), ('Krakow'), ('Gdansk'), ('Wroclaw'), ('Poznan');

INSERT INTO ride_states (name) VALUES ('not started'), ('active'), ('finished');

INSERT INTO ride_roles (name) VALUES ('driver'), ('passenger');

INSERT INTO star_ratings (value) VALUES (1), (2), (3), (4), (5);

INSERT INTO user_credentials (email, hashed_password, is_blocked, role) VALUES
    ('john.doe@gmail.com',   '$2a$12$5.c0/T.Tp2/qaTsfPALLFeZpU5EM2dvXzKtW2m8H2nh3wv1pRsIqW', false, 'USER'),
    ('jane.smith@gmail.com', '$2a$12$5.c0/T.Tp2/qaTsfPALLFeZpU5EM2dvXzKtW2m8H2nh3wv1pRsIqW', false, 'USER'),
    ('tomasz.kos@gmail.com', '$2a$12$5.c0/T.Tp2/qaTsfPALLFeZpU5EM2dvXzKtW2m8H2nh3wv1pRsIqW', false, 'USER'),
    ('adam.kowal@gmail.com', '$2a$12$5.c0/T.Tp2/qaTsfPALLFeZpU5EM2dvXzKtW2m8H2nh3wv1pRsIqW', false, 'USER'),
    ('anna.nowak@gmail.com', '$2a$12$5.c0/T.Tp2/qaTsfPALLFeZpU5EM2dvXzKtW2m8H2nh3wv1pRsIqW', false, 'USER'),
    ('piotr.wis@gmail.com',  '$2a$12$5.c0/T.Tp2/qaTsfPALLFeZpU5EM2dvXzKtW2m8H2nh3wv1pRsIqW', false, 'USER'),
    ('marek.kow@gmail.com',  '$2a$12$5.c0/T.Tp2/qaTsfPALLFeZpU5EM2dvXzKtW2m8H2nh3wv1pRsIqW', false, 'USER'),
    ('admin@carpooling.com', '$2a$12$5.c0/T.Tp2/qaTsfPALLFeZpU5EM2dvXzKtW2m8H2nh3wv1pRsIqW', false, 'SUPER_ADMIN');

INSERT INTO user_profiles (credential_id, name, surname, phone_number, is_woman, is_driver, city_id) VALUES
    ((SELECT id FROM user_credentials WHERE email = 'john.doe@gmail.com'),
     'John',   'Doe',    '500100200', false, true,  (SELECT id FROM cities WHERE name = 'Warsaw')),
    ((SELECT id FROM user_credentials WHERE email = 'jane.smith@gmail.com'),
     'Jane',   'Smith',  '500100201', true,  true,  (SELECT id FROM cities WHERE name = 'Wroclaw')),
    ((SELECT id FROM user_credentials WHERE email = 'tomasz.kos@gmail.com'),
     'Tomasz', 'Kos',    '500100207', false, true,  (SELECT id FROM cities WHERE name = 'Gdansk')),
    ((SELECT id FROM user_credentials WHERE email = 'adam.kowal@gmail.com'),
     'Adam',   'Kowal',  '500100202', false, false, (SELECT id FROM cities WHERE name = 'Gdansk')),
    ((SELECT id FROM user_credentials WHERE email = 'anna.nowak@gmail.com'),
     'Anna',   'Nowak',  '500100203', true,  false, (SELECT id FROM cities WHERE name = 'Warsaw')),
    ((SELECT id FROM user_credentials WHERE email = 'piotr.wis@gmail.com'),
     'Piotr',  'Wis',    '500100204', false, false, (SELECT id FROM cities WHERE name = 'Wroclaw')),
    ((SELECT id FROM user_credentials WHERE email = 'marek.kow@gmail.com'),
     'Marek',  'Kow',    '500100205', false, false, (SELECT id FROM cities WHERE name = 'Warsaw')),
    ((SELECT id FROM user_credentials WHERE email = 'admin@carpooling.com'),
     'Admin',  'System', '500100206', false, false, (SELECT id FROM cities WHERE name = 'Warsaw'));

INSERT INTO car_details (model, seat_count, mileage, color, user_id) VALUES
    ('Toyota Corolla', 4, 15000, 'Silver',
     (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'john.doe@gmail.com'))),
    ('Honda Civic',    4, 28500, 'Blue',
     (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'jane.smith@gmail.com'))),
    ('Skoda Octavia',  4, 31500, 'Gray',
     (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'tomasz.kos@gmail.com')));

INSERT INTO rides (start_latitude, start_longitude, end_latitude, end_longitude, date, cost, is_payed, state_id, available_seats)
VALUES (52.2297, 21.0122, 50.0647, 19.9450, '2026-08-01 08:00:00', 35.00, false,
        (SELECT id FROM ride_states WHERE name = 'not started'), 4);

INSERT INTO rides (start_latitude, start_longitude, end_latitude, end_longitude, date, cost, is_payed, state_id, available_seats)
VALUES (51.1079, 17.0385, 52.4064, 16.9252, '2026-08-15 10:00:00', 25.00, false,
        (SELECT id FROM ride_states WHERE name = 'not started'), 4);

INSERT INTO ride_participants (user_id, ride_id, role_id)
VALUES
    ((SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'john.doe@gmail.com')),
     (SELECT id FROM rides WHERE start_latitude = 52.2297 AND end_latitude = 50.0647),
     (SELECT id FROM ride_roles WHERE name = 'driver')),
    ((SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'jane.smith@gmail.com')),
     (SELECT id FROM rides WHERE start_latitude = 51.1079 AND end_latitude = 52.4064),
     (SELECT id FROM ride_roles WHERE name = 'driver'));

INSERT INTO ride_requests (user_id, ride_id, is_accepted, pickup_latitude, pickup_longitude, dropoff_latitude, dropoff_longitude)
VALUES (
    (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'anna.nowak@gmail.com')),
    (SELECT id FROM rides WHERE start_latitude = 52.2297),
    true, 52.2551, 21.0358, 50.0780, 19.9340);

INSERT INTO ride_requests (user_id, ride_id, is_accepted, pickup_latitude, pickup_longitude, dropoff_latitude, dropoff_longitude)
VALUES (
    (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'piotr.wis@gmail.com')),
    (SELECT id FROM rides WHERE start_latitude = 52.2297),
    true, 52.2207, 20.9857, 50.0614, 19.9374);

INSERT INTO ride_requests (user_id, ride_id, is_accepted, pickup_latitude, pickup_longitude, dropoff_latitude, dropoff_longitude)
VALUES (
    (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'marek.kow@gmail.com')),
    (SELECT id FROM rides WHERE start_latitude = 52.2297),
    true, 52.2360, 21.0120, 50.0500, 19.9600);

INSERT INTO ride_requests (user_id, ride_id, is_accepted, pickup_latitude, pickup_longitude, dropoff_latitude, dropoff_longitude)
VALUES (
    (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'adam.kowal@gmail.com')),
    (SELECT id FROM rides WHERE start_latitude = 51.1079),
    true, 51.1200, 17.0480, 52.3900, 16.9100);

INSERT INTO ride_requests (user_id, ride_id, is_accepted, pickup_latitude, pickup_longitude, dropoff_latitude, dropoff_longitude)
VALUES (
    (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'marek.kow@gmail.com')),
    (SELECT id FROM rides WHERE start_latitude = 51.1079),
    true, 51.6100, 17.0700, 52.4100, 16.9400);

INSERT INTO ride_participants (user_id, ride_id, role_id)
VALUES
    ((SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'anna.nowak@gmail.com')),
     (SELECT id FROM rides WHERE start_latitude = 52.2297 AND end_latitude = 50.0647),
     (SELECT id FROM ride_roles WHERE name = 'passenger')),
    ((SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'piotr.wis@gmail.com')),
     (SELECT id FROM rides WHERE start_latitude = 52.2297 AND end_latitude = 50.0647),
     (SELECT id FROM ride_roles WHERE name = 'passenger')),
    ((SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'marek.kow@gmail.com')),
     (SELECT id FROM rides WHERE start_latitude = 52.2297 AND end_latitude = 50.0647),
     (SELECT id FROM ride_roles WHERE name = 'passenger')),
    ((SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'adam.kowal@gmail.com')),
     (SELECT id FROM rides WHERE start_latitude = 51.1079 AND end_latitude = 52.4064),
     (SELECT id FROM ride_roles WHERE name = 'passenger')),
    ((SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'marek.kow@gmail.com')),
     (SELECT id FROM rides WHERE start_latitude = 51.1079 AND end_latitude = 52.4064),
     (SELECT id FROM ride_roles WHERE name = 'passenger'));

INSERT INTO ride_waypoints (latitude, longitude, type, sequence_order, ride_id, passenger_id)
VALUES
    -- Anna
    (52.2551, 21.0358, 'PICKUP',  1,
     (SELECT id FROM rides WHERE start_latitude = 52.2297),
     (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'anna.nowak@gmail.com'))),
    (50.0780, 19.9340, 'DROPOFF', 2,
     (SELECT id FROM rides WHERE start_latitude = 52.2297),
     (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'anna.nowak@gmail.com'))),
    -- Piotr
    (52.2207, 20.9857, 'PICKUP',  3,
     (SELECT id FROM rides WHERE start_latitude = 52.2297),
     (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'piotr.wis@gmail.com'))),
    (50.0614, 19.9374, 'DROPOFF', 4,
     (SELECT id FROM rides WHERE start_latitude = 52.2297),
     (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'piotr.wis@gmail.com'))),
    -- Marek
    (52.2360, 21.0120, 'PICKUP',  5,
     (SELECT id FROM rides WHERE start_latitude = 52.2297),
     (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'marek.kow@gmail.com'))),
    (50.0500, 19.9600, 'DROPOFF', 6,
     (SELECT id FROM rides WHERE start_latitude = 52.2297),
     (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'marek.kow@gmail.com')));

INSERT INTO ride_waypoints (latitude, longitude, type, sequence_order, ride_id, passenger_id)
VALUES
    -- Adam
    (51.1200, 17.0480, 'PICKUP',  1,
     (SELECT id FROM rides WHERE start_latitude = 51.1079),
     (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'adam.kowal@gmail.com'))),
    (52.3900, 16.9100, 'DROPOFF', 2,
     (SELECT id FROM rides WHERE start_latitude = 51.1079),
     (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'adam.kowal@gmail.com'))),
    (51.6100, 17.0700, 'PICKUP',  3,
     (SELECT id FROM rides WHERE start_latitude = 51.1079),
     (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'marek.kow@gmail.com'))),
    (52.4100, 16.9400, 'DROPOFF', 4,
     (SELECT id FROM rides WHERE start_latitude = 51.1079),
     (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'marek.kow@gmail.com')));

INSERT INTO rides (start_latitude, start_longitude, end_latitude, end_longitude, date, cost, is_payed, state_id, available_seats)
VALUES (54.3520, 18.6466, 50.0647, 19.9450, '2026-09-10 07:00:00', 55.00, false,
        (SELECT id FROM ride_states WHERE name = 'not started'), 4);

INSERT INTO ride_participants (user_id, ride_id, role_id) VALUES
    ((SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'tomasz.kos@gmail.com')),
     (SELECT id FROM rides WHERE start_latitude = 54.3520),
     (SELECT id FROM ride_roles WHERE name = 'driver'));

INSERT INTO ride_requests (user_id, ride_id, is_accepted, pickup_latitude, pickup_longitude, dropoff_latitude, dropoff_longitude) VALUES (
    (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'anna.nowak@gmail.com')),
    (SELECT id FROM rides WHERE start_latitude = 54.3520),
    true, 54.0800, 18.6100, 53.0138, 18.5948);

INSERT INTO ride_requests (user_id, ride_id, is_accepted, pickup_latitude, pickup_longitude, dropoff_latitude, dropoff_longitude) VALUES (
    (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'piotr.wis@gmail.com')),
    (SELECT id FROM rides WHERE start_latitude = 54.3520),
    true, 52.6593, 19.0678, 51.7592, 19.4560);

INSERT INTO ride_requests (user_id, ride_id, is_accepted, pickup_latitude, pickup_longitude, dropoff_latitude, dropoff_longitude) VALUES (
    (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'marek.kow@gmail.com')),
    (SELECT id FROM rides WHERE start_latitude = 54.3520),
    true, 52.2282, 19.3634, 50.8112, 19.1189);

INSERT INTO ride_requests (user_id, ride_id, is_accepted, pickup_latitude, pickup_longitude, dropoff_latitude, dropoff_longitude) VALUES (
    (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'adam.kowal@gmail.com')),
    (SELECT id FROM rides WHERE start_latitude = 54.3520),
    true, 51.4057, 19.7024, 50.3576, 19.9688);

INSERT INTO ride_participants (user_id, ride_id, role_id) VALUES
    ((SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'anna.nowak@gmail.com')),
     (SELECT id FROM rides WHERE start_latitude = 54.3520),
     (SELECT id FROM ride_roles WHERE name = 'passenger')),
    ((SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'piotr.wis@gmail.com')),
     (SELECT id FROM rides WHERE start_latitude = 54.3520),
     (SELECT id FROM ride_roles WHERE name = 'passenger')),
    ((SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'marek.kow@gmail.com')),
     (SELECT id FROM rides WHERE start_latitude = 54.3520),
     (SELECT id FROM ride_roles WHERE name = 'passenger')),
    ((SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'adam.kowal@gmail.com')),
     (SELECT id FROM rides WHERE start_latitude = 54.3520),
     (SELECT id FROM ride_roles WHERE name = 'passenger'));

INSERT INTO ride_waypoints (latitude, longitude, type, sequence_order, ride_id, passenger_id)
VALUES
    (54.0800, 18.6100, 'PICKUP',  1,
     (SELECT id FROM rides WHERE start_latitude = 54.3520),
     (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'anna.nowak@gmail.com'))),
    (53.0138, 18.5948, 'DROPOFF', 2,
     (SELECT id FROM rides WHERE start_latitude = 54.3520),
     (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'anna.nowak@gmail.com'))),
    (52.6593, 19.0678, 'PICKUP',  3,
     (SELECT id FROM rides WHERE start_latitude = 54.3520),
     (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'piotr.wis@gmail.com'))),
    (51.7592, 19.4560, 'DROPOFF', 4,
     (SELECT id FROM rides WHERE start_latitude = 54.3520),
     (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'piotr.wis@gmail.com'))),
    (52.2282, 19.3634, 'PICKUP',  5,
     (SELECT id FROM rides WHERE start_latitude = 54.3520),
     (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'marek.kow@gmail.com'))),
    (50.8112, 19.1189, 'DROPOFF', 6,
     (SELECT id FROM rides WHERE start_latitude = 54.3520),
     (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'marek.kow@gmail.com'))),
    (51.4057, 19.7024, 'PICKUP',  7,
     (SELECT id FROM rides WHERE start_latitude = 54.3520),
     (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'adam.kowal@gmail.com'))),
    (50.3576, 19.9688, 'DROPOFF', 8,
     (SELECT id FROM rides WHERE start_latitude = 54.3520),
     (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'adam.kowal@gmail.com')));
