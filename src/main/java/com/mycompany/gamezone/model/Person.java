package com.mycompany.gamezone.model;
import java.util.List;

/**
 * Base Abstract Class for every person in GameZone Unicesar
 * Holds common atributes and methods shared by every subclass
 * 
 * @author EstefaniaMarquez
 */

public abstract class Person {
    private String name;
    private String iD;
    private long contactNumber;

    public Person() {
    }

    /**
    * Creates a new Person
    * 
    * @param name the name of the person
    * @param iD the identification number of the person
    * @param contactNumber the contact number of the person
    */ 
    
    public Person(String name, String iD, long contactNumber) {
        this.name = name;
        this.iD = iD;
        this.contactNumber = contactNumber;
    }

    /**
    * Gets the name of the person 
    * @return the person's name
    */
    
    public String getName() {
        return name;
    }

    /**
    * Sets the name of the person 
    * @param name sets the person's name
    */
    
    public void setName(String name) {
        this.name = name;
    }

    /**
    * Gets the Identification Number of the person 
    * @return the person's iD
    */
    
    public String getiD() {
        return iD;
    }

    /**
    * Sets the ID of the person 
    * @param iD sets the person's ID
    */
    
    public void setiD(String iD) {
        this.iD = iD;
    }

    /**
     * Gets the contact number of the person
     *
     * @return the person's contact number
     */
    public long getContactNumber() {
        return contactNumber;
    }

    /**
     * Sets the contact number of the person
     *
     * @param contactNumber sets the person's contact number
     */
    public void setContactNumber(long contactNumber) {
        this.contactNumber = contactNumber;
    }

    /**
     * Unique key inside its own type (iD for customers, employee code for
     * sellers).
     *
     * @return
     */
    public abstract String getKey();

    /**
     * Validates its own data. Same exceptions and messages as before.
     */
    public abstract void validate();

    /**
     * Throws if a person of the same type already uses this key.
     *
     * @param personsOfSameType
     */
    public abstract void requireUniqueAmong(List<Person> personsOfSameType);

    /**
     * Copies the editable data of {@code changes} (same type and key) into this
     * person.
     *
     * @param changes
     */
    public abstract void updateFrom(Person changes);

    /**
     * Returns the object's information formatted as a tab-separated string for
     * file persistence.
     *
     * @return a tab-separated string containing the object's information
     */
    public abstract String textFormat();

}
