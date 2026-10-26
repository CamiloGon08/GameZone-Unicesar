package com.mycompany.gamezone.persistence;

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

public class WarrantyRepository {

    private final String filePath;

    public WarrantyRepository() {
        this.filePath = FilePath.WARRANTIES;
    }

    public void saveAll(List<Warranty> warranties) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            for (Warranty warranty : warranties) {
                writer.write(toLine(warranty));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not save warranties to " + filePath, e);
        }
    }

    public List<WarrantyData> loadAll() {
        List<WarrantyData> warranties = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

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
            throw new RuntimeException("Could not load warranties from " + filePath, e);
        }

        return warranties;
    }

    private String toLine(Warranty warranty) {
        return warranty.getTypeCode() + ","
                + warranty.getId() + ","
                + warranty.getProduct().getId() + ","
                + warranty.getSale().getId() + ","
                + warranty.getStartDate();
    }

    private WarrantyData toWarranty(String line) {
        String[] fields = line.split(",", -1);

        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }

        if (fields.length < 5) {
            throw new IllegalArgumentException("Invalid warranty record: " + line);
        }

        return new WarrantyData(
                fields[0], fields[1], fields[2], fields[3],
                LocalDate.parse(fields[4]));
    }

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

        public String getType() { return type; }
        public String getId() { return id; }
        public String getProductId() { return productId; }
        public String getSaleId() { return saleId; }
        public LocalDate getStartDate() { return startDate; }
    }
}