package com.mycompany.gamezone.service;

import java.util.List;

import com.mycompany.gamezone.model.Accessory;
import com.mycompany.gamezone.model.Product;

/**
 * Service that gives a single entry point to the stock of every sellable
 * item, whether it is a product (video game or console) or an accessory.
 * Clients no longer decide which service owns an item: this class routes
 * each operation to {@link ProductService} or {@link AccessoryService}.
 */
public class InventoryService {

    private final ProductService productService;
    private final AccessoryService accessoryService;

    /**
     * Creates an InventoryService that delegates to the product and
     * accessory services.
     *
     * @param productService   service that manages products
     * @param accessoryService service that manages accessories
     */
    public InventoryService(ProductService productService,
                            AccessoryService accessoryService) {
        this.productService = productService;
        this.accessoryService = accessoryService;
    }

    /**
     * Finds a sellable item by its identifier, looking first among the
     * products and then among the accessories.
     *
     * @param id identification number of the item
     * @return the matching product or accessory, or null if it does not exist
     */
    public Product findAnyById(String id) {
        Product product = productService.findById(id);
        if (product == null) {
            product = accessoryService.findById(id);
        }
        return product;
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
            if (item instanceof Accessory) {
                accessoryService.updateStock(item.getId(), -1);
            } else {
                productService.updateStock(item.getId(), -1);
            }
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
        if (findAnyById(itemId) instanceof Accessory) {
            accessoryService.restoreStock(itemId, quantity);
        } else {
            productService.restoreStock(itemId, quantity);
        }
    }
}
