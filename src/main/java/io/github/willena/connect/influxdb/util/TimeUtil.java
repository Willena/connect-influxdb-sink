package io.github.willena.connect.influxdb.util;

import java.time.Duration;

public class TimeUtil {
    public static String durationAsString(long millis) {
        return durationAsString(Duration.ofMillis(millis));
    }

    public static String durationAsString(Duration duration) {
        return String.format("%02d:%02d:%02d.%03d", duration.toHours(),
                duration.toMinutes() % 60L,
                duration.getSeconds() % 60L,
                duration.getNano() / 1000000 % 1000);
    }
}
