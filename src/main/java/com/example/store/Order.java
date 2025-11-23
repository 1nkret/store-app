package com.example.store;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class Order {
    private int id;
    private LocalDate orderDate;
    private OrderStatus status;
    private int customerId;
    private transient Customer customer;
    private List<OrderItem> items;
    private double totalPrice;

    public Order(int id, Customer customer, List<OrderItem> items) {
        if (customer == null) throw new IllegalArgumentException("Клієнт не може бути null");
        if (items == null || items.isEmpty()) throw new IllegalArgumentException("Замовлення має містити товари");

        this.id = id;
        this.customer = customer;
        this.customerId = customer.getId();
        this.items = items;
        this.orderDate = LocalDate.now();
        this.status = OrderStatus.NEW;
        this.totalPrice = items.stream()
                .mapToDouble(item -> item.getPriceAtPurchase() * item.getQuantity())
                .sum();
    }

    public void linkCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getId() { return id; }
    public LocalDate getOrderDate() { return orderDate; }
    public OrderStatus getStatus() { return status; }
    public int getCustomerId() { return customerId; }
    public Customer getCustomer() { return customer; }
    public List<OrderItem> getItems() { return items; }
    public double getTotalPrice() { return totalPrice; }
}