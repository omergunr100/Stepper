package com.main.stepper.admin.resources.fxml.tabs.flowmaker.componentdragview.datalink;

import com.main.stepper.data.DDRegistry;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.shared.structures.dataio.DataIODTO;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class DataLink {
    private DataIODTO dataIO;
    public final SimpleObjectProperty<DataNecessity> necessityProperty = new SimpleObjectProperty<>();
    public final SimpleStringProperty nameProperty = new SimpleStringProperty();
    public final SimpleStringProperty aliasProperty = new SimpleStringProperty();
    public final SimpleObjectProperty<DDRegistry> typeProperty = new SimpleObjectProperty<>();
    public final SimpleBooleanProperty hasInitialValueProperty = new SimpleBooleanProperty();
    public final SimpleStringProperty initialValueProperty = new SimpleStringProperty();
    public final SimpleStringProperty prevInitialValueProperty = new SimpleStringProperty();
    // for tracking if the data link has been changed since last checked for connections
    public final SimpleBooleanProperty changedProperty = new SimpleBooleanProperty();

    public DataLink(DataIODTO dataIO) {
        this.dataIO = dataIO;
        this.necessityProperty.set(dataIO.necessity());
        this.nameProperty.set(dataIO.name());
        this.aliasProperty.set("");
        this.typeProperty.set(dataIO.type());
        this.hasInitialValueProperty.set(false);
        this.initialValueProperty.set("");
        this.prevInitialValueProperty.set("");
        this.changedProperty.set(false);
    }

    public DataIODTO dataIODTO() {
        return dataIO;
    }
}
