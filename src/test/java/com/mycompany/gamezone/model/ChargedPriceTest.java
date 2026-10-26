package com.mycompany.gamezone.model;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChargedPriceTest {

    private static final LocalDate DATE = LocalDate.of(2026, 10, 26);

    @Test
    void refundAndReceiptKeepChargedPricesAfterCatalogChanges() {
        Product game = game();
        Product console = console("C1", 200);
        Sale sale = saleWith(List.of(game, console));
        sale.applyPromotion("Promo20", 60);
        Return returned = new Return("R1", DATE.plusDays(1), sale,
                List.of(console, game), "Reason", 15);
        String originalReceipt = returned.generateReturnReceipt();

        game.setPrice(450);
        console.setPrice(800);

        assertEquals(255.0, returned.calculateRefundAmount(), 1e-9);
        assertEquals(originalReceipt, returned.generateReturnReceipt());
        Product otherInstance = console("C1", 999);
        Return registeredLater = new Return("R2", DATE.plusDays(1), sale,
                List.of(otherInstance), "Reason", 15);
        assertEquals(175.0, registeredLater.getRefundAmount(), 1e-9);
        assertEquals(300.0, sale.getSubtotal(), 1e-9);
        assertEquals(240.0, sale.getTotal(), 1e-9);
    }

    @Test
    void warrantyCostAndCertificateKeepTheSoldPriceIncludingAnotherProductInstance() {
        Product console = console("C1", 200);
        Sale sale = saleWith(List.of(console));
        ExtendedWarranty warranty = new ExtendedWarranty("W1", console, sale, DATE);
        String originalCertificate = warranty.generateWarrantyCertificate();

        console.setPrice(900);

        assertEquals(20.0, warranty.getAdditionalCost(), 1e-9);
        assertEquals(originalCertificate, warranty.generateWarrantyCertificate());
        ExtendedWarranty registeredLater = new ExtendedWarranty(
                "W2", console("C1", 999), sale, DATE);
        assertEquals(20.0, registeredLater.getAdditionalCost(), 1e-9);
    }

    @ParameterizedTest
    @CsvSource({"VIDEOGAME, 20", "CONSOLE, 40", "ACCESSORY, 10"})
    void categoryDiscountsKeepChargedPricesForEveryFamily(String category, double expected) {
        Product game = game();
        Product console = console("C1", 200);
        Product controller = new Controller("A1", "Pad", 50, 1, "WIRELESS");
        Sale sale = saleWith(List.of(game, console, controller));
        CategoryDiscount promotion = categoryDiscount(category);

        game.setPrice(500);
        console.setPrice(900);
        controller.setPrice(300);

        assertEquals(expected, promotion.calculateDiscount(sale), 1e-9);
        assertEquals(350.0, sale.getSubtotal(), 1e-9);
        assertEquals(350.0, sale.getTotal(), 1e-9);
    }

    @Test
    void categoryDiscountUsesEachEntryPriceWhenProductIdsRepeat() {
        Product first = console("C1", 200);
        Product second = console("C1", 500);
        Sale sale = saleWith(List.of(first, second, game()));
        first.setPrice(900);
        second.setPrice(800);

        assertEquals(140.0, categoryDiscount("CONSOLE").calculateDiscount(sale), 1e-9);
    }

    @Test
    void unmatchedProductsKeepTheAgreedCurrentPriceFallback() {
        Sale sale = saleWith(List.of(game()));
        sale.applyPromotion("Promo10", 10);
        Product unmatched = console("UNKNOWN", 35);
        Return returned = new Return("R1", DATE.plusDays(1), sale,
                List.of(unmatched), "Reason");
        ExtendedWarranty warranty = new ExtendedWarranty("W1", unmatched, sale, DATE);

        assertEquals(31.5, returned.getRefundAmount(), 1e-9);
        assertEquals(3.5, warranty.getAdditionalCost(), 1e-9);
    }

    private Product game() {
        return new VideoGame("G1", "Game", 100, 1, "PC", "RPG", "E");
    }

    private Product console(String id, double price) {
        return new Console(id, "Console", price, 1, "Sony", "PS5", "9");
    }

    private CategoryDiscount categoryDiscount(String category) {
        return new CategoryDiscount("P1", "Category", LocalDate.of(2020, 1, 1),
                LocalDate.of(2099, 12, 31), 20, category);
    }

    private Sale saleWith(List<Product> products) {
        Customer customer = new Customer("a@b.co", "Ana", "U1", 3001234567L);
        Seller seller = new Seller("EMP1", "MORNING", "Luis", "S1", 3001234568L);
        return new Sale(DATE, "SALE-1", products, seller, customer);
    }
}
