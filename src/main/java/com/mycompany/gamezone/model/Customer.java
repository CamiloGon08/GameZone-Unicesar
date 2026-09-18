package com.mycompany.gamezone.model;

import java.util.ArrayList;

/**
 * Concret class for every Customer in GameZone Unicesar
 * It extends from Person and contains specific Customer information
 * 
 * @author EstefaniaMarquez
 */

public class Customer extends Person{
    
    private String eMail;
    private ArrayList<Sale> purchases;
    
    public Customer() {
    }
    
    /**
    * Creates a new Customer with the information of Person and the specific data of Customer
    * 
    * @param name the name of each customer
    * @param iD the identification number of each customer
    * @param contactNumber the contact number of each customer
    * @param eMail the email of each customer
    */ 
    
    public Customer(String eMail, String name, String iD, long contactNumber) {
        super(name, iD, contactNumber);
        this.eMail = eMail;
    }
    
    /**
    * Gets the personal eMail of each customer
    * @return the customer eMail 
    */
    
    public String geteMail() {
        return eMail;
    }

    /**
     * Sets the personal eMail of each customer
     *
     * @param eMail sets the customer eMail
     */
    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    /**
     * * Adds a purchase to the customer's purchase history. * * @param sale the
     * sale to be added
     * @param purchase
     */
    public void addPurchase(Sale purchase) {
        if (purchase != null) {
            purchases.add(purchase);
        }
    }

    /**
     * * Gets the customer's purchase history. * * @return a copy of the
     * customer's purchases
     * @return 
     */
    public ArrayList<Sale> getPurchases() {
        return new ArrayList<>(purchases);
    }

    /**
     * Returns a String containing the object's data in the order and format
     * required for persistence.
     *
     * @return a String with the object's data formatted for persistence.
     */
    @Override

    public String textFormat() {
        return "CUSTOMER|" + getiD() + "|" + getName() + "|" + eMail + "|" + getContactNumber();
    }

}

