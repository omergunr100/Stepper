package com.engine.deprecated.data.definitions;

import java.io.File;

public class FileData extends DataDef<File> {
    public FileData() {
        this.data = null;
        this.setUserFriendly(false);
    }
    public FileData(String path) {
        this.data = new File(path);
        this.setUserFriendly(false);
    }
    public FileData(File data) {
        this.data = data;
        this.setUserFriendly(false);
    }
    @Override
    public File getData() {
        return this.data;
    }

    @Override
    public void setData(File data) {
        this.data = data;
    }

    public void setData(String path) {
        this.data = new File(path);
    }
}
