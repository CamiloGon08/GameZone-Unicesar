package com.mycompany.gamezone.service;

import com.mycompany.gamezone.model.BulkPurchaseDiscount;
import com.mycompany.gamezone.model.CategoryDiscount;
import com.mycompany.gamezone.model.PercentageDiscount;
import com.mycompany.gamezone.model.Promotion;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.persistence.PromotionRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for the business logic related to promotions.
 *
 * This service handles the registration, consultation and selection of
 * promotions. It also determines the best applicable promotion for a specific
 * sale according to the discount amount it would provide.
 *
 * @author EstefaniaMarquez
 */
public class PromotionService {

    private final PromotionRepository promotionRepository;

    /**
     * Creates a PromotionService with the repository required for promotion
     * persistence.
     *
     * @param promotionRepository repository used to save and load promotions
     */
    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    /**
     * Registers a new percentage discount promotion.
     *
     * @param promotions current list of promotions
     * @param id unique identifier of the promotion
     * @param name display name of the promotion
     * @param startDate date on which the promotion becomes active
     * @param endDate date on which the promotion stops being active
     * @param percentage discount percentage
     * @throws IllegalArgumentException if the data is invalid or the ID already
     * exists
     */
    public void registerPercentageDiscount(
            List<Promotion> promotions,
            String id,
            String name,
            LocalDate startDate,
            LocalDate endDate,
            double percentage) {

        validateCommonFields(
                id, name, startDate, endDate);

        validatePercentage(percentage);
        validateUniqueId(promotions, id);

        PercentageDiscount promotion = new PercentageDiscount(
                id,
                name,
                startDate,
                endDate,
                percentage);

        promotions.add(promotion);
    }

    /**
     * Registers a new category discount promotion.
     *
     * @param promotions current list of promotions
     * @param id unique identifier of the promotion
     * @param name display name of the promotion
     * @param startDate date on which the promotion becomes active
     * @param endDate date on which the promotion stops being active
     * @param category product category to which the discount applies
     * @param percentage discount percentage
     * @throws IllegalArgumentException if the data is invalid or the ID already
     * exists
     */
    public void registerCategoryDiscount(
            List<Promotion> promotions,
            String id,
            String name,
            LocalDate startDate,
            LocalDate endDate,
            String category,
            double percentage) {

        validateCommonFields(
                id, name, startDate, endDate);

        validateCategory(category);
        validatePercentage(percentage);
        validateUniqueId(promotions, id);

        CategoryDiscount promotion = new CategoryDiscount(
                id,
                name,
                startDate,
                endDate,
                percentage,
                category);

        promotions.add(promotion);
    }

    /**
     * Registers a new bulk purchase discount promotion.
     *
     * @param promotions current list of promotions
     * @param id unique identifier of the promotion
     * @param name display name of the promotion
     * @param startDate date on which the promotion becomes active
     * @param endDate date on which the promotion stops being active
     * @param minimumQuantity minimum number of products required
     * @param percentage discount percentage
     * @throws IllegalArgumentException if the data is invalid or the ID already
     * exists
     */
    public void registerBulkPurchaseDiscount(
            List<Promotion> promotions,
            String id,
            String name,
            LocalDate startDate,
            LocalDate endDate,
            int minimumQuantity,
            double percentage) {

        validateCommonFields(
                id, name, startDate, endDate);

        validateMinimumQuantity(minimumQuantity);
        validatePercentage(percentage);
        validateUniqueId(promotions, id);

        BulkPurchaseDiscount promotion = new BulkPurchaseDiscount(
                id,
                name,
                startDate,
                endDate,
                minimumQuantity,
                percentage);

        promotions.add(promotion);
    }

    /**
     * Returns all promotions registered in the repository.
     *
     * @return list containing all registered promotions
     */
    public List<Promotion> listAllPromotions() {
        return promotionRepository.loadAll();
    }

    /**
     * Returns all promotions that are active on the current date.
     *
     * A promotion is considered active when the current date is between its
     * start date and end date, including both dates.
     *
     * @return list of currently active promotions
     */
    public List<Promotion> listActivePromotions() {

        List<Promotion> promotions = listAllPromotions();
        List<Promotion> activePromotions = new ArrayList<>();

        LocalDate currentDate = LocalDate.now();

        for (Promotion promotion : promotions) {

            if (promotion.isActive(currentDate)) {
                activePromotions.add(promotion);
            }
        }

        return activePromotions;
    }

    /**
     * Finds the best promotion applicable to a specific sale.
     *
     * All currently active promotions are evaluated against the sale. The
     * promotion that produces the highest monetary discount is selected. Only
     * one promotion is returned because promotions are not cumulative.
     *
     * If no promotion applies to the sale, or if the highest discount is zero,
     * the method returns null.
     *
     * @param sale sale for which the best promotion must be determined
     * @return promotion that provides the greatest discount, or null if no
     * applicable promotion exists
     */
    public Promotion findBestPromotionFor(Sale sale) {

        if (sale == null) {
            return null;
        }

        List<Promotion> activePromotions = listActivePromotions();

        Promotion bestPromotion = null;
        double maximumDiscount = 0.0;

        for (Promotion promotion : activePromotions) {

            double discount = promotion.calculateDiscount(sale);

            if (discount > maximumDiscount) {
                maximumDiscount = discount;
                bestPromotion = promotion;
            }
        }

        return bestPromotion;
    }

    /**
     * Finds a promotion using its unique identifier.
     *
     * @param id identifier of the promotion
     * @return promotion with the specified ID, or null if it does not exist
     */
    public Promotion findById(String id) {

        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        List<Promotion> promotions = listAllPromotions();

        for (Promotion promotion : promotions) {

            if (promotion.getId().equals(id)) {
                return promotion;
            }
        }

        return null;
    }

    /**
     * Saves the complete promotion list using the repository.
     *
     * @param promotions list of promotions to persist
     */
    public void saveAll(List<Promotion> promotions) {
        promotionRepository.saveAll(promotions);
    }

    /**
     * Validates the fields shared by all promotion types.
     *
     * @param id promotion identifier
     * @param name promotion name
     * @param startDate promotion start date
     * @param endDate promotion end date
     * @throws IllegalArgumentException if any common field is invalid
     */
    private void validateCommonFields(
            String id,
            String name,
            LocalDate startDate,
            LocalDate endDate) {

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Promotion ID cannot be empty.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Promotion name cannot be empty.");
        }

        if (startDate == null) {
            throw new IllegalArgumentException(
                    "Start date cannot be null.");
        }

        if (endDate == null) {
            throw new IllegalArgumentException(
                    "End date cannot be null.");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date.");
        }
    }

    /**
     * Validates a promotion discount percentage.
     *
     * The percentage must be greater than zero and cannot exceed 100%.
     *
     * @param percentage discount percentage
     * @throws IllegalArgumentException if the percentage is invalid
     */
    private void validatePercentage(double percentage) {

        if (percentage <= 0 || percentage > 100) {
            throw new IllegalArgumentException(
                    "Percentage must be greater than 0 and cannot exceed 100.");
        }
    }

    /**
     * Validates a category used by a category promotion.
     *
     * @param category product category
     * @throws IllegalArgumentException if the category is empty
     */
    private void validateCategory(String category) {

        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Category cannot be empty.");
        }
    }

    /**
     * Validates the minimum quantity required by a bulk purchase promotion.
     *
     * @param minimumQuantity minimum number of products
     * @throws IllegalArgumentException if the quantity is not positive
     */
    private void validateMinimumQuantity(int minimumQuantity) {

        if (minimumQuantity <= 0) {
            throw new IllegalArgumentException(
                    "Minimum quantity must be greater than zero.");
        }
    }

    /**
     * Verifies that a promotion ID is not already being used.
     *
     * @param promotions current list of promotions
     * @param id promotion ID to verify
     * @throws IllegalArgumentException if the ID already exists
     */
    private void validateUniqueId(
            List<Promotion> promotions,
            String id) {

        for (Promotion promotion : promotions) {

            if (promotion.getId().equals(id)) {
                throw new IllegalArgumentException(
                        "A promotion with ID " + id
                        + " already exists.");
            }
        }
    }
}
