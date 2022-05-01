package io.github.willena.connect.influxdb.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FieldMapParser {
    public static List<Map<String, String>> parse(String input) {
        return Arrays.stream(input.split(","))
                .map(s -> s.split(":"))
                .map(splits -> Collections.singletonMap(splits.length == 2 ? splits[1] : splits[0], splits[0]))
                .collect(Collectors.toList());
    }
}
