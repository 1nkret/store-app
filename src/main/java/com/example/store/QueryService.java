package com.example.store;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QueryService {
    private static QueryService instance;
    private final AuthService authService;
    private final OrderService orderService;
    private final CatalogService catalogService;

    private QueryService() {
        this.authService = AuthService.getInstance();
        this.orderService = OrderService.getInstance();
        this.catalogService = CatalogService.getInstance();
    }

    public static QueryService getInstance() {
        if (instance == null) instance = new QueryService();
        return instance;
    }

    public List<Customer> getUsersList() {
        return authService.getAllCustomers();
    }

    public List<Order> getOrdersByDate(LocalDate date) {
        if (date == null) return Collections.emptyList();
        return orderService.getAllOrders().stream()
                .filter(o -> o.getOrderDate().equals(date))
                .collect(Collectors.toList());
    }

    public double getAverageOrderCost() {
        return orderService.getAllOrders().stream()
                .mapToDouble(Order::getTotalPrice)
                .average().orElse(0.0);
    }

    public Product getMostPopularProduct() {
        Map<Product, Integer> productCounts = orderService.getAllOrders().stream()
                .flatMap(order -> order.getItems().stream())
                .filter(item -> item.getProduct() != null)
                .collect(Collectors.groupingBy(
                        OrderItem::getProduct,
                        Collectors.summingInt(OrderItem::getQuantity)
                ));

        return productCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public Customer getHighestSpender() {
        Map<Customer, Double> customerSpending = orderService.getAllOrders().stream()
                .filter(o -> o.getCustomer() != null)
                .collect(Collectors.groupingBy(
                        Order::getCustomer,
                        Collectors.summingDouble(Order::getTotalPrice)
                ));

        return customerSpending.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public List<Category> getTop3Categories() {
        Map<Category, Integer> categoryCounts = orderService.getAllOrders().stream()
                .flatMap(order -> order.getItems().stream())
                .filter(item -> item.getProduct() != null && item.getProduct().getCategory() != null)
                .collect(Collectors.groupingBy(
                        item -> item.getProduct().getCategory(),
                        Collectors.summingInt(OrderItem::getQuantity)
                ));

        return categoryCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}