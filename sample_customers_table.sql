-- Sample Customers Table with Revenue Data
-- Run this SQL in your Render PostgreSQL database

-- Create customers table
CREATE TABLE IF NOT EXISTS customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    city VARCHAR(50),
    country VARCHAR(50),
    revenue DECIMAL(12, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO customers (name, email, city, country, revenue) VALUES
('John Smith', 'john.smith@email.com', 'New York', 'USA', 125000.00),
('Emma Johnson', 'emma.johnson@email.com', 'London', 'UK', 98000.50),
('Michael Chen', 'michael.chen@email.com', 'Tokyo', 'Japan', 156750.25),
('Sarah Williams', 'sarah.williams@email.com', 'Sydney', 'Australia', 112500.75),
('David Brown', 'david.brown@email.com', 'Toronto', 'Canada', 87500.00),
('Lisa Anderson', 'lisa.anderson@email.com', 'Berlin', 'Germany', 134200.50),
('Robert Taylor', 'robert.taylor@email.com', 'Paris', 'France', 98750.25),
('Jennifer Martinez', 'jennifer.martinez@email.com', 'Madrid', 'Spain', 145600.00),
('James Wilson', 'james.wilson@email.com', 'Mumbai', 'India', 76500.75),
('Maria Garcia', 'maria.garcia@email.com', 'São Paulo', 'Brazil', 118900.50);

-- Verify data
SELECT * FROM customers ORDER BY revenue DESC;

-- Test queries you can try:
-- "show me top 5 customers by revenue"
-- "list all customers from USA"
-- "what is the total revenue of all customers"
-- "show me customers with revenue greater than 100000"
-- "how many customers are there"

