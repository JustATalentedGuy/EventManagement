package UI;

import eventmanagement.Main;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import system.SystemManager;
import users.Admin;
import users.Organizer;
import users.User;
import users.Viewer;

public class LoginPage {

    private Main app;
    private StackPane rootPane;

    public LoginPage(Main app) {
        this.app = app;
        createLoginPage();
    }

    private void createLoginPage() {
        // Use StackPane as the root container to center the content
        rootPane = new StackPane();
        rootPane.setBackground(new Background(new BackgroundFill(Color.web("#3498db"), CornerRadii.EMPTY, Insets.EMPTY)));

        // Center container with the login form
        GridPane loginPane = new GridPane();
        loginPane.setPadding(new Insets(20));
        loginPane.setHgap(20);
        loginPane.setVgap(20);
        loginPane.setAlignment(Pos.CENTER);  // Center align components within GridPane

        Label titleLabel = new Label("Event Management System");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        loginPane.add(titleLabel, 0, 0, 2, 1);

        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Options");
        MenuItem aboutItem = new MenuItem("About");
        MenuItem exitItem = new MenuItem("Exit");
        menu.getItems().addAll(aboutItem, exitItem);
        menuBar.getMenus().add(menu);
        exitItem.setOnAction(e -> System.exit(0));
        aboutItem.setOnAction(e -> showAlert("About", "This is an event management system for college events."));
        loginPane.add(menuBar, 0, 1, 2, 1);

        Label userLabel = new Label("Username:");
        userLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        TextField userField = new TextField();
        userField.setStyle("-fx-background-color: #f7f7f7; -fx-text-fill: black; -fx-font-size: 12px;");

        Label passLabel = new Label("Password:");
        passLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        PasswordField passField = new PasswordField();
        passField.setStyle("-fx-background-color: #f7f7f7; -fx-text-fill: black; -fx-font-size: 12px;");

        loginPane.add(userLabel, 0, 2);
        loginPane.add(userField, 1, 2);
        loginPane.add(passLabel, 0, 3);
        loginPane.add(passField, 1, 3);

        Label userTypeLabel = new Label("User Type:");
        userTypeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        ComboBox<String> userTypeBox = new ComboBox<>();
        userTypeBox.getItems().addAll("Viewer", "Organizer", "Admin");
        userTypeBox.setStyle("-fx-background-color: #f7f7f7; -fx-text-fill: black; -fx-font-size: 12px;");

        loginPane.add(userTypeLabel, 0, 4);
        loginPane.add(userTypeBox, 1, 4);

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 16px;");
        loginButton.setOnAction(e -> {
            String userType = userTypeBox.getValue();
            String username = userField.getText();
            String password = passField.getText();

            int id = SystemManager.validateUser(username, password, userType);
            if (id != -1) {
                User user = SystemManager.getUser(id);
                if (userType.equals("Viewer")) app.showViewerPage((Viewer) user);
                else if (userType.equals("Organizer")) app.showOrganizerPage((Organizer) user);
                else if (userType.equals("Admin")) app.showAdminPage((Admin) user);
            } else {
                showAlert("Login failed", "Invalid username, password, or user type.");
            }
        });

        Button switchToRegisterButton = new Button("Register");
        switchToRegisterButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px;");
        switchToRegisterButton.setOnAction(e -> app.showRegisterPage());

        loginPane.add(loginButton, 1, 5);
        loginPane.add(switchToRegisterButton, 1, 6);

        // Add loginPane to the center of the rootPane
        rootPane.getChildren().add(loginPane);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public StackPane getRootPane() {
        return rootPane;
    }
}