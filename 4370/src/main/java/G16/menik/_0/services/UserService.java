package G16.menik._0.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import G16.menik._0.models.User;
import jakarta.annotation.PostConstruct;

/**
 * Service class for managing user authentication and profile management.
 * Handles user registration, login, logout, and profile updates with secure password hashing.
 * Session-scoped to maintain user context throughout the user's session.
 */
@Service
@SessionScope
public class UserService {

    private final DataSource dataSource;
    private final BCryptPasswordEncoder passwordEncoder;
    private User loggedInUser = null;

    /**
     * Constructs UserService with the specified DataSource.
     * Initializes the BCryptPasswordEncoder for secure password handling.
     *
     * @param dataSource the data source for database connections
     */
    @Autowired
    public UserService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Initializes the users table if it does not already exist.
     * Called automatically after bean construction.
     */
    @PostConstruct
    public void initializeTable() {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "CREATE TABLE IF NOT EXISTS users (" +
                 "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                 "username VARCHAR(50) UNIQUE NOT NULL, " +
                 "password_hash VARCHAR(255) NOT NULL, " +
                 "full_name VARCHAR(100) NOT NULL" +
                 ")")) {
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create users table", e);
        }
    }

    /**
     * Authenticates a user by verifying username and password.
     * Sets the loggedInUser upon successful authentication.
     *
     * @param username the user's login username
     * @param password the user's plaintext password
     * @return {@code true} if authentication succeeds, {@code false} otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean authenticate(String username, String password) throws SQLException {
        final String sql = "select id, username, password_hash, full_name from users where username = ?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (passwordEncoder.matches(password, storedHash)) {
                        Long id = rs.getLong("id");
                        String dbUsername = rs.getString("username");
                        String fullName = rs.getString("full_name");
                        loggedInUser = new User(id, dbUsername, fullName);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Clears the current user session by setting loggedInUser to null.
     */
    public void unAuthenticate() {
        loggedInUser = null;
    }

    /**
     * Checks if a user is currently authenticated.
     *
     * @return {@code true} if a user is logged in, {@code false} otherwise
     */
    public boolean isAuthenticated() {
        return loggedInUser != null;
    }

    /**
     * Retrieves the currently logged-in user.
     *
     * @return the current User object, or {@code null} if no user is authenticated
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Finds and retrieves a user by username from the database.
     *
     * @param username the username to search for
     * @return the User object if found
     * @throws SQLException if the user is not found or a database error occurs
     */
    public User findByUsername(String username) throws SQLException {
        final String sql = "select id, username, password_hash, full_name from users where username = ?";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Long id = rs.getLong("id");
                    String dbUsername = rs.getString("username");
                    String fullName = rs.getString("full_name");

                    return new User(id, dbUsername, fullName);
                }
            }
        }
        throw new SQLException("User not found: " + username);
    }

    /**
     * Creates a new user account with the specified credentials.
     * Password is hashed using BCrypt before storage.
     *
     * @param username the new user's login username
     * @param password the new user's plaintext password
     * @param fullName the new user's full name
     * @throws SQLException if a database error occurs or username already exists
     */
    public void createUser(String username, String password, String fullName) throws SQLException {
        String hashedPassword = passwordEncoder.encode(password);
        final String sql = "INSERT INTO users (username, password_hash, full_name) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, fullName);
            pstmt.executeUpdate();
        }
    }

    /**
     * Updates a user's username to a new value.
     * Checks that the new username is not already taken by another user.
     * Updates the session user if the updated user is the current logged-in user.
     *
     * @param id the user ID to update
     * @param newUsername the new username
     * @throws SQLException if the username is already taken or a database error occurs
     */
    public void updateUsername(Long id, String newUsername) throws SQLException {
        // Check if username is already taken by someone else
        final String checkSql = "SELECT id FROM users WHERE username = ? AND id != ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setString(1, newUsername);
            check.setLong(2, id);
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) {
                  throw new SQLException("Username is already taken.");
                }
            }
        }
        final String sql = "UPDATE users SET username = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newUsername);
            pstmt.setLong(2, id);
            pstmt.executeUpdate();
            if (loggedInUser != null && loggedInUser.getId().equals(id)) {
                loggedInUser.setUsername(newUsername);
            }
        }
    }

    /**
     * Updates a user's full name.
     * Updates the session user if the updated user is the current logged-in user.
     *
     * @param id the user ID to update
     * @param newFullName the new full name
     * @throws SQLException if a database error occurs
     */
    public void updateFullName(Long id, String newFullName) throws SQLException {
        final String sql = "UPDATE users SET full_name = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newFullName);
            pstmt.setLong(2, id);
            pstmt.executeUpdate();
            if (loggedInUser != null && loggedInUser.getId().equals(id)) {
                loggedInUser.setFullName(newFullName);
            }
        }
    }

    /**
     * Updates a user's password after verifying the current password.
     * New password is hashed using BCrypt before storage.
     *
     * @param id the user ID to update
     * @param currentPassword the user's current plaintext password for verification
     * @param newPassword the new plaintext password
     * @throws SQLException if the current password is incorrect or a database error occurs
     */
    public void updatePassword(Long id, String currentPassword, String newPassword) throws SQLException {
        // Verify current password first
        final String fetchSql = "SELECT password_hash FROM users WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(fetchSql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next() || !passwordEncoder.matches(currentPassword, rs.getString("password_hash"))) {
                    throw new SQLException("Current password is incorrect.");
                }
            }
        }
        String newHash = passwordEncoder.encode(newPassword);
        final String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newHash);
            pstmt.setLong(2, id);
            pstmt.executeUpdate();
        }
    }
    

    /**
     * Deletes a user account from the database.
     *
     * @param id the user ID to delete
     * @throws SQLException if a database error occurs
     */
    public void deleteUser(Long id) throws SQLException {
        final String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }
}
