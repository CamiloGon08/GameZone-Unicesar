package com.mycompany.gamezone.persistence;

import java.util.List;

import com.mycompany.gamezone.model.Accessory;

/**
 * Stores and loads the accessories of the system. The service layer depends on
 * this interface, so the storage can change without changing the services.
 * 
 * @author EstefaniaMarquez
 * 
 */
public interface AccessoryRepository {

    /**
     * Saves the full list, replacing what was stored before.
     *
     * @param items the accessories to persist
     */
    void saveAll(List<Accessory> items);

    /**
     * Loads every stored record.
     *
     * @return the stored accessories, or an empty list if there are none
     */
    List<Accessory> loadAll();
}
