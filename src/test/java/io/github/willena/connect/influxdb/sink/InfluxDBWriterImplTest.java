package io.github.willena.connect.influxdb.sink;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InfluxDBWriterImplTest {


    @Test
    void dbrpEquality() {
        InfluxDBWriterImpl.DBRP a = new InfluxDBWriterImpl.DBRP("db", "rp");
        InfluxDBWriterImpl.DBRP c = new InfluxDBWriterImpl.DBRP("db", "rp");
        InfluxDBWriterImpl.DBRP b = new InfluxDBWriterImpl.DBRP("b", "b");
        assertNotEquals(a, b);
        assertEquals(a, c);

        assertEquals(a.hashCode(), c.hashCode());
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void notNullOrEmptyDbRP() {
        assertThrows(IllegalArgumentException.class, () -> new InfluxDBWriterImpl.DBRP("dd", null));
        assertThrows(IllegalArgumentException.class, () -> new InfluxDBWriterImpl.DBRP(null, "dd"));
        assertThrows(IllegalArgumentException.class, () -> new InfluxDBWriterImpl.DBRP(null, null));
        assertThrows(IllegalArgumentException.class, () -> new InfluxDBWriterImpl.DBRP("", ""));
        assertThrows(IllegalArgumentException.class, () -> new InfluxDBWriterImpl.DBRP("q", ""));
        assertThrows(IllegalArgumentException.class, () -> new InfluxDBWriterImpl.DBRP("", "s"));
    }

    @Test
    void dbrpGetter() {
        InfluxDBWriterImpl.DBRP a = new InfluxDBWriterImpl.DBRP("a", "b");
        assertEquals("b", a.getRp());
        assertEquals("a", a.getDb());
    }

}