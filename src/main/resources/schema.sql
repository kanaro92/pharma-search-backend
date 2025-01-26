DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS medication_stocks CASCADE;
DROP TABLE IF EXISTS medication_requests CASCADE;
DROP TABLE IF EXISTS medication_inquiries CASCADE;
DROP TABLE IF EXISTS pharmacies CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP SEQUENCE IF EXISTS user_id_seq;

CREATE SEQUENCE user_id_seq;

CREATE TABLE users (
    id BIGINT PRIMARY KEY DEFAULT nextval('user_id_seq'),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    dtype VARCHAR(31) NOT NULL
);

CREATE TABLE pharmacies (
    id BIGINT PRIMARY KEY REFERENCES users(id),
    address VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    opening_hours VARCHAR(255),
    phone_number VARCHAR(50)
);

CREATE TABLE medication_inquiries (
    id BIGSERIAL PRIMARY KEY,
    medication_name VARCHAR(255) NOT NULL,
    patient_note TEXT,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE medication_requests (
    id BIGSERIAL PRIMARY KEY,
    medication_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    note TEXT,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    pharmacy_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (pharmacy_id) REFERENCES pharmacies(id)
);

CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    request_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    FOREIGN KEY (request_id) REFERENCES medication_requests(id),
    FOREIGN KEY (sender_id) REFERENCES users(id)
);

CREATE TABLE medication_stocks (
    id BIGSERIAL PRIMARY KEY,
    pharmacy_id BIGINT NOT NULL,
    medication_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(10,2),
    last_updated TIMESTAMP NOT NULL,
    FOREIGN KEY (pharmacy_id) REFERENCES pharmacies(id)
);
