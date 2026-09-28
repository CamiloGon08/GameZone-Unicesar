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
 
    /**
     * Calculates the refund as the sum of each returned product's price,
     * scaled down by the same proportion of discount the original sale
     * received. If the sale had no discount, this behaves exactly like
     * summing list prices.
     *
     * NOTE (A5): assumes Sale exposes getSubtotal() and getDiscountAmount()
     * once A3 (unified sale registration, Lider Tecnico) is merged into
     * develop. Confirm these exact getter names against the real Sale
     * class before opening the Pull Request for this fix.
     *
     * @return the calculated, discount-adjusted refund amount
     */
    public double calculateRefundAmount() {
        double subtotal = originalSale.getSubtotal();
        double discount = originalSale.getDiscountAmount();
        double discountRatio = (subtotal > 0) ? (discount / subtotal) : 0.0;
 
        double sum = 0.0;
        for (Product product : returnedProducts) {
            double listPrice = product.getPrice();
            double proportionalRefund = listPrice * (1 - discountRatio);
            sum += proportionalRefund;
        }
        this.refundAmount = sum;
        return refundAmount;
    }
 
    /**
     * Builds a Spanish-language receipt showing, for each returned product,
     * the list price, the proportional discount applied, and the actual
     * refunded amount, plus the total refund.
     *
     * @return a formatted receipt describing this return
     */
    public String generateReturnReceipt() {
        double subtotal = originalSale.getSubtotal();
        double discount = originalSale.getDiscountAmount();
        double discountRatio = (subtotal > 0) ? (discount / subtotal) : 0.0;
 
        StringBuilder receipt = new StringBuilder();
        receipt.append("===== Recibo de Devolucion =====\n");
        receipt.append("Identificador: ").append(id).append("\n");
        receipt.append("Fecha: ").append(date).append("\n");
        receipt.append("Venta original: ").append(originalSale.getId()).append("\n");
        receipt.append("Motivo: ").append(reason).append("\n");
        receipt.append("Productos devueltos:\n");
        for (Product product : returnedProducts) {
            double listPrice = product.getPrice();
            double proportionalDiscount = listPrice * discountRatio;
            double refunded = listPrice - proportionalDiscount;
            receipt.append("  - ").append(product.getTitle())
                   .append(" | Precio de lista: $").append(String.format("%.2f", listPrice))
                   .append(" | Descuento proporcional: $").append(String.format("%.2f", proportionalDiscount))
                   .append(" | Reembolsado: $").append(String.format("%.2f", refunded))
                   .append("\n");
        }
        receipt.append("Monto total reembolsado: $").append(String.format("%.2f", refundAmount)).append("\n");
        receipt.append("=================================");
        return receipt.toString();
    }
}
 