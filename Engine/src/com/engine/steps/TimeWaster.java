package com.engine.steps;

import com.engine.data.definitions.NumberData;
import com.engine.data.io.Input;
import com.engine.logger.Logger;
import com.engine.statistics.StatManager;

public class TimeWaster extends Step{

    public TimeWaster(){
        super("Spend Some Time", true, 1, 0);
        this.description = "Pauses the flow for a specified number of seconds.";
        this.inputs[0] = new Input(NumberData.class, "TIME_TO_SPEND", "Total sleeping time (sec)", true);
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        if(((NumberData)inputs[0].getData()).getData() < 0){
            Logger.log("Failure - Integer must be non-negative!");
            summary = "Failure - Integer must be non-negative!";
            flag = FLAG.FAILURE;
            return;
        }
        Logger.log("About to sleep for "+inputs[0]+" seconds…");
        try {
            Thread.sleep((long) ((NumberData)inputs[0].getData()).getData() * 1000);
        } catch (InterruptedException e) {
        }
        Logger.log("Done sleeping...");
        summary = "Success!";
        flag = FLAG.SUCCESS;
        StatManager.add(StatManager.TYPE.STEP, simpleName, System.currentTimeMillis() - startTime);
    }
}
