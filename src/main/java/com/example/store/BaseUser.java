package com.example.store;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class BaseUser {
    private int id;
    private String fullName;
    private String phone;
    private String login;
    private String passwordHash;
    private String role;

    public BaseUser(int id, String fullName, String phone, String login, String passwordHash, String role) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getLogin() { return login; }
    public String getRole() { return role; }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    public boolean checkPassword(String password) {
        if (this.passwordHash == null) return false;

        if (this.passwordHash.equals(password)) {
            this.passwordHash = hashPassword(password);
            return true;
        }

        return this.passwordHash.equals(hashPassword(password));
    }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}