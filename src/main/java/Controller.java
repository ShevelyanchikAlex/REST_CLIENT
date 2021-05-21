import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TextField;
import services.AppServices;
import services.StateService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button getBtn;

    @FXML
    private Button putBtn;

    @FXML
    private Button postBtn;

    @FXML
    private Button deleteBtn;

    @FXML
    private Button moveBtn;

    @FXML
    private TextField filePathTextField;

    @FXML
    private ListView<String> listView = new ListView<>();

    static StateService stateService;
    static AppServices appServices;
    static Client client;


    @FXML
    void initialize() {
        client = new Client();
        stateService = StateService.getInstance();
        appServices = AppServices.getInstance();
        stateService.setCurrentPath("/");
        listView.setItems(stateService.getObservableList());
        client.init(stateService.getObservableList(), listView);
        filePathTextField.setText("");
    }


    public void post() throws IOException {
        MultipleSelectionModel<String> selectedRow = listView.getSelectionModel();
        String fileName = selectedRow.getSelectedItem();
        if (client.isFile(fileName))
            openPostWindow(fileName);
    }


    @FXML
    public void get(javafx.event.ActionEvent event) {
        MultipleSelectionModel<String> selectedRow = listView.getSelectionModel();
        client.doGetRequest(selectedRow.getSelectedItem(), stateService.getObservableList(), listView);
    }

    public void put(ActionEvent event) throws IOException {
        MultipleSelectionModel<String> selectedRow = listView.getSelectionModel();
        String fileName = selectedRow.getSelectedItem();
        if (client.isFile(fileName))
            openPutWindow(fileName);
    }

    public void delete(ActionEvent event) {
        MultipleSelectionModel<String> selectedRow = listView.getSelectionModel();
        client.doDeleteRequest(selectedRow.getSelectedItem(), listView);
    }

    public void move(ActionEvent event) {
        MultipleSelectionModel<String> selectedRow = listView.getSelectionModel();
        String dest = filePathTextField.getText();
        if (dest != null) {
            if (client.isFile(selectedRow.getSelectedItem()) && client.isFile(dest)) {
                client.doManageFileRequest("move", client.getCurrentPath() + selectedRow.getSelectedItem(), dest, stateService.getObservableList(), listView);
            }
        }
    }

    public void copy(ActionEvent event) {
        MultipleSelectionModel<String> row = listView.getSelectionModel();
        String dest = filePathTextField.getText();
        if (dest != null) {
            if (client.isFile(row.getSelectedItem()) && client.isFile(dest)) {
                client.doManageFileRequest("copy", client.getCurrentPath() + row.getSelectedItem(), dest, stateService.getObservableList(), listView);
            }
        }
    }

    private static void openPostWindow(String fileName) throws IOException {
        stateService.setFileName(fileName);
        stateService.setCurrentAction("post");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Controller.class.getResource("fxmls/edit_window.fxml"));
        appServices.createWindow(fxmlLoader, "Post", 400, 200);
    }


    private static void openPutWindow(String fileName) throws IOException {
        stateService.setFileName(fileName);
        stateService.setCurrentAction("put");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(Controller.class.getResource("fxmls/edit_window.fxml"));
        appServices.createWindow(fxmlLoader, "Put", 400, 200);
    }

    public void back(ActionEvent event) {
        client.goBack(stateService.getObservableList(), listView);
    }

}
