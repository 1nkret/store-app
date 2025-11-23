package com.example.store;

public final class Admin extends BaseUser {
    public Admin(int id, String fullName, String phone, String login, String passwordHash) {
        super(id, fullName, phone, login, passwordHash, "ADMIN");
    }
}