package com.mycompany.gamezone.validation;

import com.mycompany.gamezone.exceptions.InvalidDataException;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonValidationTest {

    @ParameterizedTest
    @MethodSource("invalidSellers")
    void sellerFailuresKeepTheOriginalOrderAndSpanishMessages(String employeeCode,
            String shift, String name, String id, long phone, String expectedMessage) {
        InvalidDataException canonical = assertThrows(InvalidDataException.class,
                () -> PersonValidator.validateSellerData(employeeCode, shift, name, id, phone));
        InvalidDataException consumer = assertThrows(InvalidDataException.class,
                () -> PersonValidator.validateSeller(employeeCode, shift, name, id, phone));

        assertEquals(expectedMessage, canonical.getMessage());
        assertEquals("INVALID_DATA", canonical.getErrorCode());
        assertEquals(expectedMessage, consumer.getMessage());
        assertEquals("INVALID_DATA", consumer.getErrorCode());
    }

    @ParameterizedTest
    @ValueSource(longs = {Long.MIN_VALUE, -1, 0})
    void nonpositiveContactNumbersKeepTheirTypedFailure(long phone) {
        InvalidDataException failure = assertThrows(InvalidDataException.class,
                () -> PersonValidator.validatePositiveContactNumber(phone));

        assertEquals("El campo número de contacto no es válido: debe ser mayor que cero.",
                failure.getMessage());
        assertEquals("INVALID_DATA", failure.getErrorCode());
    }

    @ParameterizedTest
    @ValueSource(longs = {1, Long.MAX_VALUE})
    void positiveContactNumbersKeepTheirExistingAcceptedRange(long phone) {
        assertDoesNotThrow(() -> PersonValidator.validatePositiveContactNumber(phone));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Ana1", "1Ana", "An1a"})
    void numericNamesKeepTheirTypedSpanishFailure(String name) {
        InvalidDataException failure = assertThrows(InvalidDataException.class,
                () -> PersonValidator.validateNameWithoutDigits(name));

        assertEquals("El campo nombre no es válido: no puede contener números.",
                failure.getMessage());
        assertEquals("INVALID_DATA", failure.getErrorCode());
    }

    @Test
    void sellerTrimChecksRemainDistinctFromRequirementSixBlankChecks() {
        String emSpace = "\u2003";

        assertDoesNotThrow(() -> PersonValidator.validateSellerData(
                emSpace, emSpace, emSpace, emSpace, 1));
        assertDoesNotThrow(() -> PersonValidator.validateSeller(
                emSpace, emSpace, emSpace, emSpace, 1));
        assertThrows(InvalidDataException.class,
                () -> PersonValidator.validatePersonData(emSpace, "Ana", "a@b.co", "1"));
    }

    @Test
    void namePatternRetainsAccentsAndItsExistingLineBreakBehavior() {
        assertDoesNotThrow(() -> PersonValidator.validateNameWithoutDigits("María José"));
        assertDoesNotThrow(() -> PersonValidator.validateNameWithoutDigits("Ana\n1"));
    }

    private static Stream<Arguments> invalidSellers() {
        return Stream.of(
                Arguments.of(null, null, null, null, 0L,
                        "El campo código de empleado no es válido: no puede estar vacío."),
                Arguments.of("EMP1", " \t", null, null, 0L,
                        "El campo turno no es válido: no puede estar vacío."),
                Arguments.of("EMP1", "MORNING", "", null, 0L,
                        "El campo nombre no es válido: no puede estar vacío."),
                Arguments.of("EMP1", "MORNING", "Ana1", null, 0L,
                        "El campo nombre no es válido: no puede contener números."),
                Arguments.of("EMP1", "MORNING", "Ana", " ", 0L,
                        "El campo identificación no es válido: no puede estar vacío."),
                Arguments.of("EMP1", "MORNING", "Ana", "S1", 0L,
                        "El campo número de contacto no es válido: debe ser mayor que cero."));
    }
}
