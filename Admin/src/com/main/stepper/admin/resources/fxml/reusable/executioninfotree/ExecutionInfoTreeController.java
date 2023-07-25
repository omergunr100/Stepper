package com.main.stepper.admin.resources.fxml.reusable.executioninfotree;

import com.main.stepper.admin.resources.dataview.customtreecell.CustomTreeItem;
import com.main.stepper.admin.resources.fxml.reusable.executioninfotree.trees.FlowInfoTree;
import com.main.stepper.admin.resources.fxml.reusable.executioninfotree.trees.StepInfoTree;
import com.main.stepper.shared.structures.flow.FlowRunResultDTO;
import com.main.stepper.shared.structures.step.StepRunResultDTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class ExecutionInfoTreeController {
    @FXML private TreeView<CustomTreeItem> tree;

    private SimpleObjectProperty<FlowRunResultDTO> selectedFlowResult = null;
    private SimpleObjectProperty<StepRunResultDTO> selectedStepResult = null;
    private SimpleBooleanProperty isFlowSelected = null;

    private final ChangeListener<? super StepRunResultDTO> stepChangeListener = new ChangeListener<StepRunResultDTO>() {
        @Override
        public void changed(ObservableValue<? extends StepRunResultDTO> observable, StepRunResultDTO oldValue, StepRunResultDTO newValue) {
            changeTree();
        }
    };
    private final ChangeListener<? super Boolean> flowSelectedListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            changeTree();
        }
    };

    public ExecutionInfoTreeController() {
    }

    public void setBindings(SimpleObjectProperty<FlowRunResultDTO> selectedFlowResult, SimpleObjectProperty<StepRunResultDTO> selectedStepResult, SimpleBooleanProperty isFlowSelected) {
        if (this.selectedStepResult != null) {
            this.selectedStepResult.removeListener(stepChangeListener);
        }
        this.selectedStepResult = selectedStepResult;
        this.selectedStepResult.addListener(stepChangeListener);

        if (this.isFlowSelected != null) {
            this.isFlowSelected.removeListener(flowSelectedListener);
        }
        this.isFlowSelected = isFlowSelected;
        this.isFlowSelected.addListener(flowSelectedListener);

        this.selectedFlowResult = selectedFlowResult;
        this.selectedFlowResult.addListener((observable, oldValue, newValue) -> {
            if (isFlowSelected.get())
                changeTree();
        });
    }

    public void reset() {
        tree.setRoot(null);
    }

    private void changeTree() {
        if ((isFlowSelected == null || isFlowSelected.not().get()) && (selectedStepResult == null || selectedStepResult.isNull().get())) {
            tree.setRoot(null);
        } else {
            tree.setRoot(makeTree());
        }
    }

    private TreeItem<CustomTreeItem> makeTree() {
        if (isFlowSelected.get())
            synchronized (selectedFlowResult) {
                return FlowInfoTree.make(selectedFlowResult.get());
            }
        synchronized (selectedStepResult) {
            return StepInfoTree.make(selectedStepResult.get());
        }
    }
}
