package service;

import model.BaseUser;
import model.Customer;
import model.Admin;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DatabaseUserManager implements AuthenticationService {
    private static final Logger LOGGER = Logger.getLogger(DatabaseUserManager.class.getName());
    
    private static final String CREATE_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS users (
            id INT AUTO_INCREMENT PRIMARY KEY,
            username VARCHAR(50) UNIQUE NOT NULL,
            password VARCHAR(255) NOT NULL,
            full_name VARCHAR(100) NOT NULL,
            phone_number VARCHAR(20) NOT NULL,
            user_type VARCHAR(20) NOT NULL,
            account_number VARCHAR(20),
            balance DECIMAL(10,2) DEFAULT 0.00
        )
    """;

    private static final String CHECK_USERNAME_SQL = "SELECT COUNT(*) FROM users WHERE username = ?";
    private static final String INSERT_USER_SQL = "INSERT INTO users (username, password, full_name, phone_number, user_type, balance, account_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String GET_USER_SQL = "SELECT * FROM users WHERE username = ?";
    private static final String DELETE_USER_SQL = "DELETE FROM users WHERE username = ?";
    private static final String GET_ALL_USERS_SQL = "SELECT * FROM users";
    private static final String UPDATE_BALANCE_SQL = "UPDATE users SET balance = ? WHERE username = ?";
    private static final String UPDATE_PASSWORD_SQL = "UPDATE users SET password = ? WHERE username = ? AND password = ?";

    public DatabaseUserManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
            LOGGER.info("Database initialized successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize database", e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    @Override
    public boolean registerUser(BaseUser user) {
        LOGGER.info(() -> "Attempting to register user: " + user.getUsername());
        
        try (Connection conn = DatabaseManager.getConnection()) {
            // First check if username exists
            try (PreparedStatement checkStmt = conn.prepareStatement(CHECK_USERNAME_SQL)) {
                checkStmt.setString(1, user.getUsername());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    LOGGER.warning(() -> "Username already exists: " + user.getUsername());
                    return false;
                }
            }

            // If username doesn't exist, proceed with registration
            try (PreparedStatement stmt = conn.prepareStatement(INSERT_USER_SQL)) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, SecurityManager.hashPassword(user.getPassword()));
                stmt.setString(3, user.getFullName());
                stmt.setString(4, user.getPhoneNumber());
                stmt.setString(5, user.getUserType());
                
                if (user instanceof Customer customer) {
                    stmt.setDouble(6, customer.getBalance());
                    stmt.setString(7, customer.getAccountNumber());
                } else {
                    stmt.setDouble(6, 0.0);
                    stmt.setString(7, null);
                }

                int rowsAffected = stmt.executeUpdate();
                LOGGER.info(() -> "Registration completed with " + rowsAffected + " rows affected");
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during registration", e);
            return false;
        }
    }

    @Override
    public BaseUser login(String username, String password) {
        LOGGER.info(() -> "Attempting login for user: " + username);
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_USER_SQL)) {
            
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    String hashedInputPassword = SecurityManager.hashPassword(password);
                    
                    if (storedPassword.equals(hashedInputPassword)) {
                        String userType = rs.getString("user_type");
                        LOGGER.info(() -> "Login successful for user type: " + userType);
                        
                        String dbUsername = rs.getString("username");
                        String dbFullName = rs.getString("full_name");
                        String dbPhoneNumber = rs.getString("phone_number");
                        String dbAccountNumber = rs.getString("account_number");
                        double dbBalance = rs.getDouble("balance");
                        
                        return "customer".equalsIgnoreCase(userType) 
                            ? createCustomer(dbUsername, password, dbFullName, dbPhoneNumber, dbAccountNumber, dbBalance)
                            : new Admin(dbUsername, password, dbFullName, dbPhoneNumber);
                    }
                    LOGGER.warning(() -> "Password mismatch for user: " + username);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during login", e);
        }
        return null;
    }

    private Customer createCustomer(String username, String password, String fullName, String phoneNumber, String accountNumber, double balance) {
        Customer customer = new Customer(username, password, fullName, phoneNumber, accountNumber);
        customer.setBalance(balance);
        return customer;
    }

    @Override
    public boolean logout(BaseUser user) {
        return true;
    }

    @Override
    public boolean changePassword(BaseUser user, String oldPassword, String newPassword) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_PASSWORD_SQL)) {
            
            pstmt.setString(1, SecurityManager.hashPassword(newPassword));
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, SecurityManager.hashPassword(oldPassword));
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error changing password", e);
            return false;
        }
    }

    public boolean updateCustomerBalance(Customer customer) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_BALANCE_SQL)) {
            
            stmt.setDouble(1, customer.getBalance());
            stmt.setString(2, customer.getUsername());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating customer balance", e);
            return false;
        }
    }

    public double getCustomerBalance(String username) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_USER_SQL)) {
            
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("balance");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting balance", e);
        }
        return 0.0;
    }

    public List<BaseUser> getAllUsers() {
        List<BaseUser> users = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_ALL_USERS_SQL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String fullName = rs.getString("full_name");
                String phoneNumber = rs.getString("phone_number");
                String userType = rs.getString("user_type");
                String accountNumber = rs.getString("account_number");
                double balance = rs.getDouble("balance");

                if ("customer".equalsIgnoreCase(userType)) {
                    users.add(createCustomer(username, password, fullName, phoneNumber, accountNumber, balance));
                } else if ("admin".equalsIgnoreCase(userType)) {
                    users.add(new Admin(username, password, fullName, phoneNumber));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all users", e);
        }
        return users;
    }

    public boolean deleteUser(String username) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_USER_SQL)) {
            
            stmt.setString(1, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting user", e);
            return false;
        }
    }
} 