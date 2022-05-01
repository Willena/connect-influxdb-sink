package io.github.willena.connect.influxdb.sink;

import io.github.willena.connect.influxdb.util.Version;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import java.util.Collection;
import java.util.Map;


public class InfluxDBSinkTask extends SinkTask {
    protected InfluxDBWriter dbWriter;

    public void start(Map<String, String> settings) {
        //InfluxDBSinkConnectorConfig config = new InfluxDBSinkConnectorConfig(settings);
        this.dbWriter = new InfluxDBWriter(settings);
    }

    public void put(Collection<SinkRecord> records) {
        if (null == records || records.isEmpty()) {
            return;
        }
        this.dbWriter.write(records);
    }

    public Map<TopicPartition, OffsetAndMetadata> preCommit(Map<TopicPartition, OffsetAndMetadata> currentOffsets) {
        if (!this.dbWriter.offset().isEmpty()) {
            return this.dbWriter.offset();
        }

        return currentOffsets;
    }

    public String version() {
        return Version.getVersion();
    }

    public void stop() {
        this.dbWriter.close();
    }
}

