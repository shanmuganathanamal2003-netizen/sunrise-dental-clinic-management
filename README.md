# Sunrise Dental Clinic - Appointment & Patient Management System

A simple, robust, and clean Java Desktop Application built for **Sunrise Dental Clinic** (Colombo) to manage patient appointments, search records, and calculate & print patient bills.

---

## 📋 System Features

1. **User Authentication (Login)**:
   - Secure login for authorized receptionists and administrators.
   - Credentials checked directly against the MySQL `users` table.
   - Input validation prevents blank submissions and displays friendly error dialogs.

2. **Register New Appointment**:
   - Auto-generates a unique Appointment Number.
   - Form inputs: Patient Name, Address, Contact Number, Assigned Dentist, Treatment Type, Appointment Date, and Time slot.
   - Live fee estimation display.
   - Mandatory field validation ensures no empty records are submitted.

3. **Search / View Appointment Details**:
   - Lookup any appointment by Appointment Number.
   - Clean display card showing all patient details, doctor, treatment, date/time, and billing status.
   - Direct button to proceed to billing.

4. **Calculate and Print Patient Bill**:
   - Computes: `Total Bill = Doctor Consultation Fee + Treatment Procedure Cost`.
   - Generates a neat, formatted dental clinic receipt.
   - Saves updated billing status to MySQL database.
   - **Print / PDF button**: Uses Java Swing's native printing system (`print()`) allowing direct hardware printing or export via *Microsoft Print to PDF*.

5. **System Help & User Guide**:
   - Built-in staff user manual explaining every feature and step.

6. **Safe Exit & Logout**:
   - Confirmation prompts to safely logout or exit.

---

## 🛠️ Technology Stack

- **Language & Runtime**: Java JDK 17 (or latest LTS)
- **UI Framework**: Java Swing (Native OS Look & Feel)
- **Database**: MySQL (running locally via WAMP / XAMPP on port `3306`)
- **Database Driver**: JDBC with MySQL Connector/J (`com.mysql.cj.jdbc.Driver`)
- **Build Tool**: Apache Maven (`pom.xml`)
- **IDE**: IntelliJ IDEA (Community Edition or Ultimate)
- **Version Control**: Git

---

## 🏗️ Project Architecture (Layered / Tiered Design)

The project follows a standard 3-tier / layered architecture:

```
sunrise-dental-clinic-management/
├── pom.xml                                 # Maven dependencies and build configuration
├── database.sql                            # SQL script to create database and sample records
├── README.md                               # Setup and execution guide
├── .gitignore                              # Git ignore rules
└── src/
    └── main/
        └── java/
            └── org/
                └── example/
                    ├── Main.java                    # Application Entry Point
                    ├── db/
                    │   └── DBConnection.java        # JDBC Singleton Connection Manager
                    ├── model/
                    │   ├── User.java                # Staff/Admin user entity
                    │   ├── Appointment.java         # Patient appointment entity
                    │   └── Treatment.java           # Dental treatment & pricing entity
                    ├── dao/
                    │   ├── UserDAO.java             # User login queries
                    │   ├── AppointmentDAO.java      # Appointment CRUD queries
                    │   └── TreatmentDAO.java        # Treatment list queries
                    └── view/
                        ├── LoginView.java           # 1. Login Screen
                        ├── MainMenuView.java        # Dashboard Navigation Screen
                        ├── RegisterAppointmentView.java # 2. Book Appointment Screen
                        ├── SearchAppointmentView.java   # 3. Search Records Screen
                        ├── BillView.java            # 4. Calculate & Print Bill Screen
                        └── HelpView.java            # 5. User Manual / Help Dialog
```

---

## 🚀 Setup & Execution Guide

### Step 1: Start WAMP / MySQL Server
1. Launch **WAMP Server** (or XAMPP) on your computer.
2. Wait until the WAMP icon in the system tray turns **Green** (indicating all services including MySQL on port `3306` are active).

---

### Step 2: Import the Database (`database.sql`)
1. Open your web browser and navigate to **phpMyAdmin**:
   ```
   http://localhost/phpmyadmin
   ```
2. Log in (default username is `root`, password is empty ` `).
3. Click on the **Import** tab at the top.
4. Click **Choose File** and select the `database.sql` file located in this project folder.
5. Click **Go** / **Import** at the bottom of the page.
   *(This will create the `sunrise_dental_db` database, create all tables, and insert sample users and dental treatments).*

---

### Step 3: Open Project in IntelliJ IDEA
1. Open **IntelliJ IDEA Community Edition**.
2. Click **Open** (or **File -> Open...**).
3. Select the folder:
   `sunrise-dental-clinic-management`
4. When prompted, select **"Trust Project"** and open as a **Maven Project**.
5. IntelliJ will automatically read `pom.xml` and download the `mysql-connector-j` dependency.

---

### Step 4: Run the Application
1. In the IntelliJ Project tool window, navigate to:
   `src/main/java/org/example/Main.java`
2. Right-click on `Main.java` and click **Run 'Main.main()'** (or click the green Play icon next to `public class Main`).
3. The **Sunrise Dental Clinic - Staff Login** window will appear!

---

## 🔑 Default Login Credentials

| Username | Password | Role | Full Name |
| :--- | :--- | :--- | :--- |
| **`admin`** | `admin123` | Administrator | Dr. Samantha |
| **`staff`** | `staff123` | Receptionist | Kasuni Silva |

---

## 🧪 Sample Preloaded Appointments for Testing

Once logged in, you can immediately test **Search** or **Calculate & Print Bill** using these preloaded Appointment Numbers:

- **1001**: Kamal Perera (*Teeth Cleaning & Scaling*)
- **1002**: Nimali Fernando (*Tooth Filling*)
- **1003**: Sunil Wickramasinghe (*Root Canal Treatment*)

---

## 📄 License & Academic Note
Created for ICBT / Cardiff Metropolitan University - Advanced Programming (CIS6003) Assessment.
