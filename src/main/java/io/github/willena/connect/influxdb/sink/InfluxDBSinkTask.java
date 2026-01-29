package io.github.willena.connect.influxdb.sink;

import io.github.willena.connect.influxdb.util.Version;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;


public class InfluxDBSinkTask extends SinkTask {
    private final Function<Map<String, String>, InfluxDBWriter> writerInit;
    private InfluxDBWriter dbWriter;

    public InfluxDBSinkTask() {
        this(InfluxDBWriterImpl::new);
    }

    protected InfluxDBSinkTask(Function<Map<String, String>, InfluxDBWriter> writer) {
        super();
        this.writerInit = writer;
    }

    public void start(Map<String, String> settings) {
        //InfluxDBSinkConnectorConfig config = new InfluxDBSinkConnectorConfig(settings);
        try {
            this.dbWriter = this.writerInit.apply(settings);
        } catch (Exception e){
            throw new ConnectException(e);
        }
    }

    public void put(Collection<SinkRecord> records) {
        if (null == records || records.isEmpty()) {
            return;
        }
        try {
            this.dbWriter.write(records);
        } catch (Exception e){
            throw new ConnectException(e);
        }
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

