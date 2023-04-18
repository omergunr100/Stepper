package com.main.stepper.xml.parsing.api;

import com.main.stepper.io.api.IDataIO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IParser {
    List<String> parse();
    <T> Optional<T> get();
}
