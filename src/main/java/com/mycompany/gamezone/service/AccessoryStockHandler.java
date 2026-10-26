package com.mycompany.gamezone.service;

import com.mycompany.gamezone.model.Accessory;
import com.mycompany.gamezone.model.Product;

/** Stock access for accessories, through {@link AccessoryService}. */
public class AccessoryStockHandler implements StockHandler {

    private final AccessoryService accessoryService;

    /**
     * Creates the handler.
     *
     * @param accessoryService service that owns the accessories
     */
    public AccessoryStockHandler(AccessoryService accessoryService) {
        this.accessoryService = accessoryService;
    }

    @Override
    public Product findById(String id) {
        return accessoryService.findById(id);
    }

    @Override
    public boolean handles(Product item) {
        return item instanceof Accessory;
    }

    @Override
    public void decrease(String id) {
        accessoryService.updateStock(id, -1);
    }

    @Override
    public void restore(String id, int quantity) {
        accessoryService.restoreStock(id, quantity);
    }
}
