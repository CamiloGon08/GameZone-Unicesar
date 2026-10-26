package com.mycompany.gamezone.persistence;

import java.time.LocalDate;

/**
 * Raw warranty record as stored in the file; references are resolved by the
 * service layer.
 * 
 * @author EstefaniaMarquez
 * 
 */
public class WarrantyData {

    private final String type;
    private final String id;
    private final String productId;
    private final String saleId;
    private final LocalDate startDate;

    /**
     * Creates a raw warranty record.
     *
     * @param type type code of the warranty
     * @param id identifier of the warranty
     * @param productId identifier of the covered product
     * @param saleId identifier of the sale
     * @param startDate start date of the coverage
     */
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
