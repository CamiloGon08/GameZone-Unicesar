package com.gamezone.service;

import com.mycompany.gamezone.model.Console;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.VideoGame;
import com.mycompany.gamezone.persistence.ProductRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the business rules for managing products: registration, listing,
 * and inventory stock updates. This is the only class of the product module
 * authorized to invoke {@link ProductRepository}; the UI layer must go
 * through this service and never access persistence directly.
 */
public class ProductService {

    private final ProductRepository productRepository;
    private final List<Product> products;

    /**
     * Creates the service and loads any products already stored in the
     * .txt file managed by the given repository.
     *
     * @param productRepository repository used to persist and load products
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.products = new ArrayList<>(productRepository.loadAll());
    }

    /**
     * Registers a new video game and saves the updated catalog to disk.
     *
     * @param id        unique identifier of the product
     * @param title     display title of the product
     * @param price     unit price of the product
     * @param stock     initial quantity available in inventory
     * @param platform  platform the game was developed for
     * @param genre     genre of the game
     * @param ageRating recommended age rating of the game
     */
    public void registerVideoGame(String id, String title, double price, int stock,
                                   String platform, String genre, String ageRating) {
        VideoGame videoGame = new VideoGame(id, title, price, stock, platform, genre, ageRating);
        products.add(videoGame);
        productRepository.saveAll(products);
    }

    /**
     * Registers a new console and saves the updated catalog to disk.
     *
     * @param id         unique identifier of the product
     * @param title      display title of the product
     * @param price      unit price of the product
     * @param stock      initial quantity available in inventory
     * @param brand      manufacturer brand of the console
     * @param model      specific model name of the console
     * @param generation hardware generation of the console
     */
    public void registerConsole(String id, String title, double price, int stock,
                                 String brand, String model, String generation) {
        Console console = new Console(id, title, price, stock, brand, model, generation);
        products.add(console);
        productRepository.saveAll(products);
    }

    /**
     * Lists every product currently in the inventory.
     *
     * @return a copy of the list of registered products
     */
    public List<Product> listProducts() {
        return new ArrayList<>(products);
    }

    /**
     * Looks for a product by its unique identifier.
     *
     * @param id the identifier to search for
     * @return the matching product, or {@code null} if no product with that
     *         id is registered
     */
    public Product findById(String id) {
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }

    /**
     * Checks whether there is enough stock of a product to cover a
     * requested quantity.
     *
     * @param id       the identifier of the product
     * @param quantity the quantity being requested
     * @return true if the product exists and has enough stock, false otherwise
     */
    public boolean hasSufficientStock(String id, int quantity) {
        Product product = findById(id);
        if (product == null) {
            return false;
        }
        return product.getStock() >= quantity;
    }

    /**
     * Adjusts the stock of a product and saves the change to disk. Intended
     * to be called with a negative amount by the sales module when a sale
     * is registered, or with a positive amount to restock inventory. The
     * actual change to the stock value is delegated to
     * {@link Product#adjustStock(int)} so the product stays in charge of
     * its own state.
     *
     * @param id     the identifier of the product
     * @param amount the quantity to add to the current stock; negative
     *               values decrease it
     * @throws IllegalArgumentException if no product with that id exists
     */
    public void updateStock(String id, int amount) {
        Product product = findById(id);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + id);
        }
        product.adjustStock(amount);
        productRepository.saveAll(products);
    }
}