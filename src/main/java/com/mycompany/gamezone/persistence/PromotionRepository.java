package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.model.Promotion;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import utilities.FilePath;

public class PromotionRepository {

    private final PromotionRecordMapper mapper;

    public PromotionRepository(PromotionRecordMapper mapper) {
        this.mapper = mapper;
    }

    public void saveAll(List<Promotion> promotions) {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(FilePath.PROMOTIONS))) {

            for (Promotion promotion : promotions) {
                bw.write(mapper.toLine(promotion));
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error: Failed to save the promotion records.");
        }
    }

    public List<Promotion> loadAll() {
        List<Promotion> promotions = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new FileReader(FilePath.PROMOTIONS))) {

            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                Promotion promotion = mapper.toPromotion(line.split(",", -1));

                if (promotion != null) {
                    promotions.add(promotion);
                }
            }

        } catch (IOException e) {
            return promotions;
        }

        return promotions;
    }
}