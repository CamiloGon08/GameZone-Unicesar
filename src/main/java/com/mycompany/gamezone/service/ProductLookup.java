package com.mycompany.gamezone.service;

import com.mycompany.gamezone.model.Product;

/**
 * Finds a product by its identifier. Classes that only need to look up
 * products depend on this interface instead of the whole product service.
 */
@FunctionalInterface
public interface ProductLookup {

    /**
     * Finds a product using its identifier.
     *
     * @param id identifier of the product
     * @return the product, or null if it does not exist
     */
    Product findById(String id);
}
