# Sunrise Dental Clinic - Appointment & Patient Management System

An advanced, user-friendly, and menu-driven Java Desktop Application developed for **Sunrise Dental Clinic (Colombo)**. The system computerizes appointment scheduling, patient record tracking, fee calculations, and receipt generation while providing management analytics for executive decision-making.

---

## 🌟 Core System Modules & Features

1. **Role-Based User Authentication**:
    - Secure authentication for authorized clinic roles: **Administrator**, **Receptionist**, and **Doctor**.
    - User credentials are pre-configured in the database and protected with role-specific access permissions.
    - Personalized welcome message and dashboard banner shown on login, greeting the staff member by name and role.

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
    - Quick date filters (**Today**, **Tomorrow**, **Next 7 Days**, or custom calendar date), combined with a **Status filter** (`All`, `Scheduled`, `Confirmed`, `Cancelled`).
    - **Confirm Appointment**: doctors can formally confirm a scheduled appointment, restricted to the doctor's own queue only.
    - **Cancel Appointment**: doctors can cancel directly from their queue; a cancellation reason is **required** before the cancellation is accepted.
    - Add & update clinical diagnosis notes and prescriptions directly into patient records.

5. **Patient Medical History & Multi-Criteria Search (`PatientHistoryView`)**:
    - Search by Patient Name, Phone Number, Patient ID, or Appointment Number.
    - Displays full patient journey: demographics, total visits, total billing spent, doctor diagnosis notes, and chronological visit history.

6. **All Confirmed Appointments Table (`AppointmentListView`)**:
    - Interactive `JTable` listing all appointments with color-coded status badges (`Scheduled`, `Confirmed`, `Billed`, `Cancelled`).
    - Instant search, status filtering (`All Statuses`, `Scheduled`, `Confirmed`, `Billed`, `Cancelled`), double-click row to bill, and direct table printing.
    - **Cancel Appointment**: Admin/Receptionist can cancel with a **required** cancellation reason; hovering over a cancelled row's status shows the recorded reason as a tooltip.
    - Note: only doctors can *confirm* an appointment (from their own queue) — Admin/Receptionist can book and cancel, but not confirm.

7. **Calculate & Print Patient Bill (`BillView`)**:
    - Loads appointments by Patient Name or Appointment Number.
    - Computes: `Total Bill = Doctor Consultation Fee + Treatment Procedure Cost`.
    - **Confirm Bill**: generates official clinic invoice with receipt numbers (`REC-XXXX`) and updates appointment status to `Billed` in the MySQL database.
    - Native Java print dialog with hardware printing and *Microsoft Print to PDF* support.

8. **Management & Revenue Reports (`ReportsView`)**:
    - Clinic revenue summaries, doctor workload metrics, and treatment popularity statistics.
    - Printable analytics sheets for administrative decision-making.

9. **Built-in System Documentation (`HelpView`)**:
    - Integrated operational guide explaining role permissions, booking steps, billing rules, and troubleshooting.

10. **Web Services Layer & REST API (`ApiServer`)**:
    - A lightweight embedded HTTP server (Java `com.sun.net.httpserver`) runs alongside the Swing desktop app on **port `8080`**, exposing live appointment data as JSON — without altering any existing Swing screens or business logic.
    - **Endpoint**: `GET /api/appointments`
        - No query params → returns **all** appointments.
        - `?id={appointmentNumber}` → returns a single appointment by its appointment number.
        - `?status={status}` → returns appointments filtered by status (`Scheduled`, `Confirmed`, `Billed`, `Cancelled`).
        - `?filter=today` → returns only today's appointments.
    - **Response format**: JSON array (or single JSON object for `?id=`), including `appointmentNumber`, `patientName`, `dentistName`, `treatmentType`, `appointmentDate`, `appointmentTime`, and `status`.
    - **CORS enabled** (`Access-Control-Allow-Origin: *`) so browser-based clients can call it directly.
    - **Web client** (`web-client.html`): a standalone HTML/CSS/JS front end (styled with `styles.css`) that consumes this REST API — lets staff view a live, filterable, auto-refreshing appointments table (All / Today Only / by Status) straight from a browser, independent of the desktop app's UI.
    - Demonstrates the system as a **distributed application**: the Java/Swing/MySQL core remains the source of truth, while the REST layer and web client act as an independent, loosely-coupled presentation tier.

---

## 🛠️ Technology Stack & Design Patterns

- **Language & Runtime**: Java JDK 17 (or latest LTS)
- **GUI Framework**: Java Swing (Native System Look & Feel)
- **Web Services**: Embedded Java HTTP server (`com.sun.net.httpserver`), REST-style JSON API (`ApiServer.java`)
- **Web Client**: HTML5 / CSS3 / vanilla JavaScript (`web-client.html`, `styles.css`) using the Fetch API
- **Database**: MySQL via WAMP Server (port `3306`)
- **Persistence Driver**: JDBC with MySQL Connector/J (`8.3.0`)
- **Build System**: Apache Maven (`pom.xml`)
- **Design Patterns Implemented**:
    - **Singleton Pattern**: Database connection manager ([`DBConnection.java`](src/main/java/org/example/db/DBConnection.java)).
    - **DAO (Data Access Object) Pattern**: [`UserDAO`](src/main/java/org/example/dao/UserDAO.java), [`AppointmentDAO`](src/main/java/org/example/dao/AppointmentDAO.java), [`TreatmentDAO`](src/main/java/org/example/dao/TreatmentDAO.java), [`ReportDAO`](src/main/java/org/example/dao/ReportDAO.java).
    - **Service / Business Logic Layer**: [`AppointmentService`](src/main/java/org/example/service/AppointmentService.java), [`BillingService`](src/main/java/org/example/service/BillingService.java).
    - **MVC / Tiered Architecture**: Clear separation between `model`, `dao`, `service`, and `view`, with an additional **web/API tier** ([`ApiServer.java`](src/main/java/org/example/ApiServer.java)) sitting alongside the desktop UI.

---

## 🗄️ Database Architecture & Schema

The system uses MySQL database `sunrise_dental_db` initialized via [`database.sql`](database.sql) or automatic code self-healing:

| Table Name | Primary Key | Description |
| :--- | :--- | :--- |
| **`users`** | `user_id` | Authorized system accounts (`Admin`, `Receptionist`, `Doctor`) pre-configured for secure login. |
| **`patients`** | `patient_id` | Patient demographic details, contact info, and medical histories. |
| **`treatments`** | `treatment_id` | Standard dental treatment catalog with baseline costs and consultation fees. |
| **`appointments`** | `appointment_number` | Patient bookings, assigned doctors, clinical diagnosis notes, fee breakdowns, cancellation reason, and status (`Scheduled`, `Confirmed`, `Billed`, `Cancelled`). |

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
3. The **Sunrise Dental Clinic - Staff Login** screen will launch, and the embedded REST API will start automatically at `http://localhost:8080/api/appointments`.

---

### Step 4: Use the Web Client
1. With the desktop app still running (so the API server is live), open `web-client.html` directly in any web browser.
2. Use the filter dropdown (**All Appointments**, **Today Only**, or by **Status**) and click **Refresh** to fetch live appointment data from the REST API.

---

## 🔑 Login Credentials

For security, login credentials are no longer displayed on the login screen itself. For testing purposes, use:

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