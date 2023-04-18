package com.main.stepper.data.implementation.list.definition;

import com.main.stepper.data.api.AbstractDataDef;
import com.main.stepper.data.implementation.list.datatype.FileList;

public class FileListDef extends AbstractDataDef {
    public FileListDef() {
        super("File List", false, FileList.class);
    }
}
