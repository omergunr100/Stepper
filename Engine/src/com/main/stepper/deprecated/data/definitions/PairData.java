package com.main.stepper.deprecated.data.definitions;

import com.engine.deprecated.data.definitions.member.Pair;

public class PairData<car extends DataDef, cdr extends DataDef> extends DataDef<Pair<car, cdr>> {
    public PairData() {
        this.data = null;
    }

    @Override
    public Pair<car, cdr> getData() {
        return this.data;
    }

    @Override
    public void setData(Pair<car, cdr> data) {
        this.data = data;
    }

    public void setFirst(car first) {
        this.data.setKey(first);
    }

    public void setSecond(cdr second) {
        this.data.setValue(second);
    }

    public car getFirst() {
        return this.data.getKey();
    }

    public cdr getSecond() {
        return this.data.getValue();
    }
}
