package flows;

import data.definitions.DataDef;
import data.io.Input;
import data.io.Output;
import steps.Step;

import java.util.*;

public class Flow {
    // Maps names to data definitions
    protected Map<String, DataDef> nameToData;
    // List of required inputs
    protected List<Input> requiredInputs;
    // List of outbound outputs
    protected List<Output> formalOutputs;
    // List of steps in the flow
    protected List<Step> steps;
    // Name of the flow
    protected String name;
    // Description of the flow
    protected String description;
    // Boolean to indicate if the flow is read-only or not
    protected Boolean readOnly;
    // Last run time
    protected Date lastRunTime;

    public Flow(String name, String description) {
        this.name = name;
        this.description = description;
        this.steps = new ArrayList<>();
        this.nameToData = new HashMap<>();
        this.requiredInputs = new ArrayList<>();
        this.readOnly = true;
        this.lastRunTime = null;
        this.formalOutputs = new ArrayList<>();
    }

    public void addStep(Step step){
        Input[] inputs = step.getInputs();
        for(int i = 0; i < inputs.length; i++){
            if(!nameToData.containsKey(inputs[i].getAlias())){
                requiredInputs.add(inputs[i]);
                DataDef temp = null;
                try {
                    temp = (DataDef)inputs[i].getType().newInstance();
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e) {
                }
                nameToData.put(inputs[i].getAlias(), temp);
            }
            inputs[i].setData(nameToData.get(inputs[i].getAlias()));
        }

        Output[] outputs = step.getOutputs();
        for(int i = 0; i < outputs.length; i++){
            if(!nameToData.containsKey(outputs[i].getAlias())){
                DataDef temp = null;
                try {
                    temp = (DataDef)outputs[i].getType().newInstance();
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e) {
                }
                nameToData.put(outputs[i].getAlias(), temp);
            }
            outputs[i].setData(nameToData.get(outputs[i].getAlias()));
        }
        if(!step.isReadOnly())
            readOnly = false;

        steps.add(step);
    }

    protected void setLastRunTime(Date lastRunTime) {
        this.lastRunTime = lastRunTime;
    }
    public Date getLastRunTime() {
        return lastRunTime;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    // Runs the flow, returns the time it took to run in ms.
    public long run(){
        long start = System.currentTimeMillis();
        for(Step step : steps)
            step.run();
        return System.currentTimeMillis() - start;
    }

    @Override
    public String toString() {
        String result = "Flow: " + name + "\n"
                + "Description: " + description + "\n"
                + "Read-only: " + readOnly + "\n"
                + "Last run time: " + lastRunTime + "\n"
                + "Steps: \n";
        for(Step step : steps)
            result += step + "\n";
        return result;
    }
}
