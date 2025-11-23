package com.example.store;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class LoginController {
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Text actionTarget;

    private final AuthService authService = AuthService.getInstance();

    @FXML
    public void handleLoginButtonAction() {
        try {
            BaseUser user = authService.login(loginField.getText(), passwordField.getText());
            if (user == null) {
                actionTarget.setText("Невірний логін або пароль.");
                return;
            }

            if ("ADMIN".equals(user.getRole())) {
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("admin-view.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) actionTarget.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Панель адміністратора");
            } else {
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("store-view.fxml"));
                Scene scene = new Scene(loader.load());

                // Передаем текущего пользователя в контроллер магазина
                StoreController controller = loader.getController();
                controller.setCurrentUser((Customer) user);

                Stage stage = (Stage) actionTarget.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Онлайн-магазин");
            }

        } catch (IOException e) {
            actionTarget.setText("Помилка завантаження сцени: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            actionTarget.setText("Помилка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRegisterButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("register-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage registerStage = new Stage();
            registerStage.setTitle("Реєстрація нового користувача");
            registerStage.setScene(scene);
            registerStage.show();
        } catch (IOException e) {
            actionTarget.setText("Помилка відкриття реєстрації.");
            e.printStackTrace();
        }
    }
}