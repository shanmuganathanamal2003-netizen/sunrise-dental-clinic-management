# Sunrise Dental Clinic - Appointment & Patient Management System

An advanced, user-friendly, and menu-driven Java Desktop Application developed for **Sunrise Dental Clinic (Colombo)**. The system computerizes appointment scheduling, patient record tracking, fee calculations, and receipt generation while providing management analytics for executive decision-making.

---

## 🌟 Core System Modules & Features

1. **Role-Based User Authentication**:
   - Secure authentication for authorized clinic roles: **Administrator**, **Receptionist**, and **Doctor**.
   - User credentials are pre-configured in the database and protected with role-specific access permissions.
   - Pre-seeded default credentials: `admin` / `admin123`, `staff` / `staff123`, `doctor1` / `doctor123`, `doctor2` / `doctor123`.

2. **Interactive Live Dashboard & Reusable Menu Bar (`JMenuBar`)**:
   - Standardized top navigation menu (`File`, `Appointments`, `Patients & History`, `Billing`, `Reports`, `Help`) across all screens.
   - Live overview table of recent clinic appointments with direct navigation.
   - Role-customized dashboard actions tailored for Admin, Doctor, and Receptionist workflows.

3. **Add New Appointments (New & Registered Patients)**:
   - Auto-generated Appointment Number.
   - Mode selector for **Registered / Old Patients** (auto-fills demographics) or **New Patients**.
   - Patient age specification starting from 1 month (unit selector for Months / Years).
   - Interactive calendar date picker dialog.
   - **Double-Booking Prevention**: Validates doctor availability and prevents conflicting time slots.
   - Structured fee breakdown for Consultation Fee, Procedure Cost, and Total Amount.

4. **Doctor Patient Schedule & Clinical Queue (`DoctorQueueView`)**:
   - Dedicated portal for doctors to view their appointment queue.
   - Quick date filters (**Today**, **Tomorrow**, **Next 7 Days**, or custom calendar date).
   - Add & update clinical diagnosis notes and prescriptions directly into patient records.

5. **Patient Medical History & Multi-Criteria Search (`PatientHistoryView`)**:
   - Search by Patient Name, Phone Number, Patient ID, or Appointment Number.
   - Displays full patient journey: demographics, total visits, total billing spent, doctor diagnosis notes, and chronological visit history.

6. **All Confirmed Appointments Table (`AppointmentListView`)**:
   - Interactive `JTable` listing all confirmed appointments with color-coded status badges.
   - Instant search, status filtering (`All`, `Scheduled`, `Billed`), double-click row to bill, and direct table printing.

7. **Calculate & Print Patient Bill (`BillView`)**:
   - Loads appointments by Patient Name or Appointment Number.
   - Computes: `Total Bill = Doctor Consultation Fee + Treatment Procedure Cost`.
   - Generates official clinic invoice with receipt numbers (`REC-XXXX`).
   - Updates appointment status to `Billed` in the MySQL database.
   - Native Java print dialog with hardware printing and *Microsoft Print to PDF* support.

8. **Management & Revenue Reports (`ReportsView`)**:
   - Clinic revenue summaries, doctor workload metrics, and treatment popularity statistics.
   - Printable analytics sheets for administrative decision-making.

9. **Built-in System Documentation (`HelpView`)**:
   - Integrated operational guide explaining role permissions, booking steps, billing rules, and troubleshooting.

---

## 🛠️ Technology Stack & Design Patterns

- **Language & Runtime**: Java JDK 17 (or latest LTS)
- **GUI Framework**: Java Swing (Native System Look & Feel)
- **Database**: MySQL via WAMP Server (port `3306`)
- **Persistence Driver**: JDBC with MySQL Connector/J (`8.3.0`)
- **Build System**: Apache Maven (`pom.xml`)
- **Design Patterns Implemented**:
  - **Singleton Pattern**: Database connection manager ([`DBConnection.java`](src/main/java/org/example/db/DBConnection.java)).
  - **DAO (Data Access Object) Pattern**: [`UserDAO`](src/main/java/org/example/dao/UserDAO.java), [`AppointmentDAO`](src/main/java/org/example/dao/AppointmentDAO.java), [`TreatmentDAO`](src/main/java/org/example/dao/TreatmentDAO.java), [`ReportDAO`](src/main/java/org/example/dao/ReportDAO.java).
  - **Service / Business Logic Layer**: [`AppointmentService`](src/main/java/org/example/service/AppointmentService.java), [`BillingService`](src/main/java/org/example/service/BillingService.java).
  - **MVC / Tiered Architecture**: Clear separation between `model`, `dao`, `service`, and `view`.

---

## 🗄️ Database Architecture & Schema

The system uses MySQL database `sunrise_dental_db` initialized via [`database.sql`](database.sql) or automatic code self-healing:

| Table Name | Primary Key | Description |
| :--- | :--- | :--- |
| **`users`** | `user_id` | Authorized system accounts (`Admin`, `Receptionist`, `Doctor`) pre-configured for secure login. |
| **`patients`** | `patient_id` | Patient demographic details, contact info, and medical histories. |
| **`treatments`** | `treatment_id` | Standard dental treatment catalog with baseline costs and consultation fees. |
| **`appointments`** | `appointment_number` | Patient bookings, assigned doctors, clinical diagnosis notes, fee breakdowns, and status (`Scheduled`, `Billed`, `Cancelled`). |

---

## 🚀 Setup & Execution Guide

### Step 1: Start WAMP Server
1. Launch **WAMP Server** and ensure the tray icon is **Green** (MySQL active on port `3306`).
2. *(The application includes an auto-initializer that automatically creates the database and tables if missing).*

---

### Step 2: Open in IntelliJ IDEA
1. Open **IntelliJ IDEA Community Edition**.
2. Click **File -> Open...** and select the `sunrise-dental-clinic-management` folder.
3. Open as a **Maven Project**.

---

### Step 3: Run the Application
1. Navigate to:
   `src/main/java/org/example/Main.java`
2. Right-click `Main.java` and select **Run 'Main.main()'**.
3. The **Sunrise Dental Clinic - Staff Login** screen will launch!

---

## 🔑 Login Credentials

| Username | Password | Role | Full Name |
| :--- | :--- | :--- | :--- |
| **`admin`** | `admin123` | Administrator | Administrator - Dr. Samantha Perera |
| **`staff`** | `staff123` | Receptionist | Receptionist - Kasuni Silva |
| **`doctor1`** | `doctor123` | Doctor | Dr. Kasun Fernando |
| **`doctor2`** | `doctor123` | Doctor | Dr. Nihal Silva |

---

## 🧪 Sample Preloaded Appointments for Testing

- **1001**: Kamal Perera (*Teeth Cleaning & Scaling*)
- **1002**: Nimali Fernando (*Tooth Filling*)
- **1003**: Sunil Wickramasinghe (*Root Canal Treatment*)
