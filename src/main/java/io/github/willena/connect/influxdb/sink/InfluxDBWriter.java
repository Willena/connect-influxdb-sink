package io.github.willena.connect.influxdb.sink;

import com.google.common.collect.HashMultimap;
import io.github.willena.connect.backoff.BackoffTimers;
import io.github.willena.connect.influxdb.util.StructUtils;
import io.github.willena.connect.retry.Condition;
import io.github.willena.connect.retry.Retryable;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBIOException;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;


public class InfluxDBWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(InfluxDBWriter.class);
    private final Set<String> existingDatabases = new HashSet<>();
    private final Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
    private final Retryable retryable;
    protected InfluxDBSinkConnectorConfig config;
    protected InfluxDB influxDB;
//    private final ObjectMapper mapper = new ObjectMapper();

    public InfluxDBWriter(Map<String, String> settings) {
        this.config = new InfluxDBSinkConnectorConfig(settings);

        this.influxDB = create(this.config);

        this.retryable = Retryable.builder()
                .withMaxRetries(config.maxRetries)
                .withBackoffTimer(BackoffTimers.exponential(Duration.ofMillis(config.backOffTime)))
                .when(Condition.isInstance(InfluxDBIOException.class))
                .build();

        if (config.autoCreateDatabase) {
            this.existingDatabases.addAll(this.retryable.call("Describing databases ...", () -> this.influxDB.describeDatabases()));
        }

    }

    public InfluxDB create(InfluxDBSinkConnectorConfig config) {
        InfluxDB influxDbConnection;
        if (config.authentication) {
            LOGGER.info("Authenticating to {} as {}", config.url, config.username);
            influxDbConnection = org.influxdb.InfluxDBFactory.connect(config.url, config.username, config.password);
        } else {
            LOGGER.info("Connecting to {}", config.url);
            influxDbConnection = org.influxdb.InfluxDBFactory.connect(config.url);
        }
        if (config.gzipEnable) {
            influxDbConnection.enableGzip();
        }
        return influxDbConnection;
    }

    public void write(Collection<SinkRecord> records) {

        HashMultimap<String, SinkRecord> recordsByDatabase = HashMultimap.create();
        for (SinkRecord record : records) {
            String databaseName = String.valueOf(config.databaseProvider.getFirst(record).orElseThrow(() -> new NoSuchElementException("No value present")));
            recordsByDatabase.put(databaseName, convertRecord(record));
        }

        if (config.autoCreateDatabase) {
            createDatabases(recordsByDatabase.keySet());
        }

        for (String databaseName : recordsByDatabase.keySet()) {
            BatchPoints batch = getBatch(databaseName, recordsByDatabase.get(databaseName));

            this.retryable.call("Writing batch of records to influxDB ...", () -> {
                this.influxDB.write(batch);
                return null;
            });

            recordsByDatabase.get(databaseName).forEach(record -> this.offsets.put(new TopicPartition(record.topic(), record.kafkaPartition()), new OffsetAndMetadata(record.kafkaOffset() + 1L)));
        }
    }

    private SinkRecord convertRecord(SinkRecord record) {
        Object key = record.key();
        Object value = record.value();

        if (key instanceof Struct) {
            key = StructUtils.structToMap((Struct) key);
        }

        if (value instanceof Struct) {
            value = StructUtils.structToMap((Struct) value);
        }

        return record.newRecord(record.topic(), record.kafkaPartition(), null, key, null, value, record.timestamp());

    }


    protected BatchPoints getBatch(String databaseName, Collection<SinkRecord> records) {
        LOGGER.trace("Processing records for database '{}'", databaseName);
        Map<SerieDescriptor, Map<String, Object>> batch = new HashMap<>(records.size());

        for (SinkRecord record : records) {
            Map<String, Object> fields = config.fieldsProvider.get(record);
            // addToBatch(record, fields);
            SerieDescriptor pk = getKey(record);
            batch.put(pk, fields);
        }


        BatchPoints.Builder batchBuilder = BatchPoints.database(databaseName).consistency(this.config.consistencyLevel);

        for (SerieDescriptor key : batch.keySet()) {
            Point.Builder builder = Point.measurement(key.measurement);
            builder.time(key.time, this.config.precision);
            if (null != key.tags && !key.tags.isEmpty()) {
                builder.tag(key.tags);
            }
            builder.fields(batch.get(key));
            Point point = builder.build();

            LOGGER.trace("Adding point {}", point);

            batchBuilder.point(point);
        }
        return batchBuilder.build();
    }


    protected SerieDescriptor getKey(SinkRecord record) {
        String measurement = String.valueOf(config.measurementProvider.getFirst(record).orElseThrow(() -> new NoSuchElementException("No value present")));
        long time = getTimestamp(record);
        Map<String, String> tags = getPointTags(record);

        return SerieDescriptor.of(measurement, time, tags);
    }


    protected Long getTimestamp(SinkRecord record) {

        Object timestampValue = config.timestampProvider.getFirst(record).orElseThrow(() -> new DataException("Could not found timestamp field..."));

        if (timestampValue instanceof String) {
            try {
                return Long.parseLong((String) timestampValue);
            } catch (NumberFormatException e) {
                LOGGER.error("Timestamp field can't be converted to long (INT64) type in record: " + record);

                throw new DataException("Timestamp field is not of long (INT64) type");
            }
        } else {
            try {
                return (Long) timestampValue;
            } catch (ClassCastException e) {
                LOGGER.error("Timestamp field is not of long (INT64) type in record: " + record);
                throw new DataException("Timestamp field is not of long (INT64) type");
            }
        }

    }


    protected void createDatabases(Set<String> databases) {
        for (String databaseName : databases) {
            if (!this.existingDatabases.contains(databaseName)) {
                this.retryable.call("Creating database ...", () -> {
                    this.influxDB.createDatabase(databaseName);
                    return null;
                });
                this.existingDatabases.add(databaseName);
            }
        }
    }

    protected Map<String, String> getPointTags(SinkRecord record) {

        return config.tagsProvider.get(record).entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));
    }

    public void close() {
        if (null != this.influxDB) {
            LOGGER.info("Closing InfluxDB client.");
            this.influxDB.close();
        }
    }

    public Map<TopicPartition, OffsetAndMetadata> offset() {
        return this.offsets;
    }

}

