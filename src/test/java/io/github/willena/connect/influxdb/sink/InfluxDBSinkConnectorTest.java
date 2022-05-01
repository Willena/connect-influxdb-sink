package io.github.willena.connect.influxdb.sink;

import io.github.willena.connect.influxdb.util.Version;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class InfluxDBSinkConnectorTest {

    @Test
    void version() {
        assertEquals(Version.getVersion(), new InfluxDBSinkConnector().version());
    }

    @Test
    void start() {
        assertDoesNotThrow(() -> new InfluxDBSinkConnector().start(new HashMap<>()));
    }

    @Test
    void taskClass() {
        assertEquals(InfluxDBSinkTask.class, new InfluxDBSinkConnector().taskClass());
    }

    @Test
    void taskConfigs() {
        InfluxDBSinkConnector con = new InfluxDBSinkConnector();
        con.start(new HashMap<>());
        assertEquals(
                Arrays.asList(new HashMap<String, String>(), new HashMap<String, String>(), new HashMap<String, String>()),
                con.taskConfigs(3));
    }

    @Test
    void stop() {
        assertDoesNotThrow(() -> new InfluxDBSinkConnector().stop());
    }

    @Test
    void config() {
        assertNotNull(new InfluxDBSinkConnector().config());
    }
}