package com.mycompany.gamezone.service;

import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Person;
import com.mycompany.gamezone.model.Seller;
import com.mycompany.gamezone.persistence.PersonRepository;
import com.mycompany.gamezone.validation.PersonValidator;
import java.util.ArrayList;
import java.util.List;

/**
 * Owns the persons list and persists after every change.
 *
 * @author EstefaniaMarquez
 * 
 */
public class PersonService {

    private final PersonRepository personRepository;
    private final List<Person> persons;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
        this.persons = new ArrayList<>(personRepository.listPersons());
    }

    /**
     * Registers any person type: validate, check uniqueness, add and save.
     */
    public void register(Person person) {
        person.validate();
        person.requireUniqueAmong(personsOfSameType(person));

        persons.add(person);
        persistPersons();
    }

    /**
     * Updates the stored person of the same type and key with the data of
     * {@code changes}.
     */
    public void update(Person changes) {
        Person current = search(changes.getClass(), changes.getKey());

        PersonValidator.validatePersonExists(current, changes.getKey());
        changes.validate();

        current.updateFrom(changes);
        persistPersons();
    }

    /**
     * Deletes by type and key (iD for Customer, employee code for Seller).
     */
    public void delete(Class<? extends Person> type, String key) {
        Person person = search(type, key);

        PersonValidator.validatePersonExists(person, key);

        persons.remove(person);
        persistPersons();
    }

    /**
     * @return the person of that type with that key, or null.
     */
    public <T extends Person> T search(Class<T> type, String key) {
        for (Person currentPerson : persons) {
            if (type.isInstance(currentPerson)
                    && currentPerson.getKey().equals(key)) {
                return type.cast(currentPerson);
            }
        }
        return null;
    }

    /**
     * @return the first person of any type with that iD, or null.
     */
    public Person searchPerson(String iD) {
        for (Person currentPerson : persons) {
            if (currentPerson.getiD().equals(iD)) {
                return currentPerson;
            }
        }
        return null;
    }

    public List<Person> listPersons() {
        return new ArrayList<>(persons);
    }

    public List<Customer> listCustomers() {
        return listByType(Customer.class);
    }

    public List<Seller> listSellers() {
        return listByType(Seller.class);
    }

    private <T extends Person> List<T> listByType(Class<T> type) {
        List<T> result = new ArrayList<>();

        for (Person currentPerson : persons) {
            if (type.isInstance(currentPerson)) {
                result.add(type.cast(currentPerson));
            }
        }
        return result;
    }

    private List<Person> personsOfSameType(Person person) {
        List<Person> sameType = new ArrayList<>();

        for (Person currentPerson : persons) {
            if (currentPerson.getClass().equals(person.getClass())) {
                sameType.add(currentPerson);
            }
        }
        return sameType;
    }

    private void persistPersons() {
        personRepository.savePersons(persons);
    }
}
