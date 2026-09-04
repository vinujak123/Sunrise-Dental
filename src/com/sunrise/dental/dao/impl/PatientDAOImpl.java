package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.IPatientDAO;
import com.sunrise.dental.model.Patient;
import com.sunrise.dental.singleton.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * PatientDAOImpl – implementation of IPatientDAO.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class PatientDAOImpl implements IPatientDAO {

    private final DBConnectionManager dbManager = DBConnectionManager.getInstance();

    @Override
    public String registerPatient(Patient p) {
        String nextNumberSql = "SELECT COALESCE(MAX(CAST(SUBSTRING(patient_number, 3) AS UNSIGNED)), 0) + 1 FROM patients";
        String insertSql = "INSERT INTO patients (patient_number, first_name, last_name, date_of_birth, gender, " +
                           "blood_group, address, city, contact, email, emergency_contact, medical_notes, allergies) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection()) {
            conn.setAutoCommit(false);
            int nextNumber = 1;
            try (PreparedStatement numberStmt = conn.prepareStatement(nextNumberSql);
                 ResultSet rs = numberStmt.executeQuery()) {
                if (rs.next()) nextNumber = rs.getInt(1);
            }

            String patientNumber = String.format("P-%04d", nextNumber);
            try (PreparedStatement stmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, patientNumber);
                stmt.setString(2, p.getFirstName());
                stmt.setString(3, p.getLastName());
                stmt.setDate(4, p.getDateOfBirth());
                stmt.setString(5, p.getGender() != null ? p.getGender().name() : null);
                stmt.setString(6, p.getBloodGroup());
                stmt.setString(7, p.getAddress());
                stmt.setString(8, p.getCity());
                stmt.setString(9, p.getContact());
                stmt.setString(10, p.getEmail());
                stmt.setString(11, p.getEmergencyContact());
                stmt.setString(12, p.getMedicalNotes());
                stmt.setString(13, p.getAllergies());
                stmt.executeUpdate();
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) p.setPatientId(keys.getInt(1));
                }
            }
            conn.commit();
            p.setPatientNumber(patientNumber);
            return patientNumber;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<Patient> findById(int id) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToPatient(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Patient> findByPatientNumber(String patientNumber) {
        String sql = "SELECT * FROM patients WHERE patient_number = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, patientNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToPatient(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Patient> searchPatients(String keyword) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE is_active = 1 AND (first_name LIKE ? OR last_name LIKE ? OR contact LIKE ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(mapRowToPatient(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    @Override
    public List<Patient> findAllActive() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE is_active = 1 ORDER BY patient_number DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                patients.add(mapRowToPatient(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    @Override
    public boolean update(Patient p) {
        String sql = "UPDATE patients SET first_name=?, last_name=?, date_of_birth=?, gender=?, " +
                     "blood_group=?, address=?, city=?, contact=?, email=?, emergency_contact=?, " +
                     "medical_notes=?, allergies=?, is_active=? WHERE patient_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, p.getFirstName());
            stmt.setString(2, p.getLastName());
            stmt.setDate(3, p.getDateOfBirth());
            stmt.setString(4, p.getGender() != null ? p.getGender().name() : null);
            stmt.setString(5, p.getBloodGroup());
            stmt.setString(6, p.getAddress());
            stmt.setString(7, p.getCity());
            stmt.setString(8, p.getContact());
            stmt.setString(9, p.getEmail());
            stmt.setString(10, p.getEmergencyContact());
            stmt.setString(11, p.getMedicalNotes());
            stmt.setString(12, p.getAllergies());
            stmt.setInt(13, p.isActive() ? 1 : 0);
            stmt.setInt(14, p.getPatientId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Patient mapRowToPatient(ResultSet rs) throws SQLException {
        Patient p = new Patient();
        p.setPatientId(rs.getInt("patient_id"));
        p.setPatientNumber(rs.getString("patient_number"));
        p.setFirstName(rs.getString("first_name"));
        p.setLastName(rs.getString("last_name"));
        p.setDateOfBirth(rs.getDate("date_of_birth"));
        
        String genderStr = rs.getString("gender");
        if (genderStr != null) {
            p.setGender(Patient.Gender.valueOf(genderStr));
        }
        
        p.setBloodGroup(rs.getString("blood_group"));
        p.setAddress(rs.getString("address"));
        p.setCity(rs.getString("city"));
        p.setContact(rs.getString("contact"));
        p.setEmail(rs.getString("email"));
        p.setEmergencyContact(rs.getString("emergency_contact"));
        p.setMedicalNotes(rs.getString("medical_notes"));
        p.setAllergies(rs.getString("allergies"));
        p.setActive(rs.getInt("is_active") == 1);
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        return p;
    }
}
