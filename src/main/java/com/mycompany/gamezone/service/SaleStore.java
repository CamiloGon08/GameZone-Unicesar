package com.mycompany.gamezone.service;

import java.util.List;

import com.mycompany.gamezone.model.Sale;

/**
 * Keeps the sales of the system: lists them, adds a new one and counts
 * them. High-level classes depend on this abstraction instead of the
 * concrete catalog.
 */
public interface SaleStore extends SaleLookup {

    /**
     * Returns every sale.
     *
     * @return the list of sales
     */
    List<Sale> findAll();

    /**
     * Adds a new sale and saves it.
     *
     * @param sale the sale to add
     */
    void add(Sale sale);

    /**
     * Returns how many sales exist.
     *
     * @return the number of sales
     */
    int size();
}
