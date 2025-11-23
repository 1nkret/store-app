package com.example.store;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class ReportController {
    @FXML private TextArea outputArea;
    @FXML private DatePicker datePicker;
    @FXML private TextField goodsNameField;

    private final QueryService queryService = QueryService.getInstance();

    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now());
    }

    @FXML
    public void handleQuery1() {
        var list = queryService.getUsersList();
        outputArea.setText("Список користувачів (" + list.size() + "):\n" + list.stream()
                .map(u -> u.getId() + ". " + u.getFullName() + " (" + u.getPhone() + ")")
                .collect(Collectors.joining("\n")));
    }
    @FXML
    public void handleQuery2() {
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate == null) {
            outputArea.setText("Будь ласка, оберіть дату.");
            return;
        }
        var list = queryService.getOrdersByDate(selectedDate);
        outputArea.setText("Замовлення на " + selectedDate + " (" + list.size() + "):\n" + list.stream()
                .map(o -> "ID: " + o.getId() + ", Клієнт: " + (o.getCustomer()!=null?o.getCustomer().getFullName():"N/A") + ", Сума: " + String.format("%.2f", o.getTotalPrice()))
                .collect(Collectors.joining("\n")));
    }
    @FXML
    public void handleQuery3() {
        double avg = queryService.getAverageOrderCost();
        outputArea.setText("Середня вартість замовлення: " + String.format("%.2f", avg) + " грн.");
    }
    @FXML
    public void handleQuery4() {
        Product p = queryService.getMostPopularProduct();
        outputArea.setText("Найпопулярніший товар: " + (p != null ? p.getName() + " (ID: " + p.getId() + ")" : "Немає продажів"));
    }
    @FXML
    public void handleQuery5() {
        Customer c = queryService.getHighestSpender();
        outputArea.setText("Найактивніший покупець (за сумою): " + (c != null ? c.getFullName() + " (ID: " + c.getId() + ")" : "Немає продажів"));
    }
    @FXML
    public void handleQuery6() {
        var list = queryService.getTop3Categories();
        outputArea.setText("Топ-3 категорії за кількістю проданих одиниць товару:\n" + list.stream()
                .map(cat -> cat.getId() + ". " + cat.getName())
                .collect(Collectors.joining("\n")));
    }

    @FXML
    public void handleBackToStore() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("admin-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) outputArea.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Панель адміністратора");
        } catch (IOException e) {
            outputArea.setText("Помилка повернення до панелі адміністратора.");
            e.printStackTrace();
        }
    }
}