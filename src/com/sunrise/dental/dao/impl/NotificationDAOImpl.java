package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.INotificationDAO;
import com.sunrise.dental.model.Notification;
import com.sunrise.dental.singleton.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAOImpl implements INotificationDAO {

    private final DBConnectionManager dbManager = DBConnectionManager.getInstance();

    private void ensureTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS notifications (" +
                     "notification_id INT AUTO_INCREMENT PRIMARY KEY, " +
                     "recipient_user_id INT NOT NULL, title VARCHAR(120) NOT NULL, " +
                     "message VARCHAR(255) NOT NULL, is_read TINYINT(1) DEFAULT 0, " +
                     "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, read_at TIMESTAMP NULL, " +
                     "FOREIGN KEY (recipient_user_id) REFERENCES users(user_id) ON DELETE CASCADE, " +
                     "INDEX idx_notification_recipient (recipient_user_id, is_read, created_at)) ENGINE=InnoDB";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    @Override
    public void createPatientRegisteredNotification(String patientName, String patientNumber) {
        String sql = "INSERT INTO notifications (recipient_user_id, title, message) " +
                     "SELECT user_id, ?, ? FROM users " +
                     "WHERE is_active = 1 AND role IN ('ADMIN', 'DENTIST')";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ensureTable(conn);
            stmt.setString(1, "New patient registered");
            stmt.setString(2, patientName + " (" + patientNumber + ") was added to the patient list.");
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Notification> findUnreadByUser(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT notification_id, title, message, created_at " +
                     "FROM notifications WHERE recipient_user_id = ? AND is_read = 0 " +
                     "ORDER BY created_at DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ensureTable(conn);
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Notification notification = new Notification();
                    notification.setNotificationId(rs.getInt("notification_id"));
                    notification.setTitle(rs.getString("title"));
                    notification.setMessage(rs.getString("message"));
                    notification.setCreatedAt(rs.getTimestamp("created_at"));
                    notifications.add(notification);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    @Override
    public boolean markAsRead(int notificationId, int userId) {
        String sql = "UPDATE notifications SET is_read = 1, read_at = CURRENT_TIMESTAMP " +
                     "WHERE notification_id = ? AND recipient_user_id = ? AND is_read = 0";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ensureTable(conn);
            stmt.setInt(1, notificationId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
