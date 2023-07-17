package com.main.stepper.admin.resources.fxml.header.loadfile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.main.stepper.admin.resources.data.PropertiesManager;
import com.main.stepper.admin.resources.data.URLManager;
import com.main.stepper.admin.resources.dynamic.errorpopup.ErrorPopup;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.main.stepper.admin.resources.data.PropertiesManager.HTTP_CLIENT;
import static com.main.stepper.admin.resources.data.PropertiesManager.primaryStage;

public class LoadFileController {
    @FXML private Button loadFileButton;
    @FXML private TextField filePathTextField;

    public LoadFileController() {
    }

    @FXML private void initialize() {
        // bind text field to property
        filePathTextField.textProperty().bind(PropertiesManager.lastLoadedFile);
    }

    @FXML private void loadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML Files", "*.xml")
        );
        File chosenFile = fileChooser.showOpenDialog(primaryStage.get());

        if (chosenFile == null) {
            return;
        }

        RequestBody body = new MultipartBody.Builder()
                .addFormDataPart(chosenFile.getName(), chosenFile.getName(), RequestBody.create(chosenFile, MediaType.parse("text/plain")))
                .build();

        Request request = new Request.Builder()
                .url(URLManager.XML_FILE_UPLOAD)
                .addHeader("isAdmin", "true")
                .post(body)
                .build();

        synchronized (HTTP_CLIENT) {
            Call call = HTTP_CLIENT.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> new ErrorPopup("Could not connect to server!"));
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if(response.code() == 200) {
                        Gson gson = new Gson();
                        List<String> errors = gson.fromJson(response.body().string(), new TypeToken<ArrayList<String>>(){}.getType());
                        if(errors.isEmpty()) {
                            Platform.runLater(() -> PropertiesManager.lastLoadedFile.set(chosenFile.getAbsolutePath()));
                        }
                        else{
                            Platform.runLater(() -> new ErrorPopup(errors));
                        }
                    }
                    else if (response.code() == 400) {
                        Platform.runLater(() -> new ErrorPopup("XML file is not scheme compliant!"));
                    }
                }
            });
        }
    }
}
