package com.mycompany.gamezone.model;

import java.time.LocalDate;


public abstract class Warranty {

    private String id;
    private Product product;
    private Sale sale;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Creates a new warranty starting on the given date. The end date is
     * calculated automatically by adding {@link #getDurationInMonths()}
     * months to the start date, so every subclass gets its own expiration
     * date without duplicating this calculation.
     *
     * @param id        unique identifier of the warranty
     * @param product   product covered by this warranty
     * @param sale      sale in which this warranty was generated
     * @param startDate date on which the warranty coverage begins
     */
    public Warranty(String id, Product product, Sale sale, LocalDate startDate) {
        this.id = id;
        this.product = product;
        this.sale = sale;
        this.startDate = startDate;
        this.endDate = startDate.plusMonths(getDurationInMonths());
    }

   
    public abstract int getDurationInMonths();

    
    public abstract String getWarrantyType();

    /**
     * Returns the stable type token used in persisted warranty records.
     *
     * @return the stored type code, independent of the display label
     */
    public abstract String getTypeCode();

    /**
     * Checks the product and sale IDs using exact, case-sensitive comparisons.
     *
     * @param productId the product ID to match
     * @param saleId the sale ID to match
     * @return whether both IDs match exactly
     */
    public boolean matches(String productId, String saleId) {
        return product.getId().equals(productId) && sale.getId().equals(saleId);
    }

    /**
     * Checks the product and sale IDs using case-insensitive comparisons.
     *
     * @param productId the product ID to match
     * @param saleId the sale ID to match
     * @return whether both IDs match ignoring case
     */
    public boolean matchesIgnoreCase(String productId, String saleId) {
        return product.getId().equalsIgnoreCase(productId) && sale.getId().equalsIgnoreCase(saleId);
    }

    
    public abstract double getAdditionalCost();

    
    public String getId() {
        return id;
    }

    
    public Product getProduct() {
        return product;
    }

    
    public Sale getSale() {
        return sale;
    }

    
    public LocalDate getStartDate() {
        return startDate;
    }

    
    public LocalDate getEndDate() {
        return endDate;
    }

   
    public boolean isActive(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}
