package com.main.stepper.xml.parsing.api;

import java.util.List;

public interface IParser {
    List<String> parse();
    <T> T get();
}
