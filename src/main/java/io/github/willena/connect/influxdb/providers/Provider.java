package io.github.willena.connect.influxdb.providers;

import org.apache.kafka.connect.sink.SinkRecord;

import java.util.Map;
import java.util.Optional;

public interface Provider {

    Map<String, Object> get(SinkRecord r);

    default Optional<Object> getFirst(SinkRecord r) {
        return get(r).values().stream().findFirst();
    }

    default Object getContent(SinkRecord r) {
        return r;
    }

}
