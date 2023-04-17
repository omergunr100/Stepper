package com.mta.java.stepper.data.implementation.file;

import com.mta.java.stepper.data.api.AbstractDataDef;

import java.io.File;

public class FileDef extends AbstractDataDef {
    public FileDef() {
        super("File", false, File.class);
    }
}
