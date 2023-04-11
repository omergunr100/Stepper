package com.engine.xml.validation;

import com.engine.steps.*;

public enum STEPS {
    SPEND_SOME_TIME("Spend Some Time", TimeWaster.class),
    COLLECT_FILES_IN_FOLDER("Collect Files In Folder", FileScanner.class),
    FILES_DELETER("Files Deleter", FilesDeleter.class),
    FILES_RENAMER("Files Renamer", FilesRenamer.class),
    FILES_CONTENT_EXTRACTOR("Files Content Extractor", /*FilesContentExtractor.class*/null),
    CSV_EXPORTER("CSV Exporter", /*CSVExporter.class*/null),
    PROPERTIES_EXPORTER("Properties Exporter", /*PropertiesExporter.class*/null),
    FILE_DUMPER("File Dumper", /*FileDumper.class*/null),
    ZIPPER("Zipper", /*Zipper.class*/null),
    COMMAND_LINE("Command Line", /*CommandLine.class*/null),
    HTTP_CALL("HTTP Call", /*HTTPCall.class*/null),
    TO_JSON("To Json", /*ToJson.class*/null),
    JSON_DATA_EXTRACTOR("Json Data Extractor", /*JsonDataExtractor.class*/null);
    private final String name;
    private final Class<? extends Step> stepClass;
    STEPS(String name, Class<? extends Step> stepClass){
        this.name = name;
        this.stepClass = stepClass;
    }
    public String getName(){
        return name;
    }
    public Class<? extends Step> getStepClass() {
        return stepClass;
    }
}
