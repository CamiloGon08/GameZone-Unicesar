package com.mycompany.gamezone.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ProductStockTest {

    @ParameterizedTest
    @CsvSource({"5, 0, 5", "5, -5, 0", "0, 7, 7", "2147483647, -2147483647, 0"})
    void allowedAdjustmentsKeepTheirExpectedStock(int initialStock, int amount, int expectedStock) {
        Product product = productWithStock(initialStock);

        assertTrue(product.canAdjustStock(amount));
        assertDoesNotThrow(() -> product.adjustStock(amount));
        assertEquals(expectedStock, product.getStock());
    }

    @ParameterizedTest
    @CsvSource({"5, -6", "0, -1", "2147483647, -2147483648"})
    void insufficientStockRejectsTheAdjustmentWithoutChangingInventory(int initialStock, int amount) {
        Product product = productWithStock(initialStock);

        assertFalse(product.canAdjustStock(amount));
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> product.adjustStock(amount));

        assertEquals("Stock cannot be negative.", error.getMessage());
        assertEquals(initialStock, product.getStock());
    }

    @Test
    void checkingAvailabilityDoesNotReserveOrChangeStock() {
        Product product = productWithStock(2);

        assertTrue(product.canAdjustStock(-2));
        assertFalse(product.canAdjustStock(-3));
        assertTrue(product.canAdjustStock(4));

        assertEquals(2, product.getStock());
    }

    private Product productWithStock(int stock) {
        return new VideoGame("P1", "Game", 100, stock, "PC", "RPG", "E");
    }
}
