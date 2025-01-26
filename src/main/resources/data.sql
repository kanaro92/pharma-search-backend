-- Sample Users
INSERT INTO users (name, email, password, role, enabled, dtype) VALUES
('Test User', 'test@example.com', '$2a$10$VrTIA/tIGll46bhl6VdUK.Y4FlvbNHLGO9bF49OdKz.DbAYYlj1xi', 'USER', true, 'User');

-- Sample Pharmacies (Paris region)
-- First insert the user data
INSERT INTO users (name, email, password, role, enabled, dtype) VALUES
('Pharmacie de la Tour Eiffel', 'tour.eiffel@pharma.com', '$2a$10$VrTIA/tIGll46bhl6VdUK.Y4FlvbNHLGO9bF49OdKz.DbAYYlj1xi', 'PHARMACIST', true, 'Pharmacy');

-- Then insert the pharmacy-specific data
INSERT INTO pharmacies (id, address, latitude, longitude, opening_hours, phone_number)
SELECT id, '12 Avenue de la Bourdonnais, 75007 Paris', 48.8584, 2.2945, 'Mon-Sat: 9:00-20:00', '+33145555555'
FROM users WHERE email = 'tour.eiffel@pharma.com';

INSERT INTO users (name, email, password, role, enabled, dtype) VALUES
('Pharmacie du Marais', 'marais@pharma.com', '$2a$10$VrTIA/tIGll46bhl6VdUK.Y4FlvbNHLGO9bF49OdKz.DbAYYlj1xi', 'PHARMACIST', true, 'Pharmacy');

INSERT INTO pharmacies (id, address, latitude, longitude, opening_hours, phone_number)
SELECT id, '25 Rue des Archives, 75004 Paris', 48.8589, 2.3567, 'Mon-Sat: 9:00-20:00', '+33144444444'
FROM users WHERE email = 'marais@pharma.com';

INSERT INTO users (name, email, password, role, enabled, dtype) VALUES
('Pharmacie Saint-Germain', 'saint.germain@pharma.com', '$2a$10$VrTIA/tIGll46bhl6VdUK.Y4FlvbNHLGO9bF49OdKz.DbAYYlj1xi', 'PHARMACIST', true, 'Pharmacy');

INSERT INTO pharmacies (id, address, latitude, longitude, opening_hours, phone_number)
SELECT id, '144 Boulevard Saint-Germain, 75006 Paris', 48.8527, 2.3335, 'Mon-Sat: 9:00-20:00', '+33143333333'
FROM users WHERE email = 'saint.germain@pharma.com';

INSERT INTO users (name, email, password, role, enabled, dtype) VALUES
('Pharmacie Montmartre', 'montmartre@pharma.com', '$2a$10$VrTIA/tIGll46bhl6VdUK.Y4FlvbNHLGO9bF49OdKz.DbAYYlj1xi', 'PHARMACIST', true, 'Pharmacy');

INSERT INTO pharmacies (id, address, latitude, longitude, opening_hours, phone_number)
SELECT id, '56 Rue des Abbesses, 75018 Paris', 48.8845, 2.3370, 'Mon-Sat: 9:00-20:00', '+33142222222'
FROM users WHERE email = 'montmartre@pharma.com';

INSERT INTO users (name, email, password, role, enabled, dtype) VALUES
('Pharmacie Bastille', 'bastille@pharma.com', '$2a$10$VrTIA/tIGll46bhl6VdUK.Y4FlvbNHLGO9bF49OdKz.DbAYYlj1xi', 'PHARMACIST', true, 'Pharmacy');

INSERT INTO pharmacies (id, address, latitude, longitude, opening_hours, phone_number)
SELECT id, '5 Place de la Bastille, 75004 Paris', 48.8533, 2.3692, 'Mon-Sat: 9:00-20:00', '+33141111111'
FROM users WHERE email = 'bastille@pharma.com';

-- Sample Pharmacies (Gif-sur-Yvette region)
INSERT INTO users (name, email, password, role, enabled, dtype) VALUES
('Pharmacie de Gif', 'gif@pharma.com', '$2a$10$VrTIA/tIGll46bhl6VdUK.Y4FlvbNHLGO9bF49OdKz.DbAYYlj1xi', 'PHARMACIST', true, 'Pharmacy');

INSERT INTO pharmacies (id, address, latitude, longitude, opening_hours, phone_number)
SELECT id, '12 Route de Chartres, Gif-sur-Yvette', 48.6824, 2.1679, 'Mon-Sat: 9:00-20:00', '+33169999999'
FROM users WHERE email = 'gif@pharma.com';

INSERT INTO users (name, email, password, role, enabled, dtype) VALUES
('Pharmacie du Centre', 'centre@pharma.com', '$2a$10$VrTIA/tIGll46bhl6VdUK.Y4FlvbNHLGO9bF49OdKz.DbAYYlj1xi', 'PHARMACIST', true, 'Pharmacy');

INSERT INTO pharmacies (id, address, latitude, longitude, opening_hours, phone_number)
SELECT id, '2 Place du Marché Neuf, Gif-sur-Yvette', 48.6850, 2.1340, 'Mon-Sat: 9:00-20:00', '+33169888888'
FROM users WHERE email = 'centre@pharma.com';

INSERT INTO users (name, email, password, role, enabled, dtype) VALUES
('Pharmacie de la Vallée', 'vallee@pharma.com', '$2a$10$VrTIA/tIGll46bhl6VdUK.Y4FlvbNHLGO9bF49OdKz.DbAYYlj1xi', 'PHARMACIST', true, 'Pharmacy');

INSERT INTO pharmacies (id, address, latitude, longitude, opening_hours, phone_number)
SELECT id, '15 Avenue du Général Leclerc, Gif-sur-Yvette', 48.6797, 2.1347, 'Mon-Sat: 9:00-20:00', '+33169777777'
FROM users WHERE email = 'vallee@pharma.com';

-- Sample Pharmacies (Les Ulis region)
INSERT INTO users (name, email, password, role, enabled, dtype) VALUES
('Pharmacie des Ulis', 'ulis@pharma.com', '$2a$10$VrTIA/tIGll46bhl6VdUK.Y4FlvbNHLGO9bF49OdKz.DbAYYlj1xi', 'PHARMACIST', true, 'Pharmacy');

INSERT INTO pharmacies (id, address, latitude, longitude, opening_hours, phone_number)
SELECT id, '1 Avenue de Provence, Les Ulis', 48.6803, 2.1689, 'Mon-Sat: 9:00-20:00', '+33169666666'
FROM users WHERE email = 'ulis@pharma.com';

INSERT INTO users (name, email, password, role, enabled, dtype) VALUES
('Pharmacie Centrale des Ulis', 'centrale.ulis@pharma.com', '$2a$10$VrTIA/tIGll46bhl6VdUK.Y4FlvbNHLGO9bF49OdKz.DbAYYlj1xi', 'PHARMACIST', true, 'Pharmacy');

INSERT INTO pharmacies (id, address, latitude, longitude, opening_hours, phone_number)
SELECT id, '5 Avenue des Champs Lasniers, Les Ulis', 48.6789, 2.1677, 'Mon-Sat: 9:00-20:00', '+33169555555'
FROM users WHERE email = 'centrale.ulis@pharma.com';

-- Sample Medication Inquiries
INSERT INTO medication_inquiries (medication_name, patient_note, status, created_at, user_id)
SELECT 'Aspirin', 'Need information about dosage', 'PENDING', CURRENT_TIMESTAMP, id
FROM users WHERE email = 'test@example.com';

-- Sample Medication Requests
INSERT INTO medication_requests (medication_name, quantity, note, status, created_at, updated_at, user_id, pharmacy_id)
WITH user_data AS (
    SELECT id FROM users WHERE email = 'test@example.com'
),
pharmacy_data AS (
    SELECT p.id FROM pharmacies p 
    JOIN users u ON p.id = u.id 
    WHERE u.email = 'centrale.ulis@pharma.com'
)
SELECT 'Paracetamol', 2, 'Urgent request', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,
       (SELECT id FROM user_data),
       (SELECT id FROM pharmacy_data);
