package model;

public class Admin extends BaseUser {
    public Admin(String username, String password, String fullName, String phoneNumber) {
        super(username, password, fullName, phoneNumber);
    }

    @Override
    public String getUserType() {
        return "admin";
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    @Override
    public boolean canPerformTransaction() {
        return false;
    }
} 