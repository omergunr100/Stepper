package com.main.stepper.data.implementation.file;

import java.io.File;
import java.net.URI;

public class FileData extends File {
    public FileData(File file){
        super(file.getAbsolutePath());
    }

    public FileData(String pathname) {
        super(pathname);
    }

    @Override
    public String toString() {
        return getAbsolutePath();
    }
}
