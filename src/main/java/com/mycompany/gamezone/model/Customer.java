package com.mycompany.gamezone.model;
import com.mycompany.gamezone.validation.PersonValidator;
import java.util.List;

/**
 * Concret class for every Customer in GameZone Unicesar
 * It extends from Person and contains specific Customer information
 * 
 * @author EstefaniaMarquez
 */

public class Customer extends Person{
    
    private String eMail;
    
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

    @Override
    public String getKey() {
        return getiD();
    }

    @Override
    public void validate() {
        PersonValidator.validatePersonData(
                getiD(), getName(), eMail, String.valueOf(getContactNumber()));
        PersonValidator.validateNameWithoutDigits(getName());
        PersonValidator.validatePositiveContactNumber(getContactNumber());
    }

    @Override
    public void requireUniqueAmong(List<Person> personsOfSameType) {
        PersonValidator.validateUniquePerson(personsOfSameType, getiD());
    }

    @Override
    public void updateFrom(Person changes) {
        Customer other = (Customer) changes;
        seteMail(other.geteMail());
        setName(other.getName());
        setContactNumber(other.getContactNumber());
    }

}

