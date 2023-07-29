package com.main.stepper.admin.resources.fxml.tabs.flowmaker.stepproperties;

import com.main.stepper.admin.resources.data.PropertiesManager;
import com.main.stepper.admin.resources.fxml.tabs.flowmaker.componentdragview.datalink.DataLink;
import com.main.stepper.admin.resources.fxml.tabs.flowmaker.draggablestep.DraggableStep;
import com.main.stepper.shared.structures.dataio.DataIODTO;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StepPropertiesController {
    @FXML private TableView<DataLink> dataIOTable;



    public StepPropertiesController() {
    }

    @FXML public void initialize() {
        dataIOTable.setPlaceholder(new Text("No data inputs or outputs"));

        PropertiesManager.selectedDraggableStepForProperties.addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                setStep(newValue);
            else
                reset();
        });
    }

    public void reset() {
        dataIOTable.getItems().clear();
    }

    private void setStep(DraggableStep step) {
        List<DataIODTO> dataIOs = new ArrayList<>();
        dataIOs.addAll(step.step().inputs());
        dataIOs.addAll(step.step().outputs());
        if (!dataIOs.isEmpty())
            dataIOTable.getItems().setAll(dataIOs.stream().map(DataLink::new).collect(Collectors.toList()));
        else
            dataIOTable.getItems().clear();
    }
}
