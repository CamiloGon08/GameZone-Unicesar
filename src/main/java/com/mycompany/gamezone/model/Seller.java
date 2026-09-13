package com.mycompany.gamezone.model;

//import java.util.ArrayList;

/**
 * Concret Class for every Seller in GameZone Unicesar
 * It Extends the Person class and contains specific information
 * 
 * @author EstefaniaMarquez
 */

public class Seller extends Person{
    private String employeeCode;
    private String shift;
    //private ArrayList<Sale> sales;
    //Relación de Composición 1:n entre Seller y Sale. 
    //Sale NO puede existir si no está ligada a un vendedor
    //Seller puede tener varias Sales (ventas) asignados, incluso si es 0
    
    public Seller() {
    }
    
    /**
    * Creates a new Seller with the information of Person and the specific data of Seller
    * 
    * @param name the name of each seller
    * @param iD the identification number of each seller
    * @param contactNumber the contact number of each seller
    * @param employeeCode the unique employee code of each seller
    * @param shift the specific shift of each seller
    */ 

    public Seller(String employeeCode, String shift, String name, String iD, long contactNumber) {
        super(name, iD, contactNumber);
        this.employeeCode = employeeCode;
        this.shift = shift;
    }

    /**
    * Gets the unique employee code of each seller
    * @return the employee code 
    */
    
    public String getEmployeeCode() {
        return employeeCode;
    }

    /**
    * Sets the employee code of the seller 
    * @param employeeCode sets the unique employee code of each seller
    */
    
    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

     /**
    * Gets the specific work shift of each seller
    * @return the shift of each seller
    */
    
    public String getShift() {
        return shift;
    }

    /**
    * Sets the shift of the seller 
    * @param shift sets the shift of each seller
    */
    
    public void setShift(String shift) {
        this.shift = shift;
    }
    
    /**
     * Returns a String containing the object's data in the order and format
     * required for persistence.
     *
     * @return a String with the object's data formatted for persistence.
     */
    
    @Override
    public String textFormat(){
        return getiD() + "|" + getName() + "|" + getContactNumber() + "|" + employeeCode + "|" + shift;
    }
}
