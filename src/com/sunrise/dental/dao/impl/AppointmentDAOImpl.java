package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.IAppointmentDAO;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.singleton.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * AppointmentDAOImpl – implementation of IAppointmentDAO.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class AppointmentDAOImpl implements IAppointmentDAO {

    private final DBConnectionManager dbManager = DBConnectionManager.getInstance();

    @Override
    public String bookAppointment(Appointment appt, int userId) {
        String sql = "{CALL sp_register_appointment(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = dbManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
             
            stmt.setInt(1, appt.getPatientId());
            stmt.setInt(2, appt.getDentistId());
            stmt.setInt(3, appt.getTreatmentId());
            stmt.setDate(4, appt.getApptDate());
            stmt.setTime(5, appt.getApptTime());
            stmt.setString(6, appt.getNotes());
            stmt.setInt(7, userId);
            
            // Output parameters
            stmt.registerOutParameter(8, Types.INTEGER); // p_appt_id
            stmt.registerOutParameter(9, Types.VARCHAR); // p_appt_number
            stmt.registerOutParameter(10, Types.VARCHAR); // p_message
            
            stmt.execute();
            
            int apptId = stmt.getInt(8);
            String apptNumber = stmt.getString(9);
            String message = stmt.getString(10);
            
            if (apptId > 0 && apptNumber != null) {
                appt.setApptId(apptId);
                appt.setApptNumber(apptNumber);
                return apptNumber;
            } else {
                return "ERROR:" + message;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR:" + e.getMessage();
        }
    }

    @Override
    public Optional<Appointment> findById(int id) {
        String sql = "SELECT * FROM appointments WHERE appt_id = ?";
        return findSingle(sql, stmt -> stmt.setInt(1, id));
    }

    @Override
    public Optional<Appointment> findByApptNumber(String apptNumber) {
        String sql = "SELECT * FROM appointments WHERE appt_number = ?";
        return findSingle(sql, stmt -> stmt.setString(1, apptNumber));
    }

    @Override
    public List<Appointment> findByDate(Date date) {
        String sql = "SELECT a.*, p.first_name, p.last_name, p.patient_number, d.name AS dentist_name, t.treatment_name " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN dentists d ON a.dentist_id = d.dentist_id " +
                     "JOIN treatments t ON a.treatment_id = t.treatment_id " +
                     "WHERE a.appt_date = ? " +
                     "ORDER BY a.appt_time";
                     
        List<Appointment> list = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setDate(1, date);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapJoinedRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Appointment> findByPatientId(int patientId) {
        String sql = "SELECT a.*, p.first_name, p.last_name, p.patient_number, d.name AS dentist_name, t.treatment_name " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN dentists d ON a.dentist_id = d.dentist_id " +
                     "JOIN treatments t ON a.treatment_id = t.treatment_id " +
                     "WHERE a.patient_id = ? " +
                     "ORDER BY a.appt_date DESC, a.appt_time DESC";
                     
        List<Appointment> list = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapJoinedRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean updateStatus(int apptId, String status, int userId) {
        String sql = "{CALL sp_update_appointment_status(?, ?, ?, ?)}";
        try (Connection conn = dbManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
             
            stmt.setInt(1, apptId);
            stmt.setString(2, status);
            stmt.setInt(3, userId);
            stmt.registerOutParameter(4, Types.VARCHAR); // message
            
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Optional<Appointment> findSingle(String sql, ParameterSetter setter) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            setter.set(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapBaseRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Appointment mapBaseRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setApptId(rs.getInt("appt_id"));
        a.setApptNumber(rs.getString("appt_number"));
        a.setPatientId(rs.getInt("patient_id"));
        a.setDentistId(rs.getInt("dentist_id"));
        a.setTreatmentId(rs.getInt("treatment_id"));
        a.setApptDate(rs.getDate("appt_date"));
        a.setApptTime(rs.getTime("appt_time"));
        a.setEndTime(rs.getTime("end_time"));
        
        String status = rs.getString("status");
        if (status != null) {
            a.setStatus(Appointment.Status.valueOf(status));
        }
        
        a.setNotes(rs.getString("notes"));
        a.setCreatedBy(rs.getInt("created_by"));
        a.setUpdatedBy(rs.getInt("updated_by"));
        a.setCreatedAt(rs.getTimestamp("created_at"));
        a.setUpdatedAt(rs.getTimestamp("updated_at"));
        return a;
    }
    
    private Appointment mapJoinedRow(ResultSet rs) throws SQLException {
        Appointment a = mapBaseRow(rs);
        a.setPatientName(rs.getString("first_name") + " " + rs.getString("last_name"));
        a.setPatientNumber(rs.getString("patient_number"));
        a.setDentistName(rs.getString("dentist_name"));
        a.setTreatmentName(rs.getString("treatment_name"));
        return a;
    }

    @FunctionalInterface
    private interface ParameterSetter {
        void set(PreparedStatement stmt) throws SQLException;
    }
}
