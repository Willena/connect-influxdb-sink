package io.github.willena.connect.influxdb.validator;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

import java.util.Arrays;

public final class EnumValidator {

    public static <E extends Enum<E>> ConfigDef.Validator oneOf(Class<E> enumType) {
        return (s, o) -> Arrays.stream(enumType.getEnumConstants()).map(Enum::name)
                .filter(s1 -> s1.equals(o))
                .findFirst()
                .orElseThrow(() -> new ConfigException(String.format("Value '%s' could not found in enum '%s'; Valid values are: '%s'", o, enumType.getSimpleName(), Arrays.toString(enumType.getEnumConstants()))));
    }
}
