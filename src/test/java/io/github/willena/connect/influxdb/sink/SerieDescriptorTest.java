package io.github.willena.connect.influxdb.sink;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class SerieDescriptorTest {

    @Test
    void of() {
        SerieDescriptor desc = SerieDescriptor.of("Me1", 1, Collections.singletonMap("tag", "value"));
        assertEquals("Me1", desc.measurement);
        assertEquals(1, desc.time);
        assertEquals(Collections.singletonMap("tag", "value"), desc.tags);
    }

    @Test
    void testToString() {
        SerieDescriptor desc = SerieDescriptor.of("Me1", 1, Collections.singletonMap("tag", "value"));
        assertNotNull(desc.toString());
        assertTrue(desc.toString().contains("Me1"));
        assertTrue(desc.toString().contains("1"));
        assertTrue(desc.toString().contains("tag"));
        assertTrue(desc.toString().contains("value"));
    }

    @Test
    void compareTo() {
        SerieDescriptor desc = SerieDescriptor.of("Me1", 1, Collections.singletonMap("tag", "value"));
        SerieDescriptor desc2 = SerieDescriptor.of("Me1", 1, Collections.singletonMap("tag", "value"));
        SerieDescriptor desc3 = SerieDescriptor.of("Me1", 10, Collections.singletonMap("tag", "value"));

        assertEquals(0, desc.compareTo(desc2));
        assertEquals(-1, desc.compareTo(desc3));
    }

    @Test
    void testHashCode() {
        SerieDescriptor desc = SerieDescriptor.of("Me1", 1, Collections.singletonMap("tag", "value"));
        SerieDescriptor desc2 = SerieDescriptor.of("Me1", 1, Collections.singletonMap("tag", "value"));
        SerieDescriptor desc3 = SerieDescriptor.of("Me1", 10, Collections.singletonMap("tag", "value"));

        assertEquals(desc.hashCode(), desc2.hashCode());
        assertNotEquals(desc2.hashCode(), desc3.hashCode());
    }

    @Test
    void testEquals() {
        SerieDescriptor desc = SerieDescriptor.of("Me1", 1, Collections.singletonMap("tag", "value"));
        SerieDescriptor desc2 = SerieDescriptor.of("Me1", 1, Collections.singletonMap("tag", "value"));
        SerieDescriptor desc3 = SerieDescriptor.of("Me1", 10, Collections.singletonMap("tag", "value"));

        assertEquals(desc, desc2);
        assertNotEquals(desc2, desc3);
    }
}