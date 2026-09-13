package com.mycompany.gamezone.model;

//import java.util.ArrayList;

public class Customer extends Person{
    
    private String eMail;
    //private ArrayList<Sale> purchases;
    //Relación de Composición 1:n entre Customer y Sale. 
    //Sale NO puede existir si no está ligada a un cliente
    //Customer puede tener varias Sales (compras) asignados, pero no puede tener 0
    
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
    
    public Customer(String eMail, String name, long iD, long contactNumber) {
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
    * @param eMail sets the customer eMail 
    */
    
    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    /**
     * Returns a String containing the object's data in the order and format
     * required for persistence.
     *
     * @return a String with the object's data formatted for persistence.
     */
    @Override
    
    public String textFormat() {
        return getiD() + "|" + getName() + "|" + eMail + "|" + getContactNumber();
    }
}

