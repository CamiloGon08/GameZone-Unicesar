package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.model.Warranty;
import utilities.FilePath;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Text-file implementation of {@link WarrantyRepository}. It only opens, reads
 * and writes the file; the line format belongs to {@link WarrantyRecordMapper}.
 * 
 * @author EstefaniaMarquez
 * 
 */
public class FileWarrantyRepository implements WarrantyRepository {

    private final String filePath;
    private final WarrantyRecordMapper mapper;

    /**
     * Creates a repository backed by the warranties file.
     *
     * @param mapper converter between warranties and stored lines
     */
    public FileWarrantyRepository(WarrantyRecordMapper mapper) {
        this.filePath = FilePath.WARRANTIES;
        this.mapper = mapper;
    }

    @Override
    public void saveAll(List<Warranty> warranties) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            for (Warranty warranty : warranties) {
                writer.write(mapper.toLine(warranty));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not save warranties to " + filePath, e);
        }
    }

    @Override
    public List<WarrantyData> loadAll() {
        List<WarrantyData> warranties = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                WarrantyData warranty = mapper.toWarrantyData(line);

                if (warranty != null) {
                    warranties.add(warranty);
                }
            }

        } catch (FileNotFoundException e) {
            return warranties;

        } catch (IOException e) {
            throw new RuntimeException("Could not load warranties from " + filePath, e);
        }

        return warranties;
    }
}
