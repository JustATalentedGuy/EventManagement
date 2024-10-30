package UI;

import eventmanagement.Main;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import system.SystemManager;
import users.Admin;
import users.Organizer;
import users.User;
import users.Viewer;

public class LoginPage {

    private Main app;
    private GridPane rootPane;

    public LoginPage(Main app) {
        this.app = app;
        createLoginPage();
    }

    private void createLoginPage() {
        rootPane = new GridPane();
        rootPane.setPadding(new Insets(20));
        rootPane.setHgap(20);
        rootPane.setVgap(20);
        rootPane.setBackground(new Background(new BackgroundFill(Color.web("#3498db"), CornerRadii.EMPTY, Insets.EMPTY)));

        Label titleLabel = new Label("Event Management System");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        rootPane.add(titleLabel, 0, 0, 2, 1);

        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("Options");
        MenuItem aboutItem = new MenuItem("About");
        MenuItem exitItem = new MenuItem("Exit");
        menu.getItems().addAll(aboutItem, exitItem);
        menuBar.getMenus().add(menu);
        exitItem.setOnAction(e -> System.exit(0));
        aboutItem.setOnAction(e -> showAlert("About", "This is an event management system for college events."));
        rootPane.add(menuBar, 0, 1, 2, 1);

        Label userLabel = new Label("Username:");
        userLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        TextField userField = new TextField();
        userField.setStyle("-fx-background-color: #f7f7f7; -fx-text-fill: black; -fx-font-size: 12px;");

        Label passLabel = new Label("Password:");
        passLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        PasswordField passField = new PasswordField();
        passField.setStyle("-fx-background-color: #f7f7f7; -fx-text-fill: black; -fx-font-size: 12px;");

        rootPane.add(userLabel, 0, 2);
        rootPane.add(userField, 1, 2);
        rootPane.add(passLabel, 0, 3);
        rootPane.add(passField, 1, 3);

        Label userTypeLabel = new Label("User Type:");
        userTypeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        ComboBox<String> userTypeBox = new ComboBox<>();
        userTypeBox.getItems().addAll("Viewer", "Organizer", "Admin");
        userTypeBox.setStyle("-fx-background-color: #f7f7f7; -fx-text-fill: black; -fx-font-size: 12px;");

        rootPane.add(userTypeLabel, 0, 4);
        rootPane.add(userTypeBox, 1, 4);

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

        rootPane.add(loginButton, 1, 5);
        rootPane.add(switchToRegisterButton, 1, 6);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public GridPane getRootPane() {
        return rootPane;
    }
}
