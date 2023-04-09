package steps;

import data.definitions.FileData;
import data.definitions.ListData;
import data.definitions.NumberData;
import data.definitions.StringData;
import data.io.Input;
import data.io.Output;
import shared.Logger;
import shared.StatManager;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileScanner extends Step{
    public FileScanner(){
        super("Collect Files In Folder", true, 2, 2);
        this.description = "Given a folder directory, returns list of files in that folder.";
        this.inputs[0] = new Input(StringData.class, "FOLDER_NAME", "Folder name to scan", true);
        this.inputs[1] = new Input(StringData.class, new StringData(".*"), "FILTER", "Filter only these files", false);
        this.outputs[0] = new Output(ListData.class, "FILES_LIST", "Files list");
        this.outputs[1] = new Output(NumberData.class, "TOTAL_FOUND", "Total files found");
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        // check if path is legal and exists
        String path = ((StringData) inputs[0].getData()).getData();
        try {
            Paths.get(path);
        } catch (InvalidPathException e) {
            Logger.log("Failure - Invalid path!");
            summary = "Failure - Invalid path!";
            flag = FLAG.FAILURE;
            return;
        }
        Logger.log("Reading folder " + path + " content with filter " + ((StringData) inputs[1].getData()).getData());
        ListData<FileData> filesList = (ListData) outputs[0].getData();
        File dir = new File(path);
        Set<File> files = Stream.of(dir.listFiles()).filter(file -> !file.isDirectory()).collect(Collectors.toSet());
        for (File file : files)
            if (file.getName().endsWith(((StringData) outputs[1].getData()).getData()))
                filesList.add(new FileData(file));
        outputs[1].setData(new NumberData(files.size()));
        if(files.size() == 0){
            Logger.log("No files found!");
            flag = FLAG.WARNING;
        }
        else{
            Logger.log("Found " + files.size() + " files in folder matching the filter");
            flag = FLAG.SUCCESS;
        }
        StatManager.add(StatManager.TYPE.STEP, simpleName, System.currentTimeMillis() - startTime);
    }
}
