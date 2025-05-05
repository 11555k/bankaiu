package model;

import service.AuthenticationService;
import service.DatabaseUserManager;

public class Customer extends BaseUser {
    private double balance;
    private String accountNumber;
    private AuthenticationService authService;

    public Customer(String username, String password, String fullName, String phoneNumber, String accountNumber) {
        super(username, password, fullName, phoneNumber);
        this.balance = 0.0;
        this.accountNumber = accountNumber;
        this.authService = new DatabaseUserManager();
    }

    @Override
    public String getRole() {
        return "CUSTOMER";
    }

    @Override
    public boolean canPerformTransaction() {
        return true;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
        if (authService instanceof DatabaseUserManager) {
            ((DatabaseUserManager) authService).updateCustomerBalance(this);
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public String getUserType() {
        return "customer";
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            if (authService instanceof DatabaseUserManager) {
                ((DatabaseUserManager) authService).updateCustomerBalance(this);
            }
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            if (authService instanceof DatabaseUserManager) {
                ((DatabaseUserManager) authService).updateCustomerBalance(this);
            }
            return true;
        }
        return false;
    }
} 