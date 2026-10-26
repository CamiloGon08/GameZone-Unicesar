package com.mycompany.gamezone.service;

import java.util.List;

import com.mycompany.gamezone.model.Product;

/**
 * Service that gives a single entry point to the stock of every sellable
 * item, whether it is a product (video game or console) or an accessory.
 * Each family of items is reached through a {@link StockHandler}, so a new
 * family only adds a handler and this class does not change.
 */
public class InventoryService {

    private final List<StockHandler> handlers;

    /**
     * Creates an InventoryService that delegates to the given handlers. The
     * first handler is also used when no handler recognizes an item.
     *
     * @param handlers one handler per family, in search order
     */
    public InventoryService(List<StockHandler> handlers) {
        this.handlers = List.copyOf(handlers);
    }

    /**
     * Finds a sellable item by its identifier, asking each handler in order.
     *
     * @param id identification number of the item
     * @return the matching item, or null if it does not exist
     */
    public Product findAnyById(String id) {
        for (StockHandler handler : handlers) {
            Product item = handler.findById(id);
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    /**
     * Removes one unit of stock for each item of the list, saving the
     * inventory of each item after the change.
     *
     * @param items items whose stock will be decreased
     * @throws com.mycompany.gamezone.exceptions.ResourceNotFoundException if an
     *         item no longer exists
     * @throws com.mycompany.gamezone.exceptions.PersistenceException if the
     *         inventory cannot be saved
     */
    public void decreaseStock(List<Product> items) {
        for (Product item : items) {
            handlerFor(item).decrease(item.getId());
        }
    }

    /**
     * Restores the given quantity of stock for the item with the given
     * identifier.
     *
     * @param itemId   identifier of the item whose stock will be restored
     * @param quantity quantity of stock to restore
     * @throws com.mycompany.gamezone.exceptions.ResourceNotFoundException if
     *         the item no longer exists
     * @throws com.mycompany.gamezone.exceptions.PersistenceException if the
     *         inventory cannot be saved
     */
    public void increaseStock(String itemId, int quantity) {
        handlerFor(findAnyById(itemId)).restore(itemId, quantity);
    }

    private StockHandler handlerFor(Product item) {
        for (StockHandler handler : handlers) {
            if (handler.handles(item)) {
                return handler;
            }
        }
        return handlers.get(0);
    }
}
