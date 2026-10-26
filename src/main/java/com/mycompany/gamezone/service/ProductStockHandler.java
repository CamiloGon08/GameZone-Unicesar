package com.mycompany.gamezone.service;

import com.mycompany.gamezone.model.Console;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.VideoGame;

/** Stock access for video games and consoles, through {@link ProductService}. */
public class ProductStockHandler implements StockHandler {

    private final ProductService productService;

    /**
     * Creates the handler.
     *
     * @param productService service that owns the products
     */
    public ProductStockHandler(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public Product findById(String id) {
        return productService.findById(id);
    }

    @Override
    public boolean handles(Product item) {
        return item instanceof VideoGame || item instanceof Console;
    }

    @Override
    public void decrease(String id) {
        productService.updateStock(id, -1);
    }

    @Override
    public void restore(String id, int quantity) {
        productService.restoreStock(id, quantity);
    }
}
