package com.mycompany.gamezone.validation;

import com.mycompany.gamezone.model.Promotion;
import java.time.LocalDate;
import java.util.List;

public final class PromotionValidator {

    private PromotionValidator() {
    }

    public static void validateCommonFields(
            String id, String name, LocalDate startDate, LocalDate endDate) {

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Promotion ID cannot be empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Promotion name cannot be empty.");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null.");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null.");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }
    }

    public static void validateCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty.");
        }
    }

    public static void validateMinimumQuantity(int minimumQuantity) {
        if (minimumQuantity <= 0) {
            throw new IllegalArgumentException(
                    "Minimum quantity must be greater than zero.");
        }
    }

    /** The promotion decides if its percentage is valid; this only reports it. */
    public static void validatePercentage(Promotion promotion) {
        if (!promotion.isValid()) {
            throw new IllegalArgumentException(
                    "Percentage must be greater than 0 and cannot exceed 100.");
        }
    }

    public static void validateUniqueId(List<Promotion> promotions, String id) {
        for (Promotion promotion : promotions) {
            if (promotion.getId().equals(id)) {
                throw new IllegalArgumentException(
                        "A promotion with ID " + id + " already exists.");
            }
        }
    }
}