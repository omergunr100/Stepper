package com.main.stepper.data.implementation.file;

import com.main.stepper.data.api.AbstractDataDef;

public class FileDef extends AbstractDataDef {
    public FileDef() {
        super("File", false, FileData.class);
    }
}
