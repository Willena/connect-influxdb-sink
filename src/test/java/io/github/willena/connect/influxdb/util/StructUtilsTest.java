package io.github.willena.connect.influxdb.util;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StructUtilsTest {

    @Test
    void structToMap() {
        Schema sc = SchemaBuilder.struct()
                .field("key_name", Schema.STRING_SCHEMA)
                .field("key2", Schema.FLOAT64_SCHEMA)
                .build();
        Struct a = new Struct(sc);
        a.put("key_name", "test");
        a.put("key2", 1.65555d);

        HashMap<String, Object> map = new HashMap<>();
        map.put("key_name", "test");
        map.put("key2", 1.65555d);

        assertEquals(map, StructUtils.structToMap(a));
    }

}