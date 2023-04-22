package com.main.stepper.data.implementation.mapping.api;

import javafx.util.Pair;

public class PairData<car, cdr> extends Pair<car, cdr> {

    /**
     * Creates a new pair
     *
     * @param key   The key for this pair
     * @param value The value to use for this pair
     */
    public PairData(car key, cdr value) {
        super(key, value);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder
                .append("(")
                .append(getKey())
                .append(", ")
                .append(getValue())
                .append(")");
        return builder.toString();
    }
}
