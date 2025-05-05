package gui;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Admin;
import model.BaseUser;
import model.Customer;
import service.AuthenticationService;
import service.DatabaseUserManager;

public class MainUI extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(MainUI.class.getName());
    private AuthenticationService authService;
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 250);
    private static final Color PRIMARY_COLOR = new Color(100, 149, 237);
    private static final Color SECONDARY_COLOR = new Color(176, 196, 222);
    private static final Color TEXT_COLOR = new Color(51, 51, 51);

    public MainUI() {
        try {
            LOGGER.info("Initializing MainUI");
            authService = new DatabaseUserManager();
            initializeUI();
            LOGGER.info("MainUI initialization completed successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize MainUI", e);
            handleInitializationError(e);
        }
    }

    private void handleInitializationError(Exception e) {
        LOGGER.log(Level.SEVERE, "Failed to initialize the application", e);
        JOptionPane.showMessageDialog(this, 
            "Failed to initialize the application. Please check your database connection.",
            "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    private void initializeUI() {
        setTitle("Banking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Create main panel with padding
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title label
        JLabel titleLabel = new JLabel("Welcome to Banking System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(titleLabel, gbc);

        // Username field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setForeground(TEXT_COLOR);
        panel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        JTextField usernameField = createStyledTextField();
        panel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(TEXT_COLOR);
        panel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        JPasswordField passwordField = createStyledPasswordField();
        panel.add(passwordField, gbc);

        // Login button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        JButton loginButton = createStyledButton("Login");
        panel.add(loginButton, gbc);

        // Register button
        gbc.gridy = 4;
        JButton registerButton = createStyledButton("Register");
        panel.add(registerButton, gbc);

        // Add action listeners
        loginButton.addActionListener(_ -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter both username and password",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            handleLogin(username, password);
        });

        registerButton.addActionListener(_ -> showRegistrationDialog());

        add(panel);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(15);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setBackground(Color.WHITE);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(15);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setBackground(Color.WHITE);
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 40));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
        
        return button;
    }

    private void handleLogin(String username, String password) {
        LOGGER.log(Level.INFO, "Attempting login for user: {0}", username);
        try {
            BaseUser user = authService.login(username, password);
            if (user != null) {
                LOGGER.log(Level.INFO, "Login successful for user: {0}", username);
                JOptionPane.showMessageDialog(this, "Login successful!");
                switch (user) {
                    case Customer customer -> {
                        LOGGER.info("Redirecting to customer dashboard");
                        showCustomerDashboard(customer);
                    }
                    case Admin admin -> {
                        LOGGER.info("Redirecting to admin dashboard");
                        showAdminDashboard(admin);
                    }
                    default -> LOGGER.warning("Unknown user type");
                }
            } else {
                LOGGER.log(Level.WARNING, "Login failed for user: {0}", username);
                JOptionPane.showMessageDialog(this, 
                    "Invalid username or password", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
            } catch (IllegalArgumentException | IllegalStateException e) {
                LOGGER.log(Level.SEVERE, "Validation error during login for user: " + username, e);
                JOptionPane.showMessageDialog(this, 
                    "Invalid login attempt: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (RuntimeException e) {
                LOGGER.log(Level.SEVERE, "Error during login for user: " + username, e);
                JOptionPane.showMessageDialog(this, 
                    "An error occurred during login: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showCustomerDashboard(Customer customer) {
        JFrame dashboard = new JFrame("Customer Dashboard - " + customer.getUsername());
        dashboard.setSize(800, 600);
        dashboard.setLocationRelativeTo(null);
        dashboard.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dashboard.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top panel with user info
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Customer Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        topPanel.add(titleLabel, gbc);

        // User Info
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        addInfoLabel(topPanel, "Full Name:", customer.getFullName(), gbc);
        
        gbc.gridy = 2;
        addInfoLabel(topPanel, "Phone:", customer.getPhoneNumber(), gbc);
        
        gbc.gridy = 3;
        addInfoLabel(topPanel, "Account Number:", customer.getAccountNumber(), gbc);

        // Balance display
        gbc.gridy = 4;
        JLabel balanceTitle = new JLabel("Current Balance:");
        balanceTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        balanceTitle.setForeground(TEXT_COLOR);
        topPanel.add(balanceTitle, gbc);
        
        gbc.gridx = 1;
        JLabel balanceLabel = new JLabel(String.format("$%.2f", customer.getBalance()));
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        balanceLabel.setForeground(PRIMARY_COLOR);
        topPanel.add(balanceLabel, gbc);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel for transactions
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BACKGROUND_COLOR);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Deposit section
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JPanel depositPanel = createTransactionPanel("Deposit Amount:", "Deposit");
        centerPanel.add(depositPanel, gbc);

        // Withdraw section
        gbc.gridy = 1;
        JPanel withdrawPanel = createTransactionPanel("Withdraw Amount:", "Withdraw");
        centerPanel.add(withdrawPanel, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for additional features
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(BACKGROUND_COLOR);

        JButton changePasswordButton = createStyledButton("Change Password");
        JButton viewProfileButton = createStyledButton("View Profile");
        JButton transactionHistoryButton = createStyledButton("Transaction History");

        bottomPanel.add(changePasswordButton);
        bottomPanel.add(viewProfileButton);
        bottomPanel.add(transactionHistoryButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Add action listeners
        JTextField depositField = (JTextField) depositPanel.getComponent(1);
        JButton depositButton = (JButton) depositPanel.getComponent(2);
        JTextField withdrawField = (JTextField) withdrawPanel.getComponent(1);
        JButton withdrawButton = (JButton) withdrawPanel.getComponent(2);

        depositButton.addActionListener(_ -> {
            try {
                double amount = Double.parseDouble(depositField.getText());
                customer.deposit(amount);
                balanceLabel.setText(String.format("$%.2f", customer.getBalance()));
                depositField.setText("");
                JOptionPane.showMessageDialog(dashboard, "Deposit successful!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dashboard, "Please enter a valid amount", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        withdrawButton.addActionListener(_ -> {
            try {
                double amount = Double.parseDouble(withdrawField.getText());
                if (customer.withdraw(amount)) {
                    balanceLabel.setText(String.format("$%.2f", customer.getBalance()));
                    withdrawField.setText("");
                    JOptionPane.showMessageDialog(dashboard, "Withdrawal successful!");
                } else {
                    JOptionPane.showMessageDialog(dashboard, "Insufficient funds", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dashboard, "Please enter a valid amount", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        changePasswordButton.addActionListener(_ -> {
            JPanel passwordPanel = new JPanel(new GridBagLayout());
            passwordPanel.setBackground(BACKGROUND_COLOR);
            GridBagConstraints pgbc = new GridBagConstraints();
            pgbc.insets = new Insets(5, 5, 5, 5);

            JPasswordField oldPasswordField = createStyledPasswordField();
            JPasswordField newPasswordField = createStyledPasswordField();
            JPasswordField confirmPasswordField = createStyledPasswordField();

            pgbc.gridx = 0;
            pgbc.gridy = 0;
            passwordPanel.add(new JLabel("Old Password:"), pgbc);
            pgbc.gridx = 1;
            passwordPanel.add(oldPasswordField, pgbc);

            pgbc.gridx = 0;
            pgbc.gridy = 1;
            passwordPanel.add(new JLabel("New Password:"), pgbc);
            pgbc.gridx = 1;
            passwordPanel.add(newPasswordField, pgbc);

            pgbc.gridx = 0;
            pgbc.gridy = 2;
            passwordPanel.add(new JLabel("Confirm Password:"), pgbc);
            pgbc.gridx = 1;
            passwordPanel.add(confirmPasswordField, pgbc);

            int result = JOptionPane.showConfirmDialog(dashboard, passwordPanel, 
                "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String oldPassword = new String(oldPasswordField.getPassword());
                String newPassword = new String(newPasswordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (!newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(dashboard, "New passwords do not match",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (authService.changePassword(customer, oldPassword, newPassword)) {
                    JOptionPane.showMessageDialog(dashboard, "Password changed successfully!");
                } else {
                    JOptionPane.showMessageDialog(dashboard, "Failed to change password",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        viewProfileButton.addActionListener(_ -> {
            JPanel profilePanel = new JPanel(new GridBagLayout());
            profilePanel.setBackground(BACKGROUND_COLOR);
            GridBagConstraints pgbc = new GridBagConstraints();
            pgbc.insets = new Insets(5, 5, 5, 5);

            pgbc.gridx = 0;
            pgbc.gridy = 0;
            addInfoLabel(profilePanel, "Username:", customer.getUsername(), pgbc);
            
            pgbc.gridy = 1;
            addInfoLabel(profilePanel, "Full Name:", customer.getFullName(), pgbc);
            
            pgbc.gridy = 2;
            addInfoLabel(profilePanel, "Phone:", customer.getPhoneNumber(), pgbc);
            
            pgbc.gridy = 3;
            addInfoLabel(profilePanel, "Account Number:", customer.getAccountNumber(), pgbc);
            
            pgbc.gridy = 4;
            addInfoLabel(profilePanel, "Balance:", String.format("$%.2f", customer.getBalance()), pgbc);

            JOptionPane.showMessageDialog(dashboard, profilePanel, "Profile Information", 
                JOptionPane.PLAIN_MESSAGE);
        });

        transactionHistoryButton.addActionListener(_ -> {
            // In a real application, this would show transaction history
            JOptionPane.showMessageDialog(dashboard, 
                "Transaction history feature coming soon!",
                "Information", JOptionPane.INFORMATION_MESSAGE);
        });

        dashboard.add(mainPanel);
        dashboard.setVisible(true);
    }

    private void addInfoLabel(JPanel panel, String label, String value, GridBagConstraints gbc) {
        JLabel infoLabel = new JLabel(label);
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setForeground(TEXT_COLOR);
        panel.add(infoLabel, gbc);

        gbc.gridx = 1;
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valueLabel.setForeground(PRIMARY_COLOR);
        panel.add(valueLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
    }

    private JPanel createTransactionPanel(String labelText, String buttonText) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(label, gbc);

        JTextField field = createStyledTextField();
        gbc.gridx = 1;
        panel.add(field, gbc);

        JButton button = createStyledButton(buttonText);
        gbc.gridx = 2;
        panel.add(button, gbc);

        return panel;
    }

    private void showAdminDashboard(Admin admin) {
        JFrame dashboard = new JFrame("Admin Dashboard - " + admin.getUsername());
        dashboard.setSize(800, 600);
        dashboard.setLocationRelativeTo(null);
        dashboard.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dashboard.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top panel with admin info
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        topPanel.add(titleLabel, gbc);

        // Admin Info
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        addInfoLabel(topPanel, "Username:", admin.getUsername(), gbc);
        
        gbc.gridy = 2;
        addInfoLabel(topPanel, "Full Name:", admin.getFullName(), gbc);
        
        gbc.gridy = 3;
        addInfoLabel(topPanel, "Phone:", admin.getPhoneNumber(), gbc);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel for user management
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BACKGROUND_COLOR);
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // User management section
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel sectionTitle = new JLabel("User Management");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionTitle.setForeground(TEXT_COLOR);
        centerPanel.add(sectionTitle, gbc);

        gbc.gridy = 1;
        JButton viewUsersButton = createStyledButton("View All Users");
        centerPanel.add(viewUsersButton, gbc);

        gbc.gridy = 2;
        JButton deleteUserButton = createStyledButton("Delete User");
        centerPanel.add(deleteUserButton, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for additional features
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(BACKGROUND_COLOR);

        JButton changePasswordButton = createStyledButton("Change Password");
        JButton viewProfileButton = createStyledButton("View Profile");

        bottomPanel.add(changePasswordButton);
        bottomPanel.add(viewProfileButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Action listeners
        viewUsersButton.addActionListener(_ -> {
            StringBuilder usersList = new StringBuilder("All Users:\n\n");
            for (BaseUser user : authService.getAllUsers()) {
                usersList.append("Username: ").append(user.getUsername())
                        .append(", Type: ").append(user.getUserType())
                        .append("\n");
            }
            JOptionPane.showMessageDialog(dashboard, usersList.toString());
        });

        deleteUserButton.addActionListener(_ -> {
            String username = JOptionPane.showInputDialog(dashboard, "Enter username to delete:");
            if (username != null && !username.trim().isEmpty()) {
                if (authService.deleteUser(username)) {
                    JOptionPane.showMessageDialog(dashboard, "User deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(dashboard, "Failed to delete user", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        changePasswordButton.addActionListener(_ -> {
            JPanel passwordPanel = new JPanel(new GridBagLayout());
            passwordPanel.setBackground(BACKGROUND_COLOR);
            GridBagConstraints pgbc = new GridBagConstraints();
            pgbc.insets = new Insets(5, 5, 5, 5);

            JPasswordField oldPasswordField = createStyledPasswordField();
            JPasswordField newPasswordField = createStyledPasswordField();
            JPasswordField confirmPasswordField = createStyledPasswordField();

            pgbc.gridx = 0;
            pgbc.gridy = 0;
            passwordPanel.add(new JLabel("Old Password:"), pgbc);
            pgbc.gridx = 1;
            passwordPanel.add(oldPasswordField, pgbc);

            pgbc.gridx = 0;
            pgbc.gridy = 1;
            passwordPanel.add(new JLabel("New Password:"), pgbc);
            pgbc.gridx = 1;
            passwordPanel.add(newPasswordField, pgbc);

            pgbc.gridx = 0;
            pgbc.gridy = 2;
            passwordPanel.add(new JLabel("Confirm Password:"), pgbc);
            pgbc.gridx = 1;
            passwordPanel.add(confirmPasswordField, pgbc);

            int result = JOptionPane.showConfirmDialog(dashboard, passwordPanel, 
                "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String oldPassword = new String(oldPasswordField.getPassword());
                String newPassword = new String(newPasswordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (!newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(dashboard, "New passwords do not match",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (authService.changePassword(admin, oldPassword, newPassword)) {
                    JOptionPane.showMessageDialog(dashboard, "Password changed successfully!");
                } else {
                    JOptionPane.showMessageDialog(dashboard, "Failed to change password",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        viewProfileButton.addActionListener(_ -> {
            JPanel profilePanel = new JPanel(new GridBagLayout());
            profilePanel.setBackground(BACKGROUND_COLOR);
            GridBagConstraints pgbc = new GridBagConstraints();
            pgbc.insets = new Insets(5, 5, 5, 5);

            pgbc.gridx = 0;
            pgbc.gridy = 0;
            addInfoLabel(profilePanel, "Username:", admin.getUsername(), pgbc);
            
            pgbc.gridy = 1;
            addInfoLabel(profilePanel, "Full Name:", admin.getFullName(), pgbc);
            
            pgbc.gridy = 2;
            addInfoLabel(profilePanel, "Phone:", admin.getPhoneNumber(), pgbc);
            
            pgbc.gridy = 3;
            addInfoLabel(profilePanel, "Role:", admin.getRole(), pgbc);

            JOptionPane.showMessageDialog(dashboard, profilePanel, "Profile Information", 
                JOptionPane.PLAIN_MESSAGE);
        });

        dashboard.add(mainPanel);
        dashboard.setVisible(true);
    }

    private void showRegistrationDialog() {
        LOGGER.info("Showing registration dialog");
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        JLabel titleLabel = new JLabel("Register New User");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(titleLabel, gbc);

        // Username field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setForeground(TEXT_COLOR);
        panel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        JTextField usernameField = createStyledTextField();
        panel.add(usernameField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(TEXT_COLOR);
        panel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        JPasswordField passwordField = createStyledPasswordField();
        panel.add(passwordField, gbc);

        // Full Name field
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel fullNameLabel = new JLabel("Full Name:");
        fullNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fullNameLabel.setForeground(TEXT_COLOR);
        panel.add(fullNameLabel, gbc);
        
        gbc.gridx = 1;
        JTextField fullNameField = createStyledTextField();
        panel.add(fullNameField, gbc);

        // Phone field
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel phoneLabel = new JLabel("Phone (11 digits starting with 01):");
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        phoneLabel.setForeground(TEXT_COLOR);
        panel.add(phoneLabel, gbc);
        
        gbc.gridx = 1;
        JTextField phoneField = createStyledTextField();
        panel.add(phoneField, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Register New User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String fullName = fullNameField.getText().trim();
            String phone = phoneField.getText().trim();

            LOGGER.log(Level.INFO, "Attempting to register new user: {0}", username);
            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || phone.isEmpty()) {
                LOGGER.warning("Registration failed: Empty fields");
                JOptionPane.showMessageDialog(this, 
                    "Please fill in all fields",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate phone number
            if (!phone.matches("^01\\d{9}$")) {
                LOGGER.log(Level.WARNING, "Registration failed: Invalid phone number format - {0}", phone);
                JOptionPane.showMessageDialog(this, 
                    "Phone number must be 11 digits starting with 01",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                if (authService.registerUser(new Customer(username, password, fullName, phone, "ACC" + System.currentTimeMillis()))) {
                    LOGGER.log(Level.INFO, "Registration successful for user: {0}", username);
                    JOptionPane.showMessageDialog(this, "Registration successful!");
                } else {
                    LOGGER.log(Level.WARNING, "Registration failed: Username may already exist - {0}", username);
                    JOptionPane.showMessageDialog(this, 
                        "Registration failed. Username may already exist.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                LOGGER.log(Level.SEVERE, "Validation error during registration for user: " + username, e);
                JOptionPane.showMessageDialog(this, 
                    "A validation error occurred during registration: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (RuntimeException e) {
                LOGGER.log(Level.SEVERE, "Unexpected error during registration for user: " + username, e);
                JOptionPane.showMessageDialog(this, 
                    "An unexpected error occurred during registration: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainUI().setVisible(true);
        });
    }
}
