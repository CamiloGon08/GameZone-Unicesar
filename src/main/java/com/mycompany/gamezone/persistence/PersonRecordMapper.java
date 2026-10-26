package com.mycompany.gamezone.persistence;

import static com.mycompany.gamezone.factory.PersonFactory.*;

import com.mycompany.gamezone.factory.PersonFactory;
import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Person;
import com.mycompany.gamezone.model.Seller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Knows the text layout of each person type in persons.txt.
 *
 * @author EstefaniaMarquez
 * 
 */
public class PersonRecordMapper {

    private static final String SEPARATOR = "|";

    private final PersonFactory personFactory;
    private final List<RecordLayout<?>> layouts = new ArrayList<>();

    public PersonRecordMapper(PersonFactory personFactory) {
        this.personFactory = personFactory;

        layouts.add(new RecordLayout<>(CUSTOMER_TYPE, Customer.class,
                List.of(ID, NAME, E_MAIL, CONTACT_NUMBER),
                c -> new Object[]{c.getiD(), c.getName(), c.geteMail(), c.getContactNumber()}));

        layouts.add(new RecordLayout<>(SELLER_TYPE, Seller.class,
                List.of(ID, NAME, CONTACT_NUMBER, EMPLOYEE_CODE, SHIFT),
                s -> new Object[]{s.getiD(), s.getName(), s.getContactNumber(),
                    s.getEmployeeCode(), s.getShift()}));
    }

    public String toLine(Person person) {
        for (RecordLayout<?> layout : layouts) {
            if (layout.supports(person)) {
                return layout.format(person);
            }
        }
        throw new IllegalArgumentException(
                "Unsupported person type: " + person.getClass());
    }

    /**
     * @return the person, or null if the type key is unknown.
     */
    public Person toPerson(String[] fields) {
        String type = fields[0];

        for (RecordLayout<?> layout : layouts) {
            if (layout.typeKey.equals(type)) {
                Map<String, String> attributes = new HashMap<>();
                for (int i = 0; i < layout.attributeNames.size(); i++) {
                    attributes.put(layout.attributeNames.get(i), fields[i + 1]);
                }
                return personFactory.create(type, attributes);
            }
        }
        return null;
    }

    private static final class RecordLayout<T extends Person> {

        private final String typeKey;
        private final Class<T> personClass;
        private final List<String> attributeNames;
        private final Function<T, Object[]> valuesOf;

        private RecordLayout(String typeKey, Class<T> personClass,
                List<String> attributeNames, Function<T, Object[]> valuesOf) {
            this.typeKey = typeKey;
            this.personClass = personClass;
            this.attributeNames = attributeNames;
            this.valuesOf = valuesOf;
        }

        private boolean supports(Person person) {
            return personClass.isInstance(person);
        }

        private String format(Person person) {
            StringBuilder line = new StringBuilder(typeKey);
            for (Object value : valuesOf.apply(personClass.cast(person))) {
                line.append(SEPARATOR).append(String.valueOf(value));
            }
            return line.toString();
        }
    }
}
