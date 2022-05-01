package io.github.willena.connect.influxdb.sink;

import org.apache.kafka.connect.sink.SinkRecord;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.kafka.common.record.TimestampType.LOG_APPEND_TIME;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class InfluxDBSinkTaskTest {

    private HashMap<String, String> config;

    @BeforeEach
    void config() {
        this.config = new HashMap<>();
        config.put("influxdb.url", "http://localhost:8086");
        config.put("influxdb.database", "mydb");
        config.put("influxdb.tags.provider.class", "io.github.willena.connect.influxdb.providers.Fields$Value");
        config.put("influxdb.tags.provider.fields", "tag1,tag2");
        config.put("influxdb.fields.provider.class", "io.github.willena.connect.influxdb.providers.Static");
        config.put("influxdb.fields.provider.fieldA", "fieldValue");
        config.put("influxdb.fields.provider.fieldB", "fieldValue2");
        config.put("influxdb.timestamp.provider.class", "io.github.willena.connect.influxdb.providers.Record");
        config.put("influxdb.timestamp.provider.fields", "timestamp");
        config.put("influxdb.measurement.provider.class", "io.github.willena.connect.influxdb.providers.Simple$Key");

    }

    @Test
    void start() {
        InfluxDBSinkTask task = new InfluxDBSinkTask();
        task.start(config);
    }


    private Map<String, Object> createValue(String t1, String t2, String f1, Boolean f2, Float f3) {

        Map<String, Object> map = new HashMap<>();
        map.put("tag1", t1);
        map.put("tag2", t2);
        map.put("field1", f1);
        map.put("field2", f2);
        map.put("field3", f3);

        return map;
    }

    @Test
    void put() {

        SinkRecord[] records = {
                new SinkRecord("TOPIC", 0, null, "Measurement1", null, createValue("v1", "v2", "sss", true, 23.33F), 0, 0L, LOG_APPEND_TIME),
                new SinkRecord("TOPIC", 0, null, "Measurement2", null, createValue("v4", "v3", "sss", false, 2233.33F), 1, 1L, LOG_APPEND_TIME),
                new SinkRecord("TOPIC", 0, null, "Measurement1", null, createValue("v6", "v5", "sss", true, 223.33F), 2, 2L, LOG_APPEND_TIME),
                new SinkRecord("TOPIC", 0, null, "Measurement3", null, createValue("v8", "v7", "sss", false, 203.33F), 3, 3L, LOG_APPEND_TIME),
        };

        InfluxDBSinkTask task = new InfluxDBSinkTask();
        task.start(config);
        InfluxDB influxdbmock = mock(InfluxDB.class);

        task.dbWriter.influxDB = influxdbmock;
        task.put(Arrays.asList(records));

        BatchPoints.Builder batch = BatchPoints.database("mydb")
                .consistency(InfluxDB.ConsistencyLevel.ONE);


        batch.point(Point.measurement("Measurement1")
                .time(0, TimeUnit.MILLISECONDS)
                .tag("tag1", "v1")
                .tag("tag2", "v2")
                .addField("fieldA", "fieldValue")
                .addField("fieldB", "fieldValue2").build());

        batch.point(Point.measurement("Measurement3")
                .time(3, TimeUnit.MILLISECONDS)
                .tag("tag1", "v8")
                .tag("tag2", "v7")
                .addField("fieldA", "fieldValue")
                .addField("fieldB", "fieldValue2").build());

        batch.point(Point.measurement("Measurement1")
                .time(2, TimeUnit.MILLISECONDS)
                .tag("tag1", "v6")
                .tag("tag2", "v5")
                .addField("fieldA", "fieldValue")
                .addField("fieldB", "fieldValue2").build());

        batch.point(Point.measurement("Measurement2")
                .time(1, TimeUnit.MILLISECONDS)
                .tag("tag1", "v4")
                .tag("tag2", "v3")
                .addField("fieldA", "fieldValue")
                .addField("fieldB", "fieldValue2").build());

        verify(influxdbmock).write(batch.build());
    }
}