package com.mycompany.gamezone.persistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Return;

/**
 * Converts returns to the comma-separated line stored in the returns file and
 * converts each stored line back to a raw {@link ReturnData} record.
 * 
 * @author EstefaniaMarquez
 * 
 */
public class ReturnRecordMapper {

    /**
     * Builds the stored line of a return.
     *
     * @param returnRecord the return to convert
     * @return the line with id, date, sale id, product ids, reason, refund and
     * warranty refund
     */
    public String toLine(Return returnRecord) {
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

    /**
     * Converts a stored line to a raw record.
     *
     * @param line the stored line
     * @return the raw record with the identifiers stored in the line
     */
    public ReturnData toReturnData(String line) {
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
