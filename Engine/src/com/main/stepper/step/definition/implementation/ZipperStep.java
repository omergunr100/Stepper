package com.main.stepper.step.definition.implementation;

import com.main.stepper.data.DDRegistry;
import com.main.stepper.data.implementation.enumeration.zipper.ZipperEnumData;
import com.main.stepper.engine.executor.api.IStepRunResult;
import com.main.stepper.engine.executor.implementation.StepRunResult;
import com.main.stepper.io.api.DataNecessity;
import com.main.stepper.io.api.IDataIO;
import com.main.stepper.io.implementation.DataIO;
import com.main.stepper.step.definition.api.AbstractStepDefinition;
import com.main.stepper.step.definition.api.StepResult;
import com.main.stepper.step.execution.api.IStepExecutionContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipperStep extends AbstractStepDefinition {
    public ZipperStep() {
        super("Zipper", false);
        addInput(new DataIO("SOURCE", "Source", DataNecessity.MANDATORY, DDRegistry.STRING));
        addInput(new DataIO("OPERATION", "Operation type", DataNecessity.MANDATORY, DDRegistry.ZIPPER_ENUM));
        addOutput(new DataIO("RESULT", "Zip operation result", DDRegistry.STRING));
    }

    @Override
    public Class<? extends AbstractStepDefinition> getStepClass() {
        return ZipperStep.class;
    }

    @Override
    public IStepRunResult execute(IStepExecutionContext context) {
        Instant startTime = Instant.now();
        // Read inputs and output
        List<IDataIO> inputs = getInputs();
        String source = (String) context.getInput(inputs.get(0), DDRegistry.STRING.getType());
        ZipperEnumData operation = (ZipperEnumData) context.getInput(inputs.get(1), DDRegistry.ZIPPER_ENUM.getType());
        String op = operation.getValue().orElse("non-specified");
        List<IDataIO> outputs = getOutputs();
        IDataIO result = outputs.get(0);
        // Log operation
        context.log("About to perform operation " + op + " on source " + source);

        // Check for edge cases
        File srcFile = new File(source);
        if(!srcFile.exists()) {
            context.log("Source file does not exist at: " + source);
            context.setOutput(result, "Source file does not exist at: " + source);
            Duration duration = Duration.between(startTime, Instant.now());
            return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.FAILURE, startTime, duration, "Source file does not exist at: " + source);
        }
        else if(!operation.getValue().isPresent()) {
            context.log("Operation is not specified");
            context.setOutput(result, "Operation is not specified");
            Duration duration = Duration.between(startTime, Instant.now());
            return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.FAILURE, startTime, duration, "Operation is not specified");
        }
        else if(op.equals("UNZIP") && !source.endsWith(".zip")) {
            context.log("Can't unzip file that is not a zip archive");
            context.setOutput(result, "Can't unzip file that is not a zip archive");
            Duration duration = Duration.between(startTime, Instant.now());
            return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.FAILURE, startTime, duration, "Can't unzip file that is not a zip archive");
        }

        // Perform operation
        if (op.equals("ZIP")) {
            File outFile;
            if(srcFile.isFile()) {
                String outPath = srcFile.getAbsolutePath().substring(0, srcFile.getAbsolutePath().lastIndexOf(".")) + ".zip";
                outFile = new File(outPath);
            }
            else{
                outFile = new File(srcFile.getAbsolutePath() + ".zip");
            }
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outFile))) {
                zip(zos, srcFile, "");
            } catch (IOException ignored) {
            }
        }
        else if (op.equals("UNZIP")) {
            File outFile = new File(srcFile.getParentFile().getAbsolutePath());
            try (ZipInputStream zis = new ZipInputStream(new FileInputStream(srcFile))) {
                unzip(zis, outFile.toPath());
            } catch (IOException ignored) {
            }
        }

        context.setOutput(result, "Success");
        Duration duration = Duration.between(startTime, Instant.now());
        return new StepRunResult(context.getUniqueRunId(), getName(), StepResult.SUCCESS, startTime, duration, "Success");
    }

    private static void zip(ZipOutputStream zos, File src, String parentDir){
        if(src.isFile()) {
            try (FileInputStream fis = new FileInputStream(src)) {
                ZipEntry entry = new ZipEntry(parentDir + "/" + src.getName());
                zos.putNextEntry(entry);

                int readChar = -1;
                while ((readChar = fis.read()) != -1) {
                    zos.write(readChar);
                }
                zos.closeEntry();
            } catch (IOException ignored) {
            }
        }
        else { // if file is a directory
            if(src.listFiles() == null){
                return;
            }
            for(File file : src.listFiles())
                zip(zos, file, parentDir + "/" + src.getName());
        }
    }

    private static void unzip(ZipInputStream zis, Path dest){
        ZipEntry entry;
        try{
            while((entry = zis.getNextEntry()) != null){
                File file = dest.resolve(entry.getName()).toFile();
                if(entry.isDirectory()){
                    file.mkdirs();
                }
                else{
                    file.getParentFile().mkdirs();
                    if(!file.exists())
                        file.createNewFile();
                    try(FileOutputStream fos = new FileOutputStream(file)){
                        int readChar = -1;
                        while((readChar = zis.read()) != -1){
                            fos.write(readChar);
                        }
                    }
                }
            }
        } catch (IOException ignored) {
        }
    }
}
