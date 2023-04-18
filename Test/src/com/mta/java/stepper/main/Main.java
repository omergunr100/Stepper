package com.mta.java.stepper.main;

import com.mta.java.stepper.data.implementation.list.datatype.FileList;
import com.mta.java.stepper.data.implementation.relation.Relation;
import com.mta.java.stepper.io.api.IDataIO;
import com.mta.java.stepper.logger.api.ILogger;
import com.mta.java.stepper.logger.implementation.MapLogger;
import com.mta.java.stepper.logger.implementation.data.Log;
import com.mta.java.stepper.step.definition.StepRegistry;
import com.mta.java.stepper.step.definition.api.IStepDefinition;
import com.mta.java.stepper.step.definition.api.StepResult;
import com.mta.java.stepper.step.execution.api.IStepExecutionContext;
import com.mta.java.stepper.step.execution.implementation.StepExecutionContext;
import javafx.util.Pair;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
         // Test for SpendSomeTime
//        SpendSomeTimeTest();

        // Test for Collect Files In Folder
//        CollectFilesInFolderTest();

        // Test for Files Deleter
//        FilesDeleterTest();

        // Test for Files Renamer
//        FilesRenamerTest();

        // Test for Files Content Extractor
//        FilesContentExtractorTest(2);

        // Test for CSV Exporter
//        CSVExporterTest();

        // Test for Properties Exporter
//        PropertiesExporterTest();
    }

    private static void PropertiesExporterTest() {
        IStepDefinition step = StepRegistry.PROPERTIES_EXPORTER;
        Map<IDataIO, Object> variables = new HashMap<>();
        IDataIO sourceIO = step.getInputs().get(0);
        IDataIO resultIO = step.getOutputs().get(0);

        List<String> columns = Arrays.asList("Column1", "Column2", "Column3");
        List<String> row1 = Arrays.asList("1", "2", "3");
        List<String> row2 = Arrays.asList("4", "5", "6");
        List<String> row3 = Arrays.asList("7", "8", "9");
        Relation source = new Relation(columns);
        source.addRow(row1);
        source.addRow(row2);
        source.addRow(row3);
        variables.put(sourceIO, source);

        Map<IDataIO, IDataIO> mapping = new HashMap<>();
        mapping.put(sourceIO, sourceIO);
        mapping.put(resultIO, resultIO);

        ILogger logger = new MapLogger();

        IStepExecutionContext context = new StepExecutionContext(variables, mapping, logger.getSubLogger("Step"));

        StepResult result = step.execute(context);

        System.out.println("Result: " + result);
        System.out.println("Summary: " + context.getSummary());
        System.out.println("Logs:");
        for(Log log : logger.getLog("Step")) {
            System.out.println(log);
        }
        String csvStr = (String) variables.get(resultIO);
        System.out.println("Properties String: " + csvStr);
    }

    private static void CSVExporterTest() {
        IStepDefinition step = StepRegistry.CSV_EXPORTER;
        Map<IDataIO, Object> variables = new HashMap<>();
        IDataIO sourceIO = step.getInputs().get(0);
        IDataIO resultIO = step.getOutputs().get(0);


        List<String> columns = Arrays.asList("Column1", "Column2", "Column3");
        List<String> row1 = Arrays.asList("1", "2", "3");
        List<String> row2 = Arrays.asList("4", "5", "6");
        List<String> row3 = Arrays.asList("7", "8", "9");
        Relation source = new Relation(columns);
        source.addRow(row1);
        source.addRow(row2);
        source.addRow(row3);
        variables.put(sourceIO, source);

        Map<IDataIO, IDataIO> mapping = new HashMap<>();
        mapping.put(sourceIO, sourceIO);
        mapping.put(resultIO, resultIO);

        ILogger logger = new MapLogger();

        IStepExecutionContext context = new StepExecutionContext(variables, mapping, logger.getSubLogger("Step"));

        StepResult result = step.execute(context);

        System.out.println("Result: " + result);
        System.out.println("Summary: " + context.getSummary());
        System.out.println("Logs:");
        for(Log log : logger.getLog("Step")) {
            System.out.println(log);
        }
        String csvStr = (String) variables.get(resultIO);
        System.out.println("CSV String: " + csvStr);
    }

    private static void FilesContentExtractorTest(int line) {
        IStepDefinition step = StepRegistry.FILES_CONTENT_EXTRACTOR;
        Map<IDataIO, Object> variables = new HashMap<>();
        IDataIO filesIO = step.getInputs().get(0);
        IDataIO lineIO = step.getInputs().get(1);
        IDataIO dataResultIO = step.getOutputs().get(0);

        FileList files = new FileList();
        files.add(new File("C:\\Users\\omere\\Desktop\\Test\\File1.txt"));
        files.add(new File("C:\\Users\\omere\\Desktop\\Test\\File2.txt"));
        variables.put(filesIO, files);
        variables.put(lineIO, line);

        Map<IDataIO, IDataIO> mapping = new HashMap<>();
        mapping.put(filesIO, filesIO);
        mapping.put(lineIO, lineIO);
        mapping.put(dataResultIO, dataResultIO);

        ILogger logger = new MapLogger();

        IStepExecutionContext context = new StepExecutionContext(variables, mapping, logger.getSubLogger("Step"));

        StepResult result = step.execute(context);

        System.out.println("Result: " + result);
        System.out.println("Logs:");
        for(Log log : logger.getLog("Step"))
            System.out.println(log);
        System.out.println("Summary: " + context.getSummary());
        Relation dataResult = (Relation) variables.get(dataResultIO);
        System.out.println(dataResult.getColumns());
        for(Integer i = 0; i < dataResult.getRowCount(); i++)
            System.out.println(dataResult.getRowByColumnsOrder(i));
    }

    private static void FilesRenamerTest() {
        IStepDefinition step = StepRegistry.FILES_RENAMER;
        Map<IDataIO, Object> variables = new HashMap<>();
        IDataIO filesIO = step.getInputs().get(0);
        IDataIO prefixIO = step.getInputs().get(1);
        IDataIO suffixIO = step.getInputs().get(2);
        IDataIO renameResultIO = step.getOutputs().get(0);

        FileList files = new FileList();
        files.add(new File("C:\\Users\\omere\\Desktop\\Test\\File1.txt"));
        String prefix = "prefix";
        String suffix = "suffix";
        variables.put(filesIO, files);
        variables.put(prefixIO, prefix);
        variables.put(suffixIO, suffix);

        Map<IDataIO, IDataIO> mapping = new HashMap<>();
        mapping.put(filesIO, filesIO);
        mapping.put(prefixIO, prefixIO);
        mapping.put(suffixIO, suffixIO);
        mapping.put(renameResultIO, renameResultIO);

        ILogger logger = new MapLogger();

        IStepExecutionContext context = new StepExecutionContext(variables, mapping, logger.getSubLogger("Step"));
        StepResult result = step.execute(context);

        System.out.println("Result: " + result);
        System.out.println("Logs:");
        for(Log log : logger.getLog("Step"))
            System.out.println(log);
        System.out.println("Summary: " + context.getSummary());
        Relation renameResult = (Relation) variables.get(renameResultIO);
        System.out.println(renameResult.getColumns());
        for(Integer i = 0; i < renameResult.getRowCount(); i++)
            System.out.println(renameResult.getRowByColumnsOrder(i));
    }

    private static void FilesDeleterTest() {
        IStepDefinition step = StepRegistry.FILES_DELETER;
        Map<IDataIO, Object> variables = new HashMap<>();
        IDataIO filesIO = step.getInputs().get(0);
        IDataIO deletedFilesIO = step.getOutputs().get(0);
        IDataIO deletionStatsIO = step.getOutputs().get(1);
        FileList files = new FileList();
        files.add(new File("C:\\Users\\omere\\Desktop\\Test\\File1.txt"));
        files.add(new File("C:\\Users\\omere\\Desktop\\Test\\File2.txt"));
        variables.put(filesIO, files);
        Map<IDataIO, IDataIO> mapping = new HashMap<>();
        mapping.put(filesIO, filesIO);
        mapping.put(deletedFilesIO, deletedFilesIO);
        mapping.put(deletionStatsIO, deletionStatsIO);
        ILogger logger = new MapLogger();
        IStepExecutionContext context = new StepExecutionContext(variables, mapping, logger.getSubLogger("Step"));
        StepResult result = step.execute(context);
        System.out.println("Result: " + result);
        System.out.println("The logs:");
        for(Log log : logger.getLog("Step")){
            System.out.println(log);
        }
        FileList deletedFiles = (FileList) variables.get(deletedFilesIO);
        System.out.println("Failed to delete files:");
        deletedFiles.stream().map(File::getName).forEach(System.out::println);
        Pair<Integer, Integer> deletionStats = (Pair<Integer, Integer>) variables.get(deletionStatsIO);
        System.out.println("Deletion stats: " + deletionStats);
    }

    private static void CollectFilesInFolderTest() {
        IStepDefinition step = StepRegistry.COLLECT_FILES_IN_FOLDER;
        Map<IDataIO, Object> variables = new HashMap<>();
        IDataIO folder = step.getInputs().get(0);
        IDataIO extension = step.getInputs().get(1);
        IDataIO files = step.getOutputs().get(0);
        IDataIO count = step.getOutputs().get(1);
        variables.put(folder, "C:\\Users\\omere\\Desktop\\Test");
        variables.put(extension, ".txt");
        Map<IDataIO, IDataIO> mapping = new HashMap<>();
        mapping.put(folder, folder);
        mapping.put(extension, extension);
        mapping.put(files, files);
        mapping.put(count, count);
        ILogger logger = new MapLogger();
        IStepExecutionContext context = new StepExecutionContext(variables, mapping, logger.getSubLogger("Step"));
        StepResult result = step.execute(context);
        System.out.println("Result: " + result);
        System.out.println("The logs:");
        for(Log log : logger.getLog("Step")){
            System.out.println(log);
        }
    }

    private static void SpendSomeTimeTest() {
        IStepDefinition step = StepRegistry.SPEND_SOME_TIME;
        Map<IDataIO, Object> variables = new HashMap<>();
        IDataIO time = step.getInputs().get(0);
        variables.put(time, 3);
        Map<IDataIO, IDataIO> mapping = new HashMap<>();
        mapping.put(time, time);
        ILogger logger = new MapLogger();
        IStepExecutionContext context = new StepExecutionContext(variables, mapping, logger.getSubLogger("Step"));
        StepResult result = step.execute(context);
        System.out.println("Result: " + result);
        System.out.println("The logs:");
        for(Log log : logger.getLog("Step")){
            System.out.println(log);
        }
    }
}
