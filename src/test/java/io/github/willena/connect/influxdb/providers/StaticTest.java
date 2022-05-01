package io.github.willena.connect.influxdb.providers;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StaticTest {

    @Test
    void get() {
        Map<String, String> settings = Collections.singletonMap("test.key", "value");

        Static provider = new Static("test.key", settings);
        assertEquals("value", provider.getFirst(null).get());
    }

    @Test
    void getFromProvider() {
        Map<String, String> settings = new java.util.HashMap<>();
        settings.put("test.provider.class", "fake");
        settings.put("test.provider.value", "value");

        Static provider = new Static("test.provider", settings);
        assertEquals("value", provider.getFirst(null).get());
    }

}