package com.example.store;

public class OrderItem {
    private int productId;
    private transient Product product;
    private int quantity;
    private double priceAtPurchase;

    public OrderItem(Product product, int quantity) {
        if (product == null) throw new IllegalArgumentException("Товар не може бути null");
        if (quantity <= 0) throw new IllegalArgumentException("Кількість має бути > 0");

        this.product = product;
        this.productId = product.getId();
        this.quantity = quantity;
        this.priceAtPurchase = product.getPrice();
    }

    public void linkProduct(Product product) {
        this.product = product;
    }

    public int getProductId() { return productId; }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getPriceAtPurchase() { return priceAtPurchase; }
}