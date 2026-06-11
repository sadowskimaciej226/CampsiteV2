CREATE TABLE accommodation_payment (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       reservation_id VARCHAR(36) NOT NULL UNIQUE,
                                       sector VARCHAR(255) NOT NULL,
                                       amount_of_people INT NOT NULL CHECK (amount_of_people >= 0),
                                       accommodation_type VARCHAR(255),
                                       electricity_connected BOOLEAN DEFAULT FALSE,
                                       arrived_at DATE,
                                       departed_at DATE,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_accommodation_payment_reservation_id ON accommodation_payment(reservation_id);
CREATE INDEX idx_accommodation_payment_sector ON accommodation_payment(sector);
CREATE INDEX idx_accommodation_payment_arrival_date ON accommodation_payment(arrived_at);
