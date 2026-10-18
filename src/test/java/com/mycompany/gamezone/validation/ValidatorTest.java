package com.mycompany.gamezone.validation;

import com.mycompany.gamezone.exceptions.*;
import com.mycompany.gamezone.model.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {
    private final Product product = new VideoGame("P1", "Game", 100, 2, "PC", "RPG", "E");
    private final Customer customer = new Customer("a@b.co", "Ana", "C1", 3001234567L);
    private final Seller seller = new Seller("EMP1", "MORNING", "Luis", "S1", 3001234568L);

    @Test
    void typedExceptionsKeepCodesSpanishDetailsAndOriginalCause() {
        IOException cause = new IOException("denied");
        PersistenceException persistence = new PersistenceException("Lectura de products.txt", cause);
        assertSame(cause, persistence.getCause());
        assertEquals("PERSISTENCE_ERROR", persistence.getErrorCode());
        assertEquals("BUSINESS_RULE", new BusinessRuleException("Sin existencias").getErrorCode());
        assertEquals("INVALID_DATA", new InvalidDataException("precio", "negativo").getErrorCode());
        ResourceNotFoundException missing = new ResourceNotFoundException("producto", "P1");
        assertEquals("RESOURCE_NOT_FOUND", missing.getErrorCode());
        assertTrue(missing.getMessage().contains("producto con identificador P1"));
        assertTrue(missing instanceof RuntimeException);
        assertEquals("GAMEZONE_ERROR", new GameZoneException("Error") { }.getErrorCode());
        assertEquals("CUSTOM", new GameZoneException("Error", "CUSTOM") { }.getErrorCode());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void productRequiresIdentifierAndTitle(String value) {
        assertThrows(InvalidDataException.class,
                () -> ProductValidator.validateProductData(value, "Game", 100, 0));
        assertThrows(InvalidDataException.class,
                () -> ProductValidator.validateProductData("P1", value, 100, 0));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-1, 0, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void priceMustBePositiveAndFinite(double price) {
        assertThrows(InvalidDataException.class,
                () -> ProductValidator.validateProductData("P1", "Game", price, 0));
    }

    @Test
    void stockBoundariesAndMissingProductHaveDifferentCategories() {
        assertDoesNotThrow(() -> ProductValidator.validateProductData("P1", "Game", 0.01, 0));
        assertThrows(InvalidDataException.class,
                () -> ProductValidator.validateProductData("P1", "Game", 10, -1));
        assertDoesNotThrow(() -> ProductValidator.validateStockAvailability(product, 2));
        assertThrows(BusinessRuleException.class,
                () -> ProductValidator.validateStockAvailability(product, 3));
        assertThrows(InvalidDataException.class,
                () -> ProductValidator.validateStockAvailability(product, 0));
        assertThrows(InvalidDataException.class,
                () -> ProductValidator.validateStockAvailability(product, -1));
        assertThrows(InvalidDataException.class,
                () -> ProductValidator.validateStockAvailability(null, 1));
        assertThrows(ResourceNotFoundException.class,
                () -> ProductValidator.validateProductExists(null, "missing"));
        assertDoesNotThrow(() -> ProductValidator.validateProductExists(product, "P1"));
        assertEquals(2, product.getStock());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void personRequiresAllFourFields(String value) {
        assertThrows(InvalidDataException.class,
                () -> PersonValidator.validatePersonData(value, "Ana", "a@b.co", "300"));
        assertThrows(InvalidDataException.class,
                () -> PersonValidator.validatePersonData("C1", value, "a@b.co", "300"));
        assertThrows(InvalidDataException.class,
                () -> PersonValidator.validatePersonData("C1", "Ana", value, "300"));
        assertThrows(InvalidDataException.class,
                () -> PersonValidator.validatePersonData("C1", "Ana", "a@b.co", value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "a@b", "a.b"})
    void personRequiresAtSignAndDot(String email) {
        assertThrows(InvalidDataException.class,
                () -> PersonValidator.validatePersonData("C1", "Ana", email, "300"));
    }

    @Test
    void personUniquenessUsesRealModelAccessorAndDoesNotModifyList() {
        List<Person> people = List.of(customer, seller);
        assertDoesNotThrow(() -> PersonValidator.validatePersonData("C1", "Ana", "a@b.co", "300"));
        assertDoesNotThrow(() -> PersonValidator.validateUniquePerson(people, "C2"));
        assertThrows(BusinessRuleException.class, () -> PersonValidator.validateUniquePerson(people, "C1"));
        assertThrows(InvalidDataException.class, () -> PersonValidator.validateUniquePerson(null, "C1"));
        assertThrows(InvalidDataException.class,
                () -> PersonValidator.validateUniquePerson(Arrays.asList((Person) null), "C1"));
        assertThrows(ResourceNotFoundException.class, () -> PersonValidator.validatePersonExists(null, "C1"));
        assertDoesNotThrow(() -> PersonValidator.validatePersonExists(customer, "C1"));
        assertEquals(2, people.size());
    }

    @Test
    void saleDistinguishesMissingInputEmptyItemsAndMissingResource() {
        assertDoesNotThrow(() -> SaleValidator.validateSaleData(customer, seller, List.of(product)));
        assertThrows(InvalidDataException.class,
                () -> SaleValidator.validateSaleData(null, seller, List.of(product)));
        assertThrows(InvalidDataException.class,
                () -> SaleValidator.validateSaleData(customer, null, List.of(product)));
        assertThrows(InvalidDataException.class,
                () -> SaleValidator.validateSaleData(customer, seller, null));
        assertThrows(InvalidDataException.class,
                () -> SaleValidator.validateSaleData(customer, seller, Arrays.asList((Product) null)));
        assertThrows(BusinessRuleException.class,
                () -> SaleValidator.validateSaleData(customer, seller, List.of()));
        assertThrows(ResourceNotFoundException.class, () -> SaleValidator.validateSaleExists(null, "SALE1"));
        Sale sale = new Sale(LocalDate.of(2026, 10, 1), "SALE1", List.of(product), seller, customer);
        assertDoesNotThrow(() -> SaleValidator.validateSaleExists(sale, "SALE1"));
    }
}
