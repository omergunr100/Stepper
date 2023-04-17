package com.engine.deprecated.steps;

import com.engine.deprecated.data.definitions.FileData;
import com.engine.deprecated.data.definitions.ListData;
import com.engine.deprecated.data.definitions.NumberData;
import com.engine.deprecated.data.definitions.PairData;
import com.engine.deprecated.data.io.Input;
import com.engine.deprecated.data.io.Output;
import com.engine.logger.Logger;
import com.engine.statistics.StatManager;

import java.util.List;

public class FilesDeleter extends Step{
    public FilesDeleter(){
        super("Files Deleter", false, 1, 2);
        this.description = "Given a list of files, deletes them.";
        this.inputs[0] = new Input(ListData.class, "FILES_LIST", "List of files to delete", true);
        this.outputs[0] = new Output(ListData.class, "DELETED_LIST", "Files failed to be deleted");
        this.outputs[1] = new Output(PairData.class, "DELETION_STATS", "Deletion summary results");
    }
    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        List<FileData> files = ((ListData) inputs[0].getData()).getData();
        ListData<FileData> failedFiles = (ListData) outputs[0].getData();
        Logger.log("About to start delete "+files.size()+" files");
        PairData<NumberData, NumberData> stats = (PairData<NumberData, NumberData>) outputs[1].getData();
        stats.getFirst().setData(0);
        stats.getSecond().setData(0);
        if(files.size() == 0){
            Logger.log("No files to delete!");
            flag = FLAG.SUCCESS;
            summary = "No files to delete!";
            return;
        }
        for(FileData file : files){
            if(file.getData().delete()){
                stats.getFirst().setData(stats.getFirst().getData() + 1);
            }
            else{
                stats.getSecond().setData(stats.getSecond().getData() + 1);
                failedFiles.add(file);
                Logger.log("Failed to delete file: "+file.getData().getName());
            }
        }
        if(stats.getSecond().getData() == files.size()){
            flag = FLAG.WARNING;
            Logger.log("Failed to delete all files!");
            summary = "Failed to delete all files!";
        }
        else{
            flag = FLAG.SUCCESS;
            Logger.log("Deleted "+stats.getFirst().getData()+" out of "+files.size()+" files successfully");
            summary = "Deleted "+stats.getFirst().getData()+" out of "+files.size()+" files successfully";
        }
        StatManager.add(StatManager.TYPE.STEP, simpleName, System.currentTimeMillis() - startTime);
    }
}
