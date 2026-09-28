package com.mycompany.gamezone.model;

import java.time.LocalDate;

public class BulkPurchaseDiscount extends Promotion {

    private int minimumQuantity;
    private double percentage;

    /**
     * Creates a new bulk purchase discount with the given shared and specific attributes.
     *
     * @param id              the unique identifier of the promotion
     * @param name            the promotion's display name
     * @param startDate       the date the promotion becomes active
     * @param endDate         the date the promotion stops being active
     * @param minimumQuantity the minimum number of products a sale must include
     * @param percentage      the discount percentage, from 0 to 100
     */
    public BulkPurchaseDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                                 int minimumQuantity, double percentage) {
        super(id, name, startDate, endDate);
        this.minimumQuantity = minimumQuantity;
        this.percentage = percentage;
    }

    
    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    
    public void setMinimumQuantity(int minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

   
    public double getPercentage() {
        return percentage;
    }

    
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    
    @Override
    public double calculateDiscount(Sale sale) {
        if (sale.getProducts().size() >= minimumQuantity) {
            return sale.getTotal() * percentage / 100;
        }
        return 0.0;
    }
}