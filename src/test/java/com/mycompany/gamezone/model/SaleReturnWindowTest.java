package com.mycompany.gamezone.model;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SaleReturnWindowTest {

    @ParameterizedTest
    @CsvSource({
        "2026-10-01, 2026-09-30, false",
        "2026-10-01, 2026-10-01, true",
        "2026-10-01, 2026-10-30, true",
        "2026-10-01, 2026-10-31, true",
        "2026-10-01, 2026-11-01, false",
        "2026-12-15, 2027-01-14, true",
        "2026-12-15, 2027-01-15, false",
        "2024-02-01, 2024-03-02, true",
        "2024-02-01, 2024-03-03, false"
    })
    void returnWindowUsesTheExplicitDateAcrossCalendarBoundaries(
            String saleDate, String evaluationDate, boolean expected) {
        Sale sale = new Sale(LocalDate.parse(saleDate), "SALE-1",
                List.of(new VideoGame("G1", "Game", 100, 1, "PC", "RPG", "E")),
                new Seller("EMP1", "MORNING", "Luis", "S1", 3001234568L),
                new Customer("a@b.co", "Ana", "U1", 3001234567L));

        assertEquals(expected, sale.canBeReturned(LocalDate.parse(evaluationDate)));
    }
}
