package com.engine.data.implementation.mapping;

import com.engine.data.api.AbstractDataDef;

public class MutablePair<car extends AbstractDataDef, cdr extends AbstractDataDef> {
    private car first;
    private cdr second;
    private Class<? extends AbstractDataDef> firstType;
    private Class<? extends AbstractDataDef> secondType;

    public MutablePair(Class<? extends AbstractDataDef> firstType, Class<? extends AbstractDataDef> secondType) {
        this.firstType = firstType;
        this.secondType = secondType;
    }

    public MutablePair(Class<? extends AbstractDataDef> firstType, Class<? extends AbstractDataDef> secondType, car first, cdr second) {
        this.firstType = firstType;
        this.secondType = secondType;
        this.first = first;
        this.second = second;
    }

    public car getFirst() {
        return first;
    }

    public cdr getSecond() {
        return second;
    }

    public Class<? extends AbstractDataDef> getFirstType() {
        return firstType;
    }

    public Class<? extends AbstractDataDef> getSecondType() {
        return secondType;
    }
}
