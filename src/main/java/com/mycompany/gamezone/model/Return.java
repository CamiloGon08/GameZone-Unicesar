package com.mycompany.gamezone.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents the return of one or more items from an original sale.
 *
 * A return stores the date, the original sale, the returned items, the
 * reason and the refund amount. The refund of each item is proportional to
 * the discount received by the original sale, and it also includes the
 * refundable cost of the warranties cancelled for returned consoles.
 */
public class Return {

    private String id;
    private LocalDate date;
    private Sale originalSale;
    private List<Product> returnedProducts;
    private String reason;
    private double warrantyRefundAmount;
    private double refundAmount;

    /**
     * Creates a return without warranty refund.
     *
     * @param id unique identifier of the return
     * @param date date on which the return was registered
     * @param originalSale sale from which the items are returned
     * @param returnedProducts items being returned
     * @param reason reason for the return
     */
    public Return(String id, LocalDate date, Sale originalSale, List<Product> returnedProducts, String reason) {
        this(id, date, originalSale, returnedProducts, reason, 0.0);
    }

    /**
     * Creates a return that includes the refundable cost of the warranties
     * cancelled for the returned consoles. The refund amount is calculated
     * automatically.
     *
     * @param id unique identifier of the return
     * @param date date on which the return was registered
     * @param originalSale sale from which the items are returned
     * @param returnedProducts items being returned
     * @param reason reason for the return
     * @param warrantyRefundAmount refundable cost of the cancelled warranties
     */
    public Return(String id, LocalDate date, Sale originalSale, List<Product> returnedProducts,
            String reason, double warrantyRefundAmount) {
        this.id = id;
        this.date = date;
        this.originalSale = originalSale;
        this.returnedProducts = returnedProducts;
        this.reason = reason;
        this.warrantyRefundAmount = warrantyRefundAmount;
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

    /**
     * Returns the refundable cost of the warranties cancelled in this return.
     *
     * @return the warranty refund amount
     */
    public double getWarrantyRefundAmount() {
        return warrantyRefundAmount;
    }

    /**
     * Sets the refundable cost of the warranties cancelled in this return.
     *
     * @param warrantyRefundAmount the new warranty refund amount
     */
    public void setWarrantyRefundAmount(double warrantyRefundAmount) {
        this.warrantyRefundAmount = warrantyRefundAmount;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    /**
     * Calculates the refund as the sum of each returned item's price,
     * scaled down by the same proportion of discount the original sale
     * received, plus the refundable cost of the cancelled warranties.
     * If the sale had no discount and no warranty was cancelled, this
     * behaves exactly like summing list prices.
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
        this.refundAmount = sum + warrantyRefundAmount;
        return refundAmount;
    }

    /**
     * Builds a Spanish-language receipt showing, for each returned item,
     * the list price, the proportional discount applied, and the actual
     * refunded amount, followed by the refund for cancelled warranties
     * and the total refund.
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
        receipt.append("Reembolso por garantias anuladas: $")
               .append(String.format("%.2f", warrantyRefundAmount)).append("\n");
        receipt.append("Monto total reembolsado: $").append(String.format("%.2f", refundAmount)).append("\n");
        receipt.append("=================================");
        return receipt.toString();
    }
}