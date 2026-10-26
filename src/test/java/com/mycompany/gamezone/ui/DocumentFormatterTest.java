package com.mycompany.gamezone.ui;

import com.mycompany.gamezone.model.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class DocumentFormatterTest {
    private static final LocalDate DATE = LocalDate.of(2026, 10, 26);

    @ParameterizedTest
    @ValueSource(strings = {"en-US", "de-DE"})
    void receiptAndCertificateKeepExactLayoutLocaleChargedPricesAndCachedTotal(String tag) {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag(tag));
            Product console = new Console("C", "Console", 200, 2, "Sony", "PS5", "9");
            Sale sale = new Sale(DATE, "S", List.of(console),
                    new Seller("EMP", "DAY", "Luis", "U1", 3001234567L),
                    new Customer("a@b.co", "Ana", "U2", 3001234568L));
            sale.applyPromotion("Promo", 40);
            Return returned = new Return("R", DATE.plusDays(1), sale, List.of(console), "Reason", 15);
            String decimal = tag.equals("de-DE") ? "," : ".";
            String expected = "===== Recibo de Devolucion =====\n"
                    + "Identificador: R\nFecha: 2026-10-27\nVenta original: S\nMotivo: Reason\n"
                    + "Productos devueltos:\n  - Console | Precio de lista: $200" + decimal + "00"
                    + " | Descuento proporcional: $40" + decimal + "00 | Reembolsado: $160" + decimal + "00\n"
                    + "Reembolso por garantias anuladas: $15" + decimal + "00\n"
                    + "Monto total reembolsado: $175" + decimal + "00\n=================================";
            assertEquals(expected, new ReturnReceiptFormatter().format(returned));
            Warranty warranty = new ExtendedWarranty("W", console, sale, DATE);
            String certificate = String.join(System.lineSeparator(), "\"Warranty Certificate",
                    "Type: Extended warranty", "Product: Console", "Start Date: 2026-10-26",
                    "Expiration Date: 2027-10-26", "Additional Cost: $20" + decimal + "00\"");
            console.setPrice(999);
            assertEquals(certificate, new WarrantyCertificateFormatter().format(warranty));
            assertEquals(expected, new ReturnReceiptFormatter().format(returned));
            sale.applyPromotion("Changed", 0);
            String stale = new ReturnReceiptFormatter().format(returned);
            assertTrue(stale.contains("Monto total reembolsado: $175" + decimal + "00"));
            assertEquals(175, returned.getRefundAmount());
        } finally {
            Locale.setDefault(previous);
        }
    }
}
