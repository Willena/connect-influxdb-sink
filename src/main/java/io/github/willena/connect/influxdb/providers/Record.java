package io.github.willena.connect.influxdb.providers;

import io.github.willena.connect.influxdb.util.FieldMapParser;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.sink.SinkRecord;

import java.util.*;
import java.util.stream.Collectors;

public class Record implements Provider {

    private final List<Map<String, RecordFields>> fields;

    public Record(String prefix, Map<String, String> settings) {

        if (!settings.containsKey(prefix + ".fields")) {
            throw new ConfigException("Missing " + prefix + ".fields key in config");
        }

        this.fields = FieldMapParser.parse(settings.getOrDefault(prefix + ".fields", ""))
                .stream()
                .map(map -> map.entrySet().stream().findFirst().orElseThrow(() -> new NoSuchElementException("No value present")))
                .map(entry -> Collections.singletonMap(entry.getKey(), RecordFields.valueOf(entry.getValue())))
                .collect(Collectors.toList());

    }

    @Override
    public Map<String, Object> get(SinkRecord r) {
        return this.fields.stream()
                .map(m -> {
                    Map<String, Object> mm = new HashMap<>();
                    for (Map.Entry<String, RecordFields> item : m.entrySet()) {
                        mm.put(item.getKey(), item.getValue().getFrom(r));
                    }
                    return mm;
                })
                .reduce(new HashMap<>(), (stringRecordFieldsMap, stringRecordFieldsMap2) -> {
                            stringRecordFieldsMap.putAll(stringRecordFieldsMap2);
                            return stringRecordFieldsMap;
                        }
                );
    }


    public enum RecordFields {
        kafkaOffset() {
            @Override
            public Object getFrom(SinkRecord r) {
                return r.kafkaOffset();
            }
        },
        timestamp() {
            @Override
            public Object getFrom(SinkRecord r) {
                return r.timestamp();
            }
        },
        topic() {
            @Override
            public Object getFrom(SinkRecord r) {
                return r.topic();
            }
        },
        kafkaPartition() {
            @Override
            public Object getFrom(SinkRecord r) {
                return r.kafkaPartition();
            }
        };

        public abstract Object getFrom(SinkRecord r);

    }

}
