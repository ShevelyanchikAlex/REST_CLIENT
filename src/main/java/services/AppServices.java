package services;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class AppServices {

    private static AppServices instance;

    public static AppServices getInstance() {
        if (instance == null) {
            instance = new AppServices();
        }
        return instance;
    }

    public void createWindow(FXMLLoader fxmlLoader, String action, int width, int height) throws IOException {
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        Stage stage = new Stage();
        stage.setTitle(action);
        stage.setScene(scene);
        stage.show();
    }

    public void closeWindow(ActionEvent event) {
        final Node source = (Node) event.getSource();
        final Stage lastStage = (Stage) source.getScene().getWindow();
        lastStage.close();
    }


    public void showDialog(String message, String contentText, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setHeaderText(message);
        if (contentText != null) alert.setContentText(contentText);
        alert.showAndWait();
    }
}
