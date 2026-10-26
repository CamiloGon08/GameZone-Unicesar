package com.mycompany.gamezone.persistence;

import java.util.List;

import com.mycompany.gamezone.model.Warranty;

/**
 * Stores and loads the warranties of the system. The service layer depends on
 * this interface, so the storage can change without changing the services.
 * 
 * @author EstefaniaMarquez
 * 
 */
public interface WarrantyRepository {

    /**
     * Saves the full list, replacing what was stored before.
     *
     * @param items the warranties to persist
     */
    void saveAll(List<Warranty> items);

    /**
     * Loads every stored record.
     *
     * @return the stored raw warranty records, or an empty list if there are
     * none
     */
    List<WarrantyData> loadAll();
}
