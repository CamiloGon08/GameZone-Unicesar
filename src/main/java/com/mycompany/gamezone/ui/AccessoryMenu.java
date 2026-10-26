package com.mycompany.gamezone.ui;

import java.util.List;

import com.mycompany.gamezone.exceptions.GameZoneException;
import com.mycompany.gamezone.model.Accessory;
import com.mycompany.gamezone.service.AccessoryService;

/**
 * Console submenu for the management of accessories.
 * All the messages shown to the user are written in Spanish.
 */
class AccessoryMenu {

    private final AccessoryService accessoryService;
    private final ConsoleIO io;

    /**
     * Creates the menu with the collaborators it needs.
     *
     * @param accessoryService service that handles accessory-related operations
     * @param io helper that reads the input and prints error messages
     */
    AccessoryMenu(AccessoryService accessoryService,
                  ConsoleIO io) {
        this.accessoryService = accessoryService;
        this.io = io;
    }

    /**
     * Shows the submenu until the user chooses to go back to the main menu.
     */
    void start() {
        boolean running = true;
        while (running) {
            printAccessoryMenu();
            int option = io.readInt();
            switch (option) {
                case 1 -> registerController();
                case 2 -> registerCable();
                case 3 -> registerMemory();
                case 4 -> listAllAccessories();
                case 5 -> listAccessoriesByType();
                case 6 -> findAccessoriesCompatibleWith();
                case 0 -> running = false;
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }
        }
    }

    private void printAccessoryMenu() {
        System.out.println("\n===== Gestión de accesorios =====");
        System.out.println("1. Registrar un nuevo control");
        System.out.println("2. Registrar un nuevo cable");
        System.out.println("3. Registrar una nueva memoria");
        System.out.println("4. Listar todos los accesorios");
        System.out.println("5. Listar accesorios por tipo");
        System.out.println("6. Consultar accesorios compatibles con una consola");
        System.out.println("0. Volver al menú principal");
        System.out.print("Elija una opción: ");
    }

    private void registerController() {
        System.out.print("ID: ");
        String id = io.readLine();
        System.out.print("Título: ");
        String title = io.readLine();
        System.out.print("Precio: ");
        double price = Double.parseDouble(io.readLine());
        System.out.print("Stock: ");
        int stock = Integer.parseInt(io.readLine());
        System.out.print("Tipo de conexión (WIRELESS/WIRED): ");
        String connectionType = io.readLine();
        System.out.print("IDs de consolas compatibles (separados por coma, o vacío): ");
        String consoleIdsRaw = io.readLine();

        List<String> compatibleConsoleIds = io.parseCommaSeparated(consoleIdsRaw);

        try {
            accessoryService.registerController(id, title, price, stock,
                    connectionType, compatibleConsoleIds);
            System.out.println("Control registrado exitosamente.");
        } catch (GameZoneException | IllegalArgumentException e) {
            io.handleException(e);
        }
    }

    private void registerCable() {
        System.out.print("ID: ");
        String id = io.readLine();
        System.out.print("Título: ");
        String title = io.readLine();
        System.out.print("Precio: ");
        double price = Double.parseDouble(io.readLine());
        System.out.print("Stock: ");
        int stock = Integer.parseInt(io.readLine());
        System.out.print("Longitud en metros: ");
        double length = Double.parseDouble(io.readLine());
        System.out.print("Tipo de conector: ");
        String connectorType = io.readLine();

        try {
            accessoryService.registerCable(id, title, price, stock,
                    length, connectorType);
            System.out.println("Cable registrado exitosamente.");
        } catch (GameZoneException | IllegalArgumentException e) {
            io.handleException(e);
        }
    }

    private void registerMemory() {
        System.out.print("ID: ");
        String id = io.readLine();
        System.out.print("Título: ");
        String title = io.readLine();
        System.out.print("Precio: ");
        double price = Double.parseDouble(io.readLine());
        System.out.print("Stock: ");
        int stock = Integer.parseInt(io.readLine());
        System.out.print("Capacidad en GB: ");
        int capacity = Integer.parseInt(io.readLine());
        System.out.print("Tipo de memoria: ");
        String memoryType = io.readLine();
        System.out.print("IDs de consolas compatibles (separados por coma, o vacío): ");
        String consoleIdsRaw = io.readLine();

        List<String> compatibleConsoleIds = io.parseCommaSeparated(consoleIdsRaw);

        try {
            accessoryService.registerMemory(id, title, price, stock,
                    capacity, memoryType, compatibleConsoleIds);
            System.out.println("Memoria registrada exitosamente.");
        } catch (GameZoneException | IllegalArgumentException e) {
            io.handleException(e);
        }
    }

    private void listAllAccessories() {
        List<Accessory> accessories = accessoryService.listAllAccessories();
        if (accessories.isEmpty()) {
            System.out.println("No hay accesorios registrados aún.");
            return;
        }
        System.out.println("--- Inventario de accesorios ---");
        for (Accessory accessory : accessories) {
            System.out.println(accessory.getDescription());
        }
    }

    private void listAccessoriesByType() {
        System.out.print("Tipo (Controller/Cable/Memory): ");
        String type = io.readLine();
        List<Accessory> accessories = accessoryService.listAccessoriesByType(type);
        if (accessories.isEmpty()) {
            System.out.println("No hay accesorios de ese tipo.");
            return;
        }
        System.out.println("--- Accesorios tipo " + type + " ---");
        for (Accessory accessory : accessories) {
            System.out.println(accessory.getDescription());
        }
    }

    private void findAccessoriesCompatibleWith() {
        System.out.print("ID de la consola: ");
        String consoleId = io.readLine();
        List<Accessory> compatible = accessoryService.findAccessoriesCompatibleWith(consoleId);
        if (compatible.isEmpty()) {
            System.out.println("No hay accesorios compatibles con esa consola.");
            return;
        }
        System.out.println("--- Accesorios compatibles con " + consoleId + " ---");
        for (Accessory accessory : compatible) {
            System.out.println(accessory.getDescription());
        }
    }
}
