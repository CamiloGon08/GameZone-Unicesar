package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Return;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.service.ProductService;
import com.mycompany.gamezone.service.SaleService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository responsible for the persistence of Return objects.
 *
 * Returns are stored in the data/returns.csv file. Since a Return contains
 * references to a Sale and to the Products being returned, this repository
 * uses SaleService and ProductService to resolve those references when
 * loading the records.
 *
 * @author EstefaniaMarquez
 */
public class ReturnRepository {

    private final String filePath;
    private final SaleService saleService;
    private final ProductService productService;

    /**
     * Creates a ReturnRepository with the services required to resolve
     * references to sales and products.
     *
     * @param filePath path of the file used to persist returns
     * @param saleService service used to find the original sales
     * @param productService service used to find the returned products
     */
    public ReturnRepository(
            String filePath,
            SaleService saleService,
            ProductService productService) {

        this.filePath = filePath;
        this.saleService = saleService;
        this.productService = productService;
    }

    /**
     * Saves the complete list of returns to the persistence file.
     *
     * Existing content is replaced by the records contained in the
     * provided list.
     *
     * Each record stores the return identifier, date, original sale ID,
     * returned product IDs, reason and refund amount.
     *
     * @param returns list of returns to persist
     */
    public void saveAll(List<Return> returns) {

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(filePath))) {

            for (Return returnRecord : returns) {
                writer.write(toLine(returnRecord));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not save returns to " + filePath, e);
        }
    }

    /**
     * Loads all returns stored in the persistence file.
     *
     * The original Sale and returned Products are reconstructed by resolving
     * their identifiers through SaleService and ProductService.
     *
     * @return list of stored returns, or an empty list if the file does not
     *         exist
     */
    public List<Return> loadAll() {

        List<Return> returns = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new FileReader(filePath))) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                Return returnRecord = toReturn(line);

                if (returnRecord != null) {
                    returns.add(returnRecord);
                }
            }

        } catch (IOException e) {
            return returns;
        }

        return returns;
    }

    /**
     * Converts a Return object into the CSV representation used for
     * persistence.
     *
     * The product identifiers are stored as a semicolon-separated list
     * inside one field.
     *
     * Format:
     *
     * returnId,date,saleId,productId1;productId2,reason,refundAmount
     *
     * @param returnRecord return to convert
     * @return CSV representation of the return
     */
    private String toLine(Return returnRecord) {

        StringBuilder productIds = new StringBuilder();

        for (int i = 0;
                i < returnRecord.getReturnedProducts().size();
                i++) {

            Product product = returnRecord.getReturnedProducts().get(i);

            if (i > 0) {
                productIds.append(";");
            }

            productIds.append(product.getId());
        }

        return returnRecord.getId() + ","
                + returnRecord.getDate() + ","
                + returnRecord.getOriginalSale().getId() + ","
                + productIds + ","
                + returnRecord.getReason() + ","
                + returnRecord.getRefundAmount();
    }

    /**
     * Converts a persisted CSV record into a Return object.
     *
     * The original Sale is resolved through SaleService and each returned
     * Product is resolved through ProductService.
     *
     * @param line CSV record representing a return
     * @return reconstructed Return object
     */
    private Return toReturn(String line) {

        String[] fields = line.split(",", -1);

        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }

        String id = fields[0];
        LocalDate date = LocalDate.parse(fields[1]);
        String saleId = fields[2];
        String productIdsField = fields[3];
        String reason = fields[4];
        double refundAmount = Double.parseDouble(fields[5]);

        Sale originalSale = saleService.findById(saleId);

        if (originalSale == null) {
            return null;
        }

        List<Product> returnedProducts = new ArrayList<>();

        if (!productIdsField.isEmpty()) {

            String[] productIds = productIdsField.split(";");

            for (String productId : productIds) {

                Product product = productService.findById(productId);

                if (product != null) {
                    returnedProducts.add(product);
                }
            }
        }

        Return returnRecord = new Return(
                id,
                date,
                originalSale,
                returnedProducts,
                reason);

        returnRecord.setRefundAmount(refundAmount);

        return returnRecord;
    }
}