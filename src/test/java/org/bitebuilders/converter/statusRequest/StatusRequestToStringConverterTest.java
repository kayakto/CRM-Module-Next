package org.bitebuilders.converter.statusRequest;

import org.bitebuilders.enums.StatusRequest;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

import static org.junit.jupiter.api.Assertions.*;

public class StatusRequestToStringConverterTest {

    private final Converter<StatusRequest, String> converter = new StatusRequestToStringConverter();

    @Test
    public void testConvert() {
        // Arrange
        StatusRequest input = StatusRequest.SENT_PERSONAL_INFO;

        // Act
        String result = converter.convert(input);

        // Assert
        assertEquals("SENT_PERSONAL_INFO", result);
    }

    @Test
    public void testConvertForAnotherValue() {
        // Arrange
        StatusRequest input = StatusRequest.REJECTED_FROM_EVENT;

        // Act
        String result = converter.convert(input);

        // Assert
        assertEquals("REJECTED_FROM_EVENT", result);
    }

    @Test
    public void testConvertNull() {
        // Arrange
        StatusRequest input = null;

        // Act and Assert
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> converter.convert(input));

        String expectedMessage = "StatusRequest cannot be null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}

