package com.mycompany.gamezone.model;

import com.mycompany.gamezone.persistence.PromotionRepository;
import com.mycompany.gamezone.service.PromotionService;
import com.mycompany.gamezone.validation.PromotionValidator;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

class PromotionRegistrationTest {
    private static final LocalDate DATE = LocalDate.of(2026, 10, 26);

    static Stream<Arguments> percentageRules() {
        return Stream.of(-1.0, 0.0, 10.0, 100.0, 100.01,
                        Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY)
                .flatMap(value -> Stream.of(
                        new PercentageDiscount("P", "Percent", DATE, DATE, value),
                        new CategoryDiscount("C", "Category", DATE, DATE, value, null),
                        new BulkPurchaseDiscount("B", "Bulk", DATE, DATE, 0, value))
                        .map(promotion -> Arguments.of(promotion, !(value <= 0 || value > 100))));
    }

    @ParameterizedTest
    @MethodSource("percentageRules")
    void neutralQueryKeepsExistingPercentageAndNanBehavior(Promotion promotion, boolean valid) {
        assertEquals(valid, promotion.isValid());
        if (valid) {
            assertDoesNotThrow(() -> PromotionValidator.validatePercentage(promotion));
        } else {
            IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
                    () -> PromotionValidator.validatePercentage(promotion));
            assertEquals("Percentage must be greater than 0 and cannot exceed 100.", failure.getMessage());
        }
    }

    @Test
    void directRegistrationDoesNotAcquireFactoryOnlyRulesAndKeepsErrorOrder() {
        PromotionService service = new PromotionService(new PromotionRepository() {
            public List<Promotion> loadAll() { return List.of(); }
            public void saveAll(List<Promotion> promotions) { }
        });
        CategoryDiscount category = new CategoryDiscount("C", "Category", DATE, DATE, 10, null);
        BulkPurchaseDiscount bulk = new BulkPurchaseDiscount("B", "Bulk", DATE, DATE, 0, 10);
        assertFalse(category.hasTargetCategory());
        assertFalse(bulk.hasValidMinimumQuantity());
        service.register(category);
        service.register(bulk);
        assertEquals(2, service.listAllPromotions().size());
        IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
                () -> service.register(new CategoryDiscount("C", "Duplicate", DATE, DATE, 0, null)));
        assertEquals("Percentage must be greater than 0 and cannot exceed 100.", failure.getMessage());
        assertEquals("A promotion with ID C already exists.",
                assertThrows(IllegalArgumentException.class, () -> service.register(category)).getMessage());
    }

    @Test
    void nonPercentagePromotionCanImplementTheGeneralContract() {
        Promotion fixed = new Promotion("F", "Fixed", DATE, DATE) {
            public double calculateDiscount(Sale sale) { return 5; }
            public boolean isValid() { return true; }
        };
        assertDoesNotThrow(() -> PromotionValidator.validatePercentage(fixed));
        assertTrue(fixed.isValid());
    }
}
