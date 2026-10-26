package com.mycompany.gamezone.factory;

import com.mycompany.gamezone.model.BulkPurchaseDiscount;
import com.mycompany.gamezone.model.CategoryDiscount;
import com.mycompany.gamezone.model.PercentageDiscount;
import com.mycompany.gamezone.model.Promotion;
import com.mycompany.gamezone.validation.PromotionValidator;
import java.time.LocalDate;

/** Creates each promotion type. A new type adds one method here. */
public class PromotionFactory {

    public Promotion createPercentageDiscount(
            String id, String name, LocalDate startDate, LocalDate endDate,
            double percentage) {

        PromotionValidator.validateCommonFields(id, name, startDate, endDate);

        return new PercentageDiscount(id, name, startDate, endDate, percentage);
    }

    public Promotion createCategoryDiscount(
            String id, String name, LocalDate startDate, LocalDate endDate,
            String category, double percentage) {

        PromotionValidator.validateCommonFields(id, name, startDate, endDate);
        PromotionValidator.validateCategory(category);

        return new CategoryDiscount(id, name, startDate, endDate, percentage, category);
    }

    public Promotion createBulkPurchaseDiscount(
            String id, String name, LocalDate startDate, LocalDate endDate,
            int minimumQuantity, double percentage) {

        PromotionValidator.validateCommonFields(id, name, startDate, endDate);
        PromotionValidator.validateMinimumQuantity(minimumQuantity);

        return new BulkPurchaseDiscount(
                id, name, startDate, endDate, minimumQuantity, percentage);
    }
}