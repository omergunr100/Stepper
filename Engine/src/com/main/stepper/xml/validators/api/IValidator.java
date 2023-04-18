package com.main.stepper.xml.validators.api;

import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface IValidator {
    List<String> validate();
    default <T> Optional<T> getAdditional() {return Optional.empty();}
}
