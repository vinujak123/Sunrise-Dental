# Sunrise Dental Clinic Management System

## Overview
This is a computerized Appointment & Patient Management System built for the CIS6003 Advanced Programming assignment (targeting Excellent Grade criteria).

## Technology Stack
- **Frontend**: Pure HTML, CSS (Custom Glassmorphism Design), Vanilla JavaScript
- **Backend**: Java Servlets (No Frameworks)
- **Database**: MySQL 8.0+
- **Architecture**: 3-Tier Architecture
- **Design Patterns**: Singleton (DBConnection), Factory (User/Treatment), DAO Pattern

## Setup Instructions

### 1. Database Setup
1. Open XAMPP and start the **MySQL** module.
2. Import the database schema:
   - Navigate to phpMyAdmin (`http://localhost/phpmyadmin`)
   - Import the `database/sunrise_dental_db.sql` file.
   - This will create the database, tables, stored procedures, triggers, and insert seed data.

### 2. Java Backend Setup (Tomcat via XAMPP)
1. Ensure the **Tomcat** module is enabled and started in XAMPP.
2. You need to compile the `.java` files in the `src/` directory into `.class` files and place them in the `WEB-INF/classes/` directory.
3. Download the MySQL JDBC Driver (`mysql-connector-j-8.x.x.jar`) and place it inside the `WEB-INF/lib/` folder.
4. Deploy the `SunriseDental` folder to Tomcat's `webapps` directory (if using standard Tomcat) or configure XAMPP Tomcat to serve this directory.

### 3. Running the System
Once deployed to Tomcat, access the system via your browser:
`http://localhost:8080/SunriseDental/web/index.html`

### Default Login Credentials
- **Admin**: `admin` / `admin123`
- **Receptionist**: `recept01` / `recept123`
- **Dentist**: `dr_silva` / `dentist123`

## Features Implemented
- Role-based Access Control (Admin, Receptionist, Dentist).
- Advanced MySQL Triggers to prevent appointment double-booking.
- Advanced MySQL Stored Procedures for transaction management (Billing, Registration).
- Secure SHA-256 password hashing.
- Premium dark-themed UI with glassmorphism effects.
