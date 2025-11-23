package com.example.store;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class OrderService {
    private static OrderService instance;
    private final Gson gson;
    private List<Order> orders;

    private static final String DATA_DIR = "data";
    private static final String ORDERS_FILE = DATA_DIR + File.separator + "orders.json";

    private OrderService() {
        gson = new GsonBuilder()
                .registerTypeAdapter(java.time.LocalDate.class, new LocalDateAdapter())
                .setPrettyPrinting()
                .create();
        loadData();
    }

    public static OrderService getInstance() {
        if (instance == null) instance = new OrderService();
        return instance;
    }

    private void ensureDir() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    private void loadData() {
        ensureDir();
        try (FileReader r = new FileReader(ORDERS_FILE)) {
            Type listType = new TypeToken<ArrayList<Order>>(){}.getType();
            orders = gson.fromJson(r, listType);
            if (orders == null) orders = new ArrayList<>();
        } catch (IOException e) {
            orders = new ArrayList<>();
        }

        Map<Integer, Customer> customersMap = AuthService.getInstance().getCustomersMap();
        CatalogService catalogService = CatalogService.getInstance();

        for (Order o : orders) {
            o.linkCustomer(customersMap.get(o.getCustomerId()));
            if (o.getItems() != null) {
                for (OrderItem item : o.getItems()) {
                    item.linkProduct(catalogService.getProductById(item.getProductId()));
                }
            }
        }
    }

    public void saveData() {
        ensureDir();
        try (FileWriter w = new FileWriter(ORDERS_FILE)) {
            gson.toJson(orders, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Order> getAllOrders() { return new ArrayList<>(orders); }

    public synchronized Order createOrder(Customer customer, List<OrderItem> items) {
        CatalogService catalogService = CatalogService.getInstance();
        for (OrderItem item : items) {
            Product product = catalogService.getProductById(item.getProductId());
            if (product == null || product.getStock() < item.getQuantity()) {
                throw new IllegalArgumentException("Недостатньо товару '" + (product != null ? product.getName() : "ID:"+item.getProductId()) + "' на складі.");
            }
        }

        for (OrderItem item : items) {
            Product product = catalogService.getProductById(item.getProductId());
            product.setStock(product.getStock() - item.getQuantity());
        }
        catalogService.saveData();

        int nextId = orders.stream().mapToInt(Order::getId).max().orElse(0) + 1;
        Order newOrder = new Order(nextId, customer, items);
        orders.add(newOrder);
        saveData();
        return newOrder;
    }
}