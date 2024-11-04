package UI;

import eventmanagement.Main;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import system.SystemManager;

import javafx.geometry.Pos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;

public class RegisterPage {

    private Main app;
    private GridPane rootPane;

    public RegisterPage(Main app) {
        this.app = app;
        createRegisterPage();
    }

    private void createRegisterPage() {
        rootPane = new GridPane();
        rootPane.setPadding(new Insets(20));
        rootPane.setHgap(20);
        rootPane.setVgap(20);
        rootPane.setAlignment(Pos.CENTER);
        rootPane.setBackground(new Background(new BackgroundFill(Color.web("#3498db"), CornerRadii.EMPTY, Insets.EMPTY)));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col1.setPercentWidth(50);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setPercentWidth(50);

        rootPane.getColumnConstraints().addAll(col1, col2);

        Label titleLabel = new Label("Register - Event Management System");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        rootPane.add(titleLabel, 0, 0, 2, 1);

        Label userLabel = new Label("Username:");
        userLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        TextField userField = new TextField();
        userField.setStyle("-fx-background-color: #f7f7f7; -fx-text-fill: black; -fx-font-size: 12px;");

        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        TextField emailField = new TextField();
        emailField.setStyle("-fx-background-color: #f7f7f7; -fx-text-fill: black; -fx-font-size: 12px;");

        Label passLabel = new Label("Password:");
        passLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        PasswordField passField = new PasswordField();
        passField.setStyle("-fx-background-color: #f7f7f7; -fx-text-fill: black; -fx-font-size: 12px;");

        rootPane.add(userLabel, 0, 2);
        rootPane.add(userField, 1, 2);
        rootPane.add(emailLabel, 0, 3);
        rootPane.add(emailField, 1, 3);
        rootPane.add(passLabel, 0, 4);
        rootPane.add(passField, 1, 4);

        Label userTypeLabel = new Label("User Type:");
        userTypeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        ComboBox<String> userTypeBox = new ComboBox<>();
        userTypeBox.getItems().addAll("Viewer", "Organizer");
        userTypeBox.setStyle("-fx-background-color: #f7f7f7; -fx-text-fill: black; -fx-font-size: 12px;");

        rootPane.add(userTypeLabel, 0, 5);
        rootPane.add(userTypeBox, 1, 5);

        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 16px;");
        registerButton.setOnAction(e -> {
            String username = userField.getText();
            String email = emailField.getText();
            String password = passField.getText();
            String userType = userTypeBox.getValue();

            if (SystemManager.registerUser(username, password, email, userType)) {
                app.showLoginPage();
                showAlert("Registration Success", "You have successfully registered.");
            } else {
                showAlert("Registration Failed", "Please check your details and try again.");
            }
        });

        Button switchToLoginButton = new Button("Login");
        switchToLoginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px;");
        switchToLoginButton.setOnAction(e -> app.showLoginPage());

        rootPane.add(registerButton, 1, 6);
        rootPane.add(switchToLoginButton, 1, 7);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public GridPane getRootPane() {
        return rootPane;
    }
}
