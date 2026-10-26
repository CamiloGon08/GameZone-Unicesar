package com.mycompany.gamezone.ui;

import java.util.List;

import com.mycompany.gamezone.exceptions.GameZoneException;
import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Return;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.service.MonthlyBalanceReport;
import com.mycompany.gamezone.service.PersonService;
import com.mycompany.gamezone.service.ReturnService;
import com.mycompany.gamezone.service.SaleLookup;

/**
 * Console submenu for the management of returns and the monthly balance.
 * All the messages shown to the user are written in Spanish.
 */
class ReturnMenu {

    private final PersonService personService;
    private final SaleLookup saleLookup;
    private final ReturnService returnService;
    private final ReturnReceiptFormatter receiptFormatter = new ReturnReceiptFormatter();
    private final MonthlyBalanceReport monthlyBalanceReport;
    private final ConsoleIO io;

    /**
     * Creates the menu with the collaborators it needs.
     *
     * @param personService service that handles person-related operations
     * @param saleLookup lookup used to find the sale of a return
     * @param returnService service that handles return-related operations
     * @param monthlyBalanceReport report that computes the monthly balance
     * @param io helper that reads the input and prints error messages
     */
    ReturnMenu(PersonService personService,
               SaleLookup saleLookup,
               ReturnService returnService,
               MonthlyBalanceReport monthlyBalanceReport,
               ConsoleIO io) {
        this.personService = personService;
        this.saleLookup = saleLookup;
        this.returnService = returnService;
        this.monthlyBalanceReport = monthlyBalanceReport;
        this.io = io;
    }

    /**
     * Shows the submenu until the user chooses to go back to the main menu.
     */
    void start() {
        boolean running = true;
        while (running) {
            printReturnMenu();
            int option = io.readInt();
            switch (option) {
                case 1 -> registerReturn();
                case 2 -> viewAllReturns();
                case 3 -> viewReturnsByCustomer();
                case 4 -> viewReturnsBySale();
                case 5 -> generateMonthlyBalance();
                case 0 -> running = false;
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }
        }
    }

    private void printReturnMenu() {
        System.out.println("\n===== Gestión de devoluciones =====");
        System.out.println("1. Registrar una nueva devolución");
        System.out.println("2. Consultar todas las devoluciones");
        System.out.println("3. Consultar devoluciones por cliente");
        System.out.println("4. Consultar devoluciones por venta");
        System.out.println("5. Consultar balance mensual");
        System.out.println("0. Volver al menú principal");
        System.out.print("Elija una opción: ");
    }

    private void registerReturn() {
        System.out.print("ID de la venta: ");
        String saleId = io.readLine();

        Sale sale = saleLookup.findById(saleId);
        if (sale == null) {
            System.out.println("No existe una venta con ese ID.");
            return;
        }

        List<Product> products = sale.getProducts();
        System.out.println("Productos de la venta:");
        for (int i = 0; i < products.size(); i++) {
            System.out.println((i + 1) + ". " + products.get(i).getTitle()
                    + " (ID: " + products.get(i).getId() + ")");
        }

        System.out.print("IDs de los productos a devolver (separados por coma): ");
        String productIdsRaw = io.readLine();
        List<String> productIds = io.parseCommaSeparated(productIdsRaw);

        System.out.print("Motivo de la devolución: ");
        String reason = io.readLine();

        try {
            Return returnObj = returnService.registerReturn(saleId, productIds, reason);
            System.out.println("Devolución registrada exitosamente.");
            System.out.println(receiptFormatter.format(returnObj));
        } catch (GameZoneException | IllegalArgumentException e) {
            io.handleException(e);
        }
    }

    private void viewAllReturns() {
        List<Return> returns = returnService.viewAllReturns();
        if (returns.isEmpty()) {
            System.out.println("No hay devoluciones registradas aún.");
            return;
        }
        System.out.println("--- Todas las devoluciones ---");
        for (Return returnObj : returns) {
            System.out.println(receiptFormatter.format(returnObj));
            System.out.println("---------------------------");
        }
    }

    private void viewReturnsByCustomer() {
        List<Customer> customers = personService.listCustomers();
        if (customers.isEmpty()) {
            System.out.println("No hay clientes registrados aún.");
            return;
        }
        System.out.println("Seleccione un cliente:");
        for (int i = 0; i < customers.size(); i++) {
            System.out.println((i + 1) + ". " + customers.get(i).getName()
                    + " (ID: " + customers.get(i).getiD() + ")");
        }
        int customerIndex = io.readInt() - 1;
        if (customerIndex < 0 || customerIndex >= customers.size()) {
            System.out.println("Selección inválida.");
            return;
        }
        Customer selected = customers.get(customerIndex);
        List<Return> returns = returnService.viewReturnsByCustomer(selected);
        if (returns.isEmpty()) {
            System.out.println("No hay devoluciones para ese cliente.");
            return;
        }
        System.out.println("--- Devoluciones de " + selected.getName() + " ---");
        for (Return returnObj : returns) {
            System.out.println(receiptFormatter.format(returnObj));
            System.out.println("---------------------------");
        }
    }

    private void viewReturnsBySale() {
        System.out.print("ID de la venta: ");
        String saleId = io.readLine();
        List<Return> returns = returnService.viewReturnsBySale(saleId);
        if (returns.isEmpty()) {
            System.out.println("No hay devoluciones para esa venta.");
            return;
        }
        System.out.println("--- Devoluciones de la venta " + saleId + " ---");
        for (Return returnObj : returns) {
            System.out.println(receiptFormatter.format(returnObj));
            System.out.println("---------------------------");
        }
    }

    private void generateMonthlyBalance() {
        System.out.print("Mes (1-12): ");
        int month;
        try {
            month = Integer.parseInt(io.readLine());
        } catch (NumberFormatException e) {
            System.out.println("Mes inválido.");
            return;
        }
        System.out.print("Año: ");
        int year;
        try {
            year = Integer.parseInt(io.readLine());
        } catch (NumberFormatException e) {
            System.out.println("Año inválido.");
            return;
        }

        try {
            double totalSales = monthlyBalanceReport.calculateMonthlySales(month, year);
            double totalReturns = monthlyBalanceReport.calculateMonthlyReturns(month, year);
            double balance = monthlyBalanceReport.generateMonthlyBalance(month, year);
            System.out.println("--- Balance mensual " + month + "/" + year + " ---");
            System.out.println("Total de ventas: $" + String.format("%.2f", totalSales));
            System.out.println("Total de devoluciones: $" + String.format("%.2f", totalReturns));
            System.out.println("Balance neto: $" + String.format("%.2f", balance));
        } catch (GameZoneException | IllegalArgumentException e) {
            io.handleException(e);
        }
    }
}
