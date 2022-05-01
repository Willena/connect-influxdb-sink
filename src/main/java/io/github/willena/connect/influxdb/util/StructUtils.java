package io.github.willena.connect.influxdb.util;

import org.apache.kafka.connect.data.Struct;

import java.util.LinkedHashMap;
import java.util.Map;

public class StructUtils {
    public static Map<String, Object> structToMap(Struct struct) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        struct.schema().fields().forEach(field -> map.put(field.name(), struct.get(field)));
        return map;
    }
}
