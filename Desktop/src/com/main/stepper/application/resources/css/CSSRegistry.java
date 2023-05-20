package com.main.stepper.application.resources.css;

import java.io.File;

public enum CSSRegistry {
    DEFAULT("Default", new File("default.css")),
    DARK("Dark", new File("dark-theme.css")),
    CASPIAN("Caspian", new File("caspian.css"));

    private String name;
    private File file;

    CSSRegistry(String name, File file) {
        this.name = name;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }
}
