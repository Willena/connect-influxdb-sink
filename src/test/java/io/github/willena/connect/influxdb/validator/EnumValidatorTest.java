package io.github.willena.connect.influxdb.validator;

import org.apache.kafka.common.config.ConfigException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnumValidatorTest {


    @Test
    void oneOf() {
        assertDoesNotThrow(() -> EnumValidator.oneOf(Toto.class).ensureValid("RandomKey", "ABC"), "Should not throw");
        assertThrows(ConfigException.class, () -> EnumValidator.oneOf(Toto.class).ensureValid("RandomKey", "sdfsdf"), "Should throw because not found");
        assertThrows(ConfigException.class, () -> EnumValidator.oneOf(Toto.class).ensureValid("RandomKey", "abc"), "Should throw because does not exist lower case");
    }

    private enum Toto {
        ABC,
        DEF
    }
}