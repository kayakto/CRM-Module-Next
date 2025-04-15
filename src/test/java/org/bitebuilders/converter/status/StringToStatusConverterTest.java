package org.bitebuilders.converter.status;

import org.bitebuilders.model.Event.Status;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

import static org.junit.jupiter.api.Assertions.*;

public class StringToStatusConverterTest {

    private final Converter<String, Status> converter = new StringToStatusConverter();

    @Test
    public void testConverter() {
        // Arrange
        String input = "REGISTRATION_OPEN";

        // Act
        Status result = converter.convert(input);

        // Assert
        assertEquals(Status.REGISTRATION_OPEN, result);
    }

    @Test
    public void testConverterAnotherValue() {
        // Arrange
        String input = "HIDDEN";

        // Act
        Status result = converter.convert(input);

        // Assert
        assertEquals(Status.HIDDEN, result);
    }

    @Test
    public void testConverterNull() {
        // Arrange
        String input = null;

        // Act and Assert
        assertThrows(
                NullPointerException.class,
                () -> converter.convert(input));
    }

    @Test
    public void testConvertNotExistingValue() {
        // Arrange
        String input = "NOT_EXISTING_VALUE";

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> converter.convert(input));
    }
}
