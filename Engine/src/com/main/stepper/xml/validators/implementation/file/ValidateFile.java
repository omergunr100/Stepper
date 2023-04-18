package com.main.stepper.xml.validators.implementation.file;

import com.main.stepper.xml.validators.api.IValidator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ValidateFile implements IValidator {
    private File file;

    public ValidateFile(String path) {
        this.file = new File(path);
    }

    public ValidateFile(File file) {
        this.file = file;
    }

    @Override
    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (!file.exists()){
            errors.add("File does not exist: " + file.getName());
        }
        else if (!file.isFile()) {
            errors.add("Path is not a file: " + file.getName());
        }
        else if(!file.getName().endsWith(".xml")){
            errors.add("File is not an XML file: " + file.getName());
        }

        return errors;
    }

    @Override
    public Optional<File> getAdditional() {
        return Optional.ofNullable(file);
    }
}
