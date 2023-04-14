package com.engine.data.implementation.file;

import com.engine.data.api.AbstractDataDef;

import java.io.File;

public class FileDef extends AbstractDataDef {
    public FileDef() {
        super("File", false, File.class);
    }
}
