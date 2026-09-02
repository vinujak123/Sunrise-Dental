package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.IUserDAO;
import com.sunrise.dental.model.User;
import com.sunrise.dental.singleton.DBConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UserDAOImpl – implementation of IUserDAO.
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class UserDAOImpl implements IUserDAO {

    private final DBConnectionManager dbManager = DBConnectionManager.getInstance();

    @Override
    public Optional<User> authenticate(String username, String passwordHash) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ? AND is_active = 1";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRowToUser(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAllActive() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_active = 1";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
             
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public boolean insert(User user) {
        String sql = "INSERT INTO users (username, password_hash, full_name, role, email, contact) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getRole().name());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getContact());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserId(generatedKeys.getInt(1));
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
    public boolean update(User user) {
        String sql = "UPDATE users SET full_name = ?, role = ?, email = ?, contact = ?, is_active = ? WHERE user_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getRole().name());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getContact());
            stmt.setInt(5, user.isActive() ? 1 : 0);
            stmt.setInt(6, user.getUserId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deactivate(int id) {
        String sql = "UPDATE users SET is_active = 0 WHERE user_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Maps a ResultSet row to a User object.
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setRole(User.Role.valueOf(rs.getString("role")));
        user.setEmail(rs.getString("email"));
        user.setContact(rs.getString("contact"));
        user.setActive(rs.getInt("is_active") == 1);
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        return user;
    }
}
