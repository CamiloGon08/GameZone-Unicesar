package com.mycompany.gamezone.service;

import com.mycompany.gamezone.model.Product;

/**
 * Gives access to the stock of one family of sellable items. A new family
 * of items only needs a new handler, without changing {@link InventoryService}.
 */
public interface StockHandler {

    /**
     * Finds an item of this family by its identifier.
     *
     * @param id identifier of the item
     * @return the item, or null if this family has no item with that identifier
     */
    Product findById(String id);

    /**
     * Reports whether the given item belongs to this family.
     *
     * @param item the item to check, possibly null
     * @return true if this handler manages the stock of the item
     */
    boolean handles(Product item);

    /**
     * Removes one unit of stock of the item and saves the change.
     *
     * @param id identifier of the item
     */
    void decrease(String id);

    /**
     * Restores the given quantity of stock of the item and saves the change.
     *
     * @param id       identifier of the item
     * @param quantity quantity to restore
     */
    void restore(String id, int quantity);
}
