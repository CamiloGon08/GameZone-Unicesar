package com.mycompany.gamezone.service;

import com.mycompany.gamezone.factory.AccessoryFactory;
import com.mycompany.gamezone.model.Accessory;
import com.mycompany.gamezone.persistence.AccessoryRepository;
import com.mycompany.gamezone.validation.AccessoryValidator;
import java.util.ArrayList;
import java.util.List;

/**
 * Owns the accessories list and persists after every change.
 * 
 * @author EstefaniaMarquez
 * 
 */
public class AccessoryService {

    private final AccessoryRepository accessoryRepository;
    private final AccessoryFactory accessoryFactory;
    private final List<Accessory> accessories;

    public AccessoryService(AccessoryRepository accessoryRepository,
            AccessoryFactory accessoryFactory) {
        this.accessoryRepository = accessoryRepository;
        this.accessoryFactory = accessoryFactory;
        this.accessories = new ArrayList<>(accessoryRepository.loadAll());
    }

    public void registerController(
            String id, String title, double price, int stock,
            String connectionType, List<String> compatibleConsoleIds) {

        register(accessoryFactory.createController(id, title, price, stock, connectionType),
                compatibleConsoleIds);
    }

    public void registerCable(
            String id, String title, double price, int stock,
            double lengthInMeters, String connectorType) {

        register(accessoryFactory.createCable(id, title, price, stock,
                lengthInMeters, connectorType), null);
    }

    public void registerMemory(
            String id, String title, double price, int stock,
            int capacityInGb, String memoryType, List<String> compatibleConsoleIds) {

        register(accessoryFactory.createMemory(id, title, price, stock,
                capacityInGb, memoryType), compatibleConsoleIds);
    }

    /**
     * Registers an accessory of any type built by {@link AccessoryFactory} and
     * saves the list.
     *
     * @param accessory the accessory to register
     * @param compatibleConsoleIds consoles compatible with it (may be null)
     */
    public void register(Accessory accessory, List<String> compatibleConsoleIds) {
        AccessoryValidator.validateUniqueId(accessories, accessory.getId());

        addCompatibleConsoles(accessory, compatibleConsoleIds);

        accessories.add(accessory);
        saveAll();
    }

    public List<Accessory> listAllAccessories() {
        return new ArrayList<>(accessories);
    }

    public List<Accessory> listAccessoriesByType(String type) {
        List<Accessory> filteredAccessories = new ArrayList<>();

        if (type == null || type.trim().isEmpty()) {
            return filteredAccessories;
        }

        for (Accessory accessory : accessories) {
            if (accessory.getTypeName().equalsIgnoreCase(type)) {
                filteredAccessories.add(accessory);
            }
        }

        return filteredAccessories;
    }

    public List<Accessory> findAccessoriesCompatibleWith(String consoleId) {
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

    public Accessory findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        for (Accessory accessory : accessories) {
            if (accessory.getId().equals(id)) {
                return accessory;
            }
        }

        return null;
    }

    /**
     * Adds {@code quantity} (may be negative) to the stock and saves.
     */
    public void updateStock(String accessoryId, int quantity) {
        AccessoryValidator.validateAccessoryId(accessoryId);

        Accessory accessory = requireById(accessoryId);

        if (!accessory.canAdjustStock(quantity)) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }
        accessory.adjustStock(quantity);

        saveAll();
    }

    public void restoreStock(String accessoryId, int quantity) {
        AccessoryValidator.validateAccessoryId(accessoryId);
        AccessoryValidator.validateQuantityToRestore(quantity);

        Accessory accessory = requireById(accessoryId);

        if (!accessory.canAdjustStock(quantity)) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }
        accessory.adjustStock(quantity);

        saveAll();
    }

    private Accessory requireById(String accessoryId) {
        Accessory accessory = findById(accessoryId);

        if (accessory == null) {
            throw new IllegalArgumentException(
                    "Accessory with ID " + accessoryId + " was not found.");
        }

        return accessory;
    }

    private void addCompatibleConsoles(
            Accessory accessory, List<String> compatibleConsoleIds) {

        if (compatibleConsoleIds == null) {
            return;
        }

        for (String consoleId : compatibleConsoleIds) {
            if (consoleId != null && !consoleId.trim().isEmpty()) {
                accessory.addCompatibleConsole(consoleId);
            }
        }
    }

    private void saveAll() {
        accessoryRepository.saveAll(accessories);
    }
}
