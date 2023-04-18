package com.main.stepper.xml.parsing.api;

import java.util.List;
import java.util.Optional;

public interface IParser {
    List<String> parse();
    <T> Optional<T> get();
}
