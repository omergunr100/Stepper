package com.main.stepper.data.implementation.list.datatype;

import com.main.stepper.data.implementation.file.FileData;

import java.io.File;

public class FileList extends GenericList<FileData> {
    public boolean add(File file) {
        return super.add(new FileData(file));
    }
}
