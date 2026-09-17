package com.gamezone.model;

import java.time.LocalDate;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.Promotion;


public class PercentageDiscount extends Promotion {

    private double percentage;

    /**
     * Creates a new percentage discount with the given shared and specific attributes.
     *
     * @param id         the unique identifier of the promotion
     * @param name       the promotion's display name
     * @param startDate  the date the promotion becomes active
     * @param endDate    the date the promotion stops being active
     * @param percentage the discount percentage, from 0 to 100
     */
    public PercentageDiscount(String id, String name, LocalDate startDate, LocalDate endDate, double percentage) {
        super(id, name, startDate, endDate);
        this.percentage = percentage;
    }

    
    public double getPercentage() {
        return percentage;
    }


    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

   
    @Override
    public double calculateDiscount(Sale sale) {
        return sale.getTotalAmount() * percentage / 100;
    }
}