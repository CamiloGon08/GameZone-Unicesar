package com.mycompany.gamezone.persistence;

import java.util.List;

import com.mycompany.gamezone.model.Promotion;

/**
 * Stores and loads the promotions of the system. The service layer depends on
 * this interface, so the storage can change without changing the services.
 * 
 * @author EstefaniaMarquez
 * 
 */
public interface PromotionRepository {

    /**
     * Saves the full list, replacing what was stored before.
     *
     * @param items the promotions to persist
     */
    void saveAll(List<Promotion> items);

    /**
     * Loads every stored record.
     *
     * @return the stored promotions, or an empty list if there are none
     */
    List<Promotion> loadAll();
}
