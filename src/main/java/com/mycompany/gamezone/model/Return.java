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

    /**
     * Checks ownership using the original sale customer's existing equality rule.
     *
     * @param customer the customer to compare with the original sale's customer
     * @return whether the original sale's customer equals the supplied customer
     */
    public boolean belongsTo(Customer customer) {
        return originalSale.getCustomer().equals(customer);
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
     * Calculates the refund as the sum of each returned item's charged price,
     * scaled down by the same proportion of discount the original sale
     * received, plus the refundable cost of the cancelled warranties.
     * If the sale had no discount and no warranty was cancelled, this
     * behaves exactly like summing the original charged prices.
     *
     * @return the calculated, discount-adjusted refund amount
     */
    public double calculateRefundAmount() {
        double discountRatio = discountRatio();

        double sum = 0.0;
        for (Product product : returnedProducts) {
            double listPrice = listPriceOf(product);
            double proportionalRefund = listPrice * (1 - discountRatio);
            sum += proportionalRefund;
        }
        this.refundAmount = sum + warrantyRefundAmount;
        return refundAmount;
    }

    /**
     * Returns the price charged for the given product in the original sale.
     * If the product is not an item of the sale, its current price is used.
     *
     * @param product the returned product
     * @return the charged price of the product
     */
    public double listPriceOf(Product product) {
        for (SaleItem item : originalSale.getItems()) {
            if (item.getProduct().getId().equals(product.getId())) {
                return item.getUnitPrice();
            }
        }
        return product.getPrice();
    }

    /**
     * Returns the part of the discount of the original sale that corresponds
     * to the given product, in proportion to its charged price.
     *
     * @param product the returned product
     * @return the proportional discount of the product
     */
    public double proportionalDiscountOf(Product product) {
        return listPriceOf(product) * discountRatio();
    }

    /** Proportion of the subtotal of the original sale that was discounted. */
    private double discountRatio() {
        double subtotal = originalSale.getSubtotal();
        double discount = originalSale.getDiscountAmount();
        return (subtotal > 0) ? (discount / subtotal) : 0.0;
    }
}
