package com.mycompany.gamezone.persistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.SaleItem;

/**
 * Converts sales to the pipe-separated line stored in the sales file and
 * converts each stored line back to a raw {@link SaleRecord}. The line
 * holds the sale identifier, the date, the customer ID, the seller ID, the
 * product IDs, the total amount, the applied promotion name and the
 * discount amount.
 */
public class SaleRecordMapper {

    /**
     * Builds the stored line of a sale.
     *
     * @param sale the sale to convert
     * @return the pipe-separated line
     */
    public String toLine(Sale sale) {
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
     * Converts the fields of a stored line to a raw record.
     *
     * @param data the fields of the line
     * @return the record, or null if the line has fewer than six fields
     * @throws java.time.format.DateTimeParseException if the date is malformed
     */
    public SaleRecord toRecord(String[] data) {
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
