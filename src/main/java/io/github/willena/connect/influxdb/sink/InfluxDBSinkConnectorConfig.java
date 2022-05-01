package io.github.willena.connect.influxdb.sink;

import io.github.willena.connect.influxdb.providers.Provider;
import io.github.willena.connect.influxdb.providers.ProviderChain;
import io.github.willena.connect.influxdb.validator.EnumValidator;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.types.Password;
import org.influxdb.InfluxDB;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.kafka.common.config.ConfigDef.Importance.*;
import static org.apache.kafka.common.config.ConfigDef.NO_DEFAULT_VALUE;
import static org.apache.kafka.common.config.ConfigDef.Type.*;


public class InfluxDBSinkConnectorConfig extends AbstractConfig {

    /*
        Configuration keys
     */

    public static final String INFLUXDB_URL_CONFIG = "influxdb.url";
    public static final String INFLUXDB_USERNAME_CONFIG = "influxdb.username";
    public static final String INFLUXDB_PASS_CONFIG = "influxdb.password";
    public static final String INFLUXDB_DB_NAME_CONFIG = "influxdb.database";
    public static final String CONSISTENCY_LEVEL_CONF = "influxdb.consistency.level";
    public static final String LOG_LEVEL_CONF = "influxdb.log.level";
    public static final String GZIP_ENABLE_CONF = "influxdb.gzip.enable";
    public static final String TIMEUNIT_CONF = "influxdb.timestamp.unit";
    public static final String RETRY_BACKOFF_CONF = "retry.backoff.ms";
    public static final String MAX_RETRIES = "max.retries";
    public static final int RETRY_BACKOFF_DEFAULT = 1000;
    public static final String INFLUXDB_USERNAME_DEFAULT = null;
    public static final Password INFLUXDB_PASS_DEFAULT = new Password("");
    static final String CONSISTENCY_LEVEL_DOC = "The default consistency level for writing data to InfluxDB.";
    static final String TIMEUNIT_DOC = "The default timeunit for writing data to InfluxDB.";

    /*
        Groups
     */
    static final String GZIP_ENABLE_DOC = "Flag to determine if gzip should be enabled.";
    static final String MAX_RETRIES_DISPLAY = "Max retries";
    static final String LOG_LEVEL_DOC = "Influxdb logger level";


    /*
        Documentation for configuration keys
     */
    private static final String AUTO_CREATE_DB = "influxdb.database.auto.create";
    private static final String INFLUXDB_MEASUREMENT_CONFIG = "influxdb.measurement";
    private static final String INFLUXDB_TAGS_CONFIG = "influxdb.tags";
    private static final String INFLUXDB_FIELDS_CONFIG = "influxdb.fields";
    private static final String INFLUXDB_TIMESTAMP_CONFIG = "influxdb.timestamp";
    private static final String GROUP_INFLUXDB = "InfluxDb";
    private static final String GROUP_CONNECTION = "Connection";
    private static final String GROUP_WRITE = "Write";
    private static final String RETRY_BACKOFF_DISPLAY = "Backoff Time";
    private static final String RETRY_BACKOFF_DOC = "Backoff time duration to wait before retrying";
    private static final int MAX_RETRIES_DEFAULT = 10;
    private static final String MAX_RETRIES_DOC = "The maximum number of times to retry on errors before failing the task.";
    private static final String INFLUXDB_DB_NAME_CONFIG_DOC = "The InfluxDB database name in which connector will write records from Apache Kafka topic.";
    private static final String INFLUXDB_DB_NAME_CONFIG_DISPLAY = "InfluxDB Database";
    private static final String INFLUXDB_DB_NAME_CONFIG_DEFAULT = null;
    private static final String TIMESTAMP_FIELD_NAME_DISPLAY = "Event Time field name";
    private static final String TIMESTAMP_FIELD_NAME_DOC = "The name of field in the Kafka record that contains the event time to be written to anInfluxDB data point. By default (if this config is unspecified), the timestamp written to InfluxDB is the Kafka record timestamp (when the Kafka record was created) which corresponds to the time that the event was processed.";
    private static final String TIMESTAMP_FIELD_NAME_DEFAULT = null;
    private static final String AUTO_CREATE_DB_DOC = "Enable autocreation of database if it does not exist";
    private static final Boolean AUTO_CREATE_DB_DEFAULT = false;
    private static final String INFLUXDB_URL_DOC = "Fully qualified InfluxDB API URL used for establishing connection.";
    private static final String INFLUXDB_USERNAME_DOC = "InfluxDB username on whose behalf database connection has to be established.";
    private static final String INFLUXDB_PASS_DOC = "The InfluxDB user's password used for establishing database connection.";
    private static final String AUTO_CREATE_DB_DISPLAY = "Auto create database";
    private static final String INFLUXDB_URL_DISPLAY = "InfluxDB API URL";
    private static final String INFLUXDB_USERNAME_DISPLAY = "InfluxDB Username";
    private static final String INFLUXDB_PASS_DISPLAY = "InfluxDB Password";

    /*
        --------------------------------
        Variables holding config values
        --------------------------------
     */
    public final InfluxDB.ConsistencyLevel consistencyLevel;

    public final TimeUnit precision;

    public final InfluxDB.LogLevel logLevel;

    public final String url;

    public final String username;

    public final String password;

    public final boolean authentication;
    public final Boolean autoCreateDatabase;
    public final boolean gzipEnable;
    public final int backOffTime;
    public final int maxRetries;
    final Provider databaseProvider;
    final Provider tagsProvider;
    final Provider fieldsProvider;
    final Provider timestampProvider;
    final Provider measurementProvider;

    public InfluxDBSinkConnectorConfig(Map<String, String> settings) {
        super(config(), settings);

        this.url = getString(INFLUXDB_URL_CONFIG);
        this.username = getString(INFLUXDB_USERNAME_CONFIG);
        this.authentication = !(this.username == null || this.username.isEmpty());
        this.password = getPassword(INFLUXDB_PASS_CONFIG).value();

        this.databaseProvider = new ProviderChain(INFLUXDB_DB_NAME_CONFIG, settings);
        this.autoCreateDatabase = getBoolean(AUTO_CREATE_DB);
        this.measurementProvider = new ProviderChain(INFLUXDB_MEASUREMENT_CONFIG, settings);
        this.tagsProvider = new ProviderChain(INFLUXDB_TAGS_CONFIG, settings);
        this.fieldsProvider = new ProviderChain(INFLUXDB_FIELDS_CONFIG, settings);
        this.timestampProvider = new ProviderChain(INFLUXDB_TIMESTAMP_CONFIG, settings);
        this.precision = TimeUnit.valueOf(getString(TIMEUNIT_CONF).toUpperCase());

        this.consistencyLevel = InfluxDB.ConsistencyLevel.valueOf(getString(CONSISTENCY_LEVEL_CONF).toUpperCase());
        this.logLevel = InfluxDB.LogLevel.valueOf(getString(LOG_LEVEL_CONF).toUpperCase());
        this.gzipEnable = getBoolean(GZIP_ENABLE_CONF);
        this.backOffTime = getInt(RETRY_BACKOFF_CONF);
        this.maxRetries = getInt(MAX_RETRIES);


    }

    public static ConfigDef config() {
        ConfigDef configDef = new ConfigDef();
        int orderInGroup = 0;

        configDef.define(INFLUXDB_URL_CONFIG, STRING, NO_DEFAULT_VALUE, HIGH, INFLUXDB_URL_DOC, GROUP_INFLUXDB, ++orderInGroup, ConfigDef.Width.LONG, INFLUXDB_URL_DISPLAY);
        configDef.define(INFLUXDB_USERNAME_CONFIG, STRING, INFLUXDB_USERNAME_DEFAULT, MEDIUM, INFLUXDB_USERNAME_DOC, GROUP_INFLUXDB, ++orderInGroup, ConfigDef.Width.LONG, INFLUXDB_USERNAME_DISPLAY);
        configDef.define(INFLUXDB_PASS_CONFIG, PASSWORD, INFLUXDB_PASS_DEFAULT, MEDIUM, INFLUXDB_PASS_DOC, GROUP_INFLUXDB, ++orderInGroup, ConfigDef.Width.LONG, INFLUXDB_PASS_DISPLAY);

        //configDef.define(INFLUXDB_DB_NAME_CONFIG, STRING, INFLUXDB_DB_NAME_CONFIG_DEFAULT, LOW, INFLUXDB_DB_NAME_CONFIG_DOC, GROUP_WRITE, ++orderInGroup, ConfigDef.Width.LONG, INFLUXDB_DB_NAME_CONFIG_DISPLAY);
        configDef.define(AUTO_CREATE_DB, BOOLEAN, AUTO_CREATE_DB_DEFAULT, LOW, AUTO_CREATE_DB_DOC, GROUP_CONNECTION, ++orderInGroup, ConfigDef.Width.MEDIUM, AUTO_CREATE_DB_DISPLAY);
        //configDef.define(INFLUXDB_MEASUREMENT_CONFIG, STRING, NO_DEFAULT_VALUE, LOW, "", GROUP_CONNECTION, ++orderInGroup, ConfigDef.Width.MEDIUM, "");
        //configDef.define(INFLUXDB_TAGS_CONFIG, STRING, NO_DEFAULT_VALUE, LOW, "", GROUP_CONNECTION, ++orderInGroup, ConfigDef.Width.MEDIUM, "");
        //configDef.define(INFLUXDB_FIELDS_CONFIG, STRING, NO_DEFAULT_VALUE, LOW, "", GROUP_CONNECTION, ++orderInGroup, ConfigDef.Width.MEDIUM, "");
        //configDef.define(INFLUXDB_TIMESTAMP_CONFIG, STRING, NO_DEFAULT_VALUE, LOW, "", GROUP_CONNECTION, ++orderInGroup, ConfigDef.Width.MEDIUM, "");
        configDef.define(TIMEUNIT_CONF, STRING, TimeUnit.MILLISECONDS.toString(), EnumValidator.oneOf(TimeUnit.class), MEDIUM, TIMEUNIT_DOC, GROUP_WRITE, ++orderInGroup, ConfigDef.Width.LONG, TIMEUNIT_CONF);

        configDef.define(LOG_LEVEL_CONF, STRING, InfluxDB.LogLevel.NONE.toString(), EnumValidator.oneOf(InfluxDB.LogLevel.class), LOW, LOG_LEVEL_DOC, GROUP_CONNECTION, ++orderInGroup, ConfigDef.Width.MEDIUM, LOG_LEVEL_DOC);
        configDef.define(GZIP_ENABLE_CONF, BOOLEAN, Boolean.TRUE, LOW, GZIP_ENABLE_DOC, GROUP_CONNECTION, ++orderInGroup, ConfigDef.Width.SHORT, GZIP_ENABLE_DOC);
        configDef.define(CONSISTENCY_LEVEL_CONF, STRING, InfluxDB.ConsistencyLevel.ONE.toString(), EnumValidator.oneOf(InfluxDB.ConsistencyLevel.class), LOW, CONSISTENCY_LEVEL_DOC, GROUP_WRITE, ++orderInGroup, ConfigDef.Width.MEDIUM, CONSISTENCY_LEVEL_CONF);
        configDef.define(RETRY_BACKOFF_CONF, INT, RETRY_BACKOFF_DEFAULT, MEDIUM, RETRY_BACKOFF_DOC, GROUP_WRITE, ++orderInGroup, ConfigDef.Width.MEDIUM, RETRY_BACKOFF_DISPLAY);
        configDef.define(MAX_RETRIES, INT, MAX_RETRIES_DEFAULT, MEDIUM, MAX_RETRIES_DOC, GROUP_WRITE, ++orderInGroup, ConfigDef.Width.MEDIUM, MAX_RETRIES_DISPLAY);

        return configDef;
    }

    public static void main(String[] args) {
        System.out.println(config().toEnrichedRst());
    }
}

