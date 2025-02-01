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
SELECT id, '5 Place de la Bastille, 75004 Paris', 48.8533, 2.3691, 'Mon-Sat: 9:00-20:00', '+33141111111'
FROM users WHERE email = 'bastille@pharma.com';

INSERT INTO users (name, email, password, role, enabled, dtype) VALUES
('Pharmacie Centrale Les Ulis', 'centrale.ulis@pharma.com', '$2a$10$VrTIA/tIGll46bhl6VdUK.Y4FlvbNHLGO9bF49OdKz.DbAYYlj1xi', 'PHARMACIST', true, 'Pharmacy');

INSERT INTO pharmacies (id, address, latitude, longitude, opening_hours, phone_number)
SELECT id, '5 Avenue des Champs Lasniers, Les Ulis', 48.6789, 2.1677, 'Mon-Sat: 9:00-20:00', '+33169555555'
FROM users WHERE email = 'centrale.ulis@pharma.com';

-- Sample Medications
INSERT INTO medications (name, description, manufacturer, dosage, prescription_required) VALUES
('Paracetamol', 'Pain reliever and fever reducer', 'Generic Pharma', '500mg', false),
('Ibuprofen', 'Anti-inflammatory pain reliever', 'Generic Pharma', '400mg', false),
('Amoxicillin', 'Antibiotic for bacterial infections', 'Generic Pharma', '500mg', true),
('Omeprazole', 'Proton pump inhibitor for acid reflux', 'Generic Pharma', '20mg', true),
('Loratadine', 'Antihistamine for allergies', 'Generic Pharma', '10mg', false);

-- Sample Medication Stocks
INSERT INTO medication_stocks (pharmacy_id, medication_id, quantity, price, batch_number, expiry_date)
SELECT p.id, m.id, 100, 5.99, 'BATCH001', '2025-12-31'
FROM pharmacies p, medications m 
WHERE p.id = (SELECT id FROM users WHERE email = 'tour.eiffel@pharma.com')
AND m.name = 'Paracetamol';

INSERT INTO medication_stocks (pharmacy_id, medication_id, quantity, price, batch_number, expiry_date)
SELECT p.id, m.id, 50, 7.99, 'BATCH002', '2025-12-31'
FROM pharmacies p, medications m 
WHERE p.id = (SELECT id FROM users WHERE email = 'tour.eiffel@pharma.com')
AND m.name = 'Ibuprofen';

-- Sample Medication Inquiries
INSERT INTO medication_inquiries (medication_name, patient_note, status, created_at, updated_at, user_id)
SELECT 
    'Paracetamol',
    'I need information about the proper dosage for my child who is 8 years old.',
    'PENDING',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    id
FROM users 
WHERE email = 'test@example.com';

INSERT INTO medication_inquiries (medication_name, patient_note, status, created_at, updated_at, user_id)
SELECT 
    'Ibuprofen',
    'Can this be taken together with paracetamol?',
    'PENDING',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    id
FROM users 
WHERE email = 'test@example.com';

-- Sample Inquiry Messages
INSERT INTO inquiry_messages (content, created_at, inquiry_id, sender_id)
SELECT 
    'Hello, I need help with this medication.',
    CURRENT_TIMESTAMP,
    i.id,
    u.id
FROM medication_inquiries i
CROSS JOIN users u
WHERE u.email = 'test@example.com'
AND i.medication_name = 'Paracetamol';
