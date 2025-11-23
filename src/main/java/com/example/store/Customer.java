package com.example.store;

public final class Customer extends BaseUser {
    public Customer(int id, String fullName, String phone, String login, String passwordHash) {
        super(id, fullName, phone, login, passwordHash, "CUSTOMER");
    }
}