package io.github.willena.connect.influxdb.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Version {
    public static String getVersion() {
        InputStream resourceAsStream = Version.class.getResourceAsStream("/version.properties");
        Properties prop = new Properties();
        try {
            prop.load(resourceAsStream);
        } catch (IOException e) {
            // Loading should never fail here as it is a build resource.
            return "0.0.0";
        }
        return prop.getProperty("version");
    }
}
