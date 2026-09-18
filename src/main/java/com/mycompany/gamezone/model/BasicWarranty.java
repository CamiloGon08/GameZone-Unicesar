package com.mycompany.gamezone.model;

import java.time.LocalDate;
 
public class BasicWarranty extends Warranty {

    /**
     * Creates a new basic warranty starting on the given date.
     *
     * @param id        unique identifier of the warranty
     * @param product   product covered by this warranty
     * @param sale      sale in which this warranty was generated
     * @param startDate date on which the warranty coverage begins
     */
    public BasicWarranty(String id, Product product, Sale sale, LocalDate startDate) {
        super(id, product, sale, startDate);
    }

    
    @Override
    public int getDurationInMonths() {
        return 6;
    }

    
    @Override
    public String getWarrantyType() {
        return "Basic warranty";
    }

    
    @Override
    public double getAdditionalCost() {
        return 0.0;
    }
}