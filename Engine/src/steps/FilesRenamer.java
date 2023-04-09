package steps;

import data.definitions.FileData;
import data.definitions.ListData;
import data.definitions.StringData;
import data.definitions.TableData;
import data.io.Input;
import data.io.Output;
import shared.StatManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        ArrayList<FileData> files = ((ListData<FileData>)inputs[0].getData()).getData();
        for(Integer i = 0; i < files.size(); i++){
            ArrayList<String> row = new ArrayList<>();
            File current = files.get(i).getData();
            row.add(i.toString());
            String parentDir = files.get(i).getData().getParent();
            String name = current.getName();
            row.add(name);
            String newName = parentDir +"\\"+
            current.renameTo(new File(parentDir +"\\"+))

        }

        StatManager.add(StatManager.TYPE.STEP, simpleName, System.currentTimeMillis() - startTime);
    }
}
