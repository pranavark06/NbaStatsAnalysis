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
 * This is a service class that enables user related functions.
 * The class interacts with the database through a dataSource instance.
 */
@Service
@SessionScope
public class UserService {

    // dataSource enables talking to the database.
    private final DataSource dataSource;
    // passwordEncoder is used for password security.
    private final BCryptPasswordEncoder passwordEncoder;
    // This holds the logged in user for the session.
    private User loggedInUser = null;

    /**
     * See AuthInterceptor notes regarding dependency injection and
     * inversion of control.
     */
    @Autowired
    public UserService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

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
     * Find user by username for Spring Security authentication.
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

    public void unAuthenticate() {
        loggedInUser = null;
    }

    public boolean isAuthenticated() {
        return loggedInUser != null;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

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
     * Create a new user account.
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
}
