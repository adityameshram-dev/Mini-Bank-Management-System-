-- Active: 1773502807532@@localhost@3306@bank
CREATE DATABASE bank;
USE bank;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100),
    address VARCHAR(200),
    phone VARCHAR(10),
    username VARCHAR(50) UNIQUE,
    password VARCHAR(50),
    account_number BIGINT UNIQUE,
    balance DOUBLE DEFAULT 0
);

CREATE TABLE transactions (
    txn_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_account BIGINT,
    receiver_account BIGINT,
    amount DOUBLE,
    type VARCHAR(20),
    txn_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
