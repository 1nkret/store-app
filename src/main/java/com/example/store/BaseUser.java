package com.example.store;

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
    public boolean checkPassword(String password) {
        return this.passwordHash != null && this.passwordHash.equals(password);
    }
}