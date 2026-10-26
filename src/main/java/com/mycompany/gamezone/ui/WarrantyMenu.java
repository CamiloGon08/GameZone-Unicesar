package com.mycompany.gamezone.ui;

import java.time.LocalDate;
import java.util.List;

import com.mycompany.gamezone.model.Warranty;
import com.mycompany.gamezone.service.WarrantyService;

/**
 * Console submenu for the queries over warranties.
 * All the messages shown to the user are written in Spanish.
 */
class WarrantyMenu {

    private final WarrantyService warrantyService;
    private final ConsoleIO io;

    /**
     * Creates the menu with the collaborators it needs.
     *
     * @param warrantyService service that handles warranty-related operations
     * @param io helper that reads the input and prints error messages
     */
    WarrantyMenu(WarrantyService warrantyService,
                 ConsoleIO io) {
        this.warrantyService = warrantyService;
        this.io = io;
    }

    /**
     * Shows the submenu until the user chooses to go back to the main menu.
     */
    void start() {
        boolean running = true;
        while (running) {
            printWarrantyMenu();
            int option = io.readInt();
            switch (option) {
                case 1 -> findWarrantyByProductAndSale();
                case 2 -> viewAllWarranties();
                case 3 -> viewActiveWarranties();
                case 4 -> viewWarrantiesExpiringSoon();
                case 0 -> running = false;
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }
        }
    }

    private void printWarrantyMenu() {
        System.out.println("\n===== Gestión de garantías =====");
        System.out.println("1. Consultar garantía de un producto en una venta");
        System.out.println("2. Listar todas las garantías");
        System.out.println("3. Listar garantías vigentes");
        System.out.println("4. Listar garantías próximas a vencer (30 días)");
        System.out.println("0. Volver al menú principal");
        System.out.print("Elija una opción: ");
    }

    private void findWarrantyByProductAndSale() {
        System.out.print("ID de la venta: ");
        String saleId = io.readLine();
        System.out.print("ID del producto: ");
        String productId = io.readLine();

        Warranty warranty = warrantyService.findByProductAndSale(productId, saleId);
        if (warranty == null) {
            System.out.println("No se encontró una garantía para ese producto en esa venta.");
            return;
        }
        System.out.println(warranty.generateWarrantyCertificate());
    }

    private void viewAllWarranties() {
        List<Warranty> warranties = warrantyService.viewAllWarranties();
        if (warranties.isEmpty()) {
            System.out.println("No hay garantías registradas aún.");
            return;
        }
        System.out.println("--- Todas las garantías ---");
        for (Warranty warranty : warranties) {
            System.out.println(warranty.generateWarrantyCertificate());
            System.out.println("---------------------------");
        }
    }

    private void viewActiveWarranties() {
        List<Warranty> warranties = warrantyService.viewActiveWarranties(LocalDate.now());
        if (warranties.isEmpty()) {
            System.out.println("No hay garantías vigentes en la fecha actual.");
            return;
        }
        System.out.println("--- Garantías vigentes ---");
        for (Warranty warranty : warranties) {
            System.out.println(warranty.generateWarrantyCertificate());
            System.out.println("---------------------------");
        }
    }

    private void viewWarrantiesExpiringSoon() {
        List<Warranty> warranties = warrantyService.viewWarrantiesExpiringSoon(30);
        if (warranties.isEmpty()) {
            System.out.println("No hay garantías próximas a vencer en los próximos 30 días.");
            return;
        }
        System.out.println("--- Garantías próximas a vencer (30 días) ---");
        for (Warranty warranty : warranties) {
            System.out.println(warranty.generateWarrantyCertificate());
            System.out.println("---------------------------");
        }
    }
}
