package com.main.stepper.deprecated.steps;

import com.engine.deprecated.data.io.Input;
import com.engine.deprecated.data.io.Output;

public abstract class Step {
    enum FLAG{  SUCCESS, WARNING, FAILURE, NOT_RUN }
    // Boolean to indicate if the step is read-only or not
    protected Boolean readOnly;
    // The name of the step as it will appear in the UI
    protected String simpleName;
    // The description of the step as it will appear to the user
    protected String description;
    // The summary of the step as it will appear to the user
    protected String summary;
    // The alias of the step in the current flow
    protected String alias;
    // The inputs
    protected Input[] inputs;
    // The outputs
    protected Output[] outputs;
    // The flag with which the step ended
    protected FLAG flag;
    protected Step(String simpleName, Boolean readOnly, Integer inCount, Integer outCount){
        this.simpleName=simpleName;
        this.readOnly=readOnly;
        this.inputs=new Input[inCount];
        this.outputs=new Output[outCount];
        this.flag = FLAG.NOT_RUN;
        this.alias = null;
    }
    // The method that will be called when the step is run
    public abstract void run();
    public void setAlias(String alias) {
        this.alias = alias;
    }
    public Boolean isReadOnly() {
        return readOnly;
    }
    public String getSimpleName() {
        return simpleName;
    }
    public String getDescription() {
        return description;
    }
    public String getSummary() {
        return summary;
    }
    public String getAlias() {
        return alias;
    }
    public Input[] getInputs() {
        return inputs;
    }
    public Output[] getOutputs() {
        return outputs;
    }
}
