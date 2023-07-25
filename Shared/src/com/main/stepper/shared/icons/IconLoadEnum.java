package com.main.stepper.shared.icons;

import java.net.URL;

public enum IconLoadEnum {
    ERROR("error.png"),
    WARNING("warning.png"),
    SUCCESS("success.png"),
    RUNNING("running.gif");

    private String path;

    IconLoadEnum(String path) {
        this.path = path;
    }

    public URL getResource() {
        return IconLoadEnum.class.getResource(path);
    }
}
