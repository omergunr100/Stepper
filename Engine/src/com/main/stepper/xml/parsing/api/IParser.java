package com.main.stepper.xml.parsing.api;

import com.main.stepper.exceptions.xml.XMLException;

public interface IParser {
    <T> T parse() throws XMLException;
    <T> T get();
}
