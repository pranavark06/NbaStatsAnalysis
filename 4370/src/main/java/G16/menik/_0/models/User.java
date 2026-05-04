package G16.menik._0.models;

/**
 * Represents a user account in the application.
 * Contains user identity information and credentials management.
 */
public class User {

    private Long id;
    private String username;
    private String fullName;

    /**
     * Default constructor for User.
     */
    public User() {
    }

    /**
     * Constructs a User with specified id, username, and full name.
     *
     * @param id the unique user identifier
     * @param username the user's login username
     * @param fullName the user's full name
     */
    public User(Long id, String username, String fullName) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
    }

    /**
     * Returns the user's unique identifier.
     *
     * @return the user ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the user's unique identifier.
     *
     * @param id the user ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the user's login username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user's login username.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the user's full name.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the user's full name.
     *
     * @param fullName the full name to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
