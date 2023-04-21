package com.main.stepper.data.implementation.file;

import com.main.stepper.data.api.AbstractDataDef;

import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

public class FileDef extends AbstractDataDef {
    public FileDef() {
        super("File", false, File.class);
    }
}
