package com.mycompany.gamezone.ui;

import java.time.LocalDate;
import java.util.List;

import com.mycompany.gamezone.exceptions.GameZoneException;
import com.mycompany.gamezone.model.Promotion;
import com.mycompany.gamezone.factory.PromotionFactory;
import com.mycompany.gamezone.service.PromotionService;

/**
 * Console submenu for the management of promotions.
 * All the messages shown to the user are written in Spanish.
 */
class PromotionMenu {

    private final PromotionService promotionService;
    private final PromotionFactory promotionFactory;
    private final ConsoleIO io;

    /**
     * Creates the menu with the collaborators it needs.
     *
     * @param promotionService service that handles promotion-related operations
     * @param promotionFactory factory that builds new promotions
     * @param io helper that reads the input and prints error messages
     */
    PromotionMenu(PromotionService promotionService,
                  PromotionFactory promotionFactory,
                  ConsoleIO io) {
        this.promotionService = promotionService;
        this.promotionFactory = promotionFactory;
        this.io = io;
    }

    /**
     * Shows the submenu until the user chooses to go back to the main menu.
     */
    void start() {
        boolean running = true;
        while (running) {
            printPromotionMenu();
            int option = io.readInt();
            switch (option) {
                case 1 -> registerPercentageDiscount();
                case 2 -> registerCategoryDiscount();
                case 3 -> registerBulkPurchaseDiscount();
                case 4 -> listAllPromotions();
                case 5 -> listActivePromotions();
                case 0 -> running = false;
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }
        }
    }

    private void printPromotionMenu() {
        System.out.println("\n===== Gestión de promociones =====");
        System.out.println("1. Registrar promoción por porcentaje");
        System.out.println("2. Registrar promoción por categoría");
        System.out.println("3. Registrar promoción por volumen");
        System.out.println("4. Listar todas las promociones");
        System.out.println("5. Listar promociones vigentes");
        System.out.println("0. Volver al menú principal");
        System.out.print("Elija una opción: ");
    }

    private void registerPercentageDiscount() {
        System.out.print("ID: ");
        String id = io.readLine();
        System.out.print("Nombre: ");
        String name = io.readLine();
        System.out.print("Fecha inicio (YYYY-MM-DD): ");
        LocalDate startDate = LocalDate.parse(io.readLine());
        System.out.print("Fecha fin (YYYY-MM-DD): ");
        LocalDate endDate = LocalDate.parse(io.readLine());
        System.out.print("Porcentaje de descuento: ");
        double percentage = Double.parseDouble(io.readLine());

        try {
            promotionService.register(promotionFactory.createPercentageDiscount(
                    id, name, startDate, endDate, percentage));
            System.out.println("Promoción por porcentaje registrada exitosamente.");
        } catch (GameZoneException | IllegalArgumentException e) {
            io.handleException(e);
        }
    }

    private void registerCategoryDiscount() {
        System.out.print("ID: ");
        String id = io.readLine();
        System.out.print("Nombre: ");
        String name = io.readLine();
        System.out.print("Fecha inicio (YYYY-MM-DD): ");
        LocalDate startDate = LocalDate.parse(io.readLine());
        System.out.print("Fecha fin (YYYY-MM-DD): ");
        LocalDate endDate = LocalDate.parse(io.readLine());
        System.out.print("Categoría (VIDEOGAME/CONSOLE): ");
        String category = io.readLine();
        System.out.print("Porcentaje de descuento: ");
        double percentage = Double.parseDouble(io.readLine());

        try {
            promotionService.register(promotionFactory.createCategoryDiscount(
                    id, name, startDate, endDate, category, percentage));
            System.out.println("Promoción por categoría registrada exitosamente.");
        } catch (GameZoneException | IllegalArgumentException e) {
            io.handleException(e);
        }
    }

    private void registerBulkPurchaseDiscount() {
        System.out.print("ID: ");
        String id = io.readLine();
        System.out.print("Nombre: ");
        String name = io.readLine();
        System.out.print("Fecha inicio (YYYY-MM-DD): ");
        LocalDate startDate = LocalDate.parse(io.readLine());
        System.out.print("Fecha fin (YYYY-MM-DD): ");
        LocalDate endDate = LocalDate.parse(io.readLine());
        System.out.print("Cantidad mínima de productos: ");
        int minimumQuantity = Integer.parseInt(io.readLine());
        System.out.print("Porcentaje de descuento: ");
        double percentage = Double.parseDouble(io.readLine());

        try {
            promotionService.register(promotionFactory.createBulkPurchaseDiscount(
                    id, name, startDate, endDate, minimumQuantity, percentage));
            System.out.println("Promoción por volumen registrada exitosamente.");
        } catch (GameZoneException | IllegalArgumentException e) {
            io.handleException(e);
        }
    }

    private void listAllPromotions() {
        List<Promotion> promotions = promotionService.listAllPromotions();
        if (promotions.isEmpty()) {
            System.out.println("No hay promociones registradas aún.");
            return;
        }
        System.out.println("--- Todas las promociones ---");
        for (Promotion promotion : promotions) {
            System.out.println("ID: " + promotion.getId()
                    + " | Nombre: " + promotion.getName()
                    + " | Desde: " + promotion.getStartDate()
                    + " | Hasta: " + promotion.getEndDate());
        }
    }

    private void listActivePromotions() {
        List<Promotion> promotions = promotionService.listActivePromotions();
        if (promotions.isEmpty()) {
            System.out.println("No hay promociones vigentes en la fecha actual.");
            return;
        }
        System.out.println("--- Promociones vigentes ---");
        for (Promotion promotion : promotions) {
            System.out.println("ID: " + promotion.getId()
                    + " | Nombre: " + promotion.getName()
                    + " | Desde: " + promotion.getStartDate()
                    + " | Hasta: " + promotion.getEndDate());
        }
    }
}
