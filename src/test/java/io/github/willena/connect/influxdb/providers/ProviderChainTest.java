package io.github.willena.connect.influxdb.providers;

import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProviderChainTest {

    @Test
    void getSingleStatic() {
        Map<String, String> settings = Collections.singletonMap("influxdb.database", "superdb");

        ProviderChain provider = new ProviderChain("influxdb.database", settings);

        assertEquals("superdb", provider.getFirst(null).get());

    }

    @Test
    void getDynamicSingle() {

        Map<String, String> settings = new HashMap<>();
        settings.put("influxdb.database.provider.class", "io.github.willena.connect.influxdb.providers.Static");
        settings.put("influxdb.database.provider.value", "superdb");

        ProviderChain provider = new ProviderChain("influxdb.database", settings);

        assertEquals("superdb", provider.getFirst(null).get());
    }

    @Test
    void getDynamicStaticMultipleMerged() {
        Map<String, String> settings = new HashMap<>();
        settings.put("influxdb.database.providers", "p1,p2");
        settings.put("influxdb.database.providers.p1.class", "io.github.willena.connect.influxdb.providers.Static");
        settings.put("influxdb.database.providers.p2.class", "io.github.willena.connect.influxdb.providers.Static");
        settings.put("influxdb.database.providers.p1.value", "superdb");
        settings.put("influxdb.database.providers.p2.value", "superdb");

        ProviderChain provider = new ProviderChain("influxdb.database", settings);

        Map<String, Object> res1 = provider.get(null);

        assertEquals(1, res1.size());

        assertEquals("superdb", res1.values().stream().findFirst().get());
    }

    @Test
    void getMergeMultipleProvider() {
        Map<String, String> settings = new HashMap<>();
        settings.put("influxdb.database.providers", "p1,p2");
        settings.put("influxdb.database.providers.p1.class", "io.github.willena.connect.influxdb.providers.ProviderChainTest$testCustomProvider1");
        settings.put("influxdb.database.providers.p2.class", "io.github.willena.connect.influxdb.providers.ProviderChainTest$testCustomProvider2");

        ProviderChain provider = new ProviderChain("influxdb.database", settings);

        Map<String, Object> res1 = provider.get(null);

        assertEquals(2, res1.size());

        assertEquals("value", res1.get("v1"));
        assertEquals("value", res1.get("v2"));
    }

    public static class testCustomProvider1 implements Provider {
        private Map<String, Object> values = Collections.singletonMap("v1", "value");

        public testCustomProvider1(String prefix, Map<String, String> settings) {
            assertEquals("influxdb.database.providers.p1", prefix);
        }

        @Override
        public Map<String, Object> get(SinkRecord r) {
            return values;
        }

    }

    public static class testCustomProvider2 implements Provider {
        private Map<String, Object> values = Collections.singletonMap("v2", "value");

        public testCustomProvider2(String prefix, Map<String, String> settings) {
            assertEquals("influxdb.database.providers.p2", prefix);
        }

        @Override
        public Map<String, Object> get(SinkRecord r) {
            return values;
        }

    }

}