package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.IDentistDAO;
import com.sunrise.dental.model.Dentist;
import com.sunrise.dental.singleton.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DentistDAOImpl – implementation of IDentistDAO.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class DentistDAOImpl implements IDentistDAO {

    private final DBConnectionManager dbManager = DBConnectionManager.getInstance();

    @Override
    public List<Dentist> findAllActive() {
        List<Dentist> list = new ArrayList<>();
        String sql = "SELECT * FROM dentists WHERE is_active = 1";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                list.add(mapRowToDentist(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Optional<Dentist> findById(int id) {
        String sql = "SELECT * FROM dentists WHERE dentist_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToDentist(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Dentist> findByUserId(int userId) {
        String sql = "SELECT * FROM dentists WHERE user_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToDentist(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Dentist mapRowToDentist(ResultSet rs) throws SQLException {
        Dentist d = new Dentist();
        d.setDentistId(rs.getInt("dentist_id"));
        
        int userId = rs.getInt("user_id");
        if (!rs.wasNull()) {
            d.setUserId(userId);
        }
        
        d.setName(rs.getString("name"));
        d.setSpecialization(rs.getString("specialization"));
        d.setQualification(rs.getString("qualification"));
        d.setContact(rs.getString("contact"));
        d.setEmail(rs.getString("email"));
        d.setAvailableDays(rs.getString("available_days"));
        d.setActive(rs.getInt("is_active") == 1);
        d.setCreatedAt(rs.getTimestamp("created_at"));
        
        return d;
    }
}
