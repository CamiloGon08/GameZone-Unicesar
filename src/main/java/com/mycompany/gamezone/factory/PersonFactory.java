package com.mycompany.gamezone.factory;

import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Person;
import com.mycompany.gamezone.model.Seller;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Builds each Person subtype from its type key. A new type only registers a
 * builder here.
 * 
 * @author EstefaniaMarquez
 * 
 */
public class PersonFactory {

    public static final String CUSTOMER_TYPE = "CUSTOMER";
    public static final String SELLER_TYPE = "SELLER";

    public static final String ID = "iD";
    public static final String NAME = "name";
    public static final String CONTACT_NUMBER = "contactNumber";
    public static final String E_MAIL = "eMail";
    public static final String EMPLOYEE_CODE = "employeeCode";
    public static final String SHIFT = "shift";

    private final Map<String, Function<Map<String, String>, Person>> builders = new HashMap<>();

    public PersonFactory() {
        builders.put(CUSTOMER_TYPE, a -> createCustomer(
                a.get(E_MAIL), a.get(NAME), a.get(ID),
                Long.parseLong(a.get(CONTACT_NUMBER))));

        builders.put(SELLER_TYPE, a -> createSeller(
                a.get(EMPLOYEE_CODE), a.get(SHIFT), a.get(NAME), a.get(ID),
                Long.parseLong(a.get(CONTACT_NUMBER))));
    }

    /**
     * @return the person of that type, or null if the type key is unknown.
     */
    public Person create(String typeKey, Map<String, String> attributes) {
        Function<Map<String, String>, Person> builder = builders.get(typeKey);
        return builder == null ? null : builder.apply(attributes);
    }

    public Customer createCustomer(String eMail, String name, String iD, long contactNumber) {
        return new Customer(eMail, name, iD, contactNumber);
    }

    public Seller createSeller(String employeeCode, String shift, String name,
            String iD, long contactNumber) {
        return new Seller(employeeCode, shift, name, iD, contactNumber);
    }
}
