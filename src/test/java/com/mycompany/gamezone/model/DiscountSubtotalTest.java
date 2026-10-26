package com.mycompany.gamezone.model;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscountSubtotalTest {

    @ParameterizedTest
    @MethodSource("subtotalDiscounts")
    void discountQueriesStayStableWhenPromotionAndWarrantyChangeTheTotal(Promotion promotion) {
        Sale sale = saleWith(List.of(
                new VideoGame("G1", "Game", 100, 1, "PC", "RPG", "E"),
                new Console("C1", "Console", 200, 1, "Sony", "PS5", "9")));

        assertEquals(60.0, promotion.calculateDiscount(sale), 1e-9);
        sale.applyPromotion("Selected", 60);
        assertEquals(60.0, promotion.calculateDiscount(sale), 1e-9);
        assertEquals(240.0, sale.getTotal(), 1e-9);

        sale.addWarrantyCost(30);
        assertEquals(60.0, promotion.calculateDiscount(sale), 1e-9);
        assertEquals(60.0, promotion.calculateDiscount(sale), 1e-9);
        assertEquals(300.0, sale.getSubtotal(), 1e-9);
        assertEquals(270.0, sale.getTotal(), 1e-9);
        assertEquals("Selected", sale.getAppliedPromotionName());
        assertEquals(60.0, sale.getDiscountAmount(), 1e-9);
    }

    @Test
    void bulkThresholdStillCountsPurchasedEntriesIncludingRepeatedProducts() {
        Product game = new VideoGame("G1", "Game", 100, 1, "PC", "RPG", "E");
        BulkPurchaseDiscount promotion = new BulkPurchaseDiscount("B1", "Bulk",
                LocalDate.of(2020, 1, 1), LocalDate.of(2099, 12, 31), 2, 20);
        Sale belowThreshold = saleWith(List.of(game));
        belowThreshold.addWarrantyCost(500);
        Sale atThreshold = saleWith(List.of(game, game));
        atThreshold.applyPromotion("Previous", 20);
        atThreshold.addWarrantyCost(50);

        assertEquals(0.0, promotion.calculateDiscount(belowThreshold));
        assertEquals(40.0, promotion.calculateDiscount(atThreshold), 1e-9);
        assertEquals(600.0, belowThreshold.getTotal(), 1e-9);
        assertEquals(230.0, atThreshold.getTotal(), 1e-9);
    }

    private static Stream<Promotion> subtotalDiscounts() {
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end = LocalDate.of(2099, 12, 31);
        return Stream.of(
                new PercentageDiscount("P1", "Percentage", start, end, 20),
                new BulkPurchaseDiscount("B1", "Bulk", start, end, 2, 20));
    }

    private Sale saleWith(List<Product> products) {
        Customer customer = new Customer("a@b.co", "Ana", "U1", 3001234567L);
        Seller seller = new Seller("EMP1", "MORNING", "Luis", "S1", 3001234568L);
        return new Sale(LocalDate.of(2026, 10, 26), "SALE-1", products, seller, customer);
    }
}
