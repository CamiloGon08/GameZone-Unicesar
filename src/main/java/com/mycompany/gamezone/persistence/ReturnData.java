package com.mycompany.gamezone.persistence;

import java.time.LocalDate;
import java.util.List;

/** Raw return record as stored in the file; references are resolved by ReturnService. */
public class ReturnData {

    private final String id;
    private final LocalDate date;
    private final String saleId;
    private final List<String> productIds;
    private final String reason;
    private final double refundAmount;
    private final double warrantyRefundAmount;

    public ReturnData(String id, LocalDate date, String saleId,
            List<String> productIds, String reason,
            double refundAmount, double warrantyRefundAmount) {
        this.id = id;
        this.date = date;
        this.saleId = saleId;
        this.productIds = productIds;
        this.reason = reason;
        this.refundAmount = refundAmount;
        this.warrantyRefundAmount = warrantyRefundAmount;
    }

    public String getId() { return id; }
    public LocalDate getDate() { return date; }
    public String getSaleId() { return saleId; }
    public List<String> getProductIds() { return productIds; }
    public String getReason() { return reason; }
    public double getRefundAmount() { return refundAmount; }
    public double getWarrantyRefundAmount() { return warrantyRefundAmount; }
}