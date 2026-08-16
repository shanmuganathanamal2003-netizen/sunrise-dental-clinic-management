-- ====================================================================
-- SUNRISE DENTAL CLINIC - APPOINTMENT & PATIENT MANAGEMENT SYSTEM
-- Database Creation and Seed Data Script
-- Compatible with MySQL (WAMP / XAMPP / phpMyAdmin)
-- ====================================================================

-- 1. Create Database if it does not already exist
CREATE DATABASE IF NOT EXISTS `sunrise_dental_db` 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE `sunrise_dental_db`;

-- --------------------------------------------------------------------
-- 2. Table: users
-- Stores staff and administrator login credentials
-- --------------------------------------------------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `user_id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(100) NOT NULL,
    `full_name` VARCHAR(100) NOT NULL,
    `role` VARCHAR(30) NOT NULL DEFAULT 'Receptionist',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------
-- 3. Table: treatments
-- Stores standard dental treatments with baseline costs and consultation fees
-- --------------------------------------------------------------------
DROP TABLE IF EXISTS `treatments`;
CREATE TABLE `treatments` (
    `treatment_id` INT AUTO_INCREMENT PRIMARY KEY,
    `treatment_name` VARCHAR(100) NOT NULL UNIQUE,
    `treatment_cost` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    `consultation_fee` DECIMAL(10, 2) NOT NULL DEFAULT 1500.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------
-- 4. Table: appointments
-- Stores patient appointments, dentist assignments, and billing details
-- --------------------------------------------------------------------
DROP TABLE IF EXISTS `appointments`;
CREATE TABLE `appointments` (
    `appointment_number` INT AUTO_INCREMENT PRIMARY KEY,
    `patient_name` VARCHAR(100) NOT NULL,
    `address` VARCHAR(255) NOT NULL,
    `contact_number` VARCHAR(20) NOT NULL,
    `dentist_name` VARCHAR(100) NOT NULL,
    `treatment_type` VARCHAR(100) NOT NULL,
    `appointment_date` DATE NOT NULL,
    `appointment_time` VARCHAR(20) NOT NULL,
    `treatment_cost` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    `consultation_fee` DECIMAL(10, 2) NOT NULL DEFAULT 1500.00,
    `total_bill` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    `status` VARCHAR(20) NOT NULL DEFAULT 'Scheduled',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB AUTO_INCREMENT=1001 DEFAULT CHARSET=utf8mb4;

-- ====================================================================
-- INSERT INITIAL / SAMPLE DATA
-- ====================================================================

-- Insert Sample Authorized System Users
INSERT INTO `users` (`username`, `password`, `full_name`, `role`) VALUES
('admin', 'admin123', 'Administrator - Dr. Samantha', 'Admin'),
('staff', 'staff123', 'Receptionist - Kasuni Silva', 'Receptionist');

-- Insert Standard Treatment Catalog (Prices in LKR - Sri Lankan Rupees)
INSERT INTO `treatments` (`treatment_name`, `treatment_cost`, `consultation_fee`) VALUES
('General Dental Consultation', 1000.00, 1500.00),
('Teeth Cleaning & Scaling', 3500.00, 1500.00),
('Tooth Filling (Composite)', 4500.00, 1500.00),
('Tooth Extraction', 4000.00, 1500.00),
('Root Canal Treatment', 15000.00, 1500.00),
('Teeth Whitening', 12000.00, 1500.00),
('Orthodontic Consultation / Braces', 25000.00, 2000.00),
('Dental Crown / Bridge', 18000.00, 2000.00);

-- Insert Sample Appointments for Initial Demonstration
INSERT INTO `appointments` (`patient_name`, `address`, `contact_number`, `dentist_name`, `treatment_type`, `appointment_date`, `appointment_time`, `treatment_cost`, `consultation_fee`, `total_bill`, `status`) VALUES
('Kamal Perera', 'No. 45, Galle Road, Colombo 03', '0771234567', 'Dr. Samantha Perera', 'Teeth Cleaning & Scaling', '2026-08-20', '09:00 AM', 3500.00, 1500.00, 5000.00, 'Scheduled'),
('Nimali Fernando', 'No. 12, Kandy Road, Kelaniya', '0719876543', 'Dr. Nihal Silva', 'Tooth Filling (Composite)', '2026-08-21', '10:30 AM', 4500.00, 1500.00, 6000.00, 'Scheduled'),
('Sunil Wickramasinghe', 'No. 88, High Level Road, Nugegoda', '0765551234', 'Dr. Anoma Wickramasinghe', 'Root Canal Treatment', '2026-08-22', '02:00 PM', 15000.00, 1500.00, 16500.00, 'Scheduled');

-- Verify inserted data
SELECT * FROM `users`;
SELECT * FROM `treatments`;
SELECT * FROM `appointments`;
