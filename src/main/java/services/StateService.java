package services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class StateService {

    private static StateService instance;
    private String currentAction;
    private String currentPath;
    private String fileName;
    private String fileData;
    private ObservableList<String> observableList = FXCollections.observableArrayList();

    public static StateService getInstance() {
        if (instance == null) {
            instance = new StateService();
        }
        return instance;
    }

    public String getFileData() {
        return fileData;
    }

    public void setFileData(String fileData) {
        this.fileData = fileData;
    }

    public static void setInstance(StateService instance) {
        StateService.instance = instance;
    }

    public String getCurrentAction() {
        return currentAction;
    }

    public void setCurrentAction(String currentAction) {
        this.currentAction = currentAction;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ObservableList<String> getObservableList() {
        return observableList;
    }

    public void setObservableList(ObservableList<String> observableList) {
        this.observableList = observableList;
    }
}
