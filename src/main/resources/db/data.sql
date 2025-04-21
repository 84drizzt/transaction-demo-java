-- Clean up data
DELETE FROM transactions;
DELETE FROM accounts;
DELETE FROM users;

-- Reset auto-increment ID
ALTER TABLE transactions ALTER COLUMN id RESTART WITH 1;
ALTER TABLE accounts ALTER COLUMN id RESTART WITH 1;
ALTER TABLE users ALTER COLUMN id RESTART WITH 1;


-- Insert sample users
INSERT INTO users (username, password, full_name, email, phone_number, created_at, updated_at) VALUES
('alice_jones', '$2a$10$newHash123', 'Alice Jones', 'alice.jones@example.com', '+1098765432', '2025-04-22 08:00:00', '2025-04-22 08:00:00'),
('bob_davis', '$2a$10$newHash123', 'Bob Davis', 'bob.davis@example.com', '+1123456789', '2025-04-23 09:15:00', '2025-04-23 09:15:00'),
('john_doe', '$2a$10$newHash123', 'John Doe', 'john.doe@example.com', '+1234567890', '2025-04-21 09:30:00', '2025-04-21 09:30:00'),
('jane_smith', '$2a$10$newHash123', 'Jane Smith', 'jane.smith@example.com', '+1987654321', '2025-04-16 10:15:00', '2025-04-16 10:15:00'),
('robert_johnson', '$2a$10$newHash123', 'Robert Johnson', 'robert.j@example.com', '+1122334455', '2025-04-17 11:20:00', '2025-04-17 11:20:00'),
('sarah_williams', '$2a$10$newHash123', 'Sarah Williams', 'sarah.w@example.com', '+1555666777', '2025-04-18 14:45:00', '2025-04-18 14:45:00'),
('michael_brown', '$2a$10$newHash123', 'Michael Brown', 'michael.b@example.com', '+1888999000', '2025-04-19 16:30:00', '2025-04-19 16:30:00');


-- Insert sample accounts
INSERT INTO accounts (account_number, user_id, balance, currency, created_at, updated_at) VALUES
('ACC0000001', 1, 15000.0000, 'CNY', '2025-04-21 10:00:00', '2025-04-21 10:00:00'),
('ACC0000002', 2, 87500.0000, 'CNY',  '2025-04-16 11:00:00', '2025-04-16 11:00:00'),
('ACC0000003', 3, 30000.0000, 'CNY', '2025-04-17 12:00:00', '2025-04-17 12:00:00'),
('ACC0000004', 4, 12000.0000, 'CNY', '2025-04-18 15:00:00', '2025-04-18 15:00:00'),
('ACC0000005', 5, 8500.0000, 'CNY', '2025-04-19 17:00:00', '2025-04-19 17:00:00'),
('ACC0000006', 6, 10000.0000, 'CNY', '2025-04-21 10:05:00', '2025-04-21 10:05:00'),
('ACC0000007', 7, 50000.0000, 'CNY', '2025-04-21 10:05:00', '2025-04-21 10:05:00'),
('ACC0000008', 8, 10000.0000, 'CNY', '2025-04-21 10:05:00', '2025-04-21 10:05:00'),
('ACC0000009', 9, 10000.0000, 'CNY', '2025-04-21 10:05:00', '2025-04-21 10:05:00'),
('ACC00000010', 10, 1000.0000, 'CNY', '2025-04-21 10:05:00', '2025-04-21 10:05:00'),
('ACC00000011', 11, 5000.0000, 'CNY', '2025-04-21 10:05:00', '2025-04-21 10:05:00');


-- Insert sample transactions
INSERT INTO transactions (transaction_number, account_id, type, amount, balance_before, balance_after, description, reference_number, related_account_id, transaction_time, deleted, created_at, updated_at) VALUES
('TXN100000001', 1, 'DEPOSIT', 5000.0000, 0.0000, 5000.0000, 'Initial deposit', '', NULL, '2025-04-21 10:00:00', 0, '2025-04-21 10:00:00', '2025-04-21 10:00:00'),
('TXN100000002', 2, 'DEPOSIT', 10000.0000, 0.0000, 10000.0000, 'Initial deposit', '', NULL, '2025-04-21 10:05:00', 0, '2025-04-21 10:05:00', '2025-04-21 10:05:00'),
('TXN100000003', 3, 'DEPOSIT', 7500.0000, 0.0000, 7500.0000, 'Initial deposit', '', NULL, '2025-04-16 11:00:00', 0, '2025-04-16 11:00:00', '2025-04-16 11:00:00'),
('TXN100000004', 1, 'WITHDRAW', 500.0000, 5000.0000, 4500.0000, 'ATM withdrawal', '', NULL, '2025-04-16 14:30:00', 0, '2025-04-16 14:30:00', '2025-04-16 14:30:00'),
('TXN100000005', 1, 'TRANSFER', 1000.0000, 4500.0000, 3500.0000, 'Transfer to user 2', '', 2, '2025-04-17 09:15:00', 0, '2025-04-17 09:15:00', '2025-04-17 09:15:00'),
('TXN100000006', 2, 'TRANSFER', 1000.0000, 10000.0000, 11000.0000, 'Transfer to user 3', '', 3, '2025-04-17 09:15:00', 0, '2025-04-17 09:15:00', '2025-04-17 09:15:00'),
('TXN100000007', 4, 'DEPOSIT', 12000.0000, 0.0000, 12000.0000, 'Initial deposit', '', NULL, '2025-04-18 15:00:00', 0, '2025-04-18 15:00:00', '2025-04-18 15:00:00'),
('TXN100000008', 5, 'DEPOSIT', 8500.0000, 0.0000, 8500.0000, 'Initial deposit', '', NULL, '2025-04-19 17:00:00', 0, '2025-04-19 17:00:00', '2025-04-19 17:00:00'),
('TXN100000010', 1, 'DEPOSIT', 2000.0000, 3500.0000, 5500.0000, 'Paycheck deposit', '', NULL, '2025-04-21 08:00:00', 0, '2025-04-21 08:00:00', '2025-04-21 08:00:00'),
('TXN100000011', 6, 'WITHDRAW', 500.0000, 10500.0000, 10000.0000, 'ATM withdrawal', '', NULL, '2025-04-16 14:30:00', 0, '2025-04-16 14:30:00', '2025-04-16 14:30:00');