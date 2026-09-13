package com.mycompany.gamezone.model;


public abstract class Person {
    private String name;
    private long iD;
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
    
    public Person(String name, long iD, long contactNumber) {
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
    
    public long getiD() {
        return iD;
    }

    /**
    * Sets the ID of the person 
    * @param iD sets the person's ID
    */
    
    public void setiD(long iD) {
        this.iD = iD;
    }

    /**
    * Gets the contact number of the person 
    * @return the person's contact number
    */
    
    public long getContactNumber() {
        return contactNumber;
    }

    /**
    * Sets the contact number of the person 
    * @param contactNumber sets the person's contact number
    */
    
    public void setContactNumber(long contactNumber) {
        this.contactNumber = contactNumber;
    }
    
}
