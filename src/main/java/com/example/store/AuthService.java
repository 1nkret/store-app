package com.example.store;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AuthService {
    private static AuthService instance;
    private final Gson gson;
    private List<BaseUser> users;

    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + File.separator + "users.json";

    private AuthService() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(BaseUser.class, new BaseUserAdapter())
                .create();
        loadData();
    }

    public static AuthService getInstance() {
        if (instance == null) instance = new AuthService();
        return instance;
    }

    private void ensureDir() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    private void loadData() {
        ensureDir();
        try (FileReader reader = new FileReader(USERS_FILE)) {
            Type listType = new TypeToken<ArrayList<BaseUser>>(){}.getType();
            users = gson.fromJson(reader, listType);
            if (users == null) users = new ArrayList<>();
        } catch (IOException e) {
            users = new ArrayList<>();
        }

        if (users.isEmpty()) {
            Admin admin = new Admin(1, "Адміністратор", "+380000000000", "admin", "admin");
            users.add(admin);
            saveData();
        }
    }

    public void saveData() {
        ensureDir();
        try (FileWriter w = new FileWriter(USERS_FILE)) {
            Type listType = new TypeToken<ArrayList<BaseUser>>(){}.getType();
            gson.toJson(users, listType, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BaseUser login(String login, String password) {
        return users.stream()
                .filter(u -> u.getLogin().equals(login) && u.checkPassword(password))
                .findFirst()
                .orElse(null);
    }

    public Customer register(String fullName, String phone, String login, String password) {
        if (users.stream().anyMatch(u -> u.getLogin().equals(login))) {
            throw new IllegalArgumentException("Користувач з таким логіном вже існує.");
        }
        if (users.stream().anyMatch(u -> u.getPhone().equals(phone))) {
            throw new IllegalArgumentException("Користувач з таким телефоном вже існує.");
        }

        int nextId = users.stream().mapToInt(BaseUser::getId).max().orElse(0) + 1;
        Customer newUser = new Customer(nextId, fullName, phone, login, password);
        users.add(newUser);
        saveData();
        return newUser;
    }

    public List<Customer> getAllCustomers() {
        return users.stream()
                .filter(u -> "CUSTOMER".equals(u.getRole()))
                .map(u -> (Customer) u)
                .collect(Collectors.toList());
    }

    public Map<Integer, Customer> getCustomersMap() {
        return getAllCustomers().stream()
                .collect(Collectors.toMap(Customer::getId, Function.identity()));
    }
}