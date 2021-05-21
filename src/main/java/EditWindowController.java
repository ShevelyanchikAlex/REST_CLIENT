import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import services.AppServices;
import services.StateService;

import java.net.URL;
import java.util.ResourceBundle;

public class EditWindowController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label actionLabel;

    @FXML
    public TextArea textArea;

    @FXML
    private Button sendBtn;

    private StateService stateService;
    private AppServices appServices;

    @FXML
    void initialize() {
        appServices = AppServices.getInstance();
        stateService = StateService.getInstance();
        if (stateService.getCurrentAction().equals("put")) {
            actionLabel.setText("Put");
        } else {
            actionLabel.setText("Post");
        }
    }


    @FXML
    void send(ActionEvent event) {
        if (stateService.getCurrentAction().equals("put")) {
            Controller.client.doPutRequest(stateService.getFileName(), textArea.getText());
        } else {
            Controller.client.doPostRequest(stateService.getFileName(), textArea.getText());
        }
        appServices.closeWindow(event);
    }


}
