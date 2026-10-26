package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Return;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReturnRepository {

    private final String filePath;

    public ReturnRepository(String filePath) {
        this.filePath = filePath;
    }

    public void saveAll(List<Return> returns) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            for (Return returnRecord : returns) {
                writer.write(toLine(returnRecord));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not save returns to " + filePath, e);
        }
    }

    public List<ReturnData> loadAll() {
        List<ReturnData> returns = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                returns.add(toReturnData(line));
            }

        } catch (IOException e) {
            return returns;
        }

        return returns;
    }

    private String toLine(Return returnRecord) {
        StringBuilder productIds = new StringBuilder();

        for (int i = 0; i < returnRecord.getReturnedProducts().size(); i++) {
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
                + returnRecord.getRefundAmount() + ","
                + returnRecord.getWarrantyRefundAmount();
    }

    private ReturnData toReturnData(String line) {
        String[] fields = line.split(",", -1);

        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }

        String productIdsField = fields[3];
        double warrantyRefundAmount = 0.0;

        if (fields.length > 6 && !fields[6].isEmpty()) {
            warrantyRefundAmount = Double.parseDouble(fields[6]);
        }

        List<String> productIds = productIdsField.isEmpty()
                ? new ArrayList<>()
                : Arrays.asList(productIdsField.split(";"));

        return new ReturnData(
                fields[0],
                LocalDate.parse(fields[1]),
                fields[2],
                productIds,
                fields[4],
                Double.parseDouble(fields[5]),
                warrantyRefundAmount);
    }
}