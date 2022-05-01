package io.github.willena.connect.influxdb.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

class VersionTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionTest.class);

    @Test
    void getVersion() {
        assertNotEquals("", Version.getVersion());
        assertNotEquals("0.0.0", Version.getVersion());
        LOGGER.info("Version is {}", Version.getVersion());
    }
}