package com.example.store;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RegisterController {
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private Text actionTarget;

    private final AuthService authService = AuthService.getInstance();

    @FXML
    public void handleRegisterAction() {
        String login = loginField.getText();
        String password = passwordField.getText();
        String name = nameField.getText();
        String email = emailField.getText();

        if (login == null || login.trim().isEmpty()) {
            actionTarget.setText("Логін не може бути порожнім.");
            return;
        }
        if (password == null || password.trim().isEmpty()) {
            actionTarget.setText("Пароль не може бути порожнім.");
            return;
        }
        if (name == null || name.trim().isEmpty()) {
            actionTarget.setText("Ім'я не може бути порожнім.");
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            actionTarget.setText("Email не може бути порожнім.");
            return;
        }

        try {
            authService.register(name, email, login, password);
            actionTarget.setText("Реєстрація успішна! Тепер ви можете увійти.");

            // Закрыть окно регистрации через 2 секунды
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(this::handleCancelAction);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IllegalArgumentException e) {
            actionTarget.setText("Помилка: " + e.getMessage());
        } catch (Exception e) {
            actionTarget.setText("Невідома помилка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCancelAction() {
        Stage stage = (Stage) actionTarget.getScene().getWindow();
        stage.close();
    }
}
