package org.bitebuilders.converter.statusRequest;

import org.bitebuilders.enums.StatusRequest;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

import static org.junit.jupiter.api.Assertions.*;

public class StringToStatusRequestConverterTest {

    private final Converter<String, StatusRequest> converter = new StringToStatusRequestConverter();

    @Test
    public void testConvert() {
        // Arrange
        String input = "SENT_PERSONAL_INFO";

        // Act
        StatusRequest result = converter.convert(input);

        // Assert
        assertEquals(StatusRequest.SENT_PERSONAL_INFO, result);
    }

    @Test
    public void testConvertForAnotherValue() {
        // Arrange
        String input = "DELETED_FROM_EVENT";

        // Act
        StatusRequest result = converter.convert(input);

        // Assert
        assertEquals(StatusRequest.DELETED_FROM_EVENT, result);
    }

    @Test
    public void testConvertNull() {
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
