package com.mycompany.gamezone.persistence;

import java.util.List;

import com.mycompany.gamezone.model.Product;

/**
 * Stores and loads the products of the system. The service layer depends on
 * this interface, so the storage can change without changing the services.
 */
public interface ProductRepository {

    /**
     * Saves the full list of products, replacing what was stored before.
     *
     * @param products the products to persist
     * @throws com.mycompany.gamezone.exceptions.PersistenceException if the
     *         products cannot be saved
     */
    void saveAll(List<Product> products);

    /**
     * Loads every stored product.
     *
     * @return the stored products, or an empty list if there are none
     * @throws com.mycompany.gamezone.exceptions.PersistenceException if the
     *         products cannot be loaded
     */
    List<Product> loadAll();
}
