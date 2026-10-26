package com.mycompany.gamezone.model;

import java.time.LocalDate;


public class CategoryDiscount extends Promotion {

    private double percentage;
    private String targetCategory;
 
    /**
     * Creates a new category discount with the given shared and specific attributes.
     *
     * @param id             the unique identifier of the promotion
     * @param name           the promotion's display name
     * @param startDate      the date the promotion becomes active
     * @param endDate        the date the promotion stops being active
     * @param percentage     the discount percentage, from 0 to 100
     * @param targetCategory the category this discount applies to
     *                       ("VIDEOGAME", "CONSOLE", or "ACCESSORY")
     */
    public CategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                             double percentage, String targetCategory) {
        super(id, name, startDate, endDate);
        this.percentage = percentage;
        this.targetCategory = targetCategory;
    }
 
    public double getPercentage() {
        return percentage;
    }
 
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
 
    public String getTargetCategory() {
        return targetCategory;
    }
 
    public void setTargetCategory(String targetCategory) {
        this.targetCategory = targetCategory;
    }
 
    /**
     * Calculates the discount by summing the price of every product in the
     * sale that matches this promotion's target category, and applying the
     * discount percentage to that sum. Each product reports category membership;
     * Controller, Cable and Memory inherit the ACCESSORY family policy.
     *
     * @param sale the sale to calculate the discount for
     * @return the calculated discount amount
     */
    @Override
    public double calculateDiscount(Sale sale) {
        double matchingTotal = 0.0;
        for (Product product : sale.getProducts()) {
            if (product != null && product.matchesCategory(targetCategory)) {
                matchingTotal += product.getPrice();
            }
        }
        return matchingTotal * percentage / 100;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasValidPercentage() {
        return isValidPercentage(percentage);
    }

    /**
     * Reports whether the target category passes the existing registration rule.
     *
     * @return whether the category contains text after trimming
     */
    public boolean hasTargetCategory() {
        return targetCategory != null && !targetCategory.trim().isEmpty();
    }
}
