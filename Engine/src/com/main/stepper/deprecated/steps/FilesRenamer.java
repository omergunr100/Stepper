package com.main.stepper.deprecated.steps;

import com.engine.deprecated.data.definitions.FileData;
import com.engine.deprecated.data.definitions.ListData;
import com.engine.deprecated.data.definitions.StringData;
import com.engine.deprecated.data.definitions.TableData;
import com.engine.deprecated.data.io.Input;
import com.engine.deprecated.data.io.Output;
import com.engine.logger.Logger;
import com.engine.statistics.StatManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilesRenamer extends Step {
    public FilesRenamer(){
        super("Files Renamer", false, 3, 1);
        this.description = "Given list of files, changes their names according to the given prefix and postfix";
        this.inputs[0] = new Input(ListData.class, "FILES_TO_RENAME", "Files to rename", true);
        this.inputs[1] = new Input(StringData.class, new StringData(""), "PREFIX", "Add this prefix", false);
        this.inputs[2] = new Input(StringData.class, new StringData(""), "SUFFIX", "Append this suffix", false);
        this.outputs[0] = new Output(TableData.class, "RENAME_RESULT", "Rename operation summary");
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();

        TableData result =(TableData)outputs[0].getData();
        result.setTitles(Arrays.asList("Index", "Old Name", "New Name"));
        List<FileData> files = ((ListData<FileData>)inputs[0].getData()).getData();
        String prefix = ((StringData)inputs[1].getData()).getData();
        String postfix = ((StringData)inputs[2].getData()).getData();
        Logger.log("About to start rename"+files.size()+"files. Adding prefix: "+prefix+"; adding suffix: "+postfix);
        if(files.size() == 0){
            Logger.log("No files to rename!");
            summary = "No files to rename!";
            flag = FLAG.SUCCESS;
            return;
        }
        String failed = "";
        for(Integer i = 0; i < files.size(); i++){
            ArrayList<String> row = new ArrayList<>();
            File current = files.get(i).getData();
            row.add(i.toString());
            String parentDir = files.get(i).getData().getParent();
            String fullname = current.getName();
            row.add(fullname);
            String extension = fullname.substring(fullname.lastIndexOf("."));
            String name = fullname.substring(0, fullname.lastIndexOf("."));
            String newName = parentDir +"\\"+ prefix+ name + postfix + extension;
            Boolean success = current.renameTo(new File(newName));
            if(success)
                row.add(newName);
            else{
                row.add("N\\A");
                failed += fullname + "\n";
                Logger.log("Problem renaming file "+fullname);
            }
            result.getData().addRow(row);
        }
        if(failed == "") {
            flag = FLAG.SUCCESS;
            summary = "All files renamed successfully!";
        }
        else{
            flag = FLAG.WARNING;
            summary = "Some files failed to rename:\n" + failed;
        }

        StatManager.add(StatManager.TYPE.STEP, simpleName, System.currentTimeMillis() - startTime);
    }
}
