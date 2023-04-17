package com.engine.deprecated.steps;

/*
public class FilesContentExtractor extends Step{
    public FilesContentExtractor(){
        super("Files Content Extractor", true, 2, 1);
        this.description = "Given a list of files and a row number, returns table of files content at row, if row doesn't exist, returns N\\A";
        this.inputs[0] = new Input(ListData.class, "FILES_LIST", "Files to extract", true);
        this.inputs[1] = new Input(NumberData.class, "LINE", "Line number to extract", true);
        this.outputs[0] = new Output(TableData.class, "DATA", "Data extraction");
    }
    @Override
    public void run() {
        long startTime = System.currentTimeMillis();

        List<FileData> files = ((ListData<FileData>) inputs[0].getData()).getData();
        Integer line = ((NumberData) inputs[1].getData()).getData();

        if(files.size() == 0){
            flag = FLAG.SUCCESS;
            Logger.log("No files to extract");
            summary = "No files to extract";
            return;
        }
        for(Integer i = 0; i < files.size(); i++){
            FileData file = files.get(i);
            if(file.getData().size() < line){
                Logger.log("File " + file.getName() + " doesn't have line " + line);
                file.getData().add("N\\A");
            }
            else{
                Logger.log("File " + file.getName() + " has line " + line);
                file.getData().add(file.getData().get(line));
            }
        }

        StatManager.add(StatManager.TYPE.STEP, simpleName, System.currentTimeMillis() - startTime);
    }
}
*/