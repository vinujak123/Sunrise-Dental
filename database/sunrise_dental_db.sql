-- ============================================================
-- Sunrise Dental Clinic – Database Schema
-- Module  : CIS6003 Advanced Programming
-- Database: MySQL 8.0+
-- ============================================================

DROP DATABASE IF EXISTS sunrise_dental_db;
CREATE DATABASE sunrise_dental_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE sunrise_dental_db;

-- ============================================================
-- TABLE: users
-- Stores system login accounts for staff
-- ============================================================
CREATE TABLE users (
    user_id       INT            AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)    UNIQUE NOT NULL,
    password_hash VARCHAR(64)    NOT NULL COMMENT 'SHA-256 hex',
    full_name     VARCHAR(100)   NOT NULL,
    role          ENUM('ADMIN','RECEPTIONIST','DENTIST') NOT NULL,
    email         VARCHAR(100),
    contact       VARCHAR(20),
    is_active     TINYINT(1)     DEFAULT 1,
    created_at    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    last_login    TIMESTAMP      NULL,
    INDEX idx_username (username),
    INDEX idx_role     (role)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE: notifications
-- Unread staff notifications, scoped to admin and dentist users
-- ============================================================
CREATE TABLE notifications (
    notification_id   INT           AUTO_INCREMENT PRIMARY KEY,
    recipient_user_id INT           NOT NULL,
    title             VARCHAR(120)  NOT NULL,
    message           VARCHAR(255)  NOT NULL,
    is_read           TINYINT(1)    DEFAULT 0,
    created_at        TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    read_at           TIMESTAMP     NULL,
    FOREIGN KEY (recipient_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_notification_recipient (recipient_user_id, is_read, created_at)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE: dentists
-- Dental practitioners at the clinic
-- ============================================================
CREATE TABLE dentists (
    dentist_id      INT          AUTO_INCREMENT PRIMARY KEY,
    user_id         INT          NULL COMMENT 'linked login account',
    name            VARCHAR(100) NOT NULL,
    specialization  VARCHAR(100) DEFAULT 'General Dentistry',
    qualification   VARCHAR(200),
    contact         VARCHAR(20),
    email           VARCHAR(100),
    available_days  VARCHAR(100) DEFAULT 'MON,TUE,WED,THU,FRI',
    is_active       TINYINT(1)   DEFAULT 1,
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_dentist_active (is_active)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE: patients
-- Registered patients of the clinic
-- ============================================================
CREATE TABLE patients (
    patient_id        INT          AUTO_INCREMENT PRIMARY KEY,
    patient_number    VARCHAR(20)  UNIQUE NOT NULL,
    first_name        VARCHAR(50)  NOT NULL,
    last_name         VARCHAR(50)  NOT NULL,
    date_of_birth     DATE,
    gender            ENUM('MALE','FEMALE','OTHER'),
    blood_group       VARCHAR(5),
    address           TEXT,
    city              VARCHAR(50),
    contact           VARCHAR(20)  NOT NULL,
    email             VARCHAR(100),
    emergency_contact VARCHAR(20),
    medical_notes     TEXT,
    allergies         TEXT,
    is_active         TINYINT(1)   DEFAULT 1,
    created_at        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
                                   ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_patient_number (patient_number),
    INDEX idx_contact        (contact),
    FULLTEXT INDEX idx_name  (first_name, last_name)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE: treatments
-- Available dental treatments and fees
-- ============================================================
CREATE TABLE treatments (
    treatment_id   INT            AUTO_INCREMENT PRIMARY KEY,
    treatment_code VARCHAR(20)    UNIQUE NOT NULL,
    treatment_name VARCHAR(100)   NOT NULL,
    category       VARCHAR(50),
    description    TEXT,
    duration_min   INT            DEFAULT 30,
    fee            DECIMAL(10,2)  NOT NULL,
    is_active      TINYINT(1)     DEFAULT 1,
    INDEX idx_treatment_code   (treatment_code),
    INDEX idx_treatment_active (is_active)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE: appointments
-- Patient appointments with dentists
-- ============================================================
CREATE TABLE appointments (
    appt_id      INT    AUTO_INCREMENT PRIMARY KEY,
    appt_number  VARCHAR(20) UNIQUE NOT NULL,
    patient_id   INT    NOT NULL,
    dentist_id   INT    NOT NULL,
    treatment_id INT    NOT NULL,
    appt_date    DATE   NOT NULL,
    appt_time    TIME   NOT NULL,
    end_time     TIME   NOT NULL,
    status       ENUM('PENDING','CONFIRMED','COMPLETED','CANCELLED','NO_SHOW')
                        DEFAULT 'PENDING',
    notes        TEXT,
    created_by   INT    NULL,
    updated_by   INT    NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                           ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id)   REFERENCES patients(patient_id)   ON DELETE RESTRICT,
    FOREIGN KEY (dentist_id)   REFERENCES dentists(dentist_id)   ON DELETE RESTRICT,
    FOREIGN KEY (treatment_id) REFERENCES treatments(treatment_id) ON DELETE RESTRICT,
    FOREIGN KEY (created_by)   REFERENCES users(user_id)         ON DELETE SET NULL,
    FOREIGN KEY (updated_by)   REFERENCES users(user_id)         ON DELETE SET NULL,
    INDEX idx_appt_date     (appt_date),
    INDEX idx_appt_dentist  (dentist_id, appt_date),
    INDEX idx_appt_patient  (patient_id),
    INDEX idx_appt_status   (status)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE: bills
-- Invoices generated per appointment
-- ============================================================
CREATE TABLE bills (
    bill_id           INT           AUTO_INCREMENT PRIMARY KEY,
    bill_number       VARCHAR(20)   UNIQUE NOT NULL,
    appt_id           INT           NOT NULL,
    treatment_fee     DECIMAL(10,2) NOT NULL,
    consultation_fee  DECIMAL(10,2) DEFAULT 500.00,
    discount_percent  DECIMAL(5,2)  DEFAULT 0.00,
    discount_amount   DECIMAL(10,2) DEFAULT 0.00,
    subtotal          DECIMAL(10,2) NOT NULL,
    tax_percent       DECIMAL(5,2)  DEFAULT 0.00,
    tax_amount        DECIMAL(10,2) DEFAULT 0.00,
    total_amount      DECIMAL(10,2) NOT NULL,
    payment_method    ENUM('CASH','CARD','INSURANCE','ONLINE') DEFAULT 'CASH',
    payment_status    ENUM('PENDING','PAID','REFUNDED','CANCELLED')  DEFAULT 'PENDING',
    insurance_provider VARCHAR(100),
    insurance_amount  DECIMAL(10,2) DEFAULT 0.00,
    generated_by      INT           NULL,
    generated_at      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    paid_at           TIMESTAMP     NULL,
    notes             TEXT,
    FOREIGN KEY (appt_id)      REFERENCES appointments(appt_id) ON DELETE RESTRICT,
    FOREIGN KEY (generated_by) REFERENCES users(user_id)        ON DELETE SET NULL,
    INDEX idx_bill_appt   (appt_id),
    INDEX idx_bill_status (payment_status),
    INDEX idx_bill_date   (generated_at)
) ENGINE=InnoDB;

-- ============================================================
-- TABLE: audit_log
-- Immutable audit trail for all changes
-- ============================================================
CREATE TABLE audit_log (
    log_id          INT     AUTO_INCREMENT PRIMARY KEY,
    action_type     ENUM('INSERT','UPDATE','DELETE','LOGIN','LOGOUT') NOT NULL,
    table_name      VARCHAR(50),
    record_id       INT,
    old_value       TEXT,
    new_value       TEXT,
    user_id         INT,
    ip_address      VARCHAR(50),
    description     VARCHAR(255),
    action_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_ts    (action_timestamp),
    INDEX idx_audit_user  (user_id),
    INDEX idx_audit_table (table_name)
) ENGINE=InnoDB;

-- ============================================================
-- STORED FUNCTION: fn_get_next_patient_number
-- Generates the next sequential patient number (P-0001)
-- ============================================================
DELIMITER $$

CREATE FUNCTION fn_get_next_patient_number()
RETURNS VARCHAR(20)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE next_num INT;
    SELECT COALESCE(MAX(CAST(SUBSTRING(patient_number,3) AS UNSIGNED)), 0) + 1
    INTO next_num
    FROM patients;
    RETURN CONCAT('P-', LPAD(next_num, 4, '0'));
END$$

-- ============================================================
-- STORED FUNCTION: fn_get_next_appt_number
-- Generates the next sequential appointment number (A-0001)
-- ============================================================
CREATE FUNCTION fn_get_next_appt_number()
RETURNS VARCHAR(20)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE next_num INT;
    SELECT COALESCE(MAX(CAST(SUBSTRING(appt_number,3) AS UNSIGNED)), 0) + 1
    INTO next_num
    FROM appointments;
    RETURN CONCAT('A-', LPAD(next_num, 4, '0'));
END$$

-- ============================================================
-- STORED FUNCTION: fn_get_next_bill_number
-- Generates the next sequential bill number (B-0001)
-- ============================================================
CREATE FUNCTION fn_get_next_bill_number()
RETURNS VARCHAR(20)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE next_num INT;
    SELECT COALESCE(MAX(CAST(SUBSTRING(bill_number,3) AS UNSIGNED)), 0) + 1
    INTO next_num
    FROM bills;
    RETURN CONCAT('B-', LPAD(next_num, 4, '0'));
END$$

-- ============================================================
-- STORED FUNCTION: fn_calculate_age
-- Returns age in years from date of birth
-- ============================================================
CREATE FUNCTION fn_calculate_age(dob DATE)
RETURNS INT
READS SQL DATA
DETERMINISTIC
BEGIN
    RETURN TIMESTAMPDIFF(YEAR, dob, CURDATE());
END$$

-- ============================================================
-- STORED FUNCTION: fn_get_daily_revenue
-- Returns total revenue for a given date
-- ============================================================
CREATE FUNCTION fn_get_daily_revenue(target_date DATE)
RETURNS DECIMAL(12,2)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE revenue DECIMAL(12,2) DEFAULT 0.00;
    SELECT COALESCE(SUM(total_amount), 0.00)
    INTO revenue
    FROM bills
    WHERE DATE(generated_at) = target_date
    AND payment_status = 'PAID';
    RETURN revenue;
END$$

-- ============================================================
-- STORED PROCEDURE: sp_register_patient
-- Registers a new patient with auto-generated number
-- ============================================================
CREATE PROCEDURE sp_register_patient(
    IN  p_first_name        VARCHAR(50),
    IN  p_last_name         VARCHAR(50),
    IN  p_dob               DATE,
    IN  p_gender            VARCHAR(10),
    IN  p_blood_group       VARCHAR(5),
    IN  p_address           TEXT,
    IN  p_city              VARCHAR(50),
    IN  p_contact           VARCHAR(20),
    IN  p_email             VARCHAR(100),
    IN  p_emergency_contact VARCHAR(20),
    IN  p_medical_notes     TEXT,
    IN  p_allergies         TEXT,
    OUT p_patient_id        INT,
    OUT p_patient_number    VARCHAR(20),
    OUT p_message           VARCHAR(255)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_patient_id = -1;
        SET p_message = 'Error registering patient';
    END;

    START TRANSACTION;
        SET p_patient_number = fn_get_next_patient_number();

        INSERT INTO patients (
            patient_number, first_name, last_name, date_of_birth, gender,
            blood_group, address, city, contact, email,
            emergency_contact, medical_notes, allergies
        ) VALUES (
            p_patient_number, p_first_name, p_last_name, p_dob, p_gender,
            p_blood_group, p_address, p_city, p_contact, p_email,
            p_emergency_contact, p_medical_notes, p_allergies
        );

        SET p_patient_id = LAST_INSERT_ID();
        SET p_message = CONCAT('Patient registered: ', p_patient_number);
    COMMIT;
END$$

-- ============================================================
-- STORED PROCEDURE: sp_register_appointment
-- Books an appointment after checking availability
-- ============================================================
CREATE PROCEDURE sp_register_appointment(
    IN  p_patient_id    INT,
    IN  p_dentist_id    INT,
    IN  p_treatment_id  INT,
    IN  p_appt_date     DATE,
    IN  p_appt_time     TIME,
    IN  p_notes         TEXT,
    IN  p_created_by    INT,
    OUT p_appt_id       INT,
    OUT p_appt_number   VARCHAR(20),
    OUT p_message       VARCHAR(255)
)
BEGIN
    DECLARE v_duration     INT;
    DECLARE v_end_time     TIME;
    DECLARE v_conflict_cnt INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_appt_id = -1;
        SET p_message = 'Error booking appointment';
    END;

    -- Get treatment duration
    SELECT duration_min INTO v_duration
    FROM treatments WHERE treatment_id = p_treatment_id;

    SET v_end_time = ADDTIME(p_appt_time, SEC_TO_TIME(v_duration * 60));

    -- Check for double booking
    SELECT COUNT(*) INTO v_conflict_cnt
    FROM appointments
    WHERE dentist_id = p_dentist_id
      AND appt_date  = p_appt_date
      AND status NOT IN ('CANCELLED','NO_SHOW')
      AND (
            (p_appt_time >= appt_time AND p_appt_time < end_time)
         OR (v_end_time  >  appt_time AND v_end_time  <= end_time)
         OR (p_appt_time <= appt_time AND v_end_time  >= end_time)
      );

    IF v_conflict_cnt > 0 THEN
        SET p_appt_id = -1;
        SET p_message = 'Double booking detected: Dentist is unavailable at this time';
    ELSE
        START TRANSACTION;
            SET p_appt_number = fn_get_next_appt_number();

            INSERT INTO appointments (
                appt_number, patient_id, dentist_id, treatment_id,
                appt_date, appt_time, end_time, notes, created_by
            ) VALUES (
                p_appt_number, p_patient_id, p_dentist_id, p_treatment_id,
                p_appt_date, p_appt_time, v_end_time, p_notes, p_created_by
            );

            SET p_appt_id = LAST_INSERT_ID();
            SET p_message = CONCAT('Appointment booked: ', p_appt_number);
        COMMIT;
    END IF;
END$$

-- ============================================================
-- STORED PROCEDURE: sp_generate_bill
-- Creates a bill for a completed appointment
-- ============================================================
CREATE PROCEDURE sp_generate_bill(
    IN  p_appt_id          INT,
    IN  p_consultation_fee DECIMAL(10,2),
    IN  p_discount_percent DECIMAL(5,2),
    IN  p_tax_percent      DECIMAL(5,2),
    IN  p_payment_method   VARCHAR(20),
    IN  p_generated_by     INT,
    IN  p_notes            TEXT,
    OUT p_bill_id          INT,
    OUT p_bill_number      VARCHAR(20),
    OUT p_total_amount     DECIMAL(10,2),
    OUT p_message          VARCHAR(255)
)
BEGIN
    DECLARE v_treatment_fee  DECIMAL(10,2);
    DECLARE v_subtotal       DECIMAL(10,2);
    DECLARE v_discount_amt   DECIMAL(10,2);
    DECLARE v_tax_amt        DECIMAL(10,2);
    DECLARE v_total          DECIMAL(10,2);
    DECLARE v_existing_bill  INT DEFAULT 0;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_bill_id = -1;
        SET p_message = 'Error generating bill';
    END;

    -- Validate appointment exists and is completed
    SELECT COUNT(*) INTO v_existing_bill
    FROM bills WHERE appt_id = p_appt_id;

    IF v_existing_bill > 0 THEN
        SET p_bill_id = -1;
        SET p_message = 'Bill already generated for this appointment';
    ELSE
        -- Get treatment fee
        SELECT t.fee INTO v_treatment_fee
        FROM appointments a
        JOIN treatments t ON a.treatment_id = t.treatment_id
        WHERE a.appt_id = p_appt_id;

        -- Calculate amounts
        SET v_subtotal     = v_treatment_fee + p_consultation_fee;
        SET v_discount_amt = ROUND(v_subtotal * p_discount_percent / 100, 2);
        SET v_subtotal     = v_subtotal - v_discount_amt;
        SET v_tax_amt      = ROUND(v_subtotal * p_tax_percent / 100, 2);
        SET v_total        = v_subtotal + v_tax_amt;

        START TRANSACTION;
            SET p_bill_number = fn_get_next_bill_number();

            INSERT INTO bills (
                bill_number, appt_id, treatment_fee, consultation_fee,
                discount_percent, discount_amount, subtotal,
                tax_percent, tax_amount, total_amount,
                payment_method, generated_by, notes
            ) VALUES (
                p_bill_number, p_appt_id, v_treatment_fee, p_consultation_fee,
                p_discount_percent, v_discount_amt, v_subtotal,
                p_tax_percent, v_tax_amt, v_total,
                p_payment_method, p_generated_by, p_notes
            );

            SET p_bill_id      = LAST_INSERT_ID();
            SET p_total_amount = v_total;
            SET p_message      = CONCAT('Bill generated: ', p_bill_number);
        COMMIT;
    END IF;
END$$

-- ============================================================
-- STORED PROCEDURE: sp_get_daily_report
-- Returns appointments and revenue summary for a given date
-- ============================================================
CREATE PROCEDURE sp_get_daily_report(IN p_date DATE)
BEGIN
    -- Appointments by status
    SELECT status, COUNT(*) AS count
    FROM appointments
    WHERE appt_date = p_date
    GROUP BY status;

    -- Revenue
    SELECT
        COUNT(b.bill_id)          AS total_bills,
        SUM(b.total_amount)       AS gross_revenue,
        SUM(b.discount_amount)    AS total_discounts,
        SUM(CASE WHEN b.payment_status = 'PAID'    THEN b.total_amount ELSE 0 END) AS collected,
        SUM(CASE WHEN b.payment_status = 'PENDING' THEN b.total_amount ELSE 0 END) AS pending
    FROM bills b
    JOIN appointments a ON b.appt_id = a.appt_id
    WHERE a.appt_date = p_date;
END$$

-- ============================================================
-- STORED PROCEDURE: sp_update_appointment_status
-- Updates appointment status and logs the change
-- ============================================================
CREATE PROCEDURE sp_update_appointment_status(
    IN p_appt_id    INT,
    IN p_new_status VARCHAR(20),
    IN p_updated_by INT,
    OUT p_message   VARCHAR(255)
)
BEGIN
    DECLARE v_old_status VARCHAR(20);

    SELECT status INTO v_old_status
    FROM appointments WHERE appt_id = p_appt_id;

    UPDATE appointments
    SET status = p_new_status, updated_by = p_updated_by
    WHERE appt_id = p_appt_id;

    INSERT INTO audit_log (action_type, table_name, record_id, old_value, new_value, user_id, description)
    VALUES ('UPDATE', 'appointments', p_appt_id,
            CONCAT('status=', v_old_status),
            CONCAT('status=', p_new_status),
            p_updated_by,
            CONCAT('Appointment status changed from ', v_old_status, ' to ', p_new_status));

    SET p_message = CONCAT('Status updated to ', p_new_status);
END$$

-- ============================================================
-- TRIGGER: trg_prevent_double_booking
-- Prevents overlapping appointments for same dentist
-- ============================================================
CREATE TRIGGER trg_prevent_double_booking
BEFORE INSERT ON appointments
FOR EACH ROW
BEGIN
    DECLARE v_conflict INT DEFAULT 0;

    SELECT COUNT(*) INTO v_conflict
    FROM appointments
    WHERE dentist_id = NEW.dentist_id
      AND appt_date  = NEW.appt_date
      AND status NOT IN ('CANCELLED','NO_SHOW')
      AND (
            (NEW.appt_time >= appt_time AND NEW.appt_time < end_time)
         OR (NEW.end_time  > appt_time  AND NEW.end_time  <= end_time)
         OR (NEW.appt_time <= appt_time AND NEW.end_time  >= end_time)
      );

    IF v_conflict > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Double booking: Dentist already has an appointment at this time';
    END IF;
END$$

-- ============================================================
-- TRIGGER: trg_audit_appointment_insert
-- Logs every new appointment insertion
-- ============================================================
CREATE TRIGGER trg_audit_appointment_insert
AFTER INSERT ON appointments
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (action_type, table_name, record_id, new_value, user_id, description)
    VALUES (
        'INSERT', 'appointments', NEW.appt_id,
        CONCAT('appt_number=', NEW.appt_number,
               ', patient_id=', NEW.patient_id,
               ', dentist_id=', NEW.dentist_id,
               ', date=', NEW.appt_date,
               ', time=', NEW.appt_time,
               ', status=', NEW.status),
        NEW.created_by,
        CONCAT('New appointment registered: ', NEW.appt_number)
    );
END$$

-- ============================================================
-- TRIGGER: trg_audit_appointment_update
-- Logs every appointment status change
-- ============================================================
CREATE TRIGGER trg_audit_appointment_update
AFTER UPDATE ON appointments
FOR EACH ROW
BEGIN
    IF OLD.status <> NEW.status THEN
        INSERT INTO audit_log (action_type, table_name, record_id, old_value, new_value, user_id, description)
        VALUES (
            'UPDATE', 'appointments', NEW.appt_id,
            CONCAT('status=', OLD.status),
            CONCAT('status=', NEW.status),
            NEW.updated_by,
            CONCAT('Appointment ', NEW.appt_number, ' status: ', OLD.status, ' → ', NEW.status)
        );
    END IF;
END$$

-- ============================================================
-- TRIGGER: trg_audit_bill_insert
-- Logs every new bill
-- ============================================================
CREATE TRIGGER trg_audit_bill_insert
AFTER INSERT ON bills
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (action_type, table_name, record_id, new_value, user_id, description)
    VALUES (
        'INSERT', 'bills', NEW.bill_id,
        CONCAT('bill_number=', NEW.bill_number, ', total=', NEW.total_amount),
        NEW.generated_by,
        CONCAT('Bill generated: ', NEW.bill_number, ' Amount: Rs. ', NEW.total_amount)
    );
END$$

DELIMITER ;

-- ============================================================
-- SEED DATA
-- ============================================================

-- Admin user  (password: admin123)
INSERT INTO users (username, password_hash, full_name, role, email, contact) VALUES
('admin',    SHA2('admin123',256),        'System Administrator', 'ADMIN',        'admin@sunrisedental.lk',   '0112-345-001'),
('recept01', SHA2('recept123',256),       'Nimal Perera',         'RECEPTIONIST', 'nimal@sunrisedental.lk',   '0112-345-002'),
('recept02', SHA2('recept123',256),       'Sunethra Silva',       'RECEPTIONIST', 'sunethra@sunrisedental.lk','0112-345-003'),
('dr_silva', SHA2('dentist123',256),      'Dr. Kamal Silva',      'DENTIST',      'kamal@sunrisedental.lk',   '0112-345-010'),
('dr_perera',SHA2('dentist123',256),      'Dr. Dilani Perera',    'DENTIST',      'dilani@sunrisedental.lk',  '0112-345-011'),
('dr_fernando',SHA2('dentist123',256),    'Dr. Rohan Fernando',   'DENTIST',      'rohan@sunrisedental.lk',   '0112-345-012');

-- Dentists
INSERT INTO dentists (user_id, name, specialization, qualification, contact, email, available_days) VALUES
(4,'Dr. Kamal Silva',    'General Dentistry','BDS, MDS (Colombo)',       '0112-345-010','kamal@sunrisedental.lk',  'MON,TUE,WED,THU,FRI'),
(5,'Dr. Dilani Perera',  'Orthodontics',     'BDS, MOrth (Cardiff)',     '0112-345-011','dilani@sunrisedental.lk', 'MON,WED,FRI'),
(6,'Dr. Rohan Fernando', 'Oral Surgery',     'BDS, MDS (Oral Surgery)',  '0112-345-012','rohan@sunrisedental.lk',  'TUE,THU,SAT');

-- Treatments
INSERT INTO treatments (treatment_code, treatment_name, category, description, duration_min, fee) VALUES
('GEN-001','General Checkup',       'General',        'Routine dental examination',        30,   500.00),
('GEN-002','Teeth Cleaning',        'General',        'Scaling and polishing',             45,  1500.00),
('EXT-001','Simple Extraction',     'Extraction',     'Simple tooth extraction',           30,  2500.00),
('EXT-002','Surgical Extraction',   'Extraction',     'Impacted tooth extraction',         60,  6000.00),
('FIL-001','Composite Filling',     'Restorative',    'Tooth coloured composite filling',  45,  3000.00),
('FIL-002','Amalgam Filling',       'Restorative',    'Silver amalgam filling',            30,  2000.00),
('RCT-001','Root Canal Treatment',  'Endodontics',    'Single canal root canal treatment', 90,  8000.00),
('RCT-002','Root Canal (Multi)',    'Endodontics',    'Multi canal root canal treatment',  120, 12000.00),
('WHT-001','Teeth Whitening',       'Cosmetic',       'In-office teeth whitening',         60, 15000.00),
('BRC-001','Braces Consultation',   'Orthodontics',   'Orthodontic consultation & plan',   60,  2000.00),
('BRC-002','Braces (Metal)',        'Orthodontics',   'Full metal braces treatment',       60, 80000.00),
('BRC-003','Braces (Ceramic)',      'Orthodontics',   'Full ceramic braces treatment',     60,100000.00),
('CRN-001','Dental Crown (PFM)',    'Prosthodontics', 'Porcelain fused to metal crown',    90, 12000.00),
('CRN-002','Dental Crown (Zirconia)','Prosthodontics','Full zirconia crown',              90, 18000.00),
('IMP-001','Dental Implant',        'Implantology',   'Single tooth dental implant',      120, 50000.00),
('BRG-001','Dental Bridge (3 unit)','Prosthodontics', 'Three unit dental bridge',         90, 20000.00),
('XRY-001','Periapical X-Ray',     'Radiography',    'Single tooth x-ray',               15,   500.00),
('XRY-002','OPG X-Ray',            'Radiography',    'Full mouth panoramic x-ray',       30,  2000.00),
('GUM-001','Gum Treatment',        'Periodontics',   'Deep scaling and root planing',     60,  4000.00),
('VNR-001','Porcelain Veneer',     'Cosmetic',       'Single tooth porcelain veneer',     90, 15000.00);

-- Sample patients
INSERT INTO patients (patient_number, first_name, last_name, date_of_birth, gender, blood_group, address, city, contact, email, emergency_contact, medical_notes, allergies) VALUES
('P-0001','Amal',    'Jayawardena','1985-03-15','MALE',  'B+','45 Galle Road',          'Colombo 3',   '0771-234-567','amal.j@gmail.com',    '0712-234-567','Diabetic - Type 2',      'Penicillin'),
('P-0002','Kumari',  'Dissanayake','1992-07-22','FEMALE','A+','12 Flower Road',          'Colombo 7',   '0712-345-678','kumari.d@gmail.com',  '0771-345-678','No significant history','None'),
('P-0003','Ruwan',   'Fernando',  '1978-11-08','MALE',  'O-','78 High Level Road',       'Nugegoda',    '0761-456-789','ruwan.f@gmail.com',   '0712-456-789','Hypertensive',          'Aspirin'),
('P-0004','Thilini', 'Wickrama',  '2000-05-30','FEMALE','AB+','23 Kandy Road',           'Kadawatha',   '0751-567-890','thilini.w@gmail.com', '0761-567-890','No significant history','None'),
('P-0005','Dinesh',  'Rajapaksa', '1965-09-12','MALE',  'B-','56 Union Place',           'Colombo 2',   '0777-678-901','dinesh.r@gmail.com',  '0751-678-901','Cardiac patient',       'Sulfa drugs'),
('P-0006','Sanduni', 'Karunaratne','1998-02-18','FEMALE','A-','34 Baseline Road',         'Colombo 9',   '0741-789-012','sanduni.k@gmail.com', '0777-789-012','No significant history','None'),
('P-0007','Pradeep', 'Seneviratne','1988-12-25','MALE',  'O+','90 Negombo Road',          'Wattala',     '0725-890-123','pradeep.s@gmail.com', '0741-890-123','Asthmatic',             'NSAIDs'),
('P-0008','Amara',   'Bandara',   '1975-06-14','FEMALE','B+','15 Duplication Road',       'Colombo 4',   '0718-901-234','amara.b@gmail.com',   '0725-901-234','No significant history','None');

-- Sample appointments (today's date)
INSERT INTO appointments (appt_number, patient_id, dentist_id, treatment_id, appt_date, appt_time, end_time, status, notes, created_by) VALUES
('A-0001', 1, 1, 1, CURDATE(), '09:00:00', '09:30:00', 'CONFIRMED', 'Regular checkup',           2),
('A-0002', 2, 2, 9, CURDATE(), '09:30:00', '10:30:00', 'CONFIRMED', 'Whitening treatment',       2),
('A-0003', 3, 1, 3, CURDATE(), '10:00:00', '10:30:00', 'PENDING',   'Lower right molar',         3),
('A-0004', 4, 3, 4, CURDATE(), '10:30:00', '11:30:00', 'COMPLETED', 'Wisdom tooth removal',      2),
('A-0005', 5, 1, 7, CURDATE(), '11:00:00', '12:30:00', 'CONFIRMED', 'Upper right premolar RCT',  3),
('A-0006', 6, 2, 10,CURDATE(), '11:00:00', '12:00:00', 'COMPLETED', 'Orthodontic consultation',  2);

-- Bills for completed appointments
INSERT INTO bills (bill_number, appt_id, treatment_fee, consultation_fee, discount_percent, discount_amount, subtotal, tax_percent, tax_amount, total_amount, payment_method, payment_status, generated_by, paid_at)
VALUES
('B-0001', 4, 6000.00, 500.00, 0.00, 0.00, 6500.00, 0.00, 0.00, 6500.00, 'CASH', 'PAID', 2, NOW()),
('B-0002', 6, 2000.00, 500.00, 10.00, 250.00, 2250.00, 0.00, 0.00, 2250.00, 'CARD', 'PAID', 3, NOW());
