package service;

import model.BaseUser;
import java.util.List;

public interface AuthenticationService {
    boolean registerUser(BaseUser user);
    BaseUser login(String username, String password);
    boolean logout(BaseUser user);
    boolean changePassword(BaseUser user, String oldPassword, String newPassword);
    List<BaseUser> getAllUsers();
    boolean deleteUser(String username);
} 