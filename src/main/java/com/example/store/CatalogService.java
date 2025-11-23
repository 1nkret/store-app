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
import java.util.function.Function;
import java.util.stream.Collectors;

public class CatalogService {
    private static CatalogService instance;
    private final Gson gson;
    private List<Product> products;
    private List<Category> categories;
    private Map<Integer, Category> categoryMap;

    private static final String DATA_DIR = "data";
    private static final String PRODUCTS_FILE = DATA_DIR + File.separator + "products.json";
    private static final String CATEGORIES_FILE = DATA_DIR + File.separator + "categories.json";

    private CatalogService() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        loadData();
    }

    public static CatalogService getInstance() {
        if (instance == null) instance = new CatalogService();
        return instance;
    }

    private void ensureDir() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    private void loadData() {
        ensureDir();
        try (FileReader reader = new FileReader(CATEGORIES_FILE)) {
            Type listType = new TypeToken<ArrayList<Category>>(){}.getType();
            categories = gson.fromJson(reader, listType);
            if (categories == null) categories = new ArrayList<>();
            categoryMap = categories.stream().collect(Collectors.toMap(Category::getId, Function.identity()));
        } catch (IOException e) {
            categories = new ArrayList<>();
            categoryMap = new HashMap<>();
        }

        try (FileReader reader = new FileReader(PRODUCTS_FILE)) {
            Type listType = new TypeToken<ArrayList<Product>>(){}.getType();
            products = gson.fromJson(reader, listType);
            if (products == null) products = new ArrayList<>();
        } catch (IOException e) {
            products = new ArrayList<>();
        }

        for (Product p : products) {
            p.setCategory(categoryMap.get(p.getCategoryId()));
        }
    }

    public void saveData() {
        ensureDir();
        try (FileWriter w = new FileWriter(CATEGORIES_FILE)) {
            gson.toJson(categories, w);
        } catch (IOException e) { e.printStackTrace(); }

        try (FileWriter w = new FileWriter(PRODUCTS_FILE)) {
            gson.toJson(products, w);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public List<Product> getAllProducts() { return new ArrayList<>(products); }
    public List<Category> getAllCategories() { return new ArrayList<>(categories); }

    public Product getProductById(int id) {
        return products.stream().filter(p -> p.getId() == id).findFirst().orElse(null);
    }

    public Category getCategoryById(int id) {
        return categoryMap.get(id);
    }

    // Управление категориями
    public Category addCategory(String name, String description) {
        int nextId = categories.stream().mapToInt(Category::getId).max().orElse(0) + 1;
        Category category = new Category(nextId, name);
        categories.add(category);
        categoryMap.put(nextId, category);
        saveData();
        return category;
    }

    public void updateCategory(int id, String name, String description) {
        Category category = categoryMap.get(id);
        if (category == null) {
            throw new IllegalArgumentException("Категорія з ID " + id + " не знайдена.");
        }
        categories.remove(category);
        Category updated = new Category(id, name);
        categories.add(updated);
        categoryMap.put(id, updated);

        // Обновить ссылки на категорию во всех товарах
        for (Product p : products) {
            if (p.getCategoryId() == id) {
                p.setCategory(updated);
            }
        }

        saveData();
    }

    public void deleteCategory(int id) {
        Category category = categoryMap.get(id);
        if (category == null) {
            throw new IllegalArgumentException("Категорія з ID " + id + " не знайдена.");
        }
        if (products.stream().anyMatch(p -> p.getCategoryId() == id)) {
            throw new IllegalArgumentException("Неможливо видалити категорію: є товари в цій категорії.");
        }
        categories.remove(category);
        categoryMap.remove(id);
        saveData();
    }

    // Управление товарами
    public Product addProduct(String name, String description, double price, int stock, int categoryId) {
        if (!categoryMap.containsKey(categoryId)) {
            throw new IllegalArgumentException("Категорія з ID " + categoryId + " не існує.");
        }
        int nextId = products.stream().mapToInt(Product::getId).max().orElse(0) + 1;
        Product product = new Product(nextId, name, description, price, stock, categoryId);
        product.setCategory(categoryMap.get(categoryId));
        products.add(product);
        saveData();
        return product;
    }

    public void updateProduct(int id, String name, String description, double price, int stock, int categoryId) {
        Product product = getProductById(id);
        if (product == null) {
            throw new IllegalArgumentException("Товар з ID " + id + " не знайдено.");
        }
        if (!categoryMap.containsKey(categoryId)) {
            throw new IllegalArgumentException("Категорія з ID " + categoryId + " не існує.");
        }
        products.remove(product);
        Product updated = new Product(id, name, description, price, stock, categoryId);
        updated.setCategory(categoryMap.get(categoryId));
        products.add(updated);
        saveData();
    }

    public void deleteProduct(int id) {
        Product product = getProductById(id);
        if (product == null) {
            throw new IllegalArgumentException("Товар з ID " + id + " не знайдено.");
        }
        products.remove(product);
        saveData();
    }

    public void updateProductStock(int productId, int newStock) {
        Product product = getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Товар з ID " + productId + " не знайдено.");
        }
        products.remove(product);
        Product updated = new Product(product.getId(), product.getName(), product.getDescription(),
                                      product.getPrice(), newStock, product.getCategoryId());
        updated.setCategory(product.getCategory());
        products.add(updated);
        saveData();
    }
}