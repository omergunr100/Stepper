package com.main.stepper.application.resources.css;

import java.io.File;

public enum CSSRegistry {
    DEFAULT("Default", new File("default.css")),
    DARK("Dark", new File("dark-theme.css")),
    CASPIAN("Caspian", new File("caspian.css")),
    DREAM_OF_A_MAD_MAN("Dream of a mad man", new File("dream-of-a-mad-man.css")),
    DREAM_OF_A_MAD_AI("Dream of a mad AI", new File("dream-of-a-mad-ai.css"));

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
