[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.willena/connect-influxdb-sink/badge.svg)](https://search.maven.org/artifact/io.github.willena/connect-influxdb-sink/)

# Kafka Connect influxdb Sink connector

A configurable Kafka connector to write into influxdb database.

The Connector contains many "providers" to extract fields, timestamp, tags, from the SinkRecord.

## Providers

Some configuration key expect a valid provider class to get a value, for example:

- the database name
- the tags fields
- the fields
- the measurement name
- the timestamp field

A single provider can be specified using:

```properties
<key>.provider.class=com.my.package.Myclass
<key>.provider.<MyClassOption>=<value>
```

Providers can be chained if you need to collect fields from multiple type or places (Record, Key, Value).

Bellow an example of how to use multiple providers:

```properties
<key>.providers=provider1,provider2
<key>.providers.provider1.class=com.my.package.MyClass
<key>.providers.provider2.class=com.my.package.MyClass2
<key>.providers.provider1.<MyClassOption>=<value>
<key>.providers.provider2.<MyClass2Option>=<value>
```

### Static provider (Default provider)

Provide a static value (class: `io.github.willena.connect.influxdb.providers.Static`)

#### Usage

For example if the configuration key is `influxdb.database` the value can be specified as:

- Direct value: `influxdb.database=myvalue`
- Full provider config with multiple values:
    ```properties
    influxdb.database.provider.class=io.github.willena.connect.influxdb.providers.Static
    influxdb.database.provider.value=myvalue
    influxdb.database.provider.value2=myvalue2
    influxdb.database.provider.toto=myvalue3
    ```

### Simple provider

Provider that allow to get the raw key or value:

- Value class: `io.github.willena.connect.influxdb.providers.Simple$Value`
- Key class: `io.github.willena.connect.influxdb.providers.Simple$Key`

#### Usage

For example if the configuration key is `influxdb.database`:

```properties
influxdb.database.provider.class=io.github.willena.connect.influxdb.providers.Simple$Key
```

If the key or value is a `Struct` or a `Map` then all fields will be used, else it transformed to `String`

### Record provider

Provider that allows to collect field from the Record itself such as:

- Timestamp
- Topic
- Partition
- ...

#### Options

| Config key | Doc                          | Type        | Default | Valid Values / Comments                                                                                                                                                                                                                      |
|------------|------------------------------|-------------|---------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `fields`   | Field to get from the record | String List | null    | Any member of RecordFields: <br/>`kafkaOffset`,`timestamp`,`topic`,`kafkaPartition`<br/><br/> Multiple fields can be provided if coma separated. <br/><br/> Fields can be renamed if using the following syntax: `originalField:renamed,...` |

#### Usage

```properties
influxdb.database.provider.class=io.github.willena.connect.influxdb.providers.Record
influxdb.database.provider.fields=timestamp,topic
```

### Fields provider

Provider that gets data from fields in key or in value of the SinkRecord.
Key or Value must be valid `Struct` or `Json` (Map)

#### Options

| Config key | Doc                          | Type        | Default | Valid Values / Comments                                                                                                                                                                                   |
|------------|------------------------------|-------------|---------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `fields`   | Field to get from the record | String List | null    | Missing field does not throw. Warning is logged <br/><br/> Multiple fields can be provided if coma separated. <br/><br/> Fields can be renamed if using the following syntax: `originalField:renamed,...` |

#### Usage

```properties
influxdb.database.provider.class=io.github.willena.connect.influxdb.providers.Fields$Key
influxdb.database.provider.fields=field1,field2:renamedField2
```

## Configuration Keys

| Config key                      | Doc                                                                          | Type     | Default      | Valid Values / Comments                       |
|---------------------------------|------------------------------------------------------------------------------|----------|--------------|-----------------------------------------------|
| `influxdb.url`                  | Fully qualified InfluxDB API URL used for establishing connection.           | String   | null         | 	                                             |
| `influxdb.username`             | InfluxDB username on whose behalf database connection has to be established. | String   | null         | 	                                             |
| `influxdb.password`             | The InfluxDB user's password used for establishing database connection.      | String   | null         | 	                                             |
| `influxdb.database.auto.create` | Enable autocreation of database if it does not exist                         | Boolean  | false        | true,false                                    |
| `influxdb.log.level`            | Influxdb logger level                                                        | String   | NONE         | NONE,BASIC,HEADERS,FULL                       |
| `influxdb.gzip.enable`          | Flag to determine if gzip should be enabled.                                 | Boolean  | true         | true,false                                    |
| `influxdb.timestamp.unit`       | The default time unit for writing data to InfluxDB.                          | String   | MILLISECONDS | NANOSECONDS,MICROSECONDS,MILLISECONDS,SECONDS |
| `influxdb.consistency.level`    | The default consistency level for writing data to InfluxDB.                  | String   | ONE          | ALL,ANY,ONE,QUORUM                            |
| `retry.backoff.ms`              | Backoff time duration to wait before retrying                                | Integer  | 1000         | 	                                             |
| `max.retries`                   | The maximum number of times to retry on errors before failing the task.      | Integer  | 10           | 	                                             |
| `influxdb.database`             | The name of the database to insert the data into.                            | Provider | 	            | See #provider section                         |
| `influxdb.retention.policy`     | The name of the retention policy to insert the data into.                    | Provider | 	            | See #provider section                         |
| `influxdb.measurement`          | The name of the measurement to insert the data into.                         | Provider | 	            | See #provider section                         |
| `influxdb.fields`               | List of fields that needs to be inserted in influxdb                         | Provider | 	            | See #provider section                         |
| `influxdb.timestamp`            | Where to get the timestamp from                                              | Provider | 	            | See #provider section                         |

## Install

The connector is distributed as a single fat jar that can be placed into your kafka connect plugin folder.
It should automatically be picked up at startup.

It can be downloaded from:

* maven central repository https://search.maven.org/artifact/io.github.willena/connect-influxdb-sink
* Github releases: https://github.com/Willena/connect-influxdb-sink/releases/latest




