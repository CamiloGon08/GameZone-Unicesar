package com.mycompany.gamezone.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sale made at GameZone Unicesar. A sale is identified by
 * a unique code and stores the date, the customer, the seller, the list
 * of products purchased, the total amount, and the promotion applied
 * (if any) with its discount.
 */
public class Sale {

    private static final int RETURN_WINDOW_DAYS = 30;

    private String id;
    private LocalDate date;
    private Seller seller;
    private Customer customer;
    private List<SaleItem> items;
    private double total;
    private String appliedPromotionName;
    private double discountAmount;
    private double warrantyCost;

    /**
     * Creates a new sale with the given date, identifier, products,
     * seller and customer. The total amount is calculated automatically
     * from the prices of the products.
     *
     * @param date     date on which the sale was made
     * @param id       unique identifier of the sale
     * @param products list of products included in the sale
     * @param seller   seller who attended the sale
     * @param customer customer who made the purchase
     */
    public Sale(LocalDate date, String id, List<Product> products, Seller seller, Customer customer) {
        this.date = date;
        this.id = id;
        this.items = toItems(products);
        this.seller = seller;
        this.customer = customer;
        this.total = calculateTotal();
    }

    private static List<SaleItem> toItems(List<Product> products) {
        List<SaleItem> saleItems = new ArrayList<>();
        for (Product product : products) {
            saleItems.add(new SaleItem(product));
        }
        return saleItems;
    }

    private double calculateTotal() {
        double sum = 0;
        for (SaleItem item : items) {
            sum += item.getUnitPrice();
        }
        return sum;
    }

    /**
     * Returns the unique identifier of the sale.
     *
     * @return the sale identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Sets a new identifier for the sale.
     *
     * @param id the new sale identifier
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the date on which the sale was made.
     *
     * @return the sale date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets a new date for the sale.
     *
     * @param date the new sale date
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Returns the seller who attended the sale.
     *
     * @return the seller of the sale
     */
    public Seller getSeller() {
        return seller;
    }

    /**
     * Sets a new seller for the sale.
     *
     * @param seller the new seller of the sale
     */
    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    /**
     * Returns the customer who made the purchase.
     *
     * @return the customer of the sale
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Sets a new customer for the sale.
     *
     * @param customer the new customer of the sale
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Returns the list of products included in the sale.
     *
     * @return the list of products of the sale
     */
    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        for (SaleItem item : items) {
            products.add(item.getProduct());
        }
        return products;
    }

    /**
     * Returns the items of the sale, each with the unit price charged.
     *
     * @return the list of items of the sale
     */
    public List<SaleItem> getItems() {
        return items;
    }

    /**
     * Sets a new list of products for the sale and recalculates the
     * total amount based on the new list.
     *
     * @param products the new list of products
     */
    public void setProducts(List<Product> products) {
        this.items = toItems(products);
        this.total = calculateTotal();
    }

    /**
     * Returns the total amount of the sale.
     *
     * @return the total amount in pesos
     */
    public double getTotal() {
        return total;
    }

    /**
     * Applies the discount of a promotion to this sale. The sale keeps the
     * name of the promotion and the discount amount, and subtracts the
     * discount from its total.
     *
     * @param promotionName name of the promotion applied
     * @param amount        discount amount to subtract from the total
     */
    public void applyPromotion(String promotionName, double amount) {
        this.appliedPromotionName = promotionName;
        this.discountAmount = amount;
        this.total = total - amount;
    }

    /**
     * Adds the cost of an extended warranty to the total of this sale.
     *
     * @param amount additional cost of the warranty
     */
    public void addWarrantyCost(double amount) {
        this.total = total + amount;
    }

    /**
     * Returns the subtotal of the sale, computed as the sum of the prices
     * of every product included in it, before any discount is applied.
     *
     * @return the subtotal amount
     */
    public double getSubtotal() {
        return calculateTotal();
    }

    /**
     * @return the name of the promotion applied to the sale, or null if none
     */
    public String getAppliedPromotionName() {
        return appliedPromotionName;
    }

    /**
     * @param appliedPromotionName the name of the promotion applied
     */
    public void setAppliedPromotionName(String appliedPromotionName) {
        this.appliedPromotionName = appliedPromotionName;
    }

    /**
     * @return the discount amount applied to the sale
     */
    public double getDiscountAmount() {
        return discountAmount;
    }

    /**
    * @param discountAmount the discount amount applied
    */
    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    /**
     * @return the total cost of extended warranties applied to this sale
     */
    public double getWarrantyCost() {
        return warrantyCost;
    }

    /**
     * @param warrantyCost the total cost of extended warranties applied to this sale
     */
    public void setWarrantyCost(double warrantyCost) {
        this.warrantyCost = warrantyCost;
    }
    /**
     * Generates a formatted receipt for the sale including the subtotal,
     * the discount applied (with the promotion name) and the final total.
     *
     * @return the formatted receipt as a String
     */
    public String generateReceipt() {
        StringBuilder sb = new StringBuilder();
        appendHeader(sb);
        appendProducts(sb);
        appendTotals(sb);
        return sb.toString();
    }

    private void appendHeader(StringBuilder sb) {
        sb.append("===== RECIBO DE VENTA =====\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("Fecha: ").append(date).append("\n");
        sb.append("Cliente: ").append(customer.getName()).append("\n");
        sb.append("Vendedor: ").append(seller.getName()).append("\n");
        sb.append("Productos:\n");
    }

    private void appendProducts(StringBuilder sb) {
        for (SaleItem item : items) {
            sb.append("  - ").append(item.getProduct().getTitle())
            .append(": $").append(String.format("%.2f", item.getUnitPrice()))
            .append("\n");
        }
        sb.append("---------------------------\n");
    }

    private void appendTotals(StringBuilder sb) {
        sb.append("Subtotal:  $").append(String.format("%.2f", getSubtotal())).append("\n");

        if (discountAmount > 0) {
            sb.append("Descuento: $").append(String.format("%.2f", discountAmount));
            if (appliedPromotionName != null) {
                sb.append("  (").append(appliedPromotionName).append(")");
            }
            sb.append("\n");
        }

        if (warrantyCost > 0) {
            sb.append("Garantía extendida: $").append(String.format("%.2f", warrantyCost)).append("\n");
        }

        sb.append("TOTAL:     $").append(String.format("%.2f", total)).append("\n");
        sb.append("===========================");
    }

    @Override
    public String toString(){
        return "Sale : "+ id + " Date : "+ date +
        ",Customer : " + customer.getName()+
        ",Seller : "+ seller.getName()+
        ",Products : "+ items.size()+
        ", Total : $"+ String.format("%.2f",total);
    }

    /**
     * Dev 1(dominguez)
     * Checks whether the evaluation date is from 0 to 30 calendar days after
     * this sale, inclusive. Evaluation dates before the sale are rejected.
     *
     * @param evaluationDate the date on which return eligibility is evaluated
     * @return whether the evaluation date is inside the return window
     */
    public boolean canBeReturned(LocalDate evaluationDate) {
        long daysSinceSale = ChronoUnit.DAYS.between(date, evaluationDate);
        return daysSinceSale >= 0 && daysSinceSale <= RETURN_WINDOW_DAYS;
    }

}
