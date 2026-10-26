package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.model.Person;
import java.util.List;

public interface PersonRepository {

    void savePersons(List<Person> persons);

    List<Person> listPersons();
}