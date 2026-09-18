package com.mycompany.gamezone.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a sale made at GameZone Unicesar. A sale is identified by
 * a unique code and stores the date, the customer, the seller, the list
 * of products that were purchased and the total amount calculated from
 * the price of those products.
 */
public class Sale {

    private String id;
    private LocalDate date;
    private Seller seller;
    private Customer customer;
    private List<Product> products;
    private double total;

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
        this.products = products;
        this.seller = seller;
        this.customer = customer;
        this.total = calculateTotal();
    }

    private double calculateTotal() {
        double sum = 0;
        for (Product product : products) {
            sum += product.getPrice();
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
        return products;
    }

    /**
     * Sets a new list of products for the sale and recalculates the
     * total amount based on the new list.
     *
     * @param products the new list of products
     */
    public void setProducts(List<Product> products) {
        this.products = products;
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
     * Returns a human-readable representation of the sale, including
     * its identifier, date, customer, seller, number of products and
     * total amount.
     *
     * @return a formatted string with the sale information
     */
    @Override
    public String toString() {
        return "Sale : " + id + " Date : " + date +
                ",Customer : " + customer.getName() +
                ",Seller : " + seller.getName() +
                ",Products : " + products.size() +
                ", Total : $" + String.format("%.2f", total);
    }

    /**
     * Checks whether this sale is still within the 30 calendar day window
     * during which a return can be registered, counted from the sale date.
     *
     * @return true if today's date is within 30 days of the sale date, false otherwise
     */
    public boolean canBeReturned() {
        LocalDate deadline = getDate().plusDays(30);
        LocalDate today = LocalDate.now();
        return !today.isAfter(deadline);
    }
}