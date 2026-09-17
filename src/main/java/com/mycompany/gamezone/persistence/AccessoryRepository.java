package com.mycompany.gamezone.persistence;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import utilities.FilePath;

/**
 * Repository responsible for the persistence of Accessory objects. It handles
 * Controller, Cable and Memory records in a single file.
 *
 * Each record begins with a discriminator that identifies the concrete type of
 * Accessory:
 *
 * CONTROLLER|id|title|price|stock|connectionType|consoleId1,consoleId2
 * CABLE|id|title|price|stock|lengthInMeters|connectorType
 * MEMORY|id|title|price|stock|capacityInGb|memoryType|consoleId1,consoleId2
 *
 * @author EstefaniaMarquez
 */
public class AccessoryRepository {

    private static final String CONTROLLER_TYPE = "CONTROLLER";
    private static final String CABLE_TYPE = "CABLE";
    private static final String MEMORY_TYPE = "MEMORY";

    /**
     * Saves all accessories to the persistence file. The existing file content
     * is replaced with the current list.
     *
     * @param accessories the list of accessories to save
     */
    public void saveAll(List<Accessory> accessories) {

        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(FilePath.ACCESSORIES))) {

            for (Accessory accessory : accessories) {
                bw.write(formatAccessory(accessory));
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println(
                    "Error: Failed to save the accessories records.");
        }
    }

    /**
     * Loads all accessories from the persistence file.
     *
     * @return a list containing all persisted accessories, or an empty list if
     * the file does not exist or contains no records
     */
    public List<Accessory> loadAll() {

        List<Accessory> accessories = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new FileReader(FilePath.ACCESSORIES))) {

            String line;

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split("\\|", -1);

                Accessory accessory = createAccessory(data);

                if (accessory != null) {
                    accessories.add(accessory);
                }
            }

        } catch (IOException e) {
            return accessories;
        }

        return accessories;
    }

    /**
     * Converts an Accessory object into its persistence format.
     *
     * @param accessory the accessory to format
     * @return the text representation of the accessory
     */
    private String formatAccessory(Accessory accessory) {

        String compatibleConsoleIds = String.join(
                ",",
                accessory.getCompatibleConsoleIds());

        if (accessory instanceof Controller controller) {

            return CONTROLLER_TYPE + "|"
                    + controller.getId() + "|"
                    + controller.getTitle() + "|"
                    + controller.getPrice() + "|"
                    + controller.getStock() + "|"
                    + controller.getConnectionType() + "|"
                    + compatibleConsoleIds;

        } else if (accessory instanceof Cable cable) {

            return CABLE_TYPE + "|"
                    + cable.getId() + "|"
                    + cable.getTitle() + "|"
                    + cable.getPrice() + "|"
                    + cable.getStock() + "|"
                    + cable.getLengthInMeters() + "|"
                    + cable.getConnectorType();

        } else if (accessory instanceof Memory memory) {

            return MEMORY_TYPE + "|"
                    + memory.getId() + "|"
                    + memory.getTitle() + "|"
                    + memory.getPrice() + "|"
                    + memory.getStock() + "|"
                    + memory.getCapacityInGb() + "|"
                    + memory.getMemoryType() + "|"
                    + compatibleConsoleIds;
        }

        return "";
    }

    /**
     * Creates a concrete Accessory object from its persistence data.
     *
     * @param data the fields obtained from a persisted record
     * @return the corresponding Controller, Cable or Memory, or null if the
     * discriminator is unknown
     */
    private Accessory createAccessory(String[] data) {

        String type = data[0];

        if (CONTROLLER_TYPE.equals(type)) {

            String id = data[1];
            String title = data[2];
            double price = Double.parseDouble(data[3]);
            int stock = Integer.parseInt(data[4]);
            String connectionType = data[5];

            Controller controller = new Controller(
                    id,
                    title,
                    price,
                    stock,
                    connectionType);

            addCompatibleConsoles(controller, data[6]);

            return controller;

        } else if (CABLE_TYPE.equals(type)) {

            String id = data[1];
            String title = data[2];
            double price = Double.parseDouble(data[3]);
            int stock = Integer.parseInt(data[4]);
            double lengthInMeters = Double.parseDouble(data[5]);
            String connectorType = data[6];

            return new Cable(
                    id,
                    title,
                    price,
                    stock,
                    lengthInMeters,
                    connectorType);

        } else if (MEMORY_TYPE.equals(type)) {

            String id = data[1];
            String title = data[2];
            double price = Double.parseDouble(data[3]);
            int stock = Integer.parseInt(data[4]);
            int capacityInGb = Integer.parseInt(data[5]);
            String memoryType = data[6];

            Memory memory = new Memory(
                    id,
                    title,
                    price,
                    stock,
                    capacityInGb,
                    memoryType);

            addCompatibleConsoles(memory, data[7]);

            return memory;
        }

        return null;
    }

    /**
     * Adds the compatible console identifiers stored in a record to the
     * corresponding Accessory object.
     *
     * @param accessory the accessory being reconstructed
     * @param consoleIds the comma-separated console identifiers
     */
    private void addCompatibleConsoles(
            Accessory accessory,
            String consoleIds) {

        if (consoleIds == null || consoleIds.trim().isEmpty()) {
            return;
        }

        String[] ids = consoleIds.split(",");

        for (String consoleId : ids) {
            String trimmedId = consoleId.trim();

            if (!trimmedId.isEmpty()) {
                accessory.addCompatibleConsole(trimmedId);
            }
        }
    }
}
