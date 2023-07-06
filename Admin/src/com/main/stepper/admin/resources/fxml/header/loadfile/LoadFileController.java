package com.main.stepper.admin.resources.fxml.header.loadfile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.main.stepper.admin.resources.data.URLManager;
import com.main.stepper.admin.resources.dynamic.errorpopup.ErrorPopup;
import com.main.stepper.admin.resources.fxml.root.RootController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.main.stepper.admin.resources.data.PropertiesManager.HTTP_CLIENT;

public class LoadFileController {
    @FXML private Button loadFileButton;
    @FXML private TextField filePathTextField;
    private RootController rootController;

    public LoadFileController() {
    }

    public void setRootController(RootController rootController) {
        this.rootController = rootController;
    }

    @FXML private void loadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML Files", "*.xml")
        );
        File chosenFile = fileChooser.showOpenDialog(rootController.getPrimaryStage());

        if (chosenFile == null) {
            return;
        }

        RequestBody body = new MultipartBody.Builder()
                .addFormDataPart("file1", chosenFile.getName(), RequestBody.create(chosenFile, MediaType.parse("text/plain")))
                .build();
        Request request = new Request.Builder()
                .url(URLManager.LOAD_XML)
                .addHeader("isAdmin", "true")
                .post(body)
                .build();
        Call call;
        synchronized (HTTP_CLIENT) {
            call = HTTP_CLIENT.newCall(request);
        }
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                // check if request was successful
                if (response.code() != 200) {
                    // failed on a technicality
                    Platform.runLater(() -> new ErrorPopup("Failed to load file, make sure it's a valid XML file!"));
                }
                else {
                    // read error list
                    Gson gson = new Gson();
                    List<String> errors = gson.fromJson(response.body().charStream(), new TypeToken<List<String>>() {}.getType());
                    if(errors.isEmpty()) {
                        Platform.runLater(() -> filePathTextField.setText(chosenFile.getAbsolutePath()));
                    }
                    else{
                        Platform.runLater(() -> new ErrorPopup(errors));
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> new ErrorPopup("Failed to connect to server, ensure your connection is stable!"));
            }
        });
    }
}
