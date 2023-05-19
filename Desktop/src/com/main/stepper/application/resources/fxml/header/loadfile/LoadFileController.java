package com.main.stepper.application.resources.fxml.header.loadfile;

import com.main.stepper.application.resources.dynamic.errorpopup.ErrorPopup;
import com.main.stepper.application.resources.fxml.root.RootController;
import com.main.stepper.exceptions.xml.XMLException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class LoadFileController {
    @FXML private Button loadFileButton;
    @FXML private TextField filePathTextField;

    @FXML private RootController rootController;

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
        try {
            List<String> errors;
            synchronized (rootController.getEngine()){
                errors = rootController.getEngine().readSystemFromXML(chosenFile.getPath());
            }
            if(errors.isEmpty()) {
                filePathTextField.setText(chosenFile.getAbsolutePath());
                rootController.loadFlows();
            }
            else{
                new ErrorPopup(errors);
            }
        } catch (XMLException e) {
            new ErrorPopup("XML file is not scheme compliant!");
        }
    }
}
