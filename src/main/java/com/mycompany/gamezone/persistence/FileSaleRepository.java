package com.mycompany.gamezone.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gamezone.exceptions.PersistenceException;
import com.mycompany.gamezone.model.Sale;

import utilities.FilePath;

/**
 * Text-file implementation of {@link SaleRepository}. It only opens, reads
 * and writes the sales file; the line format belongs to
 * {@link SaleRecordMapper}.
 *
 * Loading returns raw {@link SaleRecord} objects with the identifiers
 * stored in the file; the service layer resolves the references to
 * products, customers and sellers.
 *
 * Any {@link IOException} raised while accessing the file is caught here and
 * rethrown as a {@link PersistenceException} that identifies the file and the
 * operation that failed.
 * 
 * @author EstefaniaMarquez
 * 
 */

public class FileSaleRepository implements SaleRepository {

    private static final String FILE_PATH = FilePath.SALES;
    private static final String SEPARATOR = "\\|";

    private final SaleRecordMapper mapper;

    /**
     * Creates a repository backed by the sales file.
     *
     * @param mapper converter between sales and stored lines
     */
    public FileSaleRepository(SaleRecordMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Saves the given list of sales to the sales file. Each sale is
     * written on its own line using the format produced by
     * {@link SaleRecordMapper#toLine(Sale)}.
     *
     * @param sales list of sales to persist
     * @throws PersistenceException if the file cannot be written
     */
    @Override
    public void save(List<Sale> sales) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Sale sale : sales) {
                writer.write(mapper.toLine(sale));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenceException(
                    "Write error: could not save sales to file '" + FILE_PATH + "'", e);
        }
    }

    /**
     * Loads the sale records stored in the sales file.
     *
     * @return list of records recovered from the file, or an empty list
     *         if the file does not exist or contains no data
     * @throws PersistenceException if the file cannot be read or contains a
     *         malformed record
     */
    @Override
    public List<SaleRecord> loadAll() {
        List<SaleRecord> records = new ArrayList<>();

        if (!Files.exists(Paths.get(FILE_PATH))) {
            return records;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] data = line.split(SEPARATOR, -1);
                SaleRecord record = parseRecord(data, lineNumber);
                if (record != null) {
                    records.add(record);
                }
            }
        } catch (IOException e) {
            throw new PersistenceException(
                    "Read error: could not load sales from file '" + FILE_PATH + "'", e);
        }
        return records;
    }

    /**
     * Converts one line of the sales file into a record, translating any
     * format problem into a {@link PersistenceException} that identifies the
     * file and the offending line.
     *
     * @param data       the fields of the record
     * @param lineNumber the 1-based number of the line in the file
     * @return the record, or null if the line has too few fields
     * @throws PersistenceException if the record is malformed
     */
    private SaleRecord parseRecord(String[] data, int lineNumber) {
        try {
            return mapper.toRecord(data);
        } catch (DateTimeParseException e) {
            throw new PersistenceException(
                    "Read error: malformed sale record at line " + lineNumber
                            + " of file '" + FILE_PATH + "'", e);
        }
    }
}
