-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    subscription_plan VARCHAR(50) NOT NULL DEFAULT 'FREE',
    queries_used_today INTEGER DEFAULT 0,
    last_query_reset TIMESTAMP,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);

-- Enable PostGIS extension
CREATE EXTENSION IF NOT EXISTS postgis;

-- Create gas_stations table
CREATE TABLE IF NOT EXISTS gas_stations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    location geometry(Point, 4326) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    zip_code VARCHAR(20),
    address VARCHAR(255),
    is_active BOOLEAN DEFAULT true,
    verification_count INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_gas_stations_city ON gas_stations(city);
CREATE INDEX idx_gas_stations_location ON gas_stations USING GIST(location);

-- Create prices table
CREATE TABLE IF NOT EXISTS prices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    station_id UUID NOT NULL REFERENCES gas_stations(id) ON DELETE CASCADE,
    fuel_type VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    reported_by UUID NOT NULL REFERENCES users(id) ON DELETE SET NULL,
    verification_count INTEGER DEFAULT 0,
    reported_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_prices_station_fuel ON prices(station_id, fuel_type);
CREATE INDEX idx_prices_reported_at ON prices(reported_at);

-- Create subscriptions table
CREATE TABLE IF NOT EXISTS subscriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    plan_name VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    starts_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ends_at TIMESTAMP,
    payment_method VARCHAR(50),
    external_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_subscriptions_user_status ON subscriptions(user_id, status);

-- Add some initial gas stations for São Paulo (MVP region)
INSERT INTO gas_stations (name, location, city, state, is_active)
VALUES
    ('Postos BR - Zona Sul', ST_Point(-23.5505, -46.6333, 4326), 'São Paulo', 'SP', true),
    ('Ipiranga - Avenida Paulista', ST_Point(-23.5615, -46.6559, 4326), 'São Paulo', 'SP', true),
    ('Shell - Centro', ST_Point(-23.5505, -46.6361, 4326), 'São Paulo', 'SP', true),
    ('Texaco - Vila Mariana', ST_Point(-23.5902, -46.6449, 4326), 'São Paulo', 'SP', true),
    ('Alesp - Zona Leste', ST_Point(-23.4545, -46.4794, 4326), 'São Paulo', 'SP', true)
ON CONFLICT DO NOTHING;
