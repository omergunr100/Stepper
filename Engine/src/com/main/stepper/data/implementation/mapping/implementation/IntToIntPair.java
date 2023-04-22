package com.main.stepper.data.implementation.mapping.implementation;

import com.main.stepper.data.implementation.mapping.api.PairData;

public class IntToIntPair extends PairData<Integer, Integer> {

    /**
     * Creates a new pair
     *
     * @param key   The key for this pair
     * @param value The value to use for this pair
     */
    public IntToIntPair(Integer key, Integer value) {
        super(key, value);
    }
}
