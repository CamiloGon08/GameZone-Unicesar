package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.exceptions.PersistenceException;
import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Person;
import com.mycompany.gamezone.model.Seller;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import utilities.FilePath;

/**
 * Repository responsible for the persistence of Person objects.
 * It handles both Customer and Seller records in a single file.
 *
 * Each record begins with a discriminator that identifies
 * the concrete type of Person:
 *
 * CUSTOMER|ID|name|email|contactNumber
 * SELLER|ID|name|contactNumber|employeeCode|shift
 *
 * Any {@link IOException} raised while accessing the file is caught here and
 * rethrown as a {@link PersistenceException} that identifies the file and the
 * operation that failed.
 *
 * @author EstefaniaMarquez
 */

public class PersonRepository {

    private static final String CUSTOMER_TYPE = "CUSTOMER";
    private static final String SELLER_TYPE = "SELLER";

    /**
     * Saves all Person records by overwriting the persistence file.
     *
     * @param persons the list of Person objects to be saved
     * @throws PersistenceException if the file cannot be written
     */
    public void savePersons(ArrayList<Person> persons) {

        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(FilePath.PERSONS))) {

            for (Person currentPerson : persons) {
                bw.write(currentPerson.textFormat());
                bw.newLine();
            }

        } catch (IOException e) {
            throw new PersistenceException(
                    "Write error: could not save persons to file '"
                    + FilePath.PERSONS + "'", e);
        }
    }

    /**
     * Retrieves all Person records from the persistence file.
     *
     * @return an ArrayList containing all stored Person objects; an empty list
     * if the file does not exist yet (for example, on the first run)
     * @throws PersistenceException if the file cannot be read or contains a
     * malformed record
     */
    public ArrayList<Person> listPersons() {

        ArrayList<Person> persons = new ArrayList<>();

        if (!Files.exists(Paths.get(FilePath.PERSONS))) {
            return persons;
        }

        try (BufferedReader br = new BufferedReader(
                new FileReader(FilePath.PERSONS))) {

            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {

                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] infoPerson = line.split("\\|", -1);

                Person person = parseRecord(infoPerson, lineNumber);

                if (person != null) {
                    persons.add(person);
                }
            }

        } catch (IOException e) {
            throw new PersistenceException(
                    "Read error: could not load persons from file '"
                    + FilePath.PERSONS + "'", e);
        }

        return persons;
    }

    /**
     * Converts one persistence record into a Person, translating any format
     * problem into a {@link PersistenceException} that identifies the file and
     * the offending line.
     *
     * @param infoPerson the fields of the persistence record
     * @param lineNumber the 1-based number of the line in the file
     * @return a Customer or Seller object, or null if the type is unknown
     * @throws PersistenceException if the record is malformed
     */
    private Person parseRecord(String[] infoPerson, int lineNumber) {

        try {
            return createPerson(infoPerson);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new PersistenceException(
                    "Read error: malformed person record at line "
                    + lineNumber + " of file '" + FilePath.PERSONS + "'", e);
        }
    }

    /**
     * Creates a Person object from a persistence record.
     * The first field determines the concrete type.
     *
     * @param infoPerson the fields of the persistence record
     * @return a Customer or Seller object
     */
    private Person createPerson(String[] infoPerson) {

        String type = infoPerson[0];

        if (CUSTOMER_TYPE.equals(type)) {

            String iD = infoPerson[1];
            String name = infoPerson[2];
            String eMail = infoPerson[3];
            long contactNumber = Long.parseLong(infoPerson[4]);

            return new Customer(
                    eMail,
                    name,
                    iD,
                    contactNumber
            );

        } else if (SELLER_TYPE.equals(type)) {

            String iD = infoPerson[1];
            String name = infoPerson[2];
            long contactNumber = Long.parseLong(infoPerson[3]);
            String employeeCode = infoPerson[4];
            String shift = infoPerson[5];

            return new Seller(
                    employeeCode,
                    shift,
                    name,
                    iD,
                    contactNumber
            );
        }

        return null;
    }

    /**
     * Searches for a Person by ID.
     *
     * @param persons
     * @param id the ID to search for
     * @return the Person with the specified ID,
     * or null if it is not found
     */
    public Person searchPerson(ArrayList<Person> persons, String id) {

        for (Person currentPerson : persons) {

            if (currentPerson.getiD().equals(id)) {
                return currentPerson;
            }
        }

        return null;
    }

    /**
     * Searches for a Customer by ID.
     *
     * @param persons
     * @param iD the ID to search for
     * @return the Customer with the specified ID code, or null if it is not
     * found
     */
    
    public Customer searchCustomer(
            ArrayList<Person> persons,
            String iD) {

        for (Person currentPerson : persons) {

            if (currentPerson instanceof Customer customer) {

                if (customer.getiD().equals(iD)) {
                    return customer;
                }
            }
        }

        return null;
    }

    /**
     * Searches for a Seller by employee code.
     *
     * @param persons
     * @param employeeCode the employee code to search for
     * @return the Seller with the specified employee code, or null if it is not
     * found
     */
    public Seller searchSeller(
            ArrayList<Person> persons,
            String employeeCode) {

        for (Person currentPerson : persons) {

            if (currentPerson instanceof Seller seller) {

                if (seller.getEmployeeCode().equals(employeeCode)) {
                    return seller;
                }
            }
        }

        return null;
    }
}