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

    /**
     * Validates seller data in the same order as the original service.
     *
     * @param employeeCode required employee code
     * @param shift required work shift
     * @param name required name checked with the existing digit rule
     * @param id required identification
     * @param contactNumber positive contact number
     * @throws InvalidDataException for the first invalid field
     */
    public static void validateSellerData(String employeeCode, String shift, String name,
            String id, long contactNumber) {
        requireNotBlank("código de empleado", employeeCode);
        requireNotBlank("turno", shift);
        requireNotBlank("nombre", name);
        validateNameWithoutDigits(name);
        requireNotBlank("identificación", id);
        validatePositiveContactNumber(contactNumber);
    }

    /**
     * Provides the seller-validation name used by the current Seller consumer.
     *
     * @param employeeCode required employee code
     * @param shift required work shift
     * @param name required name
     * @param id required identification
     * @param contactNumber positive contact number
     * @throws InvalidDataException for the first invalid field
     */
    public static void validateSeller(String employeeCode, String shift, String name,
            String id, long contactNumber) {
        validateSellerData(employeeCode, shift, name, id, contactNumber);
    }

    /**
     * Applies the original service's name pattern without changing its semantics.
     *
     * @param name the previously checked, non-null name
     * @throws InvalidDataException if the name matches the existing digit pattern
     */
    public static void validateNameWithoutDigits(String name) {
        if (name.matches(".*\\d.*")) {
            throw new InvalidDataException("nombre", "no puede contener números.");
        }
    }

    /**
     * Checks the contact number using the original service's positive-long rule.
     *
     * @param contactNumber the number to check
     * @throws InvalidDataException if the number is zero or negative
     */
    public static void validatePositiveContactNumber(long contactNumber) {
        if (contactNumber <= 0) {
            throw new InvalidDataException("número de contacto", "debe ser mayor que cero.");
        }
    }

    /** Preserves the seller service's trim-based check, separate from the R6 rule. */
    private static void requireNotBlank(String field, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidDataException(field, "no puede estar vacío.");
        }
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidDataException(field, "no puede estar vacío.");
        }
    }
}
