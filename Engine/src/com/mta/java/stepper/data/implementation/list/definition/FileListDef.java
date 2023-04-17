package com.mta.java.stepper.data.implementation.list.definition;

import com.mta.java.stepper.data.api.AbstractDataDef;
import com.mta.java.stepper.data.implementation.list.datatype.FileList;

public class FileListDef extends AbstractDataDef {
    public FileListDef() {
        super("File List", false, FileList.class);
    }
}
