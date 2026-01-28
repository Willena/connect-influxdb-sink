package io.github.willena.connect.influxdb.sink;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.sink.SinkRecord;
import org.influxdb.InfluxDB;

import java.util.Collection;
import java.util.Map;

public interface InfluxDBWriter {
    InfluxDB create(InfluxDBSinkConnectorConfig config);

    void write(Collection<SinkRecord> records);

    void close();

    Map<TopicPartition, OffsetAndMetadata> offset();
}
