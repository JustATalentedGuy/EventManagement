package UI;

import java.util.ArrayList;

import eventmanagement.Main;
import users.Admin;
import venue.Department;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import system.SystemManager;

public class AdminDepartmentsPage {

    private Main app;
    private Admin admin;
    private VBox rootPane;

    public AdminDepartmentsPage(Main app, Admin admin) {
        this.app = app;
        this.admin = admin;
        createAdminDepartmentsPage();
    }

    private void createAdminDepartmentsPage() {
        rootPane = new VBox();
        rootPane.setPadding(new Insets(10));
        rootPane.setSpacing(10);

        Label titleLabel = new Label("Manage Departments");

        TextField departmentField = new TextField();
        departmentField.setPromptText("Enter new department name");

        Button addDepartmentButton = new Button("Add Department");

        addDepartmentButton.setOnAction(e -> {
            addNewDepartment(departmentField.getText());
            app.showManageDepartmentsPage(admin);
        });

        VBox departmentList = new VBox();
        departmentList.setSpacing(10);
        ArrayList<Department> departments = SystemManager.getAllDepartments();

        for (Department department : departments) {
            HBox departmentItem = new HBox();
            departmentItem.setSpacing(10);

            Label departmentLabel = new Label(department.getName());
            Button deleteButton = new Button("Delete");

            HBox.setHgrow(departmentLabel, Priority.ALWAYS);
            departmentLabel.setMaxWidth(Double.MAX_VALUE);
            
            // Delete button action
            deleteButton.setOnAction(e -> {
                deleteDepartment(department);
                app.showManageDepartmentsPage(admin);
            });

            departmentItem.getChildren().addAll(departmentLabel, deleteButton);
            departmentList.getChildren().add(departmentItem);
        }

        ScrollPane scrollPane = new ScrollPane(departmentList);
        scrollPane.setFitToWidth(true);

        Button goBack = new Button("Go Back");
        goBack.setOnAction(e -> app.showAdminPage(admin));

        rootPane.getChildren().addAll(titleLabel, departmentField, addDepartmentButton, scrollPane, goBack);
    }

    private void addNewDepartment(String departmentName) {
        SystemManager.registerDepartment(departmentName);
    }

    private void deleteDepartment(Department department) {
        SystemManager.deleteDepartment(department.getDepartmentID());
    }

    public VBox getRootPane() {
        return rootPane;
    }
}
