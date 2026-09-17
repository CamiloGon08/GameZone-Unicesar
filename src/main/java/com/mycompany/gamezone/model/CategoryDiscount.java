package com.gamezone.model;

import java.time.LocalDate;
import com.mycompany.gamezone.model.Promotion;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.VideoGame;
import com.mycompany.gamezone.model.Console;


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
     * @param targetCategory the category this discount applies to ("VIDEOGAME" or "CONSOLE")
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

    
    @Override
    public double calculateDiscount(Sale sale) {
        double matchingTotal = 0.0;
        for (Product product : sale.getProducts()) {
            if ("VIDEOGAME".equals(targetCategory) && product instanceof VideoGame) {
                matchingTotal += product.getPrice();
            } else if ("CONSOLE".equals(targetCategory) && product instanceof Console) {
                matchingTotal += product.getPrice();
            }
        }
        return matchingTotal * percentage / 100;
    }
}