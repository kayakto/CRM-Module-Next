package org.bitebuilders.converter.time;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OffsetDateTimeReadConverterTest {

    private final OffsetDateTimeReadConverter converter = new OffsetDateTimeReadConverter();

    @Test
    public void testConvertValidTimestamp() {
        // Arrange
        Timestamp timestamp = Timestamp.valueOf("2024-11-30 12:00:00");

        // Act
        OffsetDateTime result = converter.convert(timestamp);

        // Assert
        assertEquals(OffsetDateTime.of(2024, 11, 30, 12, 0, 0, 0, ZoneOffset.ofHours(5)), result);
    }

    @Test
    public void testConvertAnotherValidTimestamp() {
        // Arrange
        Timestamp timestamp = Timestamp.valueOf("2024-05-18 02:12:00");

        // Act
        OffsetDateTime result = converter.convert(timestamp);

        // Assert
        assertEquals(OffsetDateTime.of(2024, 5, 18, 2, 12, 0, 0, ZoneOffset.ofHours(5)), result);
    }

    @Test
    public void testConvertNull() {
        // Arrange
        Timestamp input = null;

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> converter.convert(input)
        );
    }
}
