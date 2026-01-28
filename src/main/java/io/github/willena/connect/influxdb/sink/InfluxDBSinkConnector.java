package io.github.willena.connect.influxdb.sink;

import io.github.willena.connect.influxdb.util.Version;
import org.apache.kafka.common.config.Config;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InfluxDBSinkConnector extends SinkConnector {
    Map<String, String> settings;

    @Override
    public Config validate(Map<String, String> connectorConfigs) {
        Config c = super.validate(connectorConfigs);
        new InfluxDBSinkConnectorConfig(connectorConfigs);
        return c;
    }

    public String version() {
        return Version.getVersion();
    }

    public void start(Map<String, String> settings) {
//        InfluxDBSinkConnectorConfig config = new InfluxDBSinkConnectorConfig(settings);
        this.settings = settings;

//        try (InfluxDB influxDB = this.factory.create(config)) {
//            List<String> databases = influxDB.describeDatabases();
//            if (LOGGER.isTraceEnabled()) {
//                LOGGER.trace("start() - existing databases = '{}'", Joiner.on(", ").join(databases));
//            }
//        }

    }

    public Class<? extends Task> taskClass() {
        return InfluxDBSinkTask.class;
    }

    public List<Map<String, String>> taskConfigs(int taskConfigs) {
        if (taskConfigs <= 0) {
            throw new RuntimeException("taskConfigs must be greater than 0.");
        }
        List<Map<String, String>> result = new LinkedList<>();

        for (int i = 0; i < taskConfigs; i++) {
            result.add(this.settings);
        }

        return result;
    }


    public void stop() {
    }


    public ConfigDef config() {
        return InfluxDBSinkConnectorConfig.config();
    }
}

