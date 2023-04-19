package com.main.stepper.xml.parsing.api;

import java.util.List;
import java.util.Optional;

public interface IParser {
    <T> T parse() throws Exception;
    <T> T get();
}
