package com.sunrise.dental.singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnectionManager – Singleton Design Pattern
 * <p>
 * Ensures only one database connection pool instance exists
 * throughout the application lifecycle.
 * <p>
 * CIS6003 Advanced Programming – Sunrise Dental Clinic
 */
public class DBConnectionManager {

    // ----------------------------------------------------------------
    // Configuration – adjust if running on a different host/port
    // ----------------------------------------------------------------
    private static final String DB_URL      = "jdbc:mysql://localhost:3307/sunrise_dental_db?useSSL=false&serverTimezone=Asia/Colombo&allowPublicKeyRetrieval=true";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "";          // XAMPP default has no password
    private static final String DRIVER      = "com.mysql.cj.jdbc.Driver";

    // ----------------------------------------------------------------
    // Volatile ensures visibility across threads (double-checked locking)
    // ----------------------------------------------------------------
    private static volatile DBConnectionManager instance = null;

    /**
     * Private constructor – prevents external instantiation.
     * Loads the JDBC driver class exactly once.
     */
    private DBConnectionManager() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                "MySQL JDBC Driver not found. " +
                "Please add mysql-connector-j-*.jar to WEB-INF/lib/", e);
        }
    }

    /**
     * Returns the single instance of DBConnectionManager.
     * Thread-safe via double-checked locking.
     *
     * @return the singleton instance
     */
    public static DBConnectionManager getInstance() {
        if (instance == null) {                        // First check (no locking)
            synchronized (DBConnectionManager.class) {
                if (instance == null) {                // Second check (with locking)
                    instance = new DBConnectionManager();
                }
            }
        }
        return instance;
    }

    /**
     * Opens and returns a new database connection.
     * Callers must close the connection in a finally block or try-with-resources.
     *
     * @return a new {@link Connection}
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Quietly closes a connection, ignoring any exception.
     *
     * @param conn the connection to close (may be null)
     */
    public void closeConnection(Connection conn) {
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }

    /**
     * Tests whether a connection can be obtained from the database.
     *
     * @return true if the database is reachable
     */
    public boolean testConnection() {
        try (Connection c = getConnection()) {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // Prevent cloning (anti-pattern for Singleton)
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Singleton cannot be cloned");
    }
}
