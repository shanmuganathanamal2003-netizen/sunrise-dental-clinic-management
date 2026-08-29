# Sunrise Dental Clinic - Appointment & Patient Management System

An advanced, user-friendly, and menu-driven Java Desktop Application developed for **Sunrise Dental Clinic (Colombo)**. The system computerizes appointment scheduling, patient record tracking, fee calculations, and receipt generation while providing management analytics for executive decision-making.

---

## 🌟 Upgraded Key Features

1. **User Authentication & New Staff Registration**:
   - Secure login for authorized staff and receptionists.
   - **Register as New User**: New staff can register directly from the login screen with custom role assignment (Receptionist, Staff, Admin, Dental Assistant).
   - Pre-seeded default logins: `admin` / `admin123` or `staff` / `staff123`.

2. **Interactive Live Dashboard & Top Menu Bar (`JMenuBar`)**:
   - Standard top navigation menu (`File`, `Appointments`, `Billing`, `Reports`, `Help`) accessible across all windows.
   - Live metric cards: Total Appointments, Today's Patients, Scheduled / Pending, and Total Paid Revenue (LKR).
   - Quick overview table of recent appointments with direct navigation.

3. **Register New Appointment with Double-Booking Prevention**:
   - Auto-generated Appointment Number.
   - Captures Patient Full Name, Contact Number, Address, Assigned Dentist, Treatment Procedure, Date, and Time Slot.
   - **Double-Booking Prevention**: Prevents duplicate scheduling if a doctor is already booked for that date/time.
   - **Structured Multi-Row Fee Breakdown**: Dedicated, high-visibility rows for Doctor Consultation Fee, Treatment Procedure Cost, and Estimated Total Amount.

4. **All Confirmed Appointments Table (`AppointmentListView`)**:
   - Interactive `JTable` listing all confirmed appointments with status badges.
   - **Instant Search by Patient Name**, Appointment Number, Phone Number, or Doctor.
   - Status filtering (`All`, `Scheduled`, `Billed`).
   - Double-click any row to open billing or receipt generation directly.
   - Direct table printing for clinic records.

5. **Search / View Appointment by Patient Name or ID (`SearchAppointmentView`)**:
   - Search by Patient Name or Appointment Number.
   - Visual detail card displaying patient info, doctor, procedure, date/time, and billing status.
   - One-click navigation to calculate and print bill.

6. **Calculate and Print Patient Bill (`BillView`)**:
   - Loads appointment by Patient Name or ID.
   - Calculates: `Total Bill = Doctor Consultation Fee + Treatment Procedure Cost`.
   - Generates formatted dental clinic receipt with custom receipt number (`REC-XXXX`).
   - Saves billing details to MySQL database (marks as `Billed`).
   - **Print / PDF**: Native Java print dialog allowing direct hardware printing or *Microsoft Print to PDF*.

7. **Executive Decision-Making Reports (`ReportsView`)**:
   - **Financial Revenue Summary**: Total clinic earnings, consultation fees vs procedure costs.
   - **Doctor Workload Analysis**: Total appointments and revenue generated per dentist.
   - **Treatment Popularity Statistics**: Most requested dental procedures.
   - Printable report sheets.

8. **Help & System Documentation (`HelpView`)**:
   - Built-in operational manual explaining all features step-by-step.

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
| **`admin`** | `admin123` | Administrator | Dr. Samantha |
| **`staff`** | `staff123` | Receptionist | Kasuni Silva |

*(You can also click **"Register as New User"** on the login screen to create a new staff account).*

---

## 🧪 Sample Preloaded Appointments for Testing

- **1001**: Kamal Perera (*Teeth Cleaning & Scaling*)
- **1002**: Nimali Fernando (*Tooth Filling*)
- **1003**: Sunil Wickramasinghe (*Root Canal Treatment*)
