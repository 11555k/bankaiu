package service;

import model.BaseUser;
import model.Customer;
import model.Admin;
import java.util.ArrayList;
import java.util.List;

public class UserManager implements AuthenticationService {
    private List<BaseUser> users;

    public UserManager() {
        this.users = new ArrayList<>();
        loadUsers();
    }

    private void loadUsers() {
        users = FileManager.loadUsers();
    }

    @Override
    public boolean registerUser(BaseUser user) {
        if (userExists(user.getUsername())) {
            return false;
        }
        users.add(user);
        FileManager.saveUsers(users);
        return true;
    }

    @Override
    public BaseUser login(String username, String password) {
        for (BaseUser user : users) {
            if (user.getUsername().equals(username) && 
                SecurityManager.verifyPassword(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    @Override
    public boolean logout(BaseUser user) {
        return true;
    }

    @Override
    public boolean changePassword(BaseUser user, String oldPassword, String newPassword) {
        if (SecurityManager.verifyPassword(oldPassword, user.getPassword())) {
            String hashedPassword = SecurityManager.hashPassword(newPassword);
            if (user instanceof Admin) {
                Admin admin = (Admin) user;
                admin = new Admin(admin.getUsername(), hashedPassword,
                                admin.getFullName(), admin.getPhoneNumber());
                users.set(users.indexOf(user), admin);
            } else if (user instanceof Customer) {
                Customer customer = (Customer) user;
                customer = new Customer(customer.getUsername(), hashedPassword,
                                      customer.getFullName(), customer.getPhoneNumber(),
                                      customer.getAccountNumber());
                users.set(users.indexOf(user), customer);
            }
            FileManager.saveUsers(users);
            return true;
        }
        return false;
    }

    private boolean userExists(String username) {
        return users.stream().anyMatch(user -> user.getUsername().equals(username));
    }

    public List<BaseUser> getAllUsers() {
        return new ArrayList<>(users);
    }

    public boolean updateUser(BaseUser user) {
        int index = users.indexOf(user);
        if (index != -1) {
            users.set(index, user);
            FileManager.saveUsers(users);
            return true;
        }
        return false;
    }

    public boolean deleteUser(String username) {
        BaseUser userToRemove = users.stream()
            .filter(user -> user.getUsername().equals(username))
            .findFirst()
            .orElse(null);

        if (userToRemove != null) {
            users.remove(userToRemove);
            FileManager.saveUsers(users);
            return true;
        }
        return false;
    }
}
