package com.mycompany.gamezone.service;

import com.mycompany.gamezone.exceptions.BusinessRuleException;
import com.mycompany.gamezone.exceptions.InvalidDataException;
import com.mycompany.gamezone.exceptions.ResourceNotFoundException;
import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Person;
import com.mycompany.gamezone.model.Seller;
import com.mycompany.gamezone.persistence.PersonRepository;
import com.mycompany.gamezone.validation.PersonValidator;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for managing Person operations.
 * It validates Customer and Seller information, delegating the checks to
 * {@link PersonValidator} where it applies, and manages the list of
 * Person objects. Every failure is reported with a typed exception from
 * the GameZone exception hierarchy.
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
     * @throws InvalidDataException if any of the provided information
     * is invalid.
     */
    public void validateCustomer(
            String eMail,
            String name,
            String iD,
            long contactNumber) {

        PersonValidator.validatePersonData(
                iD,
                name,
                eMail,
                String.valueOf(contactNumber));

        requireNameWithoutDigits(name);
        requirePositiveContactNumber(contactNumber);
    }

    /**
     * Validates the information provided for a seller.
     *
     * @param employeeCode the unique employee code of the seller.
     * @param shift the work shift assigned to the seller.
     * @param name the name of the seller.
     * @param iD the identification number of the seller.
     * @param contactNumber the contact number of the seller.
     * @throws InvalidDataException if any of the provided information
     * is invalid.
     */
    public void validateSeller(
            String employeeCode,
            String shift,
            String name,
            String iD,
            long contactNumber) {

        requireNotBlank("código de empleado", employeeCode);
        requireNotBlank("turno", shift);
        requireNotBlank("nombre", name);
        requireNameWithoutDigits(name);
        requireNotBlank("identificación", iD);
        requirePositiveContactNumber(contactNumber);
    }

    /**
     * Registers a new customer in the Person list.
     *
     * @param persons the list of Person objects.
     * @param eMail the email address of the customer.
     * @param name the name of the customer.
     * @param iD the identification number of the customer.
     * @param contactNumber the contact number of the customer.
     * @throws InvalidDataException if the information is invalid.
     * @throws BusinessRuleException if a customer with the given ID
     * already exists.
     */
    public void registerCustomer(
            ArrayList<Person> persons,
            String eMail,
            String name,
            String iD,
            long contactNumber) {

        validateCustomer(eMail, name, iD, contactNumber);

        PersonValidator.validateUniquePerson(customersOf(persons), iD);

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
     * @throws InvalidDataException if the information is invalid.
     * @throws BusinessRuleException if a seller with the given employee
     * code already exists.
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
            throw new BusinessRuleException(
                    "Ya existe un vendedor con el código de empleado "
                    + employeeCode.trim() + ".");
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
     * @throws ResourceNotFoundException if the customer does not exist.
     * @throws InvalidDataException if the provided information is invalid.
     */
    public void updateCustomer(
            ArrayList<Person> persons,
            String iD,
            String eMail,
            String name,
            long contactNumber) {

        Customer customer = searchCustomer(persons, iD);

        PersonValidator.validatePersonExists(customer, iD);

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
     * @throws ResourceNotFoundException if the seller does not exist.
     * @throws InvalidDataException if the provided information is invalid.
     */
    public void updateSeller(
            ArrayList<Person> persons,
            String employeeCode,
            String shift,
            String name,
            String iD,
            long contactNumber) {

        Seller seller = searchSeller(persons, employeeCode);

        PersonValidator.validatePersonExists(seller, employeeCode);

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
     * @throws ResourceNotFoundException if no customer with the given ID
     * exists.
     */
    public void deleteCustomer(
            ArrayList<Person> persons,
            String iD) {

        Customer customer = searchCustomer(persons, iD);

        PersonValidator.validatePersonExists(customer, iD);

        persons.remove(customer);
    }

    /**
     * Deletes an existing Seller from the Person list.
     *
     * @param persons the list of Person objects.
     * @param employeeCode the employee code of the seller to delete.
     * @throws ResourceNotFoundException if no seller with the given
     * employee code exists.
     */
    public void deleteSeller(
            ArrayList<Person> persons,
            String employeeCode) {

        Seller seller = searchSeller(persons, employeeCode);

        PersonValidator.validatePersonExists(seller, employeeCode);

        persons.remove(seller);
    }

    /**
     * Retrieves all Person objects from the persistence layer.
     *
     * @return an ArrayList containing all stored Person objects.
     * @throws com.mycompany.gamezone.exceptions.PersistenceException if the
     * persistence file cannot be read.
     */
    public ArrayList<Person> listPersons() {
        return personRepository.listPersons();
    }

    /**
     * Saves the current list of Person objects to the persistence file.
     * The existing file is overwritten.
     *
     * @param persons the list of Person objects to be saved.
     * @throws com.mycompany.gamezone.exceptions.PersistenceException if the
     * persistence file cannot be written.
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

    /**
     * Returns only the customers contained in the given list, so the
     * uniqueness rule is applied among customers as it always was.
     *
     * @param persons the list of Person objects.
     * @return a new list containing only the Customer objects.
     */
    private List<Person> customersOf(ArrayList<Person> persons) {

        List<Person> customers = new ArrayList<>();

        for (Person currentPerson : persons) {

            if (currentPerson instanceof Customer) {
                customers.add(currentPerson);
            }
        }

        return customers;
    }

    /**
     * Ensures that a text field contains something other than whitespace.
     * Used for the seller fields that PersonValidator does not cover.
     *
     * @param field the name of the field, used in the error message.
     * @param value the value to check.
     * @throws InvalidDataException if the value is null or blank.
     */
    private void requireNotBlank(String field, String value) {

        if (value == null || value.trim().isEmpty()) {
            throw new InvalidDataException(field, "no puede estar vacío.");
        }
    }

    /**
     * Ensures that a name does not contain digits.
     *
     * @param name the name to check.
     * @throws InvalidDataException if the name contains a digit.
     */
    private void requireNameWithoutDigits(String name) {

        if (name.matches(".*\\d.*")) {
            throw new InvalidDataException(
                    "nombre", "no puede contener números.");
        }
    }

    /**
     * Ensures that a contact number is greater than zero.
     *
     * @param contactNumber the contact number to check.
     * @throws InvalidDataException if the number is zero or negative.
     */
    private void requirePositiveContactNumber(long contactNumber) {

        if (contactNumber <= 0) {
            throw new InvalidDataException(
                    "número de contacto", "debe ser mayor que cero.");
        }
    }
}