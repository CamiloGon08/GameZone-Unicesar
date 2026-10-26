package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.model.Accessory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import utilities.FilePath;

public class AccessoryRepository {

    private final AccessoryRecordMapper mapper;

    public AccessoryRepository(AccessoryRecordMapper mapper) {
        this.mapper = mapper;
    }

    public void saveAll(List<Accessory> accessories) {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(FilePath.ACCESSORIES))) {

            for (Accessory accessory : accessories) {
                bw.write(mapper.toLine(accessory));
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error: Failed to save the accessories records.");
        }
    }

    public List<Accessory> loadAll() {
        List<Accessory> accessories = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new FileReader(FilePath.ACCESSORIES))) {

            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                Accessory accessory = mapper.toAccessory(line.split("\\|", -1));

                if (accessory != null) {
                    accessories.add(accessory);
                }
            }

        } catch (IOException e) {
            return accessories;
        }

        return accessories;
    }
}