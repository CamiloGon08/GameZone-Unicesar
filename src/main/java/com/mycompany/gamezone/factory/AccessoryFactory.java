package com.mycompany.gamezone.factory;

import com.mycompany.gamezone.model.Accessory;
import com.mycompany.gamezone.model.Cable;
import com.mycompany.gamezone.model.Controller;
import com.mycompany.gamezone.model.Memory;
import com.mycompany.gamezone.validation.AccessoryValidator;

/**
 * Creates each accessory type after checking its fields. A new type adds one
 * method here.
 * 
 * @author EstefaniaMarquez
 * 
 */
public class AccessoryFactory {

    /**
     * Creates a controller after checking its data.
     *
     * @param id identifier of the accessory
     * @param title title of the accessory
     * @param price unit price
     * @param stock units available
     * @param connectionType connection type of the controller
     * @return the new controller
     */
    public Accessory createController(String id, String title, double price, int stock,
            String connectionType) {
        AccessoryValidator.validateCommonFields(id, title, price, stock);
        AccessoryValidator.validateControllerFields(connectionType);
        return new Controller(id, title, price, stock, connectionType);
    }

    /**
     * Creates a cable after checking its data.
     *
     * @param id identifier of the accessory
     * @param title title of the accessory
     * @param price unit price
     * @param stock units available
     * @param lengthInMeters length of the cable
     * @param connectorType connector type of the cable
     * @return the new cable
     */
    public Accessory createCable(String id, String title, double price, int stock,
            double lengthInMeters, String connectorType) {
        AccessoryValidator.validateCommonFields(id, title, price, stock);
        AccessoryValidator.validateCableFields(lengthInMeters, connectorType);
        return new Cable(id, title, price, stock, lengthInMeters, connectorType);
    }

    /**
     * Creates a memory card after checking its data.
     *
     * @param id identifier of the accessory
     * @param title title of the accessory
     * @param price unit price
     * @param stock units available
     * @param capacityInGb capacity in gigabytes
     * @param memoryType type of memory
     * @return the new memory
     */
    public Accessory createMemory(String id, String title, double price, int stock,
            int capacityInGb, String memoryType) {
        AccessoryValidator.validateCommonFields(id, title, price, stock);
        AccessoryValidator.validateMemoryFields(capacityInGb, memoryType);
        return new Memory(id, title, price, stock, capacityInGb, memoryType);
    }
}
