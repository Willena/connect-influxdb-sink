package io.github.willena.connect.influxdb.util;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FieldMapParserTest {

    @Test
    void parse() {
        LinkedList<Map<String, String>> lst = new LinkedList<>();
        lst.add(Collections.singletonMap("def", "abc"));
        lst.add(Collections.singletonMap("apcf", "ttt"));
        assertEquals(lst, FieldMapParser.parse("abc:def,ttt:apcf"));
    }
}