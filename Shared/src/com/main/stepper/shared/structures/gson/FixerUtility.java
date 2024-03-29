package com.main.stepper.shared.structures.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.main.stepper.data.implementation.enumeration.httpprotocol.HttpProtocolEnumData;
import com.main.stepper.data.implementation.enumeration.httpverb.HttpVerbEnumData;
import com.main.stepper.data.implementation.enumeration.zipper.ZipperEnumData;
import com.main.stepper.data.implementation.file.FileData;
import com.main.stepper.data.implementation.list.datatype.*;
import com.main.stepper.data.implementation.mapping.implementation.IntToIntPair;
import com.main.stepper.data.implementation.relation.Relation;
import com.main.stepper.shared.structures.dataio.DataIODTO;

import java.io.File;
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

            if (entry.getKey().type().getType().isAssignableFrom(value.getClass()))
                continue;

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
                    value = gson.fromJson(gson.toJson(value), Relation.class);
                    entry.setValue(value);
                    break;
                case FILE:
                    // convert LinkedTreeMap to FileData
                    if (value instanceof LinkedTreeMap && ((LinkedTreeMap) value).get("path") != null) {
                        value = new FileData(((LinkedTreeMap) value).get("path").toString());
                    }
                    entry.setValue(value);
                    break;
                case INT_TO_INT_MAPPING:
                    // convert LinkedTreeMap to IntToIntPair
                    value = gson.fromJson(gson.toJson(value), IntToIntPair.class);
                    entry.setValue(value);
                    break;
                case STRING_LIST:
                    // convert ArrayList to StringList
                    value = gson.fromJson(gson.toJson(value), StringList.class);
                    entry.setValue(value);
                    break;
                case RELATION_LIST:
                    // convert ArrayList of LinkedTreeMap to RelationList
                    value = gson.fromJson(gson.toJson(value), RelationList.class);
                    entry.setValue(value);
                    break;
                case NUMBER_LIST:
                    // convert ArrayList to NumberList
                    value = gson.fromJson(gson.toJson(value), NumberList.class);
                    entry.setValue(value);
                    break;
                case FILE_LIST:
                    // convert ArrayList of LinkedTreeMap to FileList
                    FileList list = new FileList();
                    for (int j = 0; j < ((ArrayList) value).size(); j++) {
                        Object tempValue = ((ArrayList) value).get(j);
                        if (tempValue instanceof LinkedTreeMap && ((LinkedTreeMap) tempValue).get("path") != null) {
                            list.add(new FileData(((LinkedTreeMap) tempValue).get("path").toString()));
                        }
                    }
                    entry.setValue(list);
                    break;
                case DOUBLE_LIST:
                    // convert ArrayList to DoubleList
                    value = gson.fromJson(gson.toJson(value), DoubleList.class);
                    entry.setValue(value);
                    break;
                case ZIPPER_ENUM:
                    // convert LinkedTreeMap to ZipperEnumData
                    value = gson.fromJson(gson.toJson(value), ZipperEnumData.class);
                    entry.setValue(value);
                    break;
                case HTTP_VERB_ENUM:
                    // convert LinkedTreeMap to HttpVerbEnumData
                    value = gson.fromJson(gson.toJson(value), HttpVerbEnumData.class);
                    entry.setValue(value);
                    break;
                case HTTP_PROTOCOL_ENUM:
                    // convert LinkedTreeMap to HttpProtocolEnumData
                    value = gson.fromJson(gson.toJson(value), HttpProtocolEnumData.class);
                    entry.setValue(value);
                    break;
                case JSON:
                    // convert LinkedTreeMap to JsonObject
                    value = gson.fromJson(gson.toJson(value), JsonObject.class);
                    entry.setValue(value);
                    break;
            }
        }
    }
}
