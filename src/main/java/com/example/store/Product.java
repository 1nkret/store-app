package com.example.store;

public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private int categoryId;
    private transient Category category;

    public static final String STORE_NAME = "MyOnlineStore";

    public Product(int id, String name, String description, double price, int stock, int categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.categoryId = categoryId;
    }

    // Конструктор для обратной совместимости (без описания)
    public Product(int id, String name, double price, int stock, int categoryId) {
        this(id, name, "", price, stock, categoryId);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public int getCategoryId() { return categoryId; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public void setStock(int stock) {
        if (stock < 0) throw new IllegalArgumentException("Залишок не може бути від'ємним.");
        this.stock = stock;
    }

    @Override public String toString() {
        return name + " (" + price + " грн)";
    }
}