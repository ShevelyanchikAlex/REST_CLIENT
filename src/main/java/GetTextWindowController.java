import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import services.StateService;

import java.net.URL;
import java.util.ResourceBundle;

public class GetTextWindowController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label actionLabel;

    @FXML
    public TextArea textArea;

    @FXML
    void initialize() {
        textArea.setText(StateService.getInstance().getFileData());
        actionLabel.setText("Get");
    }

}

