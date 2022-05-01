package io.github.willena.connect.influxdb.providers;

import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecordTest {

    @Test
    void get() {
        SinkRecord r = new SinkRecord("INPUT_KAFKA_TOPIC", 0, null, "key", null, "value", 100);
        Map<String, String> settings = new HashMap<>();
        settings.put("influxdb.database.providers", "rec");
        settings.put("influxdb.database.providers.rec.class", "io.github.willena.connect.influxdb.providers.Record");
        settings.put("influxdb.database.providers.rec.fields", "topic:renamedTopicField,kafkaOffset,kafkaPartition");

        ProviderChain provider = new ProviderChain("influxdb.database", settings);

        Map<String, Object> res1 = provider.get(r);

        assertEquals(3, res1.size());
        assertEquals(100L, res1.get("kafkaOffset"));
        assertEquals("INPUT_KAFKA_TOPIC", res1.get("renamedTopicField"));
    }

    @Test
    void getMissingField() {
        SinkRecord r = new SinkRecord("INPUT_KAFKA_TOPIC", 0, null, "key", null, "value", 100);
        Map<String, String> settings = new HashMap<>();
        settings.put("influxdb.database.class", "io.github.willena.connect.influxdb.providers.Record");
        settings.put("influxdb.database.ff", "topic:renamedTopicField,kafkaOffset");

        assertThrows(ConfigException.class, () -> new Record("influxdb.database", settings));

    }
}