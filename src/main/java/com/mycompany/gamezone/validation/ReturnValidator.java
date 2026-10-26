package com.mycompany.gamezone.validation;

import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;
import java.time.LocalDate;
import java.util.List;

public final class ReturnValidator {

    private ReturnValidator() {
    }

    public static void validateReturnRequest(
            String saleId, List<String> productIds, String reason) {

        if (saleId == null || saleId.trim().isEmpty()) {
            throw new IllegalArgumentException("The sale ID cannot be empty.");
        }
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "The return must contain at least one product.");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("The return reason cannot be empty.");
        }
    }

    public static void validateSaleExists(Sale sale) {
        if (sale == null) {
            throw new IllegalArgumentException("The specified sale does not exist.");
        }
    }

    /** The single return-window rule lives in Sale; the evaluation date is explicit. */
    public static void validateReturnWindow(Sale sale, LocalDate evaluationDate) {
        if (!sale.canBeReturned(evaluationDate)) {
            throw new IllegalArgumentException(
                    "The return cannot be processed because more than "
                    + "30 calendar days have passed since the sale.");
        }
    }

    public static void validateProductExists(Product product, String productId) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "The specified product does not exist: " + productId);
        }
    }

    public static void validateProductBelongsToSale(Sale sale, String productId) {
        if (!sale.containsProduct(productId)) {
            throw new IllegalArgumentException(
                    "The product " + productId
                    + " does not belong to the original sale.");
        }
    }

    public static void validateMonth(int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("The month must be between 1 and 12.");
        }
    }
}