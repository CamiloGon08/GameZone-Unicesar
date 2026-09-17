package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.model.BulkPurchaseDiscount;
import com.mycompany.gamezone.model.CategoryDiscount;
import com.mycompany.gamezone.model.PercentageDiscount;
import com.mycompany.gamezone.model.Promotion;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import utilities.FilePath;

/**
 * Repository responsible for the persistence of Promotion objects.
 *
 * This repository stores all promotion types in a single CSV file.
 * A discriminator is used at the beginning of each record to identify
 * the concrete promotion type when the records are loaded.
 *
 * Supported promotion types:
 * - PERCENTAGE
 * - CATEGORY
 * - BULK
 *
 * @author EstefaniaMarquez
 */
public class PromotionRepository {

    private static final String PERCENTAGE_TYPE = "PERCENTAGE";
    private static final String CATEGORY_TYPE = "CATEGORY";
    private static final String BULK_TYPE = "BULK";

    /**
     * Saves all promotions to the promotions file.
     *
     * The existing file content is replaced by the information contained
     * in the provided list.
     *
     * @param promotions list of promotions to persist
     */
    public void saveAll(List<Promotion> promotions) {

        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(FilePath.PROMOTIONS))) {

            for (Promotion promotion : promotions) {
                bw.write(formatPromotion(promotion));
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println(
                    "Error: Failed to save the promotion records.");
        }
    }

    /**
     * Loads all promotions stored in the promotions file.
     *
     * If the file does not exist or cannot be read, an empty list is
     * returned.
     *
     * @return list of persisted promotions
     */
    public List<Promotion> loadAll() {

        List<Promotion> promotions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new FileReader(FilePath.PROMOTIONS))) {

            String line;

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",", -1);

                Promotion promotion = createPromotion(data);

                if (promotion != null) {
                    promotions.add(promotion);
                }
            }

        } catch (IOException e) {
            return promotions;
        }

        return promotions;
    }

    /**
     * Converts a promotion object into its CSV representation.
     *
     * The first field acts as a discriminator that identifies the
     * concrete promotion type.
     *
     * @param promotion promotion to format
     * @return CSV representation of the promotion
     */
    private String formatPromotion(Promotion promotion) {

        if (promotion instanceof PercentageDiscount percentageDiscount) {

            return PERCENTAGE_TYPE + ","
                    + percentageDiscount.getId() + ","
                    + percentageDiscount.getName() + ","
                    + percentageDiscount.getStartDate() + ","
                    + percentageDiscount.getEndDate() + ","
                    + percentageDiscount.getPercentage();

        } else if (promotion instanceof CategoryDiscount categoryDiscount) {

            return CATEGORY_TYPE + ","
                    + categoryDiscount.getId() + ","
                    + categoryDiscount.getName() + ","
                    + categoryDiscount.getStartDate() + ","
                    + categoryDiscount.getEndDate() + ","
                    + categoryDiscount.getTargetCategory() + ","
                    + categoryDiscount.getPercentage();

        } else if (promotion instanceof BulkPurchaseDiscount bulkDiscount) {

            return BULK_TYPE + ","
                    + bulkDiscount.getId() + ","
                    + bulkDiscount.getName() + ","
                    + bulkDiscount.getStartDate() + ","
                    + bulkDiscount.getEndDate() + ","
                    + bulkDiscount.getMinimumQuantity() + ","
                    + bulkDiscount.getPercentage();
        }

        return "";
    }

    /**
     * Creates a concrete Promotion object from its CSV representation.
     *
     * The first field of the record is used as a discriminator to determine
     * which concrete promotion class must be instantiated.
     *
     * @param data fields obtained from a CSV record
     * @return reconstructed promotion, or null if the discriminator
     *         does not correspond to a supported promotion type
     */
    private Promotion createPromotion(String[] data) {

        String type = data[0];

        if (PERCENTAGE_TYPE.equals(type)) {

            String id = data[1];
            String name = data[2];
            LocalDate startDate = LocalDate.parse(data[3]);
            LocalDate endDate = LocalDate.parse(data[4]);
            double percentage = Double.parseDouble(data[5]);

            return new PercentageDiscount(
                    id,
                    name,
                    startDate,
                    endDate,
                    percentage);

        } else if (CATEGORY_TYPE.equals(type)) {

            String id = data[1];
            String name = data[2];
            LocalDate startDate = LocalDate.parse(data[3]);
            LocalDate endDate = LocalDate.parse(data[4]);
            String category = data[5];
            double percentage = Double.parseDouble(data[6]);

            return new CategoryDiscount(
                    id,
                    name,
                    startDate,
                    endDate,
                    percentage,
                    category);

        } else if (BULK_TYPE.equals(type)) {

            String id = data[1];
            String name = data[2];
            LocalDate startDate = LocalDate.parse(data[3]);
            LocalDate endDate = LocalDate.parse(data[4]);
            int minimumQuantity = Integer.parseInt(data[5]);
            double percentage = Double.parseDouble(data[6]);

            return new BulkPurchaseDiscount(
                    id,
                    name,
                    startDate,
                    endDate,
                    minimumQuantity,
                    percentage);
        }

        return null;
    }
}