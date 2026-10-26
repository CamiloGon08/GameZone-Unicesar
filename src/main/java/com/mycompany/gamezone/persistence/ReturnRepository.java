package com.mycompany.gamezone.persistence;

import java.util.List;

import com.mycompany.gamezone.model.Return;

/**
 * Stores and loads the returns of the system. The service layer depends on this
 * interface, so the storage can change without changing the services.
 * 
 * @author EstefaniaMarquez
 * 
 */
public interface ReturnRepository {

    /**
     * Saves the full list, replacing what was stored before.
     *
     * @param items the returns to persist
     */
    void saveAll(List<Return> items);

    /**
     * Loads every stored record.
     *
     * @return the stored raw return records, or an empty list if there are none
     */
    List<ReturnData> loadAll();
}
