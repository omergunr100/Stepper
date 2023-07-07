package com.main.stepper.client.resources.fxml.reusable.flowinput;

import com.main.stepper.data.implementation.enumeration.zipper.ZipperEnumData;
import com.main.stepper.shared.structures.dataio.DataIODTO;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;

public class FlowInputController {
    private DataIODTO input;
    @FXML private VBox root;
    @FXML private Label nameLabel;
    @FXML private Label userStringLabel;
    @FXML private Label necessityLabel;
    @FXML private Label typeLabel;
    @FXML private TextField valueField;
    @FXML private CheckBox validityCheckbox;

    public FlowInputController() {
    }

    @FXML public void initialize(){
        root.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        setValid(false);
    }

    public void init(DataIODTO input) {
        this.input = input;
        nameLabel.setText(input.name());
        userStringLabel.setText(input.userString());
        necessityLabel.setText(input.necessity().toString());
        if(input.type().getType().isAssignableFrom(ZipperEnumData.class)) {
            StringBuilder builder = new StringBuilder();
            List<String> values = ZipperEnumData.getValues();
            builder.append("Enumerator: (");
            for(String value : values) {
                builder.append(value).append("\\");
            }
            builder.deleteCharAt(builder.lastIndexOf("\\")).append(")");
            typeLabel.setText(builder.toString());
        }
        else
            typeLabel.setText(input.type().getName());
    }

    public DataIODTO input() {
        return input;
    }

    public String getValue(){
        return valueField.getText();
    }

    public void setValue(String value) {
        valueField.setText(value);
    }

    public void setInputStyle(String css) {
        valueField.setStyle(css);
    }

    public void setVisible(boolean visible) {
        root.setVisible(visible);
    }

    public void setValid(boolean valid) {
        validityCheckbox.setSelected(valid);
    }
}
