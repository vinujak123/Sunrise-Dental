package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.ITreatmentDAO;
import com.sunrise.dental.model.Treatment;
import com.sunrise.dental.singleton.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TreatmentDAOImpl – implementation of ITreatmentDAO.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class TreatmentDAOImpl implements ITreatmentDAO {

    private final DBConnectionManager dbManager = DBConnectionManager.getInstance();

    @Override
    public List<Treatment> findAllActive() {
        List<Treatment> list = new ArrayList<>();
        String sql = "SELECT * FROM treatments WHERE is_active = 1 ORDER BY category, treatment_name";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                list.add(mapRowToTreatment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Optional<Treatment> findById(int id) {
        String sql = "SELECT * FROM treatments WHERE treatment_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToTreatment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean insert(Treatment t) {
        String sql = "INSERT INTO treatments (treatment_code, treatment_name, category, description, duration_min, fee, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            stmt.setString(1, t.getTreatmentCode());
            stmt.setString(2, t.getTreatmentName());
            stmt.setString(3, t.getCategory());
            stmt.setString(4, t.getDescription());
            stmt.setInt(5, t.getDurationMin());
            stmt.setDouble(6, t.getFee());
            stmt.setInt(7, t.isActive() ? 1 : 0);
            
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        t.setTreatmentId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Treatment t) {
        String sql = "UPDATE treatments SET treatment_name=?, category=?, description=?, duration_min=?, fee=?, is_active=? WHERE treatment_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, t.getTreatmentName());
            stmt.setString(2, t.getCategory());
            stmt.setString(3, t.getDescription());
            stmt.setInt(4, t.getDurationMin());
            stmt.setDouble(5, t.getFee());
            stmt.setInt(6, t.isActive() ? 1 : 0);
            stmt.setInt(7, t.getTreatmentId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Treatment mapRowToTreatment(ResultSet rs) throws SQLException {
        Treatment t = new Treatment();
        t.setTreatmentId(rs.getInt("treatment_id"));
        t.setTreatmentCode(rs.getString("treatment_code"));
        t.setTreatmentName(rs.getString("treatment_name"));
        t.setCategory(rs.getString("category"));
        t.setDescription(rs.getString("description"));
        t.setDurationMin(rs.getInt("duration_min"));
        t.setFee(rs.getDouble("fee"));
        t.setActive(rs.getInt("is_active") == 1);
        return t;
    }
}
