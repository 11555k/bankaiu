package service;
import model.BaseUser;
import model.Customer;
import model.Admin;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String ENCRYPTED_FILE = "users.enc";

    public static void saveUsers(List<BaseUser> users) {
        try {
            String serializedData = serializeUsers(users);
            // Create a temporary file to store the serialized data
            File tempFile = new File("temp.dat");
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(serializedData);
            }
            // Encrypt the temporary file
            SecurityManager.encryptFile(tempFile.getPath(), ENCRYPTED_FILE);
            // Delete the temporary file
            tempFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String serializeUsers(List<BaseUser> users) {
        StringBuilder sb = new StringBuilder();
        for (BaseUser user : users) {
            sb.append(user.getUsername()).append(",")
              .append(user.getPassword()).append(",")
              .append(user.getFullName()).append(",")
              .append(user.getPhoneNumber()).append(",")
              .append(user.getUserType()).append("\n");
        };
        return sb.toString();
    }

    public static List<BaseUser> loadUsers() {
        List<BaseUser> users = new ArrayList<>();
        try {
            // Create a temporary file for decryption
            File tempFile = new File("temp.dat");
            // Decrypt the file
            SecurityManager.decryptFile(ENCRYPTED_FILE, tempFile.getPath());
            
            // Read the decrypted data
            try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 5) {
                        String userType = parts[4];
                        if ("customer".equals(userType)) {
                            users.add(new Customer(parts[0], parts[1], parts[2], parts[3], "ACC" + System.currentTimeMillis()));
                        } else if ("admin".equals(userType)) {
                            users.add(new Admin(parts[0], parts[1], parts[2], parts[3]));
                        }
                    }
                }
            }
            // Delete the temporary file
            tempFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }
} 