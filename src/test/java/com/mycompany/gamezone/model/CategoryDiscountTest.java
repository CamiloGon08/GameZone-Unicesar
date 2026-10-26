package com.mycompany.gamezone.model;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryDiscountTest {

    @ParameterizedTest
    @CsvSource({"VIDEOGAME, 20", "CONSOLE, 40", "ACCESSORY, 18"})
    void mixedSaleDiscountsOnlyTheSelectedFamily(String category, double expectedDiscount) {
        Sale sale = saleWith(List.of(
                videoGame(),
                new Console("C1", "Console", 200, 1, "Sony", "PS5", "9"),
                new Controller("CT1", "Pad", 50, 1, "WIRELESS"),
                new Cable("CB1", "Cable", 10, 1, 1.5, "HDMI"),
                new Memory("M1", "Card", 30, 1, 64, "SSD"),
                unclassifiedProduct()));

        assertEquals(expectedDiscount, discountFor(category, 20).calculateDiscount(sale), 1e-9);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"videogame", "Console", "accessory", " CONSOLE ", "UNKNOWN"})
    void unmatchedCategoryTextDoesNotChangeTheSale(String category) {
        Sale sale = saleWith(List.of(videoGame(),
                new Console("C1", "Console", 200, 1, "Sony", "PS5", "9"),
                new Controller("CT1", "Pad", 50, 1, "WIRELESS"),
                unclassifiedProduct()));
        double subtotalBefore = sale.getSubtotal();
        double totalBefore = sale.getTotal();

        assertEquals(0.0, discountFor(category, 20).calculateDiscount(sale));
        assertEquals(subtotalBefore, sale.getSubtotal());
        assertEquals(totalBefore, sale.getTotal());
    }

    @Test
    void repeatedProductsKeepTheirPerEntryDiscount() {
        Product game = videoGame();
        Sale sale = saleWith(List.of(game, game,
                new Console("C1", "Console", 200, 1, "Sony", "PS5", "9")));

        assertEquals(20.0, discountFor("VIDEOGAME", 10).calculateDiscount(sale), 1e-9);
    }

    @Test
    void aNewFamilyCanParticipateWithoutChangingTheDiscountPolicy() {
        Product book = new Product("B1", "Book", 80, 1) {
            @Override
            public String getDescription() {
                return getTitle();
            }

            @Override
            public boolean matchesCategory(String category) {
                return "BOOK".equals(category);
            }
        };

        assertEquals(12.0, discountFor("BOOK", 15)
                .calculateDiscount(saleWith(List.of(book, videoGame()))), 1e-9);
    }

    private Product videoGame() {
        return new VideoGame("G1", "Game", 100, 1, "PC", "RPG", "E");
    }

    private Product unclassifiedProduct() {
        return new Product("X1", "Other", 999, 1) {
            @Override
            public String getDescription() {
                return getTitle();
            }
        };
    }

    private CategoryDiscount discountFor(String category, double percentage) {
        return new CategoryDiscount("P1", "Category", LocalDate.of(2020, 1, 1),
                LocalDate.of(2099, 12, 31), percentage, category);
    }

    private Sale saleWith(List<Product> products) {
        Customer customer = new Customer("a@b.co", "Ana", "U1", 3001234567L);
        Seller seller = new Seller("EMP1", "MORNING", "Luis", "S1", 3001234568L);
        return new Sale(LocalDate.of(2026, 10, 26), "SALE-1", products, seller, customer);
    }
}
