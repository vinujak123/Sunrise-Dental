package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.IBillDAO;
import com.sunrise.dental.model.Bill;
import com.sunrise.dental.singleton.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * BillDAOImpl – implementation of IBillDAO.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class BillDAOImpl implements IBillDAO {

    private final DBConnectionManager dbManager = DBConnectionManager.getInstance();

    @Override
    public String generateBill(Bill bill) {
        String sql = "{CALL sp_generate_bill(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = dbManager.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
             
            stmt.setInt(1, bill.getApptId());
            stmt.setDouble(2, bill.getConsultationFee());
            stmt.setDouble(3, bill.getDiscountPercent());
            stmt.setDouble(4, bill.getTaxPercent());
            stmt.setString(5, bill.getPaymentMethod().name());
            stmt.setInt(6, bill.getGeneratedBy());
            stmt.setString(7, bill.getNotes());
            
            // Output parameters
            stmt.registerOutParameter(8, Types.INTEGER); // p_bill_id
            stmt.registerOutParameter(9, Types.VARCHAR); // p_bill_number
            stmt.registerOutParameter(10, Types.DECIMAL); // p_total_amount
            stmt.registerOutParameter(11, Types.VARCHAR); // p_message
            
            stmt.execute();
            
            int billId = stmt.getInt(8);
            String billNumber = stmt.getString(9);
            double total = stmt.getDouble(10);
            
            if (billId > 0 && billNumber != null) {
                bill.setBillId(billId);
                bill.setBillNumber(billNumber);
                bill.setTotalAmount(total);
                return billNumber;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Optional<Bill> findById(int id) {
        String sql = "SELECT * FROM bills WHERE bill_id = ?";
        return findSingle(sql, stmt -> stmt.setInt(1, id));
    }

    @Override
    public Optional<Bill> findByBillNumber(String billNumber) {
        String sql = "SELECT * FROM bills WHERE bill_number = ?";
        return findSingle(sql, stmt -> stmt.setString(1, billNumber));
    }

    @Override
    public Optional<Bill> findByAppointmentId(int apptId) {
        String sql = "SELECT * FROM bills WHERE appt_id = ?";
        return findSingle(sql, stmt -> stmt.setInt(1, apptId));
    }

    @Override
    public boolean updatePaymentStatus(int billId, String status) {
        String sql = "UPDATE bills SET payment_status = ?, paid_at = CASE WHEN ? = 'PAID' THEN NOW() ELSE paid_at END WHERE bill_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, status);
            stmt.setString(2, status);
            stmt.setInt(3, billId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public double getDailyRevenue(Date date) {
        String sql = "SELECT fn_get_daily_revenue(?) AS revenue";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setDate(1, date);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("revenue");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    @Override
    public List<Bill> findBillsByDate(Date date) {
        String sql = "SELECT b.*, a.appt_number, a.appt_date, a.appt_time, " +
                     "p.first_name, p.last_name, p.patient_number, " +
                     "d.name AS dentist_name, t.treatment_name, u.full_name AS generated_by_name " +
                     "FROM bills b " +
                     "JOIN appointments a ON b.appt_id = a.appt_id " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN dentists d ON a.dentist_id = d.dentist_id " +
                     "JOIN treatments t ON a.treatment_id = t.treatment_id " +
                     "LEFT JOIN users u ON b.generated_by = u.user_id " +
                     "WHERE DATE(b.generated_at) = ? " +
                     "ORDER BY b.generated_at DESC";
                     
        List<Bill> list = new ArrayList<>();
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

    private Optional<Bill> findSingle(String sql, ParameterSetter setter) {
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

    private Bill mapBaseRow(ResultSet rs) throws SQLException {
        Bill b = new Bill();
        b.setBillId(rs.getInt("bill_id"));
        b.setBillNumber(rs.getString("bill_number"));
        b.setApptId(rs.getInt("appt_id"));
        b.setTreatmentFee(rs.getDouble("treatment_fee"));
        b.setConsultationFee(rs.getDouble("consultation_fee"));
        b.setDiscountPercent(rs.getDouble("discount_percent"));
        b.setDiscountAmount(rs.getDouble("discount_amount"));
        b.setSubtotal(rs.getDouble("subtotal"));
        b.setTaxPercent(rs.getDouble("tax_percent"));
        b.setTaxAmount(rs.getDouble("tax_amount"));
        b.setTotalAmount(rs.getDouble("total_amount"));
        
        String method = rs.getString("payment_method");
        if (method != null) b.setPaymentMethod(Bill.PaymentMethod.valueOf(method));
        
        String status = rs.getString("payment_status");
        if (status != null) b.setPaymentStatus(Bill.PaymentStatus.valueOf(status));
        
        b.setInsuranceProvider(rs.getString("insurance_provider"));
        b.setInsuranceAmount(rs.getDouble("insurance_amount"));
        b.setGeneratedBy(rs.getInt("generated_by"));
        b.setGeneratedAt(rs.getTimestamp("generated_at"));
        b.setPaidAt(rs.getTimestamp("paid_at"));
        b.setNotes(rs.getString("notes"));
        return b;
    }
    
    private Bill mapJoinedRow(ResultSet rs) throws SQLException {
        Bill b = mapBaseRow(rs);
        b.setApptNumber(rs.getString("appt_number"));
        
        if (rs.getDate("appt_date") != null) {
             b.setApptDate(rs.getDate("appt_date").toString());
        }
        if (rs.getTime("appt_time") != null) {
             b.setApptTime(rs.getTime("appt_time").toString());
        }
        
        b.setPatientName(rs.getString("first_name") + " " + rs.getString("last_name"));
        b.setPatientNumber(rs.getString("patient_number"));
        b.setDentistName(rs.getString("dentist_name"));
        b.setTreatmentName(rs.getString("treatment_name"));
        b.setGeneratedByName(rs.getString("generated_by_name"));
        return b;
    }

    @FunctionalInterface
    private interface ParameterSetter {
        void set(PreparedStatement stmt) throws SQLException;
    }
}
