package com.main.stepper.shared.structures.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.main.stepper.data.DDRegistry;
import com.main.stepper.data.implementation.relation.Relation;
import com.main.stepper.shared.structures.dataio.DataIODTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class FixerUtility {
    public static void fixMap(Map<DataIODTO, Object> map) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        List<DataIODTO> keys = new ArrayList<>(map.keySet());
        for (int i = 0; i < keys.size(); i++) {
            // fix Integer types
            DataIODTO key = keys.get(i);
            if (key.type().equals(DDRegistry.NUMBER)) {
                Object value = map.get(key);
                if (value instanceof Double)
                    map.put(key, ((Double) value).intValue());
            }
            // fix Relation types
            else if (key.type().equals(DDRegistry.RELATION)) {
                Relation data = gson.fromJson(gson.toJson(map.get(key)), Relation.class);
                map.put(key, data);
            }
        }

    }
}
