package com.mycompany.gamezone.validation;

import com.mycompany.gamezone.exceptions.BusinessRuleException;
import com.mycompany.gamezone.exceptions.InvalidDataException;
import com.mycompany.gamezone.exceptions.ResourceNotFoundException;
import com.mycompany.gamezone.model.Person;
import java.util.List;

/** Stateless person checks; callers supply the people already loaded. */
public final class PersonValidator {
    private PersonValidator() { }

    /**
     * Validates the four fields specified by requirement 6.
     * @param id person identifier
     * @param name person's name
     * @param email email containing an at sign and a dot
     * @param phone required phone text
     * @throws InvalidDataException if a required field or the email is invalid
     */
    public static void validatePersonData(String id, String name, String email, String phone) {
        requireText(id, "identificador");
        requireText(name, "nombre");
        requireText(email, "correo electrónico");
        requireText(phone, "teléfono");
        if (!email.contains("@") || !email.contains(".")) {
            throw new InvalidDataException("correo electrónico", "debe contener arroba y punto.");
        }
    }

    /**
     * @param person lookup result
     * @param personId requested identifier
     * @throws ResourceNotFoundException if the lookup returned null
     */
    public static void validatePersonExists(Person person, String personId) {
        if (person == null) {
            throw new ResourceNotFoundException("persona", personId);
        }
    }

    /**
     * Uses the existing case-sensitive person identifier comparison.
     * @param existing current person list
     * @param newId identifier to register
     * @throws InvalidDataException if the list, an entry, or the identifier is invalid
     * @throws BusinessRuleException if the identifier is already present
     */
    public static void validateUniquePerson(List<Person> existing, String newId) {
        requireText(newId, "identificador");
        if (existing == null) {
            throw new InvalidDataException("personas", "la lista no puede ser nula.");
        }
        for (Person person : existing) {
            if (person == null) {
                throw new InvalidDataException("personas", "la lista contiene una persona nula.");
            }
            if (newId.equals(person.getiD())) {
                throw new BusinessRuleException("Ya existe una persona con identificador " + newId + ".");
            }
        }
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidDataException(field, "no puede estar vacío.");
        }
    }
}
