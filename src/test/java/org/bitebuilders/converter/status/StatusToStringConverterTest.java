package org.bitebuilders.converter.status;

import org.bitebuilders.model.Event.Status;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

import static org.junit.jupiter.api.Assertions.*;

public class StatusToStringConverterTest {

    private final Converter<Status, String> converter = new StatusToStringConverter();

    @Test
    public void testConvert() {
        // Arrange
        Status input = Status.REGISTRATION_OPEN;

        // Act
        String result = converter.convert(input);

        // Assert
        assertEquals("REGISTRATION_OPEN", result);
    }

    @Test
    public void testConvertForAnotherValue() {
        // Arrange
        Status input = Status.HIDDEN;

        // Act
        String result = converter.convert(input);

        // Assert
        assertEquals("HIDDEN", result);
    }

    @Test
    public void testConvertNull() {
        // Arrange
        Status input = null;

        // Act and Assert
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> converter.convert(input));

        String expectedMessage = "Status can`t be null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
