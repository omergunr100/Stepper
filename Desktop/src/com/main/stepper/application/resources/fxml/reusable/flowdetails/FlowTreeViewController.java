package com.main.stepper.application.resources.fxml.reusable.flowdetails;

import com.main.stepper.engine.data.api.IFlowInformation;
import com.main.stepper.flow.definition.api.IStepUsageDeclaration;
import com.main.stepper.io.api.IDataIO;
import javafx.fxml.FXML;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class FlowTreeViewController {
    @FXML private TreeView<String> flowTree;

    public FlowTreeViewController() {
    }

    public void setCurrentFlow(IFlowInformation information) {
        if(information == null){
            flowTree.setRoot(null);
            return;
        }

        TreeItem<String> name = new TreeItem<>(information.name()); // root
        // add root to view
        flowTree.setRoot(name);

        TreeItem<String> description = new TreeItem<>("Description: "); // t1
        name.getChildren().add(description);
        for(String line : information.description().split("\\. ")) {
            TreeItem<String> lineItem = new TreeItem<>(line + "."); // t2
            description.getChildren().add(lineItem);
        }

        TreeItem<String> readOnly = new TreeItem<>("Read only: " + information.isReadOnly().toString()); // t1
        name.getChildren().add(readOnly);

        TreeItem<String> formalOutputs = new TreeItem<>("Formal outputs:"); // t1
        name.getChildren().add(formalOutputs);
        for(IDataIO formalOut : information.formalOutputs()) {
            TreeItem<String> formalOutput = new TreeItem<>("Name: " + formalOut.getName()); // t2
            TreeItem<String> formalOutputDesc = new TreeItem<>("Description: " + formalOut.getUserString()); // t3
            TreeItem<String> formalOutputType = new TreeItem<>("Type: " + formalOut.getDataDefinition().getName()); // t3
            formalOutput.getChildren().addAll(formalOutputDesc, formalOutputType);
            formalOutputs.getChildren().add(formalOutput);
        }

        TreeItem<String> stepsInFlow = new TreeItem<>("Steps in flow:"); // t1
        name.getChildren().add(stepsInFlow);
        for(IStepUsageDeclaration step : information.steps()) {
            TreeItem<String> stepType = new TreeItem<>(step.step().getName()); // t2
            TreeItem<String> alias = new TreeItem<>("Flow alias: " + step.name()); // t3
            TreeItem<String> stepReadOnly = new TreeItem<>("Read only: " + step.step().isReadOnly().toString()); // t3
            stepType.getChildren().addAll(alias, stepReadOnly);
            stepsInFlow.getChildren().add(stepType);
        }

        TreeItem<String> openUserInputs = new TreeItem<>("Open user inputs:"); // t1
        name.getChildren().add(openUserInputs);
        for(IDataIO input : information.openUserInputs()) {
            TreeItem<String> inputName = new TreeItem<>("Name: " + input.getName()); // t2
            TreeItem<String> type = new TreeItem<>("Type: " + input.getDataDefinition().getName()); // t3
            TreeItem<String> necessity = new TreeItem<>("Necessity: " + input.getNecessity().toString()); // t3
            TreeItem<String> linkedSteps = new TreeItem<>("Linked steps:"); // t3
            inputName.getChildren().addAll(type, necessity, linkedSteps);
            for(IStepUsageDeclaration step : information.linkedSteps(input)) {
                TreeItem<String> stepName = new TreeItem<>(step.name()); // t4
                linkedSteps.getChildren().add(stepName);
            }
            openUserInputs.getChildren().add(inputName);
        }

        TreeItem<String> internalOutputs = new TreeItem<>("Internal outputs:"); // t1
        name.getChildren().add(internalOutputs);
        for(IDataIO output : information.internalOutputs()) {
            TreeItem<String> outputName = new TreeItem<>("Name: " + output.getName()); // t2
            TreeItem<String> type = new TreeItem<>("Type: " + output.getDataDefinition().getName()); // t3
            TreeItem<String> producer = new TreeItem<>("Produced by step: " + information.producer(output).name()); // t3
            outputName.getChildren().addAll(type, producer);
            internalOutputs.getChildren().add(outputName);
        }
    }

    public void expandAll() {
        if(flowTree.getRoot() == null)
            return;
        expand(flowTree.getRoot());
    }
    private void expand(TreeItem item) {
        if(item == null)
            return;
        item.setExpanded(true);
        for(Object child : item.getChildren()) {
            expand((TreeItem) child);
        }
    }
}
