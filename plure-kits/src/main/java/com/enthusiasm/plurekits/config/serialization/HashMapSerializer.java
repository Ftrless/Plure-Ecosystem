package com.enthusiasm.plurekits.config.serialization;

import com.enthusiasm.plurecore.config.serialization.IDataSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class HashMapSerializer implements IDataSerializer<HashMap<String, String>, List<String>> {
    public List<String> serialize(HashMap<String, String> map) {
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.toList());
    }

    public HashMap<String, String> deserialize(List<String> serializedList) {
        HashMap<String, String> map = new HashMap<>();

        for (String entry : serializedList) {
            int delimiterIndex = entry.indexOf('=');
            if (delimiterIndex != -1) {
                String key = entry.substring(0, delimiterIndex);
                String value = entry.substring(delimiterIndex + 1);

                map.put(key, value);
            }
        }

        return map;
    }
}
