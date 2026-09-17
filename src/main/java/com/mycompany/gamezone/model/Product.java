package com.mycompany.gamezone.model;

/**
 * Abstract base class representing a product commercialized by GameZone Unicesar.
 * Holds the attributes and behavior common to every product type (video games,
 * consoles, etc.). Concrete product types must extend this class and provide
 * their own implementation of {@link #getDescription()}.
 */
public abstract class Product {

    private String id;
    private String title;
    private double price;
    private int stock;

    /**
     * Creates a new product with the given common attributes.
     *
     * @param id    unique identifier of the product
     * @param title display title of the product
     * @param price unit price of the product
     * @param stock quantity currently available in inventory
     */
    public Product(String id, String title, double price, int stock) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.stock = stock;
    }

    /**
     * Returns a full description of the product, integrating the particular
     * characteristics of each concrete product type. Every subclass must
     * implement this method on its own.
     *
     * @return a human-readable description of the product
     */
    public abstract String getDescription();

    /**
     * @return the unique identifier of the product
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the new unique identifier of the product
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the display title of the product
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the new display title of the product
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the unit price of the product
     */
    public double getPrice() {
        return price;
    }

    /**
     * @param price the new unit price of the product
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * @return the quantity currently available in inventory
     */
    public int getStock() {
        return stock;
    }

    /**
     * @param stock the new quantity available in inventory
     */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /**
     * Applies a change to the current inventory level. A negative amount
     * represents units leaving the inventory (for example, after a sale),
     * while a positive amount represents units being added back in (for
     * example, a restock). Keeping this operation on the product itself,
     * instead of letting external classes recompute and overwrite the
     * stock value directly, keeps the product responsible for its own state.
     *
     * @param amount the quantity to add to the current stock; negative
     *               values decrease it
     */
    public void adjustStock(int amount) {
        this.stock += amount;
    }
}
