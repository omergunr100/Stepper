package com.main.stepper.xml.parsing.api;

public interface IParser {
    <T> T parse() throws Exception;
    <T> T get();
}
