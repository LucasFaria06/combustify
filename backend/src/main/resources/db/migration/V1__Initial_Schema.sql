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

-- Create gas_stations table
CREATE TABLE IF NOT EXISTS gas_stations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
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
CREATE INDEX idx_gas_stations_coordinates ON gas_stations(latitude, longitude);

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

-- Add some initial gas stations for Cuiabá (MVP region)
INSERT INTO gas_stations (name, latitude, longitude, city, state, is_active)
VALUES
    ('Postos BR - Centro', -15.5939, -56.0982, 'Cuiabá', 'MT', true),
    ('Ipiranga - Lagoinha', -15.5845, -56.0756, 'Cuiabá', 'MT', true),
    ('Shell - Barão de Melgaço', -15.6162, -56.1084, 'Cuiabá', 'MT', true),
    ('Texaco - Av. Getúlio Vargas', -15.5827, -56.0921, 'Cuiabá', 'MT', true),
    ('Alesp - Centro', -15.5925, -56.0998, 'Cuiabá', 'MT', true)
ON CONFLICT DO NOTHING;
