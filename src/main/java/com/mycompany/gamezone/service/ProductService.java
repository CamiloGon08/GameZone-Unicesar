package com.mycompany.gamezone.service;

import com.mycompany.gamezone.exceptions.BusinessRuleException;
import com.mycompany.gamezone.exceptions.InvalidDataException;
import com.mycompany.gamezone.model.Console;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.VideoGame;
import com.mycompany.gamezone.persistence.ProductRepository;
import com.mycompany.gamezone.validation.ProductValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Business rules for products. Data and existence checks are delegated to
 * {@link ProductValidator}; every failure is reported with a typed
 * exception from the GameZone exception hierarchy.
 * 
 * @author EstefaniaMarquez
 * 
 */

public class ProductService {

    private final ProductRepository productRepository;
    private final List<Product> products;

    /**
     * Creates the service and loads the stored products.
     *
     * @param productRepository repository used to persist and load products
     * @throws InvalidDataException if the repository is null
     * @throws com.mycompany.gamezone.exceptions.PersistenceException if the
     *         stored products cannot be read
     */
    public ProductService(ProductRepository productRepository) {
        if (productRepository == null) {
            throw new InvalidDataException("repositorio de productos", "no puede ser nulo.");
        }
        this.productRepository = productRepository;
        this.products = new ArrayList<>(productRepository.loadAll());
    }

    /**
     * Registers a new video game and persists the product list.
     *
     * @param id        product identifier
     * @param title     video game title
     * @param price     unit price, greater than zero
     * @param stock     units available, not negative
     * @param platform  platform the game runs on
     * @param genre     game genre
     * @param ageRating age rating of the game
     * @throws InvalidDataException if any field is empty or out of range
     * @throws BusinessRuleException if the identifier is already registered
     * @throws com.mycompany.gamezone.exceptions.PersistenceException if the
     *         product list cannot be saved
     */
    public void registerVideoGame(String id, String title, double price, int stock,
                                   String platform, String genre, String ageRating) {
        ProductValidator.validateProductData(id, title, price, stock);
        requireUniqueId(id);
        requireNotBlank("plataforma", platform);
        requireNotBlank("género", genre);
        requireNotBlank("clasificación de edad", ageRating);

        VideoGame videoGame = new VideoGame(id, title, price, stock, platform, genre, ageRating);
        products.add(videoGame);
        productRepository.saveAll(products);
    }

    /**
     * Registers a new console and persists the product list.
     *
     * @param id         product identifier
     * @param title      console title
     * @param price      unit price, greater than zero
     * @param stock      units available, not negative
     * @param brand      console brand
     * @param model      console model
     * @param generation console generation
     * @throws InvalidDataException if any field is empty or out of range
     * @throws BusinessRuleException if the identifier is already registered
     * @throws com.mycompany.gamezone.exceptions.PersistenceException if the
     *         product list cannot be saved
     */
    public void registerConsole(String id, String title, double price, int stock,
                                 String brand, String model, String generation) {
        ProductValidator.validateProductData(id, title, price, stock);
        requireUniqueId(id);
        requireNotBlank("marca", brand);
        requireNotBlank("modelo", model);
        requireNotBlank("generación", generation);

        Console console = new Console(id, title, price, stock, brand, model, generation);
        products.add(console);
        productRepository.saveAll(products);
    }

    /**
     * Returns a copy of the registered products.
     *
     * @return list with every product
     */
    public List<Product> listProducts() {
        return new ArrayList<>(products);
    }

    /**
     * Finds a product by identifier, ignoring case and surrounding spaces.
     *
     * @param id product identifier
     * @return the product, or null if the identifier is empty or no product
     *         matches
     */
    public Product findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        for (Product product : products) {
            if (product.getId().equalsIgnoreCase(id.trim())) {
                return product;
            }
        }
        return null;
    }

    /**
     * Checks whether a product has enough units for the requested quantity.
     *
     * @param id       product identifier
     * @param quantity requested units
     * @return true if the quantity is positive, the product exists and its
     *         stock covers the quantity
     */
    public boolean hasSufficientStock(String id, int quantity) {
        if (quantity <= 0) {
            return false;
        }
        Product product = findById(id);
        if (product == null) {
            return false;
        }
        return product.getStock() >= quantity;
    }

    /**
     * Adds or removes units of a product and persists the product list.
     *
     * @param id     product identifier
     * @param amount units to add (positive) or remove (negative)
     * @throws InvalidDataException if the identifier is empty
     * @throws com.mycompany.gamezone.exceptions.ResourceNotFoundException if
     *         the product does not exist
     * @throws BusinessRuleException if the change would leave negative stock
     * @throws com.mycompany.gamezone.exceptions.PersistenceException if the
     *         product list cannot be saved
     */
    public void updateStock(String id, int amount) {
        requireProductId(id);
        Product product = findById(id);
        ProductValidator.validateProductExists(product, id);
        if (amount < 0) {
            int unitsToRemove = (int) Math.min(Integer.MAX_VALUE, -(long) amount);
            ProductValidator.validateStockAvailability(product, unitsToRemove);
        }

        product.adjustStock(amount);
        productRepository.saveAll(products);
    }

    /**
     * Restores stock for a product after a successful return.
     *
     * @param id       identification number of the product
     * @param quantity quantity of stock to restore
     * @throws InvalidDataException if the identifier is empty or the quantity
     *         is not greater than zero
     * @throws com.mycompany.gamezone.exceptions.ResourceNotFoundException if
     *         the product does not exist
     * @throws com.mycompany.gamezone.exceptions.PersistenceException if the
     *         product list cannot be saved
     */
    public void restoreStock(String id, int quantity) {
        requireProductId(id);

        if (quantity <= 0) {
            throw new InvalidDataException("cantidad", "debe ser mayor que cero.");
        }

        Product product = findById(id);
        ProductValidator.validateProductExists(product, id);

        product.adjustStock(quantity);
        productRepository.saveAll(products);
    }

    /**
     * Ensures that no registered product uses the given identifier.
     *
     * @param id identifier about to be registered
     * @throws BusinessRuleException if the identifier is already registered
     */
    private void requireUniqueId(String id) {
        if (findById(id) != null) {
            throw new BusinessRuleException(
                    "Ya existe un producto con el identificador " + id.trim() + ".");
        }
    }

    /**
     * Ensures that a product identifier was provided.
     *
     * @param id identifier to check
     * @throws InvalidDataException if the identifier is null or blank
     */
    private void requireProductId(String id) {
        requireNotBlank("identificador", id);
    }

    /**
     * Ensures that a text field contains something other than whitespace.
     * Used for the subtype-specific fields that ProductValidator does not cover.
     *
     * @param field name of the field, used in the error message
     * @param value value to check
     * @throws InvalidDataException if the value is null or blank
     */
    private void requireNotBlank(String field, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidDataException(field, "no puede estar vacío.");
        }
    }
}