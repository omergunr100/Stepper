package com.main.stepper.shared.structures.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.main.stepper.data.DDRegistry;
import com.main.stepper.data.implementation.enumeration.zipper.ZipperEnumData;
import com.main.stepper.data.implementation.file.FileData;
import com.main.stepper.data.implementation.list.datatype.*;
import com.main.stepper.data.implementation.mapping.implementation.IntToIntPair;
import com.main.stepper.data.implementation.relation.Relation;
import com.main.stepper.exceptions.data.EnumValueMissingException;
import com.main.stepper.shared.structures.dataio.DataIODTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class FixerUtility {
    public static void fixMap(Map<DataIODTO, Object> map) {
        Gson gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .create();
        List<Map.Entry<DataIODTO, Object>> entries = new ArrayList<>(map.entrySet());
        for (int i = 0; i < entries.size(); i++) {
            // get entry
            Map.Entry<DataIODTO, Object> entry = entries.get(i);
            Object value = entry.getValue();

            if (value == null)
                continue;

            // fix value (sometimes it's the entire entry)
            if (value instanceof LinkedTreeMap) {
                LinkedTreeMap tempMap = (LinkedTreeMap) value;
                if (tempMap.get("hash") != null && tempMap.get("value") != null) {
                    value = tempMap.get("value");
                }
            }

            switch (entry.getKey().type()) {
                case STRING:
                    // no need to fix
                    break;
                case NUMBER:
                    // convert double to int
                    if (value instanceof Double) {
                        value = ((Double) value).intValue();
                        entry.setValue(value);
                    }
                    break;
                case DOUBLE:
                    // no need to fix
                    break;
                case RELATION:
                    // convert LinkedTreeMap to Relation
                    value = gson.fromJson(gson.toJson(value), new TypeToken<Relation>(){}.getType());
                    entry.setValue(value);
                    break;
                case FILE:
                    // convert LinkedTreeMap to FileData
                    value = gson.fromJson(gson.toJson(value), new TypeToken<FileData>(){}.getType());
                    entry.setValue(value);
                    break;
                case INT_TO_INT_MAPPING:
                    // convert LinkedTreeMap to IntToIntPair
                    value = gson.fromJson(gson.toJson(value), new TypeToken<IntToIntPair>(){}.getType());
                    entry.setValue(value);
                    break;
                case STRING_LIST:
                    // convert ArrayList to StringList
                    value = gson.fromJson(gson.toJson(value), new TypeToken<StringList>(){}.getType());
                    entry.setValue(value);
                    break;
                case RELATION_LIST:
                    // convert ArrayList of LinkedTreeMap to RelationList
                    value = gson.fromJson(gson.toJson(value), new TypeToken<RelationList>(){}.getType());
                    entry.setValue(value);
                    break;
                case NUMBER_LIST:
                    // convert ArrayList to NumberList
                    value = gson.fromJson(gson.toJson(value), new TypeToken<NumberList>(){}.getType());
                    entry.setValue(value);
                    break;
                case FILE_LIST:
                    // convert ArrayList of LinkedTreeMap to FileList
                    value = gson.fromJson(gson.toJson(value), new TypeToken<FileList>(){}.getType());
                    entry.setValue(value);
                    break;
                case DOUBLE_LIST:
                    // convert ArrayList to DoubleList
                    value = gson.fromJson(gson.toJson(value), new TypeToken<DoubleList>(){}.getType());
                    entry.setValue(value);
                    break;
                case ZIPPER_ENUM:
                    // convert LinkedTreeMap to ZipperEnumData
                    value = gson.fromJson(gson.toJson(value), new TypeToken<ZipperEnumData>(){}.getType());
                    entry.setValue(value);
                    break;
            }
        }
    }
}
