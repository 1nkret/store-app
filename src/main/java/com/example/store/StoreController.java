package com.example.store;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StoreController {
    @FXML private ListView<Product> productListView;
    @FXML private ListView<String> cartListView;
    @FXML private Label totalPriceLabel;
    @FXML private Text actionTarget;
    @FXML private ComboBox<Category> categoryComboBox;

    private final CatalogService catalogService = CatalogService.getInstance();
    private final OrderService orderService = OrderService.getInstance();
    private Customer currentUser;

    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private final ObservableList<OrderItem> cartItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        productListView.setItems(products);

        // Добавить отображение описания товара при наведении
        productListView.setCellFactory(lv -> new ListCell<Product>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item.toString());
                    if (item.getDescription() != null && !item.getDescription().isEmpty()) {
                        Tooltip tooltip = new Tooltip(item.getDescription());
                        tooltip.setWrapText(true);
                        tooltip.setMaxWidth(300);
                        setTooltip(tooltip);
                    }
                }
            }
        });

        loadCategories();
        loadProducts();
        updateCartView();
    }

    public void setCurrentUser(Customer user) {
        this.currentUser = user;
    }

    private void loadCategories() {
        categoryComboBox.setItems(FXCollections.observableArrayList(catalogService.getAllCategories()));
        categoryComboBox.getItems().add(0, new Category(0, "Всі категорії"));
        categoryComboBox.getSelectionModel().selectFirst();
        categoryComboBox.setOnAction(e -> filterProducts());
    }

    private void loadProducts() {
        products.setAll(catalogService.getAllProducts());
    }

    private void filterProducts() {
        Category selected = categoryComboBox.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == 0) {
            loadProducts();
        } else {
            products.setAll(catalogService.getAllProducts().stream()
                    .filter(p -> p.getCategoryId() == selected.getId())
                    .collect(Collectors.toList()));
        }
    }

    @FXML
    public void handleAddToCartAction() {
        Product selectedProduct = productListView.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            actionTarget.setText("Оберіть товар для додавання.");
            return;
        }
        if (selectedProduct.getStock() <= 0) {
            actionTarget.setText("Товару '" + selectedProduct.getName() + "' немає на складі.");
            return;
        }

        OrderItem existingItem = null;
        for (OrderItem item : cartItems) {
            if (item.getProductId() == selectedProduct.getId()) {
                existingItem = item;
                break;
            }
        }

        if (existingItem != null) {
            if (selectedProduct.getStock() >= existingItem.getQuantity() + 1) {
                cartItems.remove(existingItem);
                cartItems.add(new OrderItem(selectedProduct, existingItem.getQuantity() + 1));
            } else {
                actionTarget.setText("Більше товару '" + selectedProduct.getName() + "' немає на складі.");
            }
        } else {
            cartItems.add(new OrderItem(selectedProduct, 1));
        }
        updateCartView();
        actionTarget.setText("Товар '" + selectedProduct.getName() + "' додано до кошика.");
    }

    @FXML
    public void handleCheckoutAction() {
        if (cartItems.isEmpty()) {
            actionTarget.setText("Кошик порожній.");
            return;
        }
        if (currentUser == null) {
            actionTarget.setText("Помилка: користувач не визначений. Увійдіть знову.");
            return;
        }

        try {
            Order newOrder = orderService.createOrder(currentUser, new ArrayList<>(cartItems));
            actionTarget.setText("Замовлення #" + newOrder.getId() + " успішно створено!");
            cartItems.clear();
            updateCartView();
            filterProducts();
        } catch (IllegalArgumentException e) {
            actionTarget.setText("Помилка оформлення: " + e.getMessage());
        } catch (Exception e) {
            actionTarget.setText("Невідома помилка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateCartView() {
        List<String> cartDisplay = cartItems.stream()
                .filter(item -> item.getProduct() != null)
                .map(item -> item.getProduct().getName() + " x " + item.getQuantity() + " (" + String.format("%.2f", item.getPriceAtPurchase() * item.getQuantity()) + " грн)")
                .collect(Collectors.toList());
        cartListView.setItems(FXCollections.observableArrayList(cartDisplay));

        double total = cartItems.stream()
                .mapToDouble(item -> item.getPriceAtPurchase() * item.getQuantity())
                .sum();
        totalPriceLabel.setText(String.format("Загалом: %.2f грн", total));
    }

    @FXML
    public void handleGoToReports() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("report-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) actionTarget.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Звіти");
        } catch (IOException e) {
            actionTarget.setText("Помилка переходу до звітів.");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("login-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) actionTarget.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Вхід до системи");
        } catch (IOException e) {
            actionTarget.setText("Помилка виходу з системи.");
            e.printStackTrace();
        }
    }
}