package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.exceptions.PersistenceException;
import com.mycompany.gamezone.model.Product;

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
 * Any {@link IOException} raised while accessing the file is caught here and
 * rethrown as a {@link PersistenceException} that identifies the file and the
 * operation that failed.
 * 
 * @author EstefaniaMarquez
 *
 */
public class ProductRepository {

    private final String filePath;
    private final List<ProductLineConverter> converters = List.of(
            new VideoGameLineConverter(), new ConsoleLineConverter());

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
     * @throws PersistenceException if the file cannot be written
     */
    public void saveAll(List<Product> products) {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                writer.write(toLine(product));
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new PersistenceException(
                    "Write error: could not save products to file '" + filePath + "'", e);
        }
    }

    /**
     * Reads all products previously stored in the .txt file.
     *
     * @return the list of products found in the file; an empty list if the
     *         file does not exist yet (for example, on the first run)
     * @throws PersistenceException if the file cannot be read or contains a
     *         malformed line
     */
    public List<Product> loadAll() {
        List<Product> products = new ArrayList<>();

        if (!Files.exists(Paths.get(filePath))) {
            return products;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            int lineNumber = 1;
            while (line != null) {
                if (!line.trim().isEmpty()) {
                    products.add(parseLine(line, lineNumber));
                }
                line = reader.readLine();
                lineNumber++;
            }
        } catch (IOException e) {
            throw new PersistenceException(
                    "Read error: could not load products from file '" + filePath + "'", e);
        }

        return products;
    }

    /**
     * Converts one line of the file into a product, translating any format
     * problem into a {@link PersistenceException} that identifies the file and
     * the offending line.
     *
     * @param line       the line to convert
     * @param lineNumber the 1-based number of the line in the file
     * @return the reconstructed product
     * @throws PersistenceException if the line is malformed
     */
    private Product parseLine(String line, int lineNumber) {
        try {
            return toProduct(line);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            throw new PersistenceException(
                    "Read error: malformed product at line " + lineNumber
                            + " of file '" + filePath + "'", e);
        }
    }

    /**
     * Converts one product into the text line that will be written to the
     * .txt file.
     *
     * @param product the product to convert
     * @return the line representing that product
     * @throws PersistenceException if the product type cannot be stored
     */
    private String toLine(Product product) {
        for (ProductLineConverter converter : converters) {
            if (converter.supports(product)) {
                return converter.toLine(product);
            }
        }

        throw new PersistenceException(
                "Write error: unsupported product type " + product.getClass().getSimpleName()
                        + " for file '" + filePath + "'", null);
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

        for (ProductLineConverter converter : converters) {
            if (type.equals(converter.getTypeKey())) {
                return converter.toProduct(id, title, price, stock, fields);
            }
        }

        throw new IllegalArgumentException("Unknown product type in file: " + type);
    }
}
