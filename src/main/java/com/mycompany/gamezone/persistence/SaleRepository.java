package com.mycompany.gamezone.persistence;

import java.util.List;

import com.mycompany.gamezone.model.Sale;

/**
 * Stores and loads the sales of the system. The service layer depends on
 * this interface, so the storage can change without changing the services.
 */
public interface SaleRepository {

    /**
     * Saves the full list of sales, replacing what was stored before.
     *
     * @param sales the sales to persist
     * @throws com.mycompany.gamezone.exceptions.PersistenceException if the
     *         sales cannot be saved
     */
    void save(List<Sale> sales);

    /**
     * Loads the raw records of every stored sale.
     *
     * @return the stored records, or an empty list if there are none
     * @throws com.mycompany.gamezone.exceptions.PersistenceException if the
     *         sales cannot be loaded
     */
    List<SaleRecord> loadAll();
}
