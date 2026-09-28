package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.model.BasicWarranty;
import com.mycompany.gamezone.model.ExtendedWarranty;
import com.mycompany.gamezone.model.Warranty;
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
 * * Repository responsible for the persistence of Warranty objects. 
 * 
 *
 * Warranties are stored using the path defined in FilePath.WARRANTIES. 
 * This repository stores product and sale identifiers instead of resolving 
 * their references. The references are resolved later by WarrantyService. 
 * 
 * @author EstefaniaMarquez
 */

public class WarrantyRepository {

    private final String filePath;

    /**
     * 
     * Creates a WarrantyRepository for warranty persistence.
     */
    
    public WarrantyRepository() {
        this.filePath = FilePath.WARRANTIES;
    }

    /**
     * 
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
     * 
     * Loads all warranty records stored in the persistence file. 
     * 
     * The repository only reads the stored identifiers and basic warranty 
     * information. Product and Sale references are resolved later by 
     * WarrantyService. 
     * 
     * @return list of stored warranty records, or an empty
     * list if the file does not exist
     */
    
    public List<WarrantyData> loadAll() {

        List<WarrantyData> warranties = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new FileReader(filePath))) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                WarrantyData warranty = toWarranty(line);

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
     * 
     * Converts a Warranty object into its persistence representation. 
     *
     * Format: BASIC,id,productId,saleId,startDate 
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
     * 
     * Converts a persisted warranty record into a WarrantyData object.
     * The method only extracts the stored warranty information and does not
     * resolve the Product or Sale references. 
     * 
     * @param line persisted warranty record 
     * @return extracted warranty data
     */
    
    private WarrantyData toWarranty(String line) {

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

        return new WarrantyData(type, id, productId, saleId, startDate);
    }

    /**
     * Represents the persistence data required to reconstruct a Warranty.
     *
     * Product and Sale objects are intentionally not stored here.
     */
    
    public static class WarrantyData {

        private final String type;
        private final String id;
        private final String productId;
        private final String saleId;
        private final LocalDate startDate;

        public WarrantyData(String type, String id, String productId,
                String saleId, LocalDate startDate) {
            this.type = type;
            this.id = id;
            this.productId = productId;
            this.saleId = saleId;
            this.startDate = startDate;
        }

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }

        public String getProductId() {
            return productId;
        }

        public String getSaleId() {
            return saleId;
        }

        public LocalDate getStartDate() {
            return startDate;
        }
    }
}
