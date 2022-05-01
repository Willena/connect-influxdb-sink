package io.github.willena.connect.influxdb.providers;

import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleTest {

    @Test
    public void getKey() {
        SinkRecord r = new SinkRecord("INPUT_KAFKA_TOPIC", 0, null, "keyValue", null, "value", 100);
        Map<String, String> settings = new HashMap<>();
        settings.put("influxdb.database.providers", "rec");
        settings.put("influxdb.database.providers.rec.class", "io.github.willena.connect.influxdb.providers.Simple$Key");

        ProviderChain provider = new ProviderChain("influxdb.database", settings);

        Map<String, Object> res1 = provider.get(r);

        assertEquals(1, res1.size());
        assertEquals("keyValue", res1.get("value"));
    }

    @Test
    public void getValue() {
        SinkRecord r = new SinkRecord("INPUT_KAFKA_TOPIC", 0, null, "keyValue", null, "valueValue", 100);
        Map<String, String> settings = new HashMap<>();
        settings.put("influxdb.database.providers", "rec");
        settings.put("influxdb.database.providers.rec.class", "io.github.willena.connect.influxdb.providers.Simple$Value");

        ProviderChain provider = new ProviderChain("influxdb.database", settings);

        Map<String, Object> res1 = provider.get(r);

        assertEquals(1, res1.size());
        assertEquals("valueValue", res1.get("value"));
    }

}