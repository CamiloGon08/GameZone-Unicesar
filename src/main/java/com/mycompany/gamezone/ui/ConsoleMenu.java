package com.mycompany.gamezone.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.mycompany.gamezone.model.Accessory;
import com.mycompany.gamezone.model.Console;
import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Person;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Promotion;
import com.mycompany.gamezone.model.Return;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.Seller;
import com.mycompany.gamezone.model.Warranty;
import com.mycompany.gamezone.service.AccessoryService;
import com.mycompany.gamezone.service.PersonService;
import com.mycompany.gamezone.service.ProductService;
import com.mycompany.gamezone.service.PromotionService;
import com.mycompany.gamezone.service.ReturnService;
import com.mycompany.gamezone.service.SaleService;
import com.mycompany.gamezone.service.WarrantyService;

/**
 * Console-based user interface for GameZone Unicesar.
 * Displays the main menu, reads the user's selections and dispatches
 * each option to the corresponding service operation. All the messages
 * shown to the user are written in Spanish, while identifiers and
 * documentation follow the English convention required by the project.
 */
public class ConsoleMenu {

    private final Scanner scanner;
    private final ProductService productService;
    private final PersonService personService;
    private final SaleService saleService;
    private final AccessoryService accessoryService;
    private final PromotionService promotionService;
    private final ReturnService returnService;
    private final WarrantyService warrantyService;
    private final ArrayList<Person> persons;

    /**
     * Creates a console menu with the services required to perform the
     * user operations.
     *
     * @param productService   service that handles product-related operations
     * @param personService    service that handles person-related operations
     * @param saleService      service that handles sale-related operations
     * @param accessoryService service that handles accessory-related operations
     * @param promotionService service that handles promotion-related operations
     * @param returnService    service that handles return-related operations
     * @param warrantyService  service that handles warranty-related operations
     */
    public ConsoleMenu(ProductService productService,
                       PersonService personService,
                       SaleService saleService,
                       AccessoryService accessoryService,
                       PromotionService promotionService,
                       ReturnService returnService,
                       WarrantyService warrantyService) {
        this.scanner = new Scanner(System.in);
        this.productService = productService;
        this.personService = personService;
        this.saleService = saleService;
        this.accessoryService = accessoryService;
        this.promotionService = promotionService;
        this.returnService = returnService;
        this.warrantyService = warrantyService;
        this.persons = personService.listPersons();
    }

    /**
     * Starts the main interaction loop of the console menu. Displays the
     * main menu repeatedly and dispatches each user selection to the
     * corresponding operation until the user chooses to exit.
     */
    public void start() {
        boolean running = true;
        while (running) {
            printMainMenu();
            int option = readInt();
            switch (option) {
                case 1 -> registerVideoGame();
                case 2 -> registerConsole();
                case 3 -> listProducts();
                case 4 -> registerCustomer();
                case 5 -> listCustomers();
                case 6 -> listSellers();
                case 7 -> registerSale();
                case 8 -> viewAllSales();
                case 9 -> viewSalesByCustomer();
                case 10 -> viewSalesBySeller();
                case 11 -> accessoryMenu();
                case 12 -> promotionMenu();
                case 13 -> returnMenu();
                case 14 -> warrantyMenu();
                case 0 -> running = false;
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }
        }
        System.out.println("¡Hasta luego!");
    }

    private void printMainMenu() {
        System.out.println("\n===== GameZone Unicesar =====");
        System.out.println("1. Registrar videojuego");
        System.out.println("2. Registrar consola");
        System.out.println("3. Listar todos los productos");
        System.out.println("4. Registrar cliente");
        System.out.println("5. Listar clientes");
        System.out.println("6. Listar vendedores");
        System.out.println("7. Registrar una nueva venta");
        System.out.println("8. Ver todas las ventas");
        System.out.println("9. Ver ventas por cliente");
        System.out.println("10. Ver ventas por vendedor");
        System.out.println("11. Gestión de accesorios");
        System.out.println("12. Gestión de promociones");
        System.out.println("13. Gestión de devoluciones");
        System.out.println("14. Gestión de garantías");
        System.out.println("0. Salir");
        System.out.print("Elija una opción: ");
    }

    private void registerVideoGame() {
        System.out.print("ID: ");
        String id = scanner.nextLine();
        System.out.print("Título: ");
        String title = scanner.nextLine();
        System.out.print("Precio: ");
        double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Stock: ");
        int stock = Integer.parseInt(scanner.nextLine());
        System.out.print("Plataforma: ");
        String platform = scanner.nextLine();
        System.out.print("Género: ");
        String genre = scanner.nextLine();
        System.out.print("Clasificación de edad: ");
        String ageRating = scanner.nextLine();

        try {
            productService.registerVideoGame(id, title, price, stock, platform, genre, ageRating);
            System.out.println("Videojuego registrado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registerConsole() {
        System.out.print("ID: ");
        String id = scanner.nextLine();
        System.out.print("Título: ");
        String title = scanner.nextLine();
        System.out.print("Precio: ");
        double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Stock: ");
        int stock = Integer.parseInt(scanner.nextLine());
        System.out.print("Marca: ");
        String brand = scanner.nextLine();
        System.out.print("Modelo: ");
        String model = scanner.nextLine();
        System.out.print("Generación: ");
        String generation = scanner.nextLine();

        try {
            productService.registerConsole(id, title, price, stock, brand, model, generation);
            System.out.println("Consola registrada exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listProducts() {
        List<Product> products = productService.listProducts();
        if (products.isEmpty()) {
            System.out.println("No hay productos registrados aún.");
            return;
        }
        System.out.println("--- Inventario de productos ---");
        for (Product product : products) {
            System.out.println(product.getDescription());
        }
    }

    private void registerCustomer() {
        System.out.print("Nombre: ");
        String name = scanner.nextLine();
        System.out.print("Identificación: ");
        String id = scanner.nextLine();
        System.out.print("Teléfono (solo números): ");
        long phone;
        try {
            phone = Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Teléfono inválido.");
            return;
        }
        System.out.print("Correo electrónico: ");
        String email = scanner.nextLine();

        try {
            personService.registerCustomer(persons, email, name, id, phone);
            personService.savePersons(persons);
            System.out.println("Cliente registrado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listCustomers() {
        List<Customer> customers = getCustomers();
        if (customers.isEmpty()) {
            System.out.println("No hay clientes registrados aún.");
            return;
        }
        System.out.println("--- Clientes registrados ---");
        for (Customer customer : customers) {
            System.out.println("Nombre: " + customer.getName()
                    + " | ID: " + customer.getiD()
                    + " | Teléfono: " + customer.getContactNumber()
                    + " | Correo: " + customer.geteMail());
        }
    }

    private void listSellers() {
        List<Seller> sellers = getSellers();
        if (sellers.isEmpty()) {
            System.out.println("No hay vendedores registrados aún.");
            return;
        }
        System.out.println("--- Vendedores registrados ---");
        for (Seller seller : sellers) {
            System.out.println("Nombre: " + seller.getName()
                    + " | ID: " + seller.getiD()
                    + " | Código de empleado: " + seller.getEmployeeCode()
                    + " | Turno: " + seller.getShift());
        }
    }

    private void registerSale() {
        List<Customer> customers = getCustomers();
        if (customers.isEmpty()) {
            System.out.println("No hay clientes disponibles. Registre un cliente primero.");
            return;
        }
        System.out.println("Seleccione un cliente:");
        for (int i = 0; i < customers.size(); i++) {
            System.out.println((i + 1) + ". " + customers.get(i).getName()
                    + " (ID: " + customers.get(i).getiD() + ")");
        }
        int customerIndex = readInt() - 1;
        if (customerIndex < 0 || customerIndex >= customers.size()) {
            System.out.println("Selección de cliente inválida.");
            return;
        }
        Customer selectedCustomer = customers.get(customerIndex);

        List<Seller> sellers = getSellers();
        if (sellers.isEmpty()) {
            System.out.println("No hay vendedores disponibles. Verifique que estén precargados.");
            return;
        }
        System.out.println("Seleccione un vendedor:");
        for (int i = 0; i < sellers.size(); i++) {
            System.out.println((i + 1) + ". " + sellers.get(i).getName()
                    + " (Código: " + sellers.get(i).getEmployeeCode() + ")");
        }
        int sellerIndex = readInt() - 1;
        if (sellerIndex < 0 || sellerIndex >= sellers.size()) {
            System.out.println("Selección de vendedor inválida.");
            return;
        }
        Seller selectedSeller = sellers.get(sellerIndex);

        List<Product> selectableItems = new ArrayList<>();
        selectableItems.addAll(productService.listProducts());
        selectableItems.addAll(accessoryService.listAllAccessories());

        if (selectableItems.isEmpty()) {
            System.out.println("No hay productos ni accesorios disponibles. Registre uno primero.");
            return;
        }

        List<Product> selectedItems = new ArrayList<>();
        boolean adding = true;
        while (adding) {
            System.out.println("Seleccione un producto o accesorio para agregar (o 0 para terminar):");
            for (int i = 0; i < selectableItems.size(); i++) {
                Product item = selectableItems.get(i);
                System.out.println((i + 1) + ". " + item.getTitle()
                        + " (Stock: " + item.getStock()
                        + ", Precio: $" + item.getPrice() + ")");
            }
            int itemIndex = readInt() - 1;
            if (itemIndex == -1) {
                adding = false;
            } else if (itemIndex >= 0 && itemIndex < selectableItems.size()) {
                selectedItems.add(selectableItems.get(itemIndex));
                System.out.println("Se agregó " + selectableItems.get(itemIndex).getTitle() + " a la venta.");
            } else {
                System.out.println("Selección inválida.");
            }
        }

        if (selectedItems.isEmpty()) {
            System.out.println("No se seleccionó ningún ítem. Venta cancelada.");
            return;
        }

        List<String> productIdsWithExtendedWarranty = new ArrayList<>();
        for (Product item : selectedItems) {
            if (item instanceof Console) {
                System.out.print("¿Desea agregar garantía extendida a "
                        + item.getTitle() + "? (s/n): ");
                String respuesta = scanner.nextLine();
                if (respuesta.equalsIgnoreCase("s")) {
                    productIdsWithExtendedWarranty.add(item.getId());
                }
            }
        }

        try {
            Sale sale = saleService.registerSale(selectedCustomer, selectedSeller, selectedItems,
                    productIdsWithExtendedWarranty);
            System.out.println("¡Venta registrada exitosamente!");
            System.out.println(sale.generateReceipt());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void viewAllSales() {
        List<Sale> sales = saleService.viewAllSales();
        if (sales.isEmpty()) {
            System.out.println("No hay ventas registradas aún.");
            return;
        }
        System.out.println("--- Todas las ventas ---");
        for (Sale sale : sales) {
            System.out.println(sale);
        }
    }

    private void viewSalesByCustomer() {
        List<Customer> customers = getCustomers();
        if (customers.isEmpty()) {
            System.out.println("No hay clientes registrados aún.");
            return;
        }
        System.out.println("Seleccione un cliente:");
        for (int i = 0; i < customers.size(); i++) {
            System.out.println((i + 1) + ". " + customers.get(i).getName());
        }
        int customerIndex = readInt() - 1;
        if (customerIndex < 0 || customerIndex >= customers.size()) {
            System.out.println("Selección inválida.");
            return;
        }
        Customer selected = customers.get(customerIndex);
        List<Sale> sales = saleService.viewSalesByCustomer(selected);
        if (sales.isEmpty()) {
            System.out.println("No se encontraron ventas para este cliente.");
        } else {
            System.out.println("--- Ventas de " + selected.getName() + " ---");
            for (Sale sale : sales) {
                System.out.println(sale);
            }
        }
    }

    private void viewSalesBySeller() {
        List<Seller> sellers = getSellers();
        if (sellers.isEmpty()) {
            System.out.println("No hay vendedores registrados aún.");
            return;
        }
        System.out.println("Seleccione un vendedor:");
        for (int i = 0; i < sellers.size(); i++) {
            System.out.println((i + 1) + ". " + sellers.get(i).getName());
        }
        int sellerIndex = readInt() - 1;
        if (sellerIndex < 0 || sellerIndex >= sellers.size()) {
            System.out.println("Selección inválida.");
            return;
        }
        Seller selected = sellers.get(sellerIndex);
        List<Sale> sales = saleService.viewSalesBySeller(selected);
        if (sales.isEmpty()) {
            System.out.println("No se encontraron ventas para este vendedor.");
        } else {
            System.out.println("--- Ventas atendidas por " + selected.getName() + " ---");
            for (Sale sale : sales) {
                System.out.println(sale);
            }
        }
    }

    // ---------------------------------------------------------------
    // Accessory menu
    // ---------------------------------------------------------------

    private void accessoryMenu() {
        boolean running = true;
        while (running) {
            printAccessoryMenu();
            int option = readInt();
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
        String id = scanner.nextLine();
        System.out.print("Título: ");
        String title = scanner.nextLine();
        System.out.print("Precio: ");
        double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Stock: ");
        int stock = Integer.parseInt(scanner.nextLine());
        System.out.print("Tipo de conexión (WIRELESS/WIRED): ");
        String connectionType = scanner.nextLine();
        System.out.print("IDs de consolas compatibles (separados por coma, o vacío): ");
        String consoleIdsRaw = scanner.nextLine();

        List<String> compatibleConsoleIds = parseCommaSeparated(consoleIdsRaw);

        try {
            List<Accessory> accessories = accessoryService.listAllAccessories();
            accessoryService.registerController(accessories, id, title, price, stock,
                    connectionType, compatibleConsoleIds);
            accessoryService.saveAll(accessories);
            System.out.println("Control registrado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registerCable() {
        System.out.print("ID: ");
        String id = scanner.nextLine();
        System.out.print("Título: ");
        String title = scanner.nextLine();
        System.out.print("Precio: ");
        double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Stock: ");
        int stock = Integer.parseInt(scanner.nextLine());
        System.out.print("Longitud en metros: ");
        double length = Double.parseDouble(scanner.nextLine());
        System.out.print("Tipo de conector: ");
        String connectorType = scanner.nextLine();

        try {
            List<Accessory> accessories = accessoryService.listAllAccessories();
            accessoryService.registerCable(accessories, id, title, price, stock,
                    length, connectorType);
            accessoryService.saveAll(accessories);
            System.out.println("Cable registrado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registerMemory() {
        System.out.print("ID: ");
        String id = scanner.nextLine();
        System.out.print("Título: ");
        String title = scanner.nextLine();
        System.out.print("Precio: ");
        double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Stock: ");
        int stock = Integer.parseInt(scanner.nextLine());
        System.out.print("Capacidad en GB: ");
        int capacity = Integer.parseInt(scanner.nextLine());
        System.out.print("Tipo de memoria: ");
        String memoryType = scanner.nextLine();
        System.out.print("IDs de consolas compatibles (separados por coma, o vacío): ");
        String consoleIdsRaw = scanner.nextLine();

        List<String> compatibleConsoleIds = parseCommaSeparated(consoleIdsRaw);

        try {
            List<Accessory> accessories = accessoryService.listAllAccessories();
            accessoryService.registerMemory(accessories, id, title, price, stock,
                    capacity, memoryType, compatibleConsoleIds);
            accessoryService.saveAll(accessories);
            System.out.println("Memoria registrada exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
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
        String type = scanner.nextLine();
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
        String consoleId = scanner.nextLine();
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

    private List<String> parseCommaSeparated(String raw) {
        List<String> result = new ArrayList<>();
        if (raw == null || raw.trim().isEmpty()) {
            return result;
        }
        for (String part : raw.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    // ---------------------------------------------------------------
    // Promotion menu
    // ---------------------------------------------------------------

    private void promotionMenu() {
        boolean running = true;
        while (running) {
            printPromotionMenu();
            int option = readInt();
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
        String id = scanner.nextLine();
        System.out.print("Nombre: ");
        String name = scanner.nextLine();
        System.out.print("Fecha inicio (YYYY-MM-DD): ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());
        System.out.print("Fecha fin (YYYY-MM-DD): ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine());
        System.out.print("Porcentaje de descuento: ");
        double percentage = Double.parseDouble(scanner.nextLine());

        try {
            List<Promotion> promotions = promotionService.listAllPromotions();
            promotionService.registerPercentageDiscount(promotions, id, name, startDate, endDate, percentage);
            promotionService.saveAll(promotions);
            System.out.println("Promoción por porcentaje registrada exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registerCategoryDiscount() {
        System.out.print("ID: ");
        String id = scanner.nextLine();
        System.out.print("Nombre: ");
        String name = scanner.nextLine();
        System.out.print("Fecha inicio (YYYY-MM-DD): ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());
        System.out.print("Fecha fin (YYYY-MM-DD): ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine());
        System.out.print("Categoría (VIDEOGAME/CONSOLE): ");
        String category = scanner.nextLine();
        System.out.print("Porcentaje de descuento: ");
        double percentage = Double.parseDouble(scanner.nextLine());

        try {
            List<Promotion> promotions = promotionService.listAllPromotions();
            promotionService.registerCategoryDiscount(promotions, id, name, startDate, endDate, category, percentage);
            promotionService.saveAll(promotions);
            System.out.println("Promoción por categoría registrada exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registerBulkPurchaseDiscount() {
        System.out.print("ID: ");
        String id = scanner.nextLine();
        System.out.print("Nombre: ");
        String name = scanner.nextLine();
        System.out.print("Fecha inicio (YYYY-MM-DD): ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());
        System.out.print("Fecha fin (YYYY-MM-DD): ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine());
        System.out.print("Cantidad mínima de productos: ");
        int minimumQuantity = Integer.parseInt(scanner.nextLine());
        System.out.print("Porcentaje de descuento: ");
        double percentage = Double.parseDouble(scanner.nextLine());

        try {
            List<Promotion> promotions = promotionService.listAllPromotions();
            promotionService.registerBulkPurchaseDiscount(promotions, id, name, startDate, endDate, minimumQuantity, percentage);
            promotionService.saveAll(promotions);
            System.out.println("Promoción por volumen registrada exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
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

    // ---------------------------------------------------------------
    // Return menu
    // ---------------------------------------------------------------

    private void returnMenu() {
        boolean running = true;
        while (running) {
            printReturnMenu();
            int option = readInt();
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
        String saleId = scanner.nextLine();

        Sale sale = saleService.findById(saleId);
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
        String productIdsRaw = scanner.nextLine();
        List<String> productIds = parseCommaSeparated(productIdsRaw);

        System.out.print("Motivo de la devolución: ");
        String reason = scanner.nextLine();

        try {
            Return returnObj = returnService.registerReturn(saleId, productIds, reason);
            System.out.println("Devolución registrada exitosamente.");
            System.out.println(returnObj.generateReturnReceipt());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
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
            System.out.println(returnObj.generateReturnReceipt());
            System.out.println("---------------------------");
        }
    }

    private void viewReturnsByCustomer() {
        List<Customer> customers = getCustomers();
        if (customers.isEmpty()) {
            System.out.println("No hay clientes registrados aún.");
            return;
        }
        System.out.println("Seleccione un cliente:");
        for (int i = 0; i < customers.size(); i++) {
            System.out.println((i + 1) + ". " + customers.get(i).getName()
                    + " (ID: " + customers.get(i).getiD() + ")");
        }
        int customerIndex = readInt() - 1;
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
            System.out.println(returnObj.generateReturnReceipt());
            System.out.println("---------------------------");
        }
    }

    private void viewReturnsBySale() {
        System.out.print("ID de la venta: ");
        String saleId = scanner.nextLine();
        List<Return> returns = returnService.viewReturnsBySale(saleId);
        if (returns.isEmpty()) {
            System.out.println("No hay devoluciones para esa venta.");
            return;
        }
        System.out.println("--- Devoluciones de la venta " + saleId + " ---");
        for (Return returnObj : returns) {
            System.out.println(returnObj.generateReturnReceipt());
            System.out.println("---------------------------");
        }
    }

    private void generateMonthlyBalance() {
        System.out.print("Mes (1-12): ");
        int month;
        try {
            month = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Mes inválido.");
            return;
        }
        System.out.print("Año: ");
        int year;
        try {
            year = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Año inválido.");
            return;
        }

        try {
            double balance = returnService.generateMonthlyBalance(month, year);
            System.out.println("--- Balance mensual " + month + "/" + year + " ---");
            System.out.println("Balance neto: $" + String.format("%.2f", balance));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Warranty menu
    // ---------------------------------------------------------------

    private void warrantyMenu() {
        boolean running = true;
        while (running) {
            printWarrantyMenu();
            int option = readInt();
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
        String saleId = scanner.nextLine();
        System.out.print("ID del producto: ");
        String productId = scanner.nextLine();

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
        List<Warranty> warranties = warrantyService.viewActiveWarranties();
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
        List<Warranty> warranties = warrantyService.viewWarrantiesExpiringSoon();
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

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private List<Customer> getCustomers() {
        List<Customer> customers = new ArrayList<>();
        for (Person person : persons) {
            if (person instanceof Customer customer) {
                customers.add(customer);
            }
        }
        return customers;
    }

    private List<Seller> getSellers() {
        List<Seller> sellers = new ArrayList<>();
        for (Person person : persons) {
            if (person instanceof Seller seller) {
                sellers.add(seller);
            }
        }
        return sellers;
    }

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}