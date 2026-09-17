package com.gamezone.model;

import java.time.LocalDate;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;

public class ExtendedWarranty extends Warranty {

    private static final double EXTENDED_COST_RATE = 0.10;

    /**
     * Creates a new extended warranty starting on the given date.
     *
     * @param id        unique identifier of the warranty
     * @param product   product covered by this warranty
     * @param sale      sale in which this warranty was generated
     * @param startDate date on which the warranty coverage begins
     */
    public ExtendedWarranty(String id, Product product, Sale sale, LocalDate startDate) {
        super(id, product, sale, startDate);
    }

    
    @Override
    public int getDurationInMonths() {
        return 12;
    }

    
    @Override
    public String getWarrantyType() {
        return "Garantia Extendida";
    }

    
    @Override
    public double getAdditionalCost() {
        return getProduct().getPrice() * EXTENDED_COST_RATE;
    }
}