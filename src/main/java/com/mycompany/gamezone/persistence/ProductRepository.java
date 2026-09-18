package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.model.Console;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.VideoGame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles persistence of {@link Product} instances in a plain .txt file.
 * Each line in the file represents one product, using a simple
 * comma-separated format that starts with a word telling whether the line
 * is a video game or a console. This class only reads and writes the file;
 * it does not contain any business rules, which belong to the service layer.
 */
public class ProductRepository {

    private final String filePath;

    /**
     * Creates a repository backed by the given .txt file path.
     *
     * @param filePath path of the .txt file used to persist products
     *                 (for example, "data/products.txt")
     */
    public ProductRepository(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Writes the full list of products to the .txt file, replacing whatever
     * was stored before.
     *
     * @param products the products to persist
     */
    public void saveAll(List<Product> products) {
        try {
            FileWriter writer = new FileWriter(filePath);
            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                writer.write(toLine(product));
                writer.write(System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not save products to " + filePath, e);
        }
    }

    /**
     * Reads all products previously stored in the .txt file.
     *
     * @return the list of products found in the file; an empty list if the
     *         file does not exist yet (for example, on the first run)
     */
    public List<Product> loadAll() {
        List<Product> products = new ArrayList<>();

        if (!Files.exists(Paths.get(filePath))) {
            return products;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            while (line != null) {
                if (!line.trim().isEmpty()) {
                    products.add(toProduct(line));
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not load products from " + filePath, e);
        }

        return products;
    }

    /**
     * Converts one product into the text line that will be written to the
     * .txt file.
     *
     * @param product the product to convert
     * @return the line representing that product
     */
    private String toLine(Product product) {
        if (product instanceof VideoGame) {
            VideoGame videoGame = (VideoGame) product;
            return "VIDEOGAME," + videoGame.getId() + "," + videoGame.getTitle() + ","
                    + videoGame.getPrice() + "," + videoGame.getStock() + ","
                    + videoGame.getPlatform() + "," + videoGame.getGenre() + ","
                    + videoGame.getAgeRating();
        }

        if (product instanceof Console) {
            Console console = (Console) product;
            return "CONSOLE," + console.getId() + "," + console.getTitle() + ","
                    + console.getPrice() + "," + console.getStock() + ","
                    + console.getBrand() + "," + console.getModel() + ","
                    + console.getGeneration();
        }

        throw new IllegalArgumentException("Unsupported product type: " + product.getClass());
    }

    /**
     * Converts one text line read from the .txt file back into a
     * {@link Product} object.
     *
     * @param line the line to convert
     * @return the reconstructed product
     */
    private Product toProduct(String line) {
        String[] fields = line.split(",");
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }

        String type = fields[0];
        String id = fields[1];
        String title = fields[2];
        double price = Double.parseDouble(fields[3]);
        int stock = Integer.parseInt(fields[4]);

        if (type.equals("VIDEOGAME")) {
            return new VideoGame(id, title, price, stock, fields[5], fields[6], fields[7]);
        }

        if (type.equals("CONSOLE")) {
            return new Console(id, title, price, stock, fields[5], fields[6], fields[7]);
        }

        throw new IllegalArgumentException("Unknown product type in file: " + type);
    }
}