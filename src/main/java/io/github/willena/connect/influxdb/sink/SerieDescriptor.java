package io.github.willena.connect.influxdb.sink;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class SerieDescriptor implements Comparable<SerieDescriptor> {
    public final String measurement;
    public final long time;
    public final Map<String, String> tags;

    private SerieDescriptor(String measurement, long time, Map<String, String> tags) {
        this.measurement = measurement;
        this.tags = ImmutableMap.copyOf(tags);
        this.time = time;
    }

    public static SerieDescriptor of(String measurement, long time, Map<String, String> tags) {
        return new SerieDescriptor(measurement, time, tags);
    }


    @Override
    public String toString() {
        return "SerieDescriptor{" +
                "measurement='" + measurement + '\'' +
                ", time=" + time +
                ", tags=" + tags +
                '}';
    }

    public int compareTo(SerieDescriptor that) {
        return ComparisonChain.start().compare(this.measurement, that.measurement).compare(this.time, that.time).compare(Objects.hashCode(this.tags), Objects.hashCode(that.tags)).result();
    }


    @Override
    public int hashCode() {
        return java.util.Objects.hash(measurement, time, tags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SerieDescriptor that = (SerieDescriptor) o;
        return time == that.time && java.util.Objects.equals(measurement, that.measurement) && java.util.Objects.equals(tags, that.tags);
    }
}

