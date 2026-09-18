package com.mycompany.gamezone.service;

import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Person;
import com.mycompany.gamezone.model.Seller;
import com.mycompany.gamezone.persistence.PersonRepository;
import java.util.ArrayList;

/**
 * Service class responsible for managing Person operations.
 * It validates Customer and Seller information and manages
 * the list of Person objects.
 *
 * The service does not directly modify the persistence file
 * when a Person is registered, updated or deleted.
 * Changes are made to the Person ArrayList and can later
 * be persisted through the PersonRepository.
 *
 * @author EstefaniaMarquez
 */
public class PersonService {

    private final PersonRepository personRepository = new PersonRepository();

    /**
     * Validates the information provided for a customer.
     *
     * @param eMail the email address of the customer.
     * @param name the name of the customer.
     * @param iD the identification number of the customer.
     * @param contactNumber the contact number of the customer.
     * @throws IllegalArgumentException if any of the provided information
     * is invalid.
     */
    public void validateCustomer(
            String eMail,
            String name,
            String iD,
            long contactNumber) {

        if (eMail == null || eMail.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Email cannot be empty");
        }

        if (!eMail.contains("@") || !eMail.contains(".")) {
            throw new IllegalArgumentException(
                    "Invalid email format");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Name cannot be empty");
        }

        if (name.matches(".*\\d.*")) {
            throw new IllegalArgumentException(
                    "Name cannot contain numbers");
        }

        if (iD == null || iD.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "ID cannot be empty");
        }

        if (contactNumber <= 0) {
            throw new IllegalArgumentException(
                    "Contact number must be greater than zero");
        }
    }

    /**
     * Validates the information provided for a seller.
     *
     * @param employeeCode the unique employee code of the seller.
     * @param shift the work shift assigned to the seller.
     * @param name the name of the seller.
     * @param iD the identification number of the seller.
     * @param contactNumber the contact number of the seller.
     * @throws IllegalArgumentException if any of the provided information
     * is invalid.
     */
    public void validateSeller(
            String employeeCode,
            String shift,
            String name,
            String iD,
            long contactNumber) {

        if (employeeCode == null || employeeCode.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Employee code cannot be empty");
        }

        if (shift == null || shift.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Shift cannot be empty");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Name cannot be empty");
        }

        if (name.matches(".*\\d.*")) {
            throw new IllegalArgumentException(
                    "Name cannot contain numbers");
        }

        if (iD == null || iD.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "ID cannot be empty");
        }

        if (contactNumber <= 0) {
            throw new IllegalArgumentException(
                    "Contact number must be greater than zero");
        }
    }

    /**
     * Registers a new customer in the Person list.
     *
     * @param persons the list of Person objects.
     * @param eMail the email address of the customer.
     * @param name the name of the customer.
     * @param iD the identification number of the customer.
     * @param contactNumber the contact number of the customer.
     * @throws IllegalArgumentException if the information is invalid
     * or if a customer with the given ID already exists.
     */
    public void registerCustomer(
            ArrayList<Person> persons,
            String eMail,
            String name,
            String iD,
            long contactNumber) {

        validateCustomer(eMail, name, iD, contactNumber);

        if (searchCustomer(persons, iD) != null) {
            throw new IllegalArgumentException(
                    "A customer with this ID already exists");
        }

        Customer customer = new Customer(
                eMail,
                name,
                iD,
                contactNumber
        );

        persons.add(customer);
    }

    /**
     * Registers a new seller in the Person list.
     *
     * @param persons the list of Person objects.
     * @param employeeCode the unique employee code of the seller.
     * @param shift the work shift assigned to the seller.
     * @param name the name of the seller.
     * @param iD the identification number of the seller.
     * @param contactNumber the contact number of the seller.
     * @throws IllegalArgumentException if the information is invalid
     * or if a seller with the given employee code already exists.
     */
    public void registerSeller(
            ArrayList<Person> persons,
            String employeeCode,
            String shift,
            String name,
            String iD,
            long contactNumber) {

        validateSeller(
                employeeCode,
                shift,
                name,
                iD,
                contactNumber
        );

        if (searchSeller(persons, employeeCode) != null) {
            throw new IllegalArgumentException(
                    "A seller with this employee code already exists");
        }

        Seller seller = new Seller(
                employeeCode,
                shift,
                name,
                iD,
                contactNumber
        );

        persons.add(seller);
    }

    /**
     * Updates an existing Customer in the Person list.
     *
     * @param persons the list of Person objects.
     * @param iD the identification number of the customer to update.
     * @param eMail the new email address of the customer.
     * @param name the new name of the customer.
     * @param contactNumber the new contact number of the customer.
     * @throws IllegalArgumentException if the customer does not exist
     * or if the provided information is invalid.
     */
    public void updateCustomer(
            ArrayList<Person> persons,
            String iD,
            String eMail,
            String name,
            long contactNumber) {

        Customer customer = searchCustomer(persons, iD);

        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer not found");
        }

        validateCustomer(
                eMail,
                name,
                iD,
                contactNumber
        );

        customer.seteMail(eMail);
        customer.setName(name);
        customer.setContactNumber(contactNumber);
    }

    /**
     * Updates an existing Seller in the Person list.
     *
     * @param persons the list of Person objects.
     * @param employeeCode the employee code of the seller to update.
     * @param shift the new work shift of the seller.
     * @param name the new name of the seller.
     * @param iD the new identification number of the seller.
     * @param contactNumber the new contact number of the seller.
     * @throws IllegalArgumentException if the seller does not exist
     * or if the provided information is invalid.
     */
    public void updateSeller(
            ArrayList<Person> persons,
            String employeeCode,
            String shift,
            String name,
            String iD,
            long contactNumber) {

        Seller seller = searchSeller(persons, employeeCode);

        if (seller == null) {
            throw new IllegalArgumentException(
                    "Seller not found");
        }

        validateSeller(
                employeeCode,
                shift,
                name,
                iD,
                contactNumber
        );

        seller.setShift(shift);
        seller.setName(name);
        seller.setiD(iD);
        seller.setContactNumber(contactNumber);
    }

    /**
     * Deletes an existing Customer from the Person list.
     *
     * @param persons the list of Person objects.
     * @param iD the identification number of the customer to delete.
     * @throws IllegalArgumentException if no customer with the given ID
     * exists.
     */
    public void deleteCustomer(
            ArrayList<Person> persons,
            String iD) {

        Customer customer = searchCustomer(persons, iD);

        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer not found");
        }

        persons.remove(customer);
    }

    /**
     * Deletes an existing Seller from the Person list.
     *
     * @param persons the list of Person objects.
     * @param employeeCode the employee code of the seller to delete.
     * @throws IllegalArgumentException if no seller with the given
     * employee code exists.
     */
    public void deleteSeller(
            ArrayList<Person> persons,
            String employeeCode) {

        Seller seller = searchSeller(persons, employeeCode);

        if (seller == null) {
            throw new IllegalArgumentException(
                    "Seller not found");
        }

        persons.remove(seller);
    }

    /**
     * Retrieves all Person objects from the persistence layer.
     *
     * @return an ArrayList containing all stored Person objects.
     */
    public ArrayList<Person> listPersons() {
        return personRepository.listPersons();
    }

    /**
     * Saves the current list of Person objects to the persistence file.
     * The existing file is overwritten.
     *
     * @param persons the list of Person objects to be saved.
     */
    public void savePersons(ArrayList<Person> persons) {
        personRepository.savePersons(persons);
    }

    /**
     * Searches for a Person by identification number.
     *
     * @param persons the list of Person objects.
     * @param iD the identification number to search for.
     * @return the Person with the specified ID, or null if it is not found.
     */
    public Person searchPerson(
            ArrayList<Person> persons,
            String iD) {

        for (Person currentPerson : persons) {

            if (currentPerson.getiD().equals(iD)) {
                return currentPerson;
            }
        }

        return null;
    }

    /**
     * Searches for a Customer by identification number.
     *
     * @param persons the list of Person objects.
     * @param iD the identification number of the customer.
     * @return the Customer with the specified ID, or null if it is not found.
     */
    public Customer searchCustomer(
            ArrayList<Person> persons,
            String iD) {

        return personRepository.searchCustomer(persons, iD);
    }
    
    /**
     * Searches for a Seller by employee code.
     *
     * @param persons the list of Person objects.
     * @param employeeCode the employee code of the seller.
     * @return the Seller with the specified employee code,
     * or null if it is not found.
     */
    public Seller searchSeller(
        ArrayList<Person> persons,
        String employeeCode) {

    return personRepository.searchSeller(persons, employeeCode);
}
}

