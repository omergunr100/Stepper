package com.main.stepper.shared.structures.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.main.stepper.data.DDRegistry;
import com.main.stepper.data.implementation.enumeration.zipper.ZipperEnumData;
import com.main.stepper.data.implementation.list.datatype.GenericList;
import com.main.stepper.data.implementation.relation.Relation;
import com.main.stepper.exceptions.data.EnumValueMissingException;
import com.main.stepper.shared.structures.dataio.DataIODTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class FixerUtility {
    public static void fixMap(Map<DataIODTO, Object> map) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        List<Map.Entry<DataIODTO, Object>> entries = new ArrayList<>(map.entrySet());
        for (int i = 0; i < entries.size(); i++) {
            // get entry
            Map.Entry<DataIODTO, Object> entry = entries.get(i);
            Object fixedValue = null;
            while (entry.getValue() != null && entry.getValue() instanceof LinkedTreeMap){
                entry.setValue(((LinkedTreeMap) entry.getValue()).get("value"));
            }
            if (entry.getValue() != null){
                // fix Integer types
                if (entry.getKey().type().equals(DDRegistry.NUMBER)) {
                    if (DDRegistry.NUMBER.getType().isAssignableFrom(entry.getValue().getClass()))
                        continue;
                    fixedValue = entry.getValue();
                    if (fixedValue instanceof Double)
                        fixedValue = ((Double) fixedValue).intValue();
                }
                // fix Double types
                else if (entry.getKey().type().equals(DDRegistry.DOUBLE)) {
                    if (DDRegistry.DOUBLE.getType().isAssignableFrom(entry.getValue().getClass()))
                        continue;
                    fixedValue = entry.getValue();
                }
                // fix String types
                else if (entry.getKey().type().equals(DDRegistry.STRING)) {
                    if (DDRegistry.STRING.getType().isAssignableFrom(entry.getValue().getClass()))
                        continue;
                    fixedValue = entry.getValue();
                }
                // fix Relation types
                else if (entry.getKey().type().equals(DDRegistry.RELATION)) {
                    if (DDRegistry.RELATION.getType().isAssignableFrom(entry.getValue().getClass()))
                        continue;
                    fixedValue = gson.fromJson(gson.toJson(entry.getValue()), Relation.class);
                }
                // fix File types
                else if (entry.getKey().type().equals(DDRegistry.FILE)) {
                    if (DDRegistry.FILE.getType().isAssignableFrom(entry.getValue().getClass()))
                        continue;
                    // todo: change this to a proper fix
                    fixedValue = entry.getValue();
                    fixedValue = gson.fromJson(gson.toJson(fixedValue), DDRegistry.FILE.getType());
                }
                // fix List types
                else if (GenericList.class.isAssignableFrom(entry.getKey().type().getType())) {
                    if (GenericList.class.isAssignableFrom(entry.getValue().getClass()))
                        continue;
                    // todo: change this to a proper fix
                    fixedValue = gson.fromJson(gson.toJson(entry.getValue()), ArrayList.class);
                }
                // fix IntToInt map
                else if (entry.getKey().type().equals(DDRegistry.INT_TO_INT_MAPPING)) {
                    if (DDRegistry.INT_TO_INT_MAPPING.getType().isAssignableFrom(entry.getValue().getClass()))
                        continue;
                    // todo: change this to a proper fix
                    fixedValue = gson.fromJson(gson.toJson(entry.getValue()), DDRegistry.INT_TO_INT_MAPPING.getType());
                }
                // fix ZipperEnum
                else if (entry.getKey().type().equals(DDRegistry.ZIPPER_ENUM)) {
                    if (DDRegistry.ZIPPER_ENUM.getType().isAssignableFrom(entry.getValue().getClass()))
                        continue;
                    ZipperEnumData parsed = new ZipperEnumData();
                    try {
                        parsed.setValue((String) entry.getValue());
                        fixedValue = parsed;
                    } catch (EnumValueMissingException e) {
                        fixedValue = null;
                    }
                }
            }
            map.put(entry.getKey(), fixedValue);
        }

    }
}
