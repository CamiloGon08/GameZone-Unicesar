package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.model.BasicWarranty;
import com.mycompany.gamezone.model.ExtendedWarranty;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.Warranty;
import com.mycompany.gamezone.service.ProductService;
import com.mycompany.gamezone.service.SaleService;
import utilities.FilePath;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository responsible for the persistence of Warranty objects.
 *
 * Warranties are stored using the path defined in FilePath.WARRANTIES.
 * Since a Warranty contains references to a Product and a Sale, this
 * repository stores their identifiers and uses ProductService and
 * SaleService to resolve those references when loading the records.
 *
 * @author EstefaniaMarquez
 */
public class WarrantyRepository {

    private final String filePath;
    private final SaleService saleService;
    private final ProductService productService;

    /**
     * Creates a WarrantyRepository with the services required to resolve
     * product and sale references.
     *
     * @param saleService service used to find the original sale
     * @param productService service used to find the covered product
     */
    public WarrantyRepository(
            SaleService saleService,
            ProductService productService) {

        this.filePath = FilePath.WARRANTIES;
        this.saleService = saleService;
        this.productService = productService;
    }

    /**
     * Saves all warranties to the persistence file.
     *
     * Existing content is replaced by the warranties contained in the
     * provided list.
     *
     * @param warranties list of warranties to persist
     */
    public void saveAll(List<Warranty> warranties) {

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(filePath))) {

            for (Warranty warranty : warranties) {
                writer.write(toLine(warranty));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not save warranties to " + filePath, e);
        }
    }

    /**
     * Loads all warranties stored in the persistence file.
     *
     * @return list of stored warranties, or an empty list if the file does
     *         not exist
     */
    public List<Warranty> loadAll() {

        List<Warranty> warranties = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new FileReader(filePath))) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                Warranty warranty = toWarranty(line);

                if (warranty != null) {
                    warranties.add(warranty);
                }
            }

        } catch (FileNotFoundException e) {
            return warranties;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not load warranties from " + filePath, e);
        }

        return warranties;
    }

    /**
     * Converts a Warranty object into its persistence representation.
     *
     * Format:
     *
     * BASIC,id,productId,saleId,startDate
     * EXTENDED,id,productId,saleId,startDate
     *
     * @param warranty warranty to convert
     * @return text representation of the warranty
     */
    private String toLine(Warranty warranty) {

        String type;

        if (warranty instanceof BasicWarranty) {
            type = "BASIC";

        } else if (warranty instanceof ExtendedWarranty) {
            type = "EXTENDED";

        } else {
            throw new IllegalArgumentException(
                    "Unsupported warranty type: "
                    + warranty.getClass());
        }

        return type + ","
                + warranty.getId() + ","
                + warranty.getProduct().getId() + ","
                + warranty.getSale().getId() + ","
                + warranty.getStartDate();
    }

    /**
     * Converts a persisted record into a Warranty object.
     *
     * @param line persisted warranty record
     * @return reconstructed Warranty, or null if its references cannot
     *         be resolved
     */
    private Warranty toWarranty(String line) {

        String[] fields = line.split(",", -1);

        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }

        if (fields.length < 5) {
            throw new IllegalArgumentException(
                    "Invalid warranty record: " + line);
        }

        String type = fields[0];
        String id = fields[1];
        String productId = fields[2];
        String saleId = fields[3];
        LocalDate startDate = LocalDate.parse(fields[4]);

        Product product = productService.findById(productId);
        Sale sale = saleService.findById(saleId);

        if (product == null || sale == null) {
            return null;
        }

        if (type.equals("BASIC")) {

            return new BasicWarranty(
                    id,
                    product,
                    sale,
                    startDate);
        }

        if (type.equals("EXTENDED")) {

            return new ExtendedWarranty(
                    id,
                    product,
                    sale,
                    startDate);
        }

        throw new IllegalArgumentException(
                "Unknown warranty type in file: " + type);
    }
}