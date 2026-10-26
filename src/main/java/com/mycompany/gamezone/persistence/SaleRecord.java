package com.mycompany.gamezone.persistence;

import java.time.LocalDate;
import java.util.List;

/**
 * Raw sale record as stored in the sales file. It keeps the identifiers
 * of the customer, the seller and the products; the references are
 * resolved by the service layer.
 */
public class SaleRecord {

    private final String id;
    private final LocalDate date;
    private final String customerId;
    private final String sellerId;
    private final List<String> productIds;
    private final String promotionName;
    private final Double discountAmount;

    /**
     * Creates a sale record with the values read from one line of the file.
     *
     * @param id             sale identifier
     * @param date           sale date
     * @param customerId     identifier of the customer
     * @param sellerId       identifier of the seller
     * @param productIds     identifiers of the products sold
     * @param promotionName  name of the promotion applied, or null if none was stored
     * @param discountAmount discount applied, or null if none was stored
     */
    public SaleRecord(String id, LocalDate date, String customerId, String sellerId,
            List<String> productIds, String promotionName, Double discountAmount) {
        this.id = id;
        this.date = date;
        this.customerId = customerId;
        this.sellerId = sellerId;
        this.productIds = productIds;
        this.promotionName = promotionName;
        this.discountAmount = discountAmount;
    }

    /** @return the sale identifier */
    public String getId() {
        return id;
    }

    /** @return the sale date */
    public LocalDate getDate() {
        return date;
    }

    /** @return the identifier of the customer */
    public String getCustomerId() {
        return customerId;
    }

    /** @return the identifier of the seller */
    public String getSellerId() {
        return sellerId;
    }

    /** @return the identifiers of the products sold */
    public List<String> getProductIds() {
        return productIds;
    }

    /** @return the name of the promotion applied, or null if none was stored */
    public String getPromotionName() {
        return promotionName;
    }

    /** @return the discount applied, or null if none was stored */
    public Double getDiscountAmount() {
        return discountAmount;
    }
}
