package com.mycompany.gamezone.service;

import com.mycompany.gamezone.model.Console;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.VideoGame;
import com.mycompany.gamezone.persistence.ProductRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Business rules and validation filter for products before persistence.
 */
public class ProductService {

    private final ProductRepository productRepository;
    private final List<Product> products;

    public ProductService(ProductRepository productRepository) {
        if (productRepository == null) {
            throw new IllegalArgumentException("ProductRepository cannot be null.");
        }
        this.productRepository = productRepository;
        this.products = new ArrayList<>(productRepository.loadAll());
    }

    public void registerVideoGame(String id, String title, double price, int stock,
                                   String platform, String genre, String ageRating) {
        // Validations directly inside Register
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be empty.");
        }
        if (findById(id) != null) {
            throw new IllegalArgumentException("Product ID already exists.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Video game title cannot be empty.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }
        if (platform == null || platform.trim().isEmpty()) {
            throw new IllegalArgumentException("Platform cannot be empty.");
        }
        if (genre == null || genre.trim().isEmpty()) {
            throw new IllegalArgumentException("Genre cannot be empty.");
        }
        if (ageRating == null || ageRating.trim().isEmpty()) {
            throw new IllegalArgumentException("Age rating cannot be empty.");
        }

        VideoGame videoGame = new VideoGame(id, title, price, stock, platform, genre, ageRating);
        products.add(videoGame);
        productRepository.saveAll(products);
    }

    public void registerConsole(String id, String title, double price, int stock,
                                 String brand, String model, String generation) {
        // Validations directly inside Register
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be empty.");
        }
        if (findById(id) != null) {
            throw new IllegalArgumentException("Product ID already exists.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Console title cannot be empty.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }
        if (brand == null || brand.trim().isEmpty()) {
            throw new IllegalArgumentException("Brand cannot be empty.");
        }
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Model cannot be empty.");
        }
        if (generation == null || generation.trim().isEmpty()) {
            throw new IllegalArgumentException("Generation cannot be empty.");
        }

        Console console = new Console(id, title, price, stock, brand, model, generation);
        products.add(console);
        productRepository.saveAll(products);
    }

    public List<Product> listProducts() {
        return new ArrayList<>(products);
    }

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

    public void updateStock(String id, int amount) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be empty.");
        }
        Product product = findById(id);
        if (product == null) {
            throw new IllegalArgumentException("Product not found with ID: " + id);
        }
        if (product.getStock() + amount < 0) {
            throw new IllegalArgumentException("Insufficient stock for requested change.");
        }

        product.adjustStock(amount);
        productRepository.saveAll(products);
    }
}