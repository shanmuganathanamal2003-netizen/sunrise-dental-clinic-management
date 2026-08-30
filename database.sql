
-- SUNRISE DENTAL CLINIC - APPOINTMENT & PATIENT MANAGEMENT SYSTEM


-- 1. Create Database if it does not already exist
CREATE DATABASE IF NOT EXISTS `sunrise_dental_db` 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE `sunrise_dental_db`;

-- --------------------------------------------------------------------
-- 2. Table: users
-- Stores pre-configured clinic staff, doctors, and administrator login credentials
-- Supported roles: 'Admin', 'Receptionist', 'Doctor'
-- (User accounts are pre-seeded and managed by Database Administration)
-- --------------------------------------------------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
    `user_id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(100) NOT NULL,
    `full_name` VARCHAR(100) NOT NULL,
    `role` VARCHAR(30) NOT NULL DEFAULT 'Receptionist', -- 'Admin', 'Receptionist', 'Doctor'
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------
-- 3. Table: patients
-- Stores registered patient profiles and medical/dental histories
-- --------------------------------------------------------------------
DROP TABLE IF EXISTS `patients`;
CREATE TABLE `patients` (
    `patient_id` INT AUTO_INCREMENT PRIMARY KEY,
    `patient_name` VARCHAR(100) NOT NULL,
    `age` VARCHAR(30) NOT NULL DEFAULT '1 Month',
    `gender` VARCHAR(20) NOT NULL DEFAULT 'Not Specified',
    `contact_number` VARCHAR(20) NOT NULL,
    `address` VARCHAR(255) NOT NULL,
    `medical_history` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------------------
-- 4. Table: treatments
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
-- 5. Table: appointments
-- Stores patient appointments, doctor assignments, and billing details
-- --------------------------------------------------------------------
DROP TABLE IF EXISTS `appointments`;
CREATE TABLE `appointments` (
    `appointment_number` INT AUTO_INCREMENT PRIMARY KEY,
    `patient_id` INT DEFAULT NULL,
    `patient_name` VARCHAR(100) NOT NULL,
    `patient_age` VARCHAR(30) NOT NULL DEFAULT '1 Month',
    `address` VARCHAR(255) NOT NULL,
    `contact_number` VARCHAR(20) NOT NULL,
    `dentist_name` VARCHAR(100) NOT NULL,
    `assigned_doctor_username` VARCHAR(50) DEFAULT NULL,
    `doctor_notes` TEXT DEFAULT NULL,
    `treatment_type` VARCHAR(100) NOT NULL,
    `appointment_date` DATE NOT NULL,
    `appointment_time` VARCHAR(20) NOT NULL,
    `treatment_cost` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    `consultation_fee` DECIMAL(10, 2) NOT NULL DEFAULT 1500.00,
    `total_bill` DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    `status` VARCHAR(20) NOT NULL DEFAULT 'Scheduled',
    `cancellation_reason` VARCHAR(255) DEFAULT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT `fk_appointment_patient` FOREIGN KEY (`patient_id`) REFERENCES `patients` (`patient_id`) ON DELETE SET NULL,
    CONSTRAINT `fk_appt_doctor_user` FOREIGN KEY (`assigned_doctor_username`) REFERENCES `users` (`username`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=1001 DEFAULT CHARSET=utf8mb4;

-- ====================================================================
-- INSERT INITIAL / SAMPLE DATA
-- ====================================================================

-- Insert Sample Authorized System Users (Admin, Receptionist, Doctor)
INSERT INTO `users` (`username`, `password`, `full_name`, `role`) VALUES
('admin', 'admin123', 'Administrator - Dr. Samantha Perera', 'Admin'),
('staff', 'staff123', 'Receptionist - Kasuni Silva', 'Receptionist'),
('doctor1', 'doctor123', 'Dr. Kasun Fernando', 'Doctor'),
('doctor2', 'doctor123', 'Dr. Nihal Silva', 'Doctor');

-- Insert Sample Registered Patients (including pediatric & adult records)
INSERT INTO `patients` (`patient_id`, `patient_name`, `age`, `gender`, `contact_number`, `address`, `medical_history`) VALUES
(101, 'Kamal Perera', '35 Years', 'Male', '0771234567', 'No. 45, Galle Road, Colombo 03', 'No known allergies. Regular dental checkup patient.'),
(102, 'Nimali Fernando', '28 Years', 'Female', '0719876543', 'No. 12, Kandy Road, Kelaniya', 'Mild gingivitis reported in 2025.'),
(103, 'Sunil Wickramasinghe', '52 Years', 'Male', '0765551234', 'No. 88, High Level Road, Nugegoda', 'Hypertension under medication.'),
(104, 'Baby Arya Senanayake', '6 Months', 'Female', '0774443322', 'No. 19, Havelock Town, Colombo 05', 'Pediatric infant dental assessment.');

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

-- Insert Sample Appointments with assigned_doctor_username and clinical notes
INSERT INTO `appointments` (`appointment_number`, `patient_id`, `patient_name`, `patient_age`, `address`, `contact_number`, `dentist_name`, `assigned_doctor_username`, `doctor_notes`, `treatment_type`, `appointment_date`, `appointment_time`, `treatment_cost`, `consultation_fee`, `total_bill`, `status`, `cancellation_reason`) VALUES
(1001, 101, 'Kamal Perera', '35 Years', 'No. 45, Galle Road, Colombo 03', '0771234567', 'Dr. Kasun Fernando', 'doctor1', 'Teeth scaling completed. Recommended soft bristle toothbrush and fluoride rinse.', 'Teeth Cleaning & Scaling', '2026-08-20', '09:00 AM', 3500.00, 1500.00, 5000.00, 'Scheduled', NULL),
(1002, 102, 'Nimali Fernando', '28 Years', 'No. 12, Kandy Road, Kelaniya', '0719876543', 'Dr. Nihal Silva', 'doctor2', 'Tooth 24 cavity inspected. Composite filling scheduled.', 'Tooth Filling (Composite)', '2026-08-21', '10:30 AM', 4500.00, 1500.00, 6000.00, 'Scheduled', NULL),
(1003, 103, 'Sunil Wickramasinghe', '52 Years', 'No. 88, High Level Road, Nugegoda', '0765551234', 'Dr. Kasun Fernando', 'doctor1', 'Root canal stage 1 completed. Prescribed Amoxicillin 500mg.', 'Root Canal Treatment', '2026-08-22', '02:00 PM', 15000.00, 1500.00, 16500.00, 'Scheduled', NULL),
(1004, 101, 'Kamal Perera', '35 Years', 'No. 45, Galle Road, Colombo 03', '0771234567', 'Dr. Kasun Fernando', 'doctor1', 'Follow-up dental inspection booked.', 'General Dental Consultation', '2026-08-28', '11:00 AM', 1000.00, 1500.00, 2500.00, 'Scheduled', NULL);

-- Verify inserted data
SELECT * FROM `users`;
SELECT * FROM `patients`;
SELECT * FROM `treatments`;
SELECT * FROM `appointments`;
