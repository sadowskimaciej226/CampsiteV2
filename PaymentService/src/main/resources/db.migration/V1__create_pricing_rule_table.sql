CREATE TABLE pricing_rule (
                              id BIGINT PRIMARY KEY,
                              rule VARCHAR(255) NOT NULL,
                              season_type VARCHAR(255) NOT NULL,
                              price DECIMAL(19,2) NOT NULL,
                              valid_from DATE NOT NULL,
                              valid_to DATE,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              UNIQUE KEY unique_rule_season_period (rule, season_type, valid_from)
);
