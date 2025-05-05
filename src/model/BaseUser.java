package model;

public abstract class BaseUser {
    protected String username;
    protected String password;
    protected String fullName;
    protected String phoneNumber;

    public BaseUser(String username, String password, String fullName, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    // Abstract methods that must be implemented by subclasses
    public abstract String getRole();
    public abstract boolean canPerformTransaction();
    public abstract String getUserType();
} 