package com.mycompany.gamezone.model;

import java.time.LocalDate;

public abstract class Promotion {

    private String id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Creates a new promotion with the given identifying and vigency data.
     *
     * @param id        the unique identifier of the promotion
     * @param name      the promotion's display name
     * @param startDate the date the promotion becomes active
     * @param endDate   the date the promotion stops being active
     */
    public Promotion(String id, String name, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    
    public String getId() {
        return id;
    }

    
    public void setId(String id) {
        this.id = id;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public LocalDate getStartDate() {
        return startDate;
    }

    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    
    public LocalDate getEndDate() {
        return endDate;
    }

    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

           
    public boolean isActive(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
    
    public abstract double calculateDiscount(Sale sale);

    /**
     * Reports whether this promotion passes the existing percentage check.
     * Construction remains permissive so previously stored records can be loaded.
     *
     * @return whether the percentage passes the registration rule
     */
    public abstract boolean hasValidPercentage();

    /**
     * Preserves the service's original rejection rule, including its NaN behavior.
     *
     * @param percentage the percentage to check
     * @return whether the original service would accept the percentage
     */
    protected static boolean isValidPercentage(double percentage) {
        return !(percentage <= 0 || percentage > 100);
    }
}
