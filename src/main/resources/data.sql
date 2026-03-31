
INSERT INTO cities (name) VALUES
    ('Warsaw'),
    ('Krakow'),
    ('Gdansk'),
    ('Wroclaw'),
    ('Poznan');


INSERT INTO admins (login, hashed_password, is_super_admin) VALUES
    ('admin', '$2a$12$DC.3P/V/TzlawOuBdqZyvu0THkKTQXS1vCM2h510MX1FNa4DP4H7a', true);
-- password123
INSERT INTO users (is_driver, email, hashed_password, is_blocked, is_woman, name, phone_number, surname, city_id) VALUES
    (false, 'john.doe@gmail.com',   '$2a$12$EdFdaVfFKtMQ5AJ4w7v.6Os0NKtkFL8e.9xNHMJqqXMdarxgW2cdu', false, false, 'John',  '500100200', 'Doe',    1),
    (false, 'jane.smith@gmail.com', '$2a$12$EdFdaVfFKtMQ5AJ4w7v.6Os0NKtkFL8e.9xNHMJqqXMdarxgW2cdu', false, true,  'Jane',  '500100201', 'Smith',  2),
    (false, 'adam.kowal@gmail.com', '$2a$12$EdFdaVfFKtMQ5AJ4w7v.6Os0NKtkFL8e.9xNHMJqqXMdarxgW2cdu', false, false, 'Adam',  '500100202', 'Kowal',  3),
    (false, 'anna.nowak@gmail.com', '$2a$12$EdFdaVfFKtMQ5AJ4w7v.6Os0NKtkFL8e.9xNHMJqqXMdarxgW2cdu', false, true,  'Anna',  '500100203', 'Nowak',  1),
    (false, 'piotr.wis@gmail.com',  '$2a$12$EdFdaVfFKtMQ5AJ4w7v.6Os0NKtkFL8e.9xNHMJqqXMdarxgW2cdu', false, false, 'Piotr', '500100204', 'Wis',    4),
    (false, 'admin@carpooling.com', '$2a$12$EdFdaVfFKtMQ5AJ4w7v.6Os0NKtkFL8e.9xNHMJqqXMdarxgW2cdu', false, false, 'Admin', '500100205', 'System', 1);