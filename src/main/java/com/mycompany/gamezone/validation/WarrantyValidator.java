package com.mycompany.gamezone.validation;

import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;

public final class WarrantyValidator {

    private WarrantyValidator() {
    }

    public static void validateWarrantyData(Product product, Sale sale) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        if (sale == null) {
            throw new IllegalArgumentException("Sale cannot be null.");
        }
    }

    public static void validateCancellationData(String productId, String saleId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be empty.");
        }
        if (saleId == null || saleId.trim().isEmpty()) {
            throw new IllegalArgumentException("Sale ID cannot be empty.");
        }
    }
}