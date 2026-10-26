package com.mycompany.gamezone.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gamezone.exceptions.PersistenceException;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.SaleItem;

import utilities.FilePath;

/**
 * Repository responsible for persisting sales to a plain text file and
 * for loading them back into the system. Each sale is stored in a single
 * line using a pipe-separated format that includes the sale identifier,
 * the date, the customer ID, the seller ID, the product IDs, the total
 * amount, the applied promotion name and the discount amount.
 *
 * Loading returns raw {@link SaleRecord} objects with the identifiers
 * stored in the file; the service layer resolves the references to
 * products, customers and sellers.
 *
 * Any {@link IOException} raised while accessing the file is caught here and
 * rethrown as a {@link PersistenceException} that identifies the file and the
 * operation that failed.
 * 
 * @author EstefaniaMarquez
 * 
 */

public class SaleRepository {

    private static final String FILE_PATH = FilePath.SALES;
    private static final String SEPARATOR = "\\|";

    private String toLine(Sale sale) {
        StringBuilder productIds = new StringBuilder();
        List<SaleItem> items = sale.getItems();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                productIds.append(";");
            }
            productIds.append(items.get(i).getProduct().getId());
        }

        String promotionName = sale.getAppliedPromotionName() == null
                ? "" : sale.getAppliedPromotionName();

        return sale.getId() + "|" + sale.getDate() + "|"
                + sale.getCustomer().getiD() + "|"
                + sale.getSeller().getiD() + "|"
                + productIds + "|" + sale.getTotal() + "|"
                + promotionName + "|" + sale.getDiscountAmount();
    }

    /**
     * Saves the given list of sales to the sales file. Each sale is
     * written on its own line using the format produced by
     * {@link #toLine(Sale)}.
     *
     * @param sales list of sales to persist
     * @throws PersistenceException if the file cannot be written
     */
    public void save(List<Sale> sales) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Sale sale : sales) {
                writer.write(toLine(sale));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenceException(
                    "Write error: could not save sales to file '" + FILE_PATH + "'", e);
        }
    }

    /**
     * Loads the sale records stored in the sales file.
     *
     * @return list of records recovered from the file, or an empty list
     *         if the file does not exist or contains no data
     * @throws PersistenceException if the file cannot be read or contains a
     *         malformed record
     */
    public List<SaleRecord> loadAll() {
        List<SaleRecord> records = new ArrayList<>();

        if (!Files.exists(Paths.get(FILE_PATH))) {
            return records;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] data = line.split(SEPARATOR, -1);
                SaleRecord record = parseRecord(data, lineNumber);
                if (record != null) {
                    records.add(record);
                }
            }
        } catch (IOException e) {
            throw new PersistenceException(
                    "Read error: could not load sales from file '" + FILE_PATH + "'", e);
        }
        return records;
    }

    /**
     * Converts one line of the sales file into a record, translating any
     * format problem into a {@link PersistenceException} that identifies the
     * file and the offending line.
     *
     * @param data       the fields of the record
     * @param lineNumber the 1-based number of the line in the file
     * @return the record, or null if the line has too few fields
     * @throws PersistenceException if the record is malformed
     */
    private SaleRecord parseRecord(String[] data, int lineNumber) {
        try {
            return createRecord(data);
        } catch (DateTimeParseException e) {
            throw new PersistenceException(
                    "Read error: malformed sale record at line " + lineNumber
                            + " of file '" + FILE_PATH + "'", e);
        }
    }

    private SaleRecord createRecord(String[] data) {
        if (data.length < 6) {
            return null;
        }

        String id = data[0];
        LocalDate date = LocalDate.parse(data[1]);
        String customerId = data[2];
        String sellerId = data[3];
        String productIdsRaw = data[4];

        List<String> productIds = new ArrayList<>();
        if (!productIdsRaw.trim().isEmpty()) {
            for (String productId : productIdsRaw.split(";")) {
                productIds.add(productId);
            }
        }

        String promotionName = null;
        Double discountAmount = null;
        if (data.length >= 8) {
            promotionName = data[6];
            try {
                discountAmount = Double.parseDouble(data[7]);
            } catch (NumberFormatException ignored) {
                // ignore malformed numeric value
            }
        }

        return new SaleRecord(id, date, customerId, sellerId, productIds,
                promotionName, discountAmount);
    }
}
