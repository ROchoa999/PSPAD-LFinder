-- Create database
CREATE DATABASE IF NOT EXISTS project_finder;
USE project_finder;
 
-- Create users table
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,    
    password VARCHAR(255) NOT NULL,
    profile_picture MEDIUMTEXT DEFAULT 'default.png',
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
 
-- Create actions table (likes/rejections) with boolean
CREATE TABLE IF NOT EXISTS actions (
    action_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_user_id INT,
    receiver_user_id INT,
    is_like BOOLEAN NOT NULL,
    action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,    
    FOREIGN KEY (sender_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CHECK (sender_user_id != receiver_user_id),
    UNIQUE (sender_user_id, receiver_user_id)
);
 
-- Create matches table
CREATE TABLE IF NOT EXISTS matches (
    match_id INT AUTO_INCREMENT PRIMARY KEY,
    user_1_id INT,
    user_2_id INT,
    match_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notified_user1 bit(1) NOT NULL,
    notified_user2 bit(1) NOT NULL,
    FOREIGN KEY (user_1_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (user_2_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE (user_1_id, user_2_id) -- Unique match between two users
);
 
-- Create messages table
CREATE TABLE IF NOT EXISTS messages (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    match_id INT,
    sender_user_id INT,
    receiver_user_id int DEFAULT NULL,
    content TEXT NOT NULL,
    send_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (match_id) REFERENCES matches(match_id) ON DELETE CASCADE,
    FOREIGN KEY (sender_user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
