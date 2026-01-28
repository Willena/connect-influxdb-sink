package io.github.willena.connect.influxdb.providers;

import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.sink.SinkRecord;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Static implements Provider {

    private final Map<String, Object> staticValue;

    Static(String key, Map<String, String> settings) {

        List<String> keys = settings.keySet().stream()
                .filter(s -> s.startsWith(key))
                .filter(s -> !s.equals(key + ".class"))
                .filter(s -> s.length() > key.length())
                .collect(Collectors.toList());

        if (!keys.isEmpty()) {
            this.staticValue = keys.stream()
                    .collect(Collectors.toMap(s -> s.substring(key.length() + 1), settings::get));
            //specialCase if static is used with class initialization... Should never occur...
            //handle case with following config
            // abc.value.provider.class=Static
            // abc.value.provider.value=staticValue
            // abc.value.provider.key=staticValue2
        } else if (settings.containsKey(key)) {
            //Handle case where
            // abc.value=toto
            // abc.value=toto,tata
            this.staticValue = Collections.singletonMap("value", settings.get(key));
        } else {
            throw new ConfigException("Missing required key: " + key + " in config");
        }

        System.out.println("staticValue = " + staticValue);
    }

    @Override
    public Map<String, Object> get(SinkRecord r) {
        return staticValue;
    }

}
