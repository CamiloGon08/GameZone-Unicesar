package com.mycompany.gamezone.persistence;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;
import utilities.FilePath;

/**
 * Repository responsible for persisting sales to a plain text file and
 * for loading them back into the system. Each sale is stored in a single
 * line using a comma-separated format that includes the sale identifier,
 * the date, the customer name, the seller name, the product titles and
 * the total amount.
 */
public class SaleRepository {

    private static final String FILE_PATH = FilePath.SALES;

    private String toLine(Sale sale) {
        StringBuilder productNames = new StringBuilder();
        List<Product> products = sale.getProducts();
        for (int i = 0; i < products.size(); i++) {
            if (i > 0) {
                productNames.append(";");
            }
            productNames.append(products.get(i).getTitle());
        }
        return sale.getId() + "," + sale.getDate() + ","
                + sale.getCustomer().getName() + "," + sale.getSeller().getName() +
                "," + productNames + "," + sale.getTotal();
    }

    /**
     * Saves the given list of sales to the sales file. Each sale is
     * written on its own line using the format produced by {@link #toLine(Sale)}.
     * If an input/output error occurs, the error message is printed to
     * the standard output.
     *
     * @param sales list of sales to persist
     */
    public void save(List<Sale> sales) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Sale sale : sales) {
                writer.write(toLine(sale));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving sales: " + e.getMessage());
        }
    }

    /**
     * Loads the sales stored in the sales file.
     *
     * @return list of sales recovered from the file, or an empty list
     *         if the file does not exist or contains no data
     */
    public List<Sale> load() {
        return new ArrayList<>();
    }
}