package com.example.store;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        AuthService.getInstance();
        CatalogService.getInstance();
        OrderService.getInstance();

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("login-view.fxml"));
        Scene scene = new Scene(loader.load(), 420, 260);
        stage.setTitle("Онлайн-магазин 'MyStore' - Вхід");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        AuthService.getInstance().saveData();
        CatalogService.getInstance().saveData();
        OrderService.getInstance().saveData();
        System.out.println("Дані збережено при виході.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}