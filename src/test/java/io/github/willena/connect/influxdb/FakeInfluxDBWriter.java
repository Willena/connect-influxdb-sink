package io.github.willena.connect.influxdb;

import io.github.willena.connect.influxdb.sink.InfluxDBSinkConnectorConfig;
import io.github.willena.connect.influxdb.sink.InfluxDBWriterImpl;
import org.influxdb.InfluxDB;

import java.util.Map;

public class FakeInfluxDBWriter extends InfluxDBWriterImpl {

    private FakeInfluxdb influx;

    public FakeInfluxDBWriter(Map<String, String> settings) {
        super(settings);
    }

    public FakeInfluxdb getInflux() {
        return influx;
    }

    @Override
    public InfluxDB create(InfluxDBSinkConnectorConfig config) {
        this.influx = new FakeInfluxdb();
        return this.influx;
    }
}
