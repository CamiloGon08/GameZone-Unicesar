package com.mycompany.gamezone.validation;

import com.mycompany.gamezone.model.Accessory;
import com.mycompany.gamezone.model.ConnectionType;
import java.util.List;

public final class AccessoryValidator {

    private AccessoryValidator() {
    }

    public static void validateAccessoryId(String accessoryId) {
        if (accessoryId == null || accessoryId.trim().isEmpty()) {
            throw new IllegalArgumentException("Accessory ID cannot be empty.");
        }
    }

    public static void validateQuantityToRestore(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity to restore must be greater than zero.");
        }
    }

    public static void validateCommonFields(
            String id, String title, double price, int stock) {

        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be empty.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }
    }

    public static void validateControllerFields(String connectionType) {
        if (connectionType == null || connectionType.trim().isEmpty()) {
            throw new IllegalArgumentException("Connection type cannot be empty.");
        }
        if (!ConnectionType.isValid(connectionType)) {
            throw new IllegalArgumentException(
                    "Connection type must be "
                    + ConnectionType.optionsDescription() + ".");
        }
    }

    public static void validateCableFields(double lengthInMeters, String connectorType) {
        if (lengthInMeters <= 0) {
            throw new IllegalArgumentException(
                    "Cable length must be greater than zero.");
        }
        if (connectorType == null || connectorType.trim().isEmpty()) {
            throw new IllegalArgumentException("Connector type cannot be empty.");
        }
    }

    public static void validateMemoryFields(int capacityInGb, String memoryType) {
        if (capacityInGb <= 0) {
            throw new IllegalArgumentException(
                    "Memory capacity must be greater than zero.");
        }
        if (memoryType == null || memoryType.trim().isEmpty()) {
            throw new IllegalArgumentException("Memory type cannot be empty.");
        }
    }

    public static void validateUniqueId(List<Accessory> accessories, String id) {
        for (Accessory accessory : accessories) {
            if (accessory.getId().equals(id)) {
                throw new IllegalArgumentException(
                        "An accessory with ID " + id + " already exists.");
            }
        }
    }
}