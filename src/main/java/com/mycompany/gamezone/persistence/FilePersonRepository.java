package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.exceptions.PersistenceException;
import com.mycompany.gamezone.model.Person;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import utilities.FilePath;

public class FilePersonRepository implements PersonRepository {

    private final PersonRecordMapper mapper;

    public FilePersonRepository(PersonRecordMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void savePersons(List<Person> persons) {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(FilePath.PERSONS))) {

            for (Person currentPerson : persons) {
                bw.write(mapper.toLine(currentPerson));
                bw.newLine();
            }

        } catch (IOException e) {
            throw new PersistenceException(
                    "Write error: could not save persons to file '"
                    + FilePath.PERSONS + "'", e);
        }
    }

    @Override
    public List<Person> listPersons() {
        List<Person> persons = new ArrayList<>();

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

                Person person = parseRecord(line.split("\\|", -1), lineNumber);

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

    private Person parseRecord(String[] fields, int lineNumber) {
        try {
            return mapper.toPerson(fields);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new PersistenceException(
                    "Read error: malformed person record at line "
                    + lineNumber + " of file '" + FilePath.PERSONS + "'", e);
        }
    }
}
