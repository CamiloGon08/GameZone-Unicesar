package com.mycompany.gamezone.service;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
import com.mycompany.gamezone.persistence.AccessoryRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for managing the business logic related to accessories.
 *
 * The service handles the registration, searching, filtering and stock
 * modification of controllers, cables and memories. It also validates the
 * data before modifying the accessory list.
 *
 * @author EstefaniaMarquez
 */
public class AccessoryService {

    private final AccessoryRepository accessoryRepository;

    /**
     * Creates an AccessoryService with the repository required for
     * accessory persistence.
     *
     * @param accessoryRepository repository used to save and load accessories
     */
    public AccessoryService(AccessoryRepository accessoryRepository) {
        this.accessoryRepository = accessoryRepository;
    }

    /**
     * Registers a new controller in the provided accessory list.
     *
     * The method validates the common accessory fields, the connection type
     * and verifies that the accessory ID is not already being used.
     *
     * @param accessories current list of accessories
     * @param id unique identifier of the controller
     * @param title title or name of the controller
     * @param price selling price of the controller
     * @param stock initial stock quantity
     * @param connectionType controller connection type
     * @param compatibleConsoleIds identifiers of compatible consoles
     * @throws IllegalArgumentException if any field is invalid or the ID
     *         already exists
     */
    public void registerController(
            List<Accessory> accessories,
            String id,
            String title,
            double price,
            int stock,
            String connectionType,
            List<String> compatibleConsoleIds) {

        validateCommonFields(id, title, price, stock);
        validateControllerFields(connectionType);
        validateUniqueId(accessories, id);

        Controller controller = new Controller(
                id, title, price, stock, connectionType);

        addCompatibleConsoles(controller, compatibleConsoleIds);

        accessories.add(controller);
    }

    /**
     * Registers a new cable in the provided accessory list.
     *
     * The method validates the common accessory fields, cable-specific fields
     * and verifies that the accessory ID is not already being used.
     *
     * @param accessories current list of accessories
     * @param id unique identifier of the cable
     * @param title title or name of the cable
     * @param price selling price of the cable
     * @param stock initial stock quantity
     * @param lengthInMeters cable length in meters
     * @param connectorType type of connector used by the cable
     * @throws IllegalArgumentException if any field is invalid or the ID
     *         already exists
     */
    public void registerCable(
            List<Accessory> accessories,
            String id,
            String title,
            double price,
            int stock,
            double lengthInMeters,
            String connectorType) {

        validateCommonFields(id, title, price, stock);
        validateCableFields(lengthInMeters, connectorType);
        validateUniqueId(accessories, id);

        Cable cable = new Cable(
                id, title, price, stock,
                lengthInMeters, connectorType);

        accessories.add(cable);
    }

    /**
     * Registers a new memory in the provided accessory list.
     *
     * The method validates the common accessory fields, memory-specific fields
     * and verifies that the accessory ID is not already being used.
     *
     * @param accessories current list of accessories
     * @param id unique identifier of the memory
     * @param title title or name of the memory
     * @param price selling price of the memory
     * @param stock initial stock quantity
     * @param capacityInGb memory capacity in gigabytes
     * @param memoryType type of memory
     * @param compatibleConsoleIds identifiers of compatible consoles
     * @throws IllegalArgumentException if any field is invalid or the ID
     *         already exists
     */
    public void registerMemory(
            List<Accessory> accessories,
            String id,
            String title,
            double price,
            int stock,
            int capacityInGb,
            String memoryType,
            List<String> compatibleConsoleIds) {

        validateCommonFields(id, title, price, stock);
        validateMemoryFields(capacityInGb, memoryType);
        validateUniqueId(accessories, id);

        Memory memory = new Memory(
                id, title, price, stock,
                capacityInGb, memoryType);

        addCompatibleConsoles(memory, compatibleConsoleIds);

        accessories.add(memory);
    }

    /**
     * Loads and returns all accessories stored in the repository.
     *
     * @return list containing all persisted accessories
     */
    public List<Accessory> listAllAccessories() {
        return accessoryRepository.loadAll();
    }

    /**
     * Returns all accessories that belong to the specified type.
     *
     * Supported types are Controller, Cable and Memory.
     *
     * @param type accessory type to search for
     * @return list of accessories matching the requested type
     */
    public List<Accessory> listAccessoriesByType(String type) {

        List<Accessory> accessories = listAllAccessories();
        List<Accessory> filteredAccessories = new ArrayList<>();

        if (type == null || type.trim().isEmpty()) {
            return filteredAccessories;
        }

        for (Accessory accessory : accessories) {

            if (accessory instanceof Controller
                    && "Controller".equalsIgnoreCase(type)) {

                filteredAccessories.add(accessory);

            } else if (accessory instanceof Cable
                    && "Cable".equalsIgnoreCase(type)) {

                filteredAccessories.add(accessory);

            } else if (accessory instanceof Memory
                    && "Memory".equalsIgnoreCase(type)) {

                filteredAccessories.add(accessory);
            }
        }

        return filteredAccessories;
    }

    /**
     * Finds all accessories compatible with a specific console.
     *
     * Compatibility is determined using the console identifiers stored
     * inside each accessory.
     *
     * @param consoleId identifier of the console
     * @return list of accessories compatible with the console
     */
    public List<Accessory> findAccessoriesCompatibleWith(String consoleId) {

        List<Accessory> accessories = listAllAccessories();
        List<Accessory> compatibleAccessories = new ArrayList<>();

        if (consoleId == null || consoleId.trim().isEmpty()) {
            return compatibleAccessories;
        }

        for (Accessory accessory : accessories) {

            if (accessory.isCompatibleWith(consoleId)) {
                compatibleAccessories.add(accessory);
            }
        }

        return compatibleAccessories;
    }

    /**
     * Searches for an accessory using its unique identifier.
     *
     * @param id identifier of the accessory
     * @return the accessory with the specified ID, or null if it does not exist
     */
    public Accessory findById(String id) {

        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        List<Accessory> accessories = listAllAccessories();

        for (Accessory accessory : accessories) {

            if (accessory.getId().equals(id)) {
                return accessory;
            }
        }

        return null;
    }

    /**
     * Updates the stock of an accessory in the provided list.
     *
     * The quantity may be positive to increase stock or negative to decrease
     * it. The resulting stock can never be negative.
     *
     * @param accessories current list of accessories
     * @param accessoryId identifier of the accessory
     * @param quantity amount to add or subtract from the current stock
     * @throws IllegalArgumentException if the accessory does not exist or
     *         the resulting stock would be negative
     */
    public void updateStock(
            List<Accessory> accessories,
            String accessoryId,
            int quantity) {

        if (accessoryId == null || accessoryId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Accessory ID cannot be empty.");
        }

        for (Accessory accessory : accessories) {

            if (accessory.getId().equals(accessoryId)) {

                int newStock = accessory.getStock() + quantity;

                if (newStock < 0) {
                    throw new IllegalArgumentException(
                            "Stock cannot be negative.");
                }

                accessory.adjustStock(quantity);
                return;
            }
        }

        throw new IllegalArgumentException(
                "Accessory with ID " + accessoryId + " was not found.");
    }

    /**
     * Saves the complete accessory list using the repository.
     *
     * @param accessories list of accessories to persist
     */
    public void saveAll(List<Accessory> accessories) {
        accessoryRepository.saveAll(accessories);
    }

    /**
     * Validates fields shared by every accessory type.
     *
     * @param id accessory identifier
     * @param title accessory title
     * @param price accessory price
     * @param stock accessory stock
     * @throws IllegalArgumentException if any common field is invalid
     */
    private void validateCommonFields(
            String id,
            String title,
            double price,
            int stock) {

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "ID cannot be empty.");
        }

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Title cannot be empty.");
        }

        if (price <= 0) {
            throw new IllegalArgumentException(
                    "Price must be greater than zero.");
        }

        if (stock < 0) {
            throw new IllegalArgumentException(
                    "Stock cannot be negative.");
        }
    }

    /**
     * Validates fields specific to a controller.
     *
     * Only wireless and wired connection types are accepted.
     *
     * @param connectionType controller connection type
     * @throws IllegalArgumentException if the connection type is invalid
     */
    private void validateControllerFields(String connectionType) {

        if (connectionType == null || connectionType.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Connection type cannot be empty.");
        }

        if (!connectionType.equalsIgnoreCase("WIRELESS")
                && !connectionType.equalsIgnoreCase("WIRED")) {

            throw new IllegalArgumentException(
                    "Connection type must be WIRELESS or WIRED.");
        }
    }

    /**
     * Validates fields specific to a cable.
     *
     * @param lengthInMeters cable length in meters
     * @param connectorType cable connector type
     * @throws IllegalArgumentException if the length or connector type is invalid
     */
    private void validateCableFields(
            double lengthInMeters,
            String connectorType) {

        if (lengthInMeters <= 0) {
            throw new IllegalArgumentException(
                    "Cable length must be greater than zero.");
        }

        if (connectorType == null || connectorType.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Connector type cannot be empty.");
        }
    }

    /**
     * Validates fields specific to a memory.
     *
     * @param capacityInGb memory capacity in gigabytes
     * @param memoryType memory type
     * @throws IllegalArgumentException if the capacity or memory type is invalid
     */
    private void validateMemoryFields(
            int capacityInGb,
            String memoryType) {

        if (capacityInGb <= 0) {
            throw new IllegalArgumentException(
                    "Memory capacity must be greater than zero.");
        }

        if (memoryType == null || memoryType.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Memory type cannot be empty.");
        }
    }

    /**
     * Verifies that an accessory ID is not already present in the list.
     *
     * @param accessories current list of accessories
     * @param id ID to verify
     * @throws IllegalArgumentException if another accessory uses the same ID
     */
    private void validateUniqueId(
            List<Accessory> accessories,
            String id) {

        for (Accessory accessory : accessories) {

            if (accessory.getId().equals(id)) {
                throw new IllegalArgumentException(
                        "An accessory with ID " + id + " already exists.");
            }
        }
    }

    /**
     * Adds the identifiers of compatible consoles to an accessory.
     *
     * Null or empty identifiers are ignored because they do not represent
     * valid compatibility relationships.
     *
     * @param accessory accessory to which the compatibility identifiers
     *                  will be added
     * @param compatibleConsoleIds list of compatible console identifiers
     */
    private void addCompatibleConsoles(
            Accessory accessory,
            List<String> compatibleConsoleIds) {

        if (compatibleConsoleIds == null) {
            return;
        }

        for (String consoleId : compatibleConsoleIds) {

            if (consoleId != null && !consoleId.trim().isEmpty()) {
                accessory.addCompatibleConsole(consoleId);
            }
        }
    }
}