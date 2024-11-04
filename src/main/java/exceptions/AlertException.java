package exceptions;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertException extends Exception {
    
    public AlertException(String message) {
        super(message);
        showAlert(message);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Exception Occurred");
        alert.setHeaderText("An Error Has Occurred");
        alert.setContentText(message);
        alert.showAndWait();
    }
}