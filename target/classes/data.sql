ALTER TABLE ride_requests ALTER COLUMN is_accepted DROP NOT NULL;

ALTER TABLE ride_requests   DROP CONSTRAINT IF EXISTS fk41cbu7ct1o25egxpt4wk9uf0t;
ALTER TABLE ride_waypoints  DROP CONSTRAINT IF EXISTS fk23gkm4nibuvgxhf1ut56ukogl;
ALTER TABLE ride_waypoints    DROP CONSTRAINT IF EXISTS fk_ride_waypoints_passenger;
ALTER TABLE ride_waypoints    ADD  CONSTRAINT fk_ride_waypoints_passenger  FOREIGN KEY (passenger_id) REFERENCES user_profiles(id);
ALTER TABLE ride_participants DROP CONSTRAINT IF EXISTS fkg2tw36nkhf97l5rfdx52qisl9;
ALTER TABLE ride_participants DROP CONSTRAINT IF EXISTS fk_ride_participants_user;
ALTER TABLE ride_participants ADD  CONSTRAINT fk_ride_participants_user     FOREIGN KEY (user_id)     REFERENCES user_profiles(id);
ALTER TABLE ride_requests DROP CONSTRAINT IF EXISTS fk_ride_requests_user_profile;
ALTER TABLE ride_requests ADD CONSTRAINT fk_ride_requests_user_profile FOREIGN KEY (user_id) REFERENCES user_profiles(id);

INSERT INTO cities (name) SELECT 'Warsaw'  WHERE NOT EXISTS (SELECT 1 FROM cities WHERE name = 'Warsaw');
INSERT INTO cities (name) SELECT 'Krakow'  WHERE NOT EXISTS (SELECT 1 FROM cities WHERE name = 'Krakow');
INSERT INTO cities (name) SELECT 'Gdansk'  WHERE NOT EXISTS (SELECT 1 FROM cities WHERE name = 'Gdansk');
INSERT INTO cities (name) SELECT 'Wroclaw' WHERE NOT EXISTS (SELECT 1 FROM cities WHERE name = 'Wroclaw');
INSERT INTO cities (name) SELECT 'Poznan'  WHERE NOT EXISTS (SELECT 1 FROM cities WHERE name = 'Poznan');

INSERT INTO user_credentials (email, hashed_password, is_blocked, role)
SELECT 'john.doe@gmail.com', '$2a$12$5.c0/T.Tp2/qaTsfPALLFeZpU5EM2dvXzKtW2m8H2nh3wv1pRsIqW', false, 'USER'
    WHERE NOT EXISTS (SELECT 1 FROM user_credentials WHERE email = 'john.doe@gmail.com');

INSERT INTO user_credentials (email, hashed_password, is_blocked, role)
SELECT 'jane.smith@gmail.com', '$2a$12$5.c0/T.Tp2/qaTsfPALLFeZpU5EM2dvXzKtW2m8H2nh3wv1pRsIqW', false, 'USER'
    WHERE NOT EXISTS (SELECT 1 FROM user_credentials WHERE email = 'jane.smith@gmail.com');

INSERT INTO user_credentials (email, hashed_password, is_blocked, role)
SELECT 'adam.kowal@gmail.com', '$2a$12$5.c0/T.Tp2/qaTsfPALLFeZpU5EM2dvXzKtW2m8H2nh3wv1pRsIqW', false, 'USER'
    WHERE NOT EXISTS (SELECT 1 FROM user_credentials WHERE email = 'adam.kowal@gmail.com');

INSERT INTO user_credentials (email, hashed_password, is_blocked, role)
SELECT 'anna.nowak@gmail.com', '$2a$12$5.c0/T.Tp2/qaTsfPALLFeZpU5EM2dvXzKtW2m8H2nh3wv1pRsIqW', false, 'USER'
    WHERE NOT EXISTS (SELECT 1 FROM user_credentials WHERE email = 'anna.nowak@gmail.com');

INSERT INTO user_credentials (email, hashed_password, is_blocked, role)
SELECT 'piotr.wis@gmail.com', '$2a$12$5.c0/T.Tp2/qaTsfPALLFeZpU5EM2dvXzKtW2m8H2nh3wv1pRsIqW', false, 'USER'
    WHERE NOT EXISTS (SELECT 1 FROM user_credentials WHERE email = 'piotr.wis@gmail.com');

INSERT INTO user_credentials (email, hashed_password, is_blocked, role)
SELECT 'admin@carpooling.com', '$2a$12$5.c0/T.Tp2/qaTsfPALLFeZpU5EM2dvXzKtW2m8H2nh3wv1pRsIqW', false, 'SUPER_ADMIN'
    WHERE NOT EXISTS (SELECT 1 FROM user_credentials WHERE email = 'admin@carpooling.com');

INSERT INTO user_profiles (credential_id, name, surname, phone_number, is_woman, is_driver, city_id)
SELECT (SELECT id FROM user_credentials WHERE email = 'john.doe@gmail.com'), 'John', 'Doe', '500100200', false, true, (SELECT id FROM cities WHERE name = 'Warsaw')
    WHERE NOT EXISTS (SELECT 1 FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'john.doe@gmail.com'));

INSERT INTO user_profiles (credential_id, name, surname, phone_number, is_woman, is_driver, city_id)
SELECT (SELECT id FROM user_credentials WHERE email = 'jane.smith@gmail.com'), 'Jane', 'Smith', '500100201', true, true, (SELECT id FROM cities WHERE name = 'Wroclaw')
    WHERE NOT EXISTS (SELECT 1 FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'jane.smith@gmail.com'));

INSERT INTO user_profiles (credential_id, name, surname, phone_number, is_woman, is_driver, city_id)
SELECT (SELECT id FROM user_credentials WHERE email = 'adam.kowal@gmail.com'), 'Adam', 'Kowal', '500100202', false, false, (SELECT id FROM cities WHERE name = 'Gdansk')
    WHERE NOT EXISTS (SELECT 1 FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'adam.kowal@gmail.com'));

INSERT INTO user_profiles (credential_id, name, surname, phone_number, is_woman, is_driver, city_id)
SELECT (SELECT id FROM user_credentials WHERE email = 'anna.nowak@gmail.com'), 'Anna', 'Nowak', '500100203', true, false, (SELECT id FROM cities WHERE name = 'Warsaw')
    WHERE NOT EXISTS (SELECT 1 FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'anna.nowak@gmail.com'));

INSERT INTO user_profiles (credential_id, name, surname, phone_number, is_woman, is_driver, city_id)
SELECT (SELECT id FROM user_credentials WHERE email = 'piotr.wis@gmail.com'), 'Piotr', 'Wis', '500100204', false, false, (SELECT id FROM cities WHERE name = 'Wroclaw')
    WHERE NOT EXISTS (SELECT 1 FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'piotr.wis@gmail.com'));

INSERT INTO user_profiles (credential_id, name, surname, phone_number, is_woman, is_driver, city_id)
SELECT (SELECT id FROM user_credentials WHERE email = 'admin@carpooling.com'), 'Admin', 'System', '500100205', false, false, (SELECT id FROM cities WHERE name = 'Warsaw')
    WHERE NOT EXISTS (SELECT 1 FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'admin@carpooling.com'));

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
SELECT 'Toyota Corolla', 4, 15000, 'Silver', (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'john.doe@gmail.com'))
    WHERE NOT EXISTS (SELECT 1 FROM car_details WHERE user_id = (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'john.doe@gmail.com')));

INSERT INTO car_details (model, seat_count, mileage, color, user_id)
SELECT 'Honda Civic', 4, 28500, 'Blue', (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'jane.smith@gmail.com'))
    WHERE NOT EXISTS (SELECT 1 FROM car_details WHERE user_id = (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'jane.smith@gmail.com')));

INSERT INTO rides (start_latitude, start_longitude, end_latitude, end_longitude, date, cost, is_payed, state_id)
SELECT 52.2297, 21.0122, 50.0647, 19.9450, '2026-05-06 08:00:00', 35.00, false, (SELECT id FROM ride_states WHERE name = 'not started')
    WHERE NOT EXISTS (SELECT 1 FROM rides WHERE start_latitude = 52.2297 AND end_latitude = 50.0647);

INSERT INTO rides (start_latitude, start_longitude, end_latitude, end_longitude, date, cost, is_payed, state_id)
SELECT 51.1079, 17.0385, 52.4064, 16.9252, '2026-05-07 10:00:00', 25.00, false, (SELECT id FROM ride_states WHERE name = 'not started')
    WHERE NOT EXISTS (SELECT 1 FROM rides WHERE start_latitude = 51.1079 AND end_latitude = 52.4064);

INSERT INTO ride_participants (user_id, ride_id, role_id)
SELECT (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'john.doe@gmail.com')), (SELECT id FROM rides WHERE start_latitude = 52.2297), (SELECT id FROM ride_roles WHERE name = 'driver')
    WHERE NOT EXISTS (SELECT 1 FROM ride_participants WHERE user_id = (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'john.doe@gmail.com')) AND ride_id = (SELECT id FROM rides WHERE start_latitude = 52.2297));

INSERT INTO ride_participants (user_id, ride_id, role_id)
SELECT (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'jane.smith@gmail.com')), (SELECT id FROM rides WHERE start_latitude = 51.1079), (SELECT id FROM ride_roles WHERE name = 'driver')
    WHERE NOT EXISTS (SELECT 1 FROM ride_participants WHERE user_id = (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'jane.smith@gmail.com')) AND ride_id = (SELECT id FROM rides WHERE start_latitude = 51.1079));

INSERT INTO ride_requests (user_id, ride_id, is_accepted, pickup_latitude, pickup_longitude, dropoff_latitude, dropoff_longitude)
SELECT (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'piotr.wis@gmail.com')), (SELECT id FROM rides WHERE start_latitude = 52.2297), true, 52.2207, 20.9857, 50.0614, 19.9374
    WHERE NOT EXISTS (SELECT 1 FROM ride_requests WHERE user_id = (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'piotr.wis@gmail.com')) AND ride_id = (SELECT id FROM rides WHERE start_latitude = 52.2297));

INSERT INTO ride_requests (user_id, ride_id, is_accepted, pickup_latitude, pickup_longitude, dropoff_latitude, dropoff_longitude)
SELECT (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'anna.nowak@gmail.com')), (SELECT id FROM rides WHERE start_latitude = 52.2297), true, 52.2551, 21.0358, 50.0780, 19.9340
    WHERE NOT EXISTS (SELECT 1 FROM ride_requests WHERE user_id = (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'anna.nowak@gmail.com')) AND ride_id = (SELECT id FROM rides WHERE start_latitude = 52.2297));

INSERT INTO ride_requests (user_id, ride_id, is_accepted, pickup_latitude, pickup_longitude, dropoff_latitude, dropoff_longitude)
SELECT (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'adam.kowal@gmail.com')), (SELECT id FROM rides WHERE start_latitude = 51.1079), true, 51.1200, 17.0480, 52.3900, 16.9100
    WHERE NOT EXISTS (SELECT 1 FROM ride_requests WHERE user_id = (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'adam.kowal@gmail.com')) AND ride_id = (SELECT id FROM rides WHERE start_latitude = 51.1079));

INSERT INTO ride_participants (user_id, ride_id, role_id)
SELECT (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'piotr.wis@gmail.com')), (SELECT id FROM rides WHERE start_latitude = 52.2297), (SELECT id FROM ride_roles WHERE name = 'passenger')
    WHERE NOT EXISTS (SELECT 1 FROM ride_participants WHERE user_id = (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'piotr.wis@gmail.com')) AND ride_id = (SELECT id FROM rides WHERE start_latitude = 52.2297));

INSERT INTO ride_participants (user_id, ride_id, role_id)
SELECT (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'anna.nowak@gmail.com')), (SELECT id FROM rides WHERE start_latitude = 52.2297), (SELECT id FROM ride_roles WHERE name = 'passenger')
    WHERE NOT EXISTS (SELECT 1 FROM ride_participants WHERE user_id = (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'anna.nowak@gmail.com')) AND ride_id = (SELECT id FROM rides WHERE start_latitude = 52.2297));

INSERT INTO ride_participants (user_id, ride_id, role_id)
SELECT (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'adam.kowal@gmail.com')), (SELECT id FROM rides WHERE start_latitude = 51.1079), (SELECT id FROM ride_roles WHERE name = 'passenger')
    WHERE NOT EXISTS (SELECT 1 FROM ride_participants WHERE user_id = (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'adam.kowal@gmail.com')) AND ride_id = (SELECT id FROM rides WHERE start_latitude = 51.1079));

INSERT INTO ride_waypoints (latitude, longitude, type, sequence_order, ride_id, passenger_id)
SELECT 52.2207, 20.9857, 'PICKUP', 1, (SELECT id FROM rides WHERE start_latitude = 52.2297), (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'piotr.wis@gmail.com'))
    WHERE NOT EXISTS (SELECT 1 FROM ride_waypoints WHERE ride_id = (SELECT id FROM rides WHERE start_latitude = 52.2297) AND passenger_id = (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'piotr.wis@gmail.com')) AND type = 'PICKUP');

INSERT INTO ride_waypoints (latitude, longitude, type, sequence_order, ride_id, passenger_id)
SELECT 52.2551, 21.0358, 'PICKUP', 2, (SELECT id FROM rides WHERE start_latitude = 52.2297), (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'anna.nowak@gmail.com'))
    WHERE NOT EXISTS (SELECT 1 FROM ride_waypoints WHERE ride_id = (SELECT id FROM rides WHERE start_latitude = 52.2297) AND passenger_id = (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'anna.nowak@gmail.com')) AND type = 'PICKUP');

INSERT INTO ride_waypoints (latitude, longitude, type, sequence_order, ride_id, passenger_id)
SELECT 50.0780, 19.9340, 'DROPOFF', 3, (SELECT id FROM rides WHERE start_latitude = 52.2297), (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'anna.nowak@gmail.com'))
    WHERE NOT EXISTS (SELECT 1 FROM ride_waypoints WHERE ride_id = (SELECT id FROM rides WHERE start_latitude = 52.2297) AND passenger_id = (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'anna.nowak@gmail.com')) AND type = 'DROPOFF');

INSERT INTO ride_waypoints (latitude, longitude, type, sequence_order, ride_id, passenger_id)
SELECT 50.0614, 19.9374, 'DROPOFF', 4, (SELECT id FROM rides WHERE start_latitude = 52.2297), (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'piotr.wis@gmail.com'))
    WHERE NOT EXISTS (SELECT 1 FROM ride_waypoints WHERE ride_id = (SELECT id FROM rides WHERE start_latitude = 52.2297) AND passenger_id = (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'piotr.wis@gmail.com')) AND type = 'DROPOFF');

INSERT INTO ride_waypoints (latitude, longitude, type, sequence_order, ride_id, passenger_id)
SELECT 51.1200, 17.0480, 'PICKUP', 1, (SELECT id FROM rides WHERE start_latitude = 51.1079), (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'adam.kowal@gmail.com'))
    WHERE NOT EXISTS (SELECT 1 FROM ride_waypoints WHERE ride_id = (SELECT id FROM rides WHERE start_latitude = 51.1079) AND passenger_id = (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'adam.kowal@gmail.com')) AND type = 'PICKUP');

INSERT INTO ride_waypoints (latitude, longitude, type, sequence_order, ride_id, passenger_id)
SELECT 52.3900, 16.9100, 'DROPOFF', 2, (SELECT id FROM rides WHERE start_latitude = 51.1079), (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'adam.kowal@gmail.com'))
    WHERE NOT EXISTS (SELECT 1 FROM ride_waypoints WHERE ride_id = (SELECT id FROM rides WHERE start_latitude = 51.1079) AND passenger_id = (SELECT id FROM user_profiles WHERE credential_id = (SELECT id FROM user_credentials WHERE email = 'adam.kowal@gmail.com')) AND type = 'DROPOFF');
