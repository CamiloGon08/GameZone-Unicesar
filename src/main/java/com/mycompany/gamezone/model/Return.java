package com.mycompany.gamezone.model;

import java.time.LocalDate;
import java.util.List;

public class Return {

    private String id;
    private LocalDate date;
    private Sale originalSale;
    private List<Product> returnedProducts;
    private String reason;
    private double refundAmount;

    /**
     * Creates a new return referencing the original sale and the products
     * being returned from it. The refund amount is computed immediately by
     * summing the price of every returned product.
     *
     * @param id               the unique identifier of the return
     * @param date             the date the return was registered
     * @param originalSale     the sale this return refers to
     * @param returnedProducts the products being returned
     * @param reason           the reason given for the return
     */
    public Return(String id, LocalDate date, Sale originalSale, List<Product> returnedProducts, String reason) {
        this.id = id;
        this.date = date;
        this.originalSale = originalSale;
        this.returnedProducts = returnedProducts;
        this.reason = reason;
        this.refundAmount = calculateRefundAmount();
    }

    
    public String getId() {
        return id;
    }

    
    public LocalDate getDate() {
        return date;
    }

    
    public Sale getOriginalSale() {
        return originalSale;
    }

    
    public List<Product> getReturnedProducts() {
        return returnedProducts;
    }

   
    public String getReason() {
        return reason;
    }

    
    public void setReason(String reason) {
        this.reason = reason;
    }

    
    public double getRefundAmount() {
        return refundAmount;
    }

   
    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

   
    public double calculateRefundAmount() {
        double sum = 0.0;
        for (Product product : returnedProducts) {
            sum += product.getPrice();
        }
        this.refundAmount = sum;
        return refundAmount;
    }

    
    public String generateReturnReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("===== Return Receipt =====\n");
        receipt.append("Return ID: ").append(id).append("\n");
        receipt.append("Date: ").append(date).append("\n");
        receipt.append("Original Sale ID: ").append(originalSale.getId()).append("\n");
        receipt.append("Returned Products:\n");
        for (Product product : returnedProducts) {
            receipt.append("  - ").append(product.getTitle())
                   .append(" ($").append(String.format("%.2f", product.getPrice())).append(")\n");
        }
        receipt.append("Reason: ").append(reason).append("\n");
        receipt.append("Refund Amount: $").append(String.format("%.2f", refundAmount)).append("\n");
        receipt.append("=================================");
        return receipt.toString();
    }
}