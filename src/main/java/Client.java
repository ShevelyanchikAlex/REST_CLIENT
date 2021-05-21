import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import services.AppServices;
import services.StateService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Client {

    private final String ADDRESS = "http://localhost:8080/";

    CloseableHttpClient httpClient;
    private String currentPath;
    StateService stateService;
    AppServices appServices;

    public Client() {
        stateService = StateService.getInstance();
        appServices = AppServices.getInstance();
        currentPath = "/";
        httpClient = HttpClients.createDefault();
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public void init(ObservableList<String> observableList, ListView<String> listView) {
        HttpGet httpGet = new HttpGet(ADDRESS);
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            ObjectInputStream ois = new ObjectInputStream(httpResponse.getEntity().getContent());
            String root = (String) ois.readObject();
            listView.getItems().clear();
            observableList.clear();
            observableList.add(root);
        } catch (IOException | ClassNotFoundException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                if (httpResponse != null) httpResponse.close();
                httpGet.clear();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void doGetRequest(String path, ObservableList<String> observableList, ListView<String> listView) {
        System.out.println(currentPath + path);
        HttpGet httpGet = new HttpGet(ADDRESS + "file?path=" + currentPath + path);
        ObjectInputStream ois = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            ois = new ObjectInputStream(httpResponse.getEntity().getContent());
            boolean isDirectory = ois.readBoolean();
            if (isDirectory) {
                currentPath = currentPath + path + "/";
                String[] files = (String[]) ois.readObject();
                listView.getItems().clear();
                observableList.clear();
                observableList.addAll(Arrays.asList(files));
            } else {
                byte[] buff = new byte[4096];
                StringBuilder sb = new StringBuilder();
                int bytesCount = ois.read(buff);
                while (bytesCount != -1) {
                    sb.append(new String(buff, 0, bytesCount, StandardCharsets.UTF_8));
                    bytesCount = ois.read(buff);
                }
                stateService.setFileData(sb.toString());
                stateService.setCurrentAction("get");
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(Controller.class.getResource("fxmls/get_window.fxml"));
                appServices.createWindow(fxmlLoader, "Get", 400, 200);
            }
        } catch (IOException | ClassNotFoundException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                if (ois != null) ois.close();
                if (httpResponse != null) httpResponse.close();
                httpGet.clear();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void goBack(ObservableList<String> observableList, ListView<String> listView) {
        int i = currentPath.length() - 1;
        while (currentPath.charAt(i) != '/') i--;
        if (i != 0) {
            i--;
            while (currentPath.charAt(i) != '/') i--;
            currentPath = currentPath.substring(0, i + 1);
            if (currentPath.equals("/")) {
                init(observableList, listView);
            } else {
                HttpGet httpGet = new HttpGet(ADDRESS + "file?path=" + currentPath);
                ObjectInputStream ois = null;
                CloseableHttpResponse httpResponse = null;
                try {
                    httpResponse = httpClient.execute(httpGet);
                    ois = new ObjectInputStream(httpResponse.getEntity().getContent());
                    ois.readBoolean();
                    String[] files = (String[]) ois.readObject();
                    listView.getItems().clear();
                    observableList.clear();
                    observableList.addAll(Arrays.asList(files));
                } catch (IOException | ClassNotFoundException ioException) {
                    ioException.printStackTrace();
                } finally {
                    try {
                        if (ois != null) ois.close();
                        httpGet.clear();
                        if (httpResponse != null) httpResponse.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }


    public boolean isFile(String fileName) {
        HttpGet httpGet = new HttpGet(ADDRESS + "isFile?path=" + currentPath + fileName);
        ObjectInputStream ois = null;
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            ois = new ObjectInputStream(response.getEntity().getContent());
            //!ois.readBoolean() = !isDirectory = isFile
            return !ois.readBoolean();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                if (ois != null) ois.close();
                if (response != null) response.close();
                httpGet.clear();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return true;
    }


    public void doPostRequest(String fileName, String text) {
        HttpPost httpPost = new HttpPost(ADDRESS + "file/");
        httpPost.setEntity(new UrlEncodedFormEntity(getParams(fileName, text)));
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                httpPost.clear();
                if (response != null) response.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public void doPutRequest(String fileName, String text) {
        HttpPut httpPut = new HttpPut(ADDRESS + "file/");
        httpPut.setEntity(new UrlEncodedFormEntity(getParams(fileName, text)));
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPut);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                httpPut.clear();
                if (response != null) response.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private List<NameValuePair> getParams(String fileName, String text) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("path", (currentPath + fileName)));
        params.add(new BasicNameValuePair("text", text));
        return params;
    }

    public void doDeleteRequest(String fileName, ListView<String> listView) {
        HttpDelete httpDelete = new HttpDelete(ADDRESS + "file?path=" + currentPath + fileName);
        try (CloseableHttpResponse response = httpClient.execute(httpDelete)) {
            listView.getItems().remove(fileName);
            showStatusDialog(response);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void doManageFileRequest(String action, String sourcePath, String destPath, ObservableList<String> observableList, ListView<String> listView) {
        HttpPost httpPost = new HttpPost(ADDRESS + action + "file/");
        System.out.println(sourcePath + "   " + destPath);
        httpPost.setEntity(new UrlEncodedFormEntity(getManageFileParams(sourcePath, destPath)));
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            if (response.getCode() == 200 && action.equals("move")) {
                String fileName = getFileName(sourcePath);
                listView.getItems().remove(fileName);
                observableList.remove(fileName);
            }
            showStatusDialog(response);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private String getFileName(String fullPath) {
        String[] path = fullPath.split("/");
        return path[path.length - 1];
    }

    List<NameValuePair> getManageFileParams(String sourcePath, String destPath) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("sourcePath", sourcePath));
        params.add(new BasicNameValuePair("destPath", destPath));
        return params;
    }

    private void showStatusDialog(HttpResponse response) {
        if (response.getCode() == 200) {
            appServices.showDialog("Success of request", "Success", Alert.AlertType.CONFIRMATION);
        } else {
            appServices.showDialog("Success of the request", "Error" + response.getCode(), Alert.AlertType.CONFIRMATION);
        }
    }
}
