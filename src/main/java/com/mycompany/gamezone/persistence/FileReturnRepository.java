package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.model.Return;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Text-file implementation of {@link ReturnRepository}. It only opens, reads
 * and writes the file; the line format belongs to {@link ReturnRecordMapper}.
 * 
 * @author EstefaniaMarquez
 * 
 */
public class FileReturnRepository implements ReturnRepository {

    private final String filePath;
    private final ReturnRecordMapper mapper;

    /**
     * Creates a repository backed by the given file.
     *
     * @param filePath path of the returns file
     * @param mapper converter between returns and stored lines
     */
    public FileReturnRepository(String filePath, ReturnRecordMapper mapper) {
        this.filePath = filePath;
        this.mapper = mapper;
    }

    @Override
    public void saveAll(List<Return> returns) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            for (Return returnRecord : returns) {
                writer.write(mapper.toLine(returnRecord));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not save returns to " + filePath, e);
        }
    }

    @Override
    public List<ReturnData> loadAll() {
        List<ReturnData> returns = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                returns.add(mapper.toReturnData(line));
            }

        } catch (IOException e) {
            return returns;
        }

        return returns;
    }
}
