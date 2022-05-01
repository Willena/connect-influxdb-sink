package io.github.willena.connect.influxdb.providers;

import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FieldsTest {

    @Test
    void getValue() {

        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put("val1", "Value");
        keyMap.put("val2", "value2");
        keyMap.put("val3", "value4");
        keyMap.put("val4", "value6");

        Map<String, Object> value = new HashMap<>();
        value.put("cleValue1", "Value");
        value.put("cleValue2", "Value2");
        value.put("cleValue3", "Value4");
        value.put("cleValue4", "Value6");


        SinkRecord r = new SinkRecord("INPUT_KAFKA_TOPIC", 0, null, keyMap, null, value, 100);
        Map<String, String> settings = new HashMap<>();
        settings.put("influxdb.database.providers", "keyFields,valueFields");
        settings.put("influxdb.database.providers.keyFields.class", "io.github.willena.connect.influxdb.providers.Fields$Key");
        settings.put("influxdb.database.providers.keyFields.fields", "val2:MysuperKey,val4");
        settings.put("influxdb.database.providers.valueFields.class", "io.github.willena.connect.influxdb.providers.Fields$Value");
        settings.put("influxdb.database.providers.valueFields.fields", "cleValue1,cleValue4");

        ProviderChain provider = new ProviderChain("influxdb.database", settings);

        Map<String, Object> res1 = provider.get(r);

        assertEquals(4, res1.size());
        assertEquals("Value", res1.get("cleValue1"));
        assertEquals("Value6", res1.get("cleValue4"));
        assertEquals("value2", res1.get("MysuperKey"));
        assertEquals("value6", res1.get("val4"));
    }

}