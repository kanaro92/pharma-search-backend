-- Drop existing tables in reverse order of dependencies
DROP TABLE IF EXISTS request_messages CASCADE;
DROP TABLE IF EXISTS inquiry_messages CASCADE;
DROP TABLE IF EXISTS medication_stocks CASCADE;
DROP TABLE IF EXISTS medication_requests CASCADE;
DROP TABLE IF EXISTS medication_inquiries CASCADE;
DROP TABLE IF EXISTS medications CASCADE;
DROP TABLE IF EXISTS pharmacies CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP SEQUENCE IF EXISTS user_id_seq;

-- Create users table
CREATE SEQUENCE user_id_seq;

CREATE TABLE users (
    id BIGINT PRIMARY KEY DEFAULT nextval('user_id_seq'),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    dtype VARCHAR(31) NOT NULL
);

-- Create pharmacies table
CREATE TABLE pharmacies (
    id BIGINT PRIMARY KEY REFERENCES users(id),
    address TEXT NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    opening_hours TEXT,
    phone_number VARCHAR(20)
);

-- Create medications table
CREATE TABLE medications (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    manufacturer VARCHAR(255),
    dosage VARCHAR(100),
    prescription_required BOOLEAN NOT NULL DEFAULT FALSE
);

-- Create medication stocks table
CREATE TABLE medication_stocks (
    id BIGSERIAL PRIMARY KEY,
    pharmacy_id BIGINT NOT NULL,
    medication_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(10,2),
    batch_number VARCHAR(100),
    expiry_date DATE,
    FOREIGN KEY (pharmacy_id) REFERENCES pharmacies(id),
    FOREIGN KEY (medication_id) REFERENCES medications(id)
);

-- Create medication inquiries table
CREATE TABLE medication_inquiries (
    id BIGSERIAL PRIMARY KEY,
    medication_name VARCHAR(255) NOT NULL,
    patient_note TEXT,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    responding_pharmacy_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (responding_pharmacy_id) REFERENCES users(id)
);

-- Create medication requests table
CREATE TABLE medication_requests (
    id BIGSERIAL PRIMARY KEY,
    medication_name VARCHAR(255) NOT NULL,
    quantity INTEGER,
    note TEXT,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    pharmacy_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (pharmacy_id) REFERENCES pharmacies(id)
);

-- Create inquiry messages table
CREATE TABLE inquiry_messages (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    inquiry_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    FOREIGN KEY (inquiry_id) REFERENCES medication_inquiries(id),
    FOREIGN KEY (sender_id) REFERENCES users(id)
);

-- Create request messages table
CREATE TABLE request_messages (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    request_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    FOREIGN KEY (request_id) REFERENCES medication_requests(id),
    FOREIGN KEY (sender_id) REFERENCES users(id)
);
