package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Person;
import com.mycompany.gamezone.model.Seller;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
 * @author EstefaniaMarquez
 */
public class PersonRepository {

    private static final String CUSTOMER_TYPE = "CUSTOMER";
    private static final String SELLER_TYPE = "SELLER";

    /**
     * Saves all Person records by overwriting the persistence file.
     *
     * @param persons the list of Person objects to be saved
     */
    public void savePersons(ArrayList<Person> persons) {

        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(FilePath.PERSONS))) {

            for (Person currentPerson : persons) {
                bw.write(currentPerson.textFormat());
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println(
                    "Error: Failed to save the persons records.");
        }
    }

    /**
     * Retrieves all Person records from the persistence file.
     *
     * @return an ArrayList containing all stored Person objects
     */
    public ArrayList<Person> listPersons() {

        ArrayList<Person> persons = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new FileReader(FilePath.PERSONS))) {

            String line;

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] infoPerson = line.split("\\|", -1);

                Person person = createPerson(infoPerson);

                if (person != null) {
                    persons.add(person);
                }
            }

        } catch (IOException e) {
            System.out.println(
                    "Error: Failed to list the persons records.");
        }

        return persons;
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
     * @param id the ID to search for
     * @return the Person with the specified ID,
     * or null if it is not found
     */
    public Person searchPerson(String id) {

        ArrayList<Person> persons = listPersons();

        for (Person currentPerson : persons) {

            if (currentPerson.getiD().equals(id)) {
                return currentPerson;
            }
        }

        return null;
    }

    /**
     * Searches for a Seller by employee code.
     *
     * @param employeeCode the employee code to search for
     * @return the Seller with the specified employee code,
     * or null if it is not found
     */
    public Seller searchSeller(String employeeCode) {

        ArrayList<Person> persons = listPersons();

        for (Person currentPerson : persons) {

            if (currentPerson instanceof Seller seller) {

                if (seller.getEmployeeCode()
                        .equals(employeeCode)) {

                    return seller;
                }
            }
        }

        return null;
    }
}