package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.model.Warranty;

import java.time.LocalDate;

/**
 * Converts warranties to the comma-separated line stored in the warranties file
 * and converts each stored line back to a raw {@link WarrantyData}.
 * 
 * @author EstefaniaMarquez
 * 
 */
public class WarrantyRecordMapper {

    /**
     * Builds the stored line of a warranty.
     *
     * @param warranty the warranty to convert
     * @return the line with type code, id, product id, sale id and start date
     */
    public String toLine(Warranty warranty) {
        return warranty.getTypeCode() + ","
                + warranty.getId() + ","
                + warranty.getProduct().getId() + ","
                + warranty.getSale().getId() + ","
                + warranty.getStartDate();
    }

    /**
     * Converts a stored line to a raw record.
     *
     * @param line the stored line
     * @return the raw record
     * @throws IllegalArgumentException if the line has fewer than five fields
     */
    public WarrantyData toWarrantyData(String line) {
        String[] fields = line.split(",", -1);

        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }

        if (fields.length < 5) {
            throw new IllegalArgumentException("Invalid warranty record: " + line);
        }

        return new WarrantyData(
                fields[0], fields[1], fields[2], fields[3],
                LocalDate.parse(fields[4]));
    }
}
