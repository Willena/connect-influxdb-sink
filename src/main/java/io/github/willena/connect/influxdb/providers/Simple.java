package io.github.willena.connect.influxdb.providers;

import io.github.willena.connect.influxdb.util.StructUtils;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;

import java.util.Collections;
import java.util.Map;

public abstract class Simple implements Provider {

    public Simple(String prefix, Map<String, String> settings) {
    }

    @Override
    public Map<String, Object> get(SinkRecord r) {
        Object v = getContent(r);
        if (v instanceof Struct) {
            return StructUtils.structToMap((Struct) v);
        } else if (v instanceof Map) {
            return (Map<String, Object>) v;
        } else {
            return Collections.singletonMap("value", v);
        }
    }

    public static class Key extends Simple {

        public Key(String prefix, Map<String, String> settings) {
            super(prefix, settings);
        }

        @Override
        public Object getContent(SinkRecord r) {
            return r.key();
        }
    }

    public static class Value extends Simple {

        public Value(String prefix, Map<String, String> settings) {
            super(prefix, settings);
        }

        @Override
        public Object getContent(SinkRecord r) {
            return r.value();
        }
    }
}
