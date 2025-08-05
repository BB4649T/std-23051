CREATE TABLE payments (
    id VARCHAR(255) PRIMARY KEY,
    date TIMESTAMP NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    payment_method VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL
);
