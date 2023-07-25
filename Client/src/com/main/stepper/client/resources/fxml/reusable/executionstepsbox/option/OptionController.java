package com.main.stepper.client.resources.fxml.reusable.executionstepsbox.option;

import com.main.stepper.flow.definition.api.FlowResult;
import com.main.stepper.shared.icons.IconLoadEnum;
import com.main.stepper.step.definition.api.StepResult;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public final class OptionController {
    @FXML private ImageView statusImage;
    @FXML private Hyperlink name;

    private IconLoadEnum currentState = null;

    public OptionController() {
    }

    public void setStatus(FlowResult result) throws IOException {
        switch (result) {
            case SUCCESS:
                currentState = IconLoadEnum.SUCCESS;
                statusImage.setImage(new Image(IconLoadEnum.SUCCESS.getResource().openStream()));
                break;
            case FAILURE:
                currentState = IconLoadEnum.ERROR;
                statusImage.setImage(new Image(IconLoadEnum.ERROR.getResource().openStream()));
                break;
            case RUNNING:
                currentState = IconLoadEnum.RUNNING;
                statusImage.setImage(new Image(IconLoadEnum.RUNNING.getResource().openStream()));
                break;
            case WARNING:
                currentState = IconLoadEnum.WARNING;
                statusImage.setImage(new Image(IconLoadEnum.WARNING.getResource().openStream()));
                break;
        }
    }

    public void setStatus(StepResult result) throws IOException {
        switch (result) {
            case SUCCESS:
                currentState = IconLoadEnum.SUCCESS;
                statusImage.setImage(new Image(IconLoadEnum.SUCCESS.getResource().openStream()));
                break;
            case FAILURE:
                currentState = IconLoadEnum.ERROR;
                statusImage.setImage(new Image(IconLoadEnum.ERROR.getResource().openStream()));
                break;
            case WARNING:
                currentState = IconLoadEnum.WARNING;
                statusImage.setImage(new Image(IconLoadEnum.WARNING.getResource().openStream()));
                break;
        }
    }

    public void setOnAction(Runnable action) {
        name.setOnAction(event -> action.run());
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public String name() {
        return name.getText();
    }

    public IconLoadEnum currentState() {
        return currentState;
    }

    public void deselect() {
        name.setVisited(false);
    }
}
