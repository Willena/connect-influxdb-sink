package io.github.willena.connect.influxdb.providers;

import io.github.willena.connect.influxdb.util.FieldMapParser;
import io.github.willena.connect.influxdb.util.StructUtils;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Fields implements Provider {

    private static final Logger LOGGER = LoggerFactory.getLogger(Fields.class);

    private final List<Map<String, String>> fields;

    public Fields(String prefix, Map<String, String> settings) {
        this.fields = FieldMapParser.parse(settings.getOrDefault(prefix + ".fields", ""));
    }

    @Override
    public Map<String, Object> get(SinkRecord r) {
        Object c = getContent(r);
        if (c instanceof Struct) {
            c = StructUtils.structToMap((Struct) c);
        }

        if (!(c instanceof Map)) {
            throw new DataException("Only map and struct are supported for fields extraction");
        }

        HashMap<String, Object> result = new HashMap<>();
        Map<String, String> content = (Map<String, String>) c;
        for (Map<String, String> fieldTolookup : this.fields) {
            for (Map.Entry<String, String> e : fieldTolookup.entrySet()) {
                if (content.containsKey(e.getValue())) {
                    result.put(e.getKey(), content.get(e.getValue()));
                } else {
                    LOGGER.warn("could not find key {} in content; One day it might throw", e.getValue());
                }
            }

        }

        return result;
    }

    public static class Key extends Fields {

        public Key(String prefix, Map<String, String> settings) {
            super(prefix, settings);
        }

        @Override
        public Object getContent(SinkRecord r) {
            return r.key();
        }

    }

    public static class Value extends Fields {

        public Value(String prefix, Map<String, String> settings) {
            super(prefix, settings);
        }

        @Override
        public Object getContent(SinkRecord r) {
            return r.value();
        }
    }


}
