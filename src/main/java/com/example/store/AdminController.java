package com.example.store;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminController {
    // Вкладка управления товарами
    @FXML private ListView<Product> productListView;
    @FXML private TextField productNameField;
    @FXML private TextArea productDescField;
    @FXML private TextField productPriceField;
    @FXML private TextField productStockField;
    @FXML private ComboBox<Category> productCategoryCombo;
    @FXML private Text productActionTarget;

    // Вкладка управления категориями
    @FXML private ListView<Category> categoryListView;
    @FXML private TextField categoryNameField;
    @FXML private Text categoryActionTarget;

    private final CatalogService catalogService = CatalogService.getInstance();
    private Product selectedProduct;
    private Category selectedCategory;

    @FXML
    public void initialize() {
        loadProducts();
        loadCategories();

        productListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedProduct = newVal;
            if (newVal != null) {
                productNameField.setText(newVal.getName());
                productDescField.setText(newVal.getDescription());
                productPriceField.setText(String.valueOf(newVal.getPrice()));
                productStockField.setText(String.valueOf(newVal.getStock()));
                productCategoryCombo.getSelectionModel().select(newVal.getCategory());
            }
        });

        categoryListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedCategory = newVal;
            if (newVal != null) {
                categoryNameField.setText(newVal.getName());
            }
        });
    }

    private void loadProducts() {
        productListView.setItems(FXCollections.observableArrayList(catalogService.getAllProducts()));
        productCategoryCombo.setItems(FXCollections.observableArrayList(catalogService.getAllCategories()));
    }

    private void loadCategories() {
        categoryListView.setItems(FXCollections.observableArrayList(catalogService.getAllCategories()));
    }

    // === Управление товарами ===

    @FXML
    public void handleAddProduct() {
        try {
            String name = productNameField.getText();
            String desc = productDescField.getText();
            double price = Double.parseDouble(productPriceField.getText());
            int stock = Integer.parseInt(productStockField.getText());
            Category category = productCategoryCombo.getSelectionModel().getSelectedItem();

            if (name == null || name.trim().isEmpty()) {
                productActionTarget.setText("Введіть назву товару.");
                return;
            }
            if (category == null) {
                productActionTarget.setText("Оберіть категорію.");
                return;
            }

            catalogService.addProduct(name, desc, price, stock, category.getId());
            loadProducts();
            clearProductFields();
            productActionTarget.setText("Товар успішно додано!");
        } catch (NumberFormatException e) {
            productActionTarget.setText("Невірний формат ціни або кількості.");
        } catch (Exception e) {
            productActionTarget.setText("Помилка: " + e.getMessage());
        }
    }

    @FXML
    public void handleUpdateProduct() {
        if (selectedProduct == null) {
            productActionTarget.setText("Оберіть товар для редагування.");
            return;
        }

        try {
            String name = productNameField.getText();
            String desc = productDescField.getText();
            double price = Double.parseDouble(productPriceField.getText());
            int stock = Integer.parseInt(productStockField.getText());
            Category category = productCategoryCombo.getSelectionModel().getSelectedItem();

            if (name == null || name.trim().isEmpty()) {
                productActionTarget.setText("Введіть назву товару.");
                return;
            }
            if (category == null) {
                productActionTarget.setText("Оберіть категорію.");
                return;
            }

            catalogService.updateProduct(selectedProduct.getId(), name, desc, price, stock, category.getId());
            loadProducts();
            clearProductFields();
            productActionTarget.setText("Товар успішно оновлено!");
        } catch (NumberFormatException e) {
            productActionTarget.setText("Невірний формат ціни або кількості.");
        } catch (Exception e) {
            productActionTarget.setText("Помилка: " + e.getMessage());
        }
    }

    @FXML
    public void handleDeleteProduct() {
        if (selectedProduct == null) {
            productActionTarget.setText("Оберіть товар для видалення.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Підтвердження");
        alert.setHeaderText("Видалення товару");
        alert.setContentText("Ви впевнені, що хочете видалити товар '" + selectedProduct.getName() + "'?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                catalogService.deleteProduct(selectedProduct.getId());
                loadProducts();
                clearProductFields();
                productActionTarget.setText("Товар успішно видалено!");
            } catch (Exception e) {
                productActionTarget.setText("Помилка: " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleClearProductFields() {
        clearProductFields();
        productActionTarget.setText("Поля очищено.");
    }

    private void clearProductFields() {
        selectedProduct = null;
        productNameField.clear();
        productDescField.clear();
        productPriceField.clear();
        productStockField.clear();
        productCategoryCombo.getSelectionModel().clearSelection();
        productListView.getSelectionModel().clearSelection();
    }

    // === Управление категориями ===

    @FXML
    public void handleAddCategory() {
        try {
            String name = categoryNameField.getText();

            if (name == null || name.trim().isEmpty()) {
                categoryActionTarget.setText("Введіть назву категорії.");
                return;
            }

            catalogService.addCategory(name, "");
            loadCategories();
            loadProducts(); // Обновить комбобокс категорий
            clearCategoryFields();
            categoryActionTarget.setText("Категорію успішно додано!");
        } catch (Exception e) {
            categoryActionTarget.setText("Помилка: " + e.getMessage());
        }
    }

    @FXML
    public void handleUpdateCategory() {
        if (selectedCategory == null) {
            categoryActionTarget.setText("Оберіть категорію для редагування.");
            return;
        }

        try {
            String name = categoryNameField.getText();

            if (name == null || name.trim().isEmpty()) {
                categoryActionTarget.setText("Введіть назву категорії.");
                return;
            }

            catalogService.updateCategory(selectedCategory.getId(), name, "");
            loadCategories();
            loadProducts();
            clearCategoryFields();
            categoryActionTarget.setText("Категорію успішно оновлено!");
        } catch (Exception e) {
            categoryActionTarget.setText("Помилка: " + e.getMessage());
        }
    }

    @FXML
    public void handleDeleteCategory() {
        if (selectedCategory == null) {
            categoryActionTarget.setText("Оберіть категорію для видалення.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Підтвердження");
        alert.setHeaderText("Видалення категорії");
        alert.setContentText("Ви впевнені, що хочете видалити категорію '" + selectedCategory.getName() + "'?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                catalogService.deleteCategory(selectedCategory.getId());
                loadCategories();
                loadProducts();
                clearCategoryFields();
                categoryActionTarget.setText("Категорію успішно видалено!");
            } catch (Exception e) {
                categoryActionTarget.setText("Помилка: " + e.getMessage());
            }
        }
    }

    @FXML
    public void handleClearCategoryFields() {
        clearCategoryFields();
        categoryActionTarget.setText("Поля очищено.");
    }

    private void clearCategoryFields() {
        selectedCategory = null;
        categoryNameField.clear();
        categoryListView.getSelectionModel().clearSelection();
    }

    // === Навигация ===

    @FXML
    public void handleGoToReports() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("report-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) productListView.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Звіти");
        } catch (IOException e) {
            productActionTarget.setText("Помилка переходу до звітів.");
            e.printStackTrace();
        }
    }
}
