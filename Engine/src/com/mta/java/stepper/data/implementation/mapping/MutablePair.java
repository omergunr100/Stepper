package com.mta.java.stepper.data.implementation.mapping;

import com.mta.java.stepper.data.api.AbstractDataDef;

public class MutablePair<car, cdr> {
    private car first;
    private cdr second;
    private Class<car> firstType;
    private Class<cdr> secondType;

    public MutablePair(Class<car> firstType, Class<cdr> secondType) {
        this.firstType = firstType;
        this.secondType = secondType;
    }

    public MutablePair(Class<car> firstType, Class<cdr> secondType, car first, cdr second) {
        this(firstType, secondType);
        this.first = first;
        this.second = second;
    }

    public car getFirst() {
        return first;
    }

    public cdr getSecond() {
        return second;
    }

    public Class<car> getFirstType() {
        return firstType;
    }

    public Class<cdr> getSecondType() {
        return secondType;
    }
}
