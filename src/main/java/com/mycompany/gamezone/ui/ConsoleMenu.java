package com.mycompany.gamezone.ui;

import com.mycompany.gamezone.factory.PersonFactory;
import com.mycompany.gamezone.factory.PromotionFactory;
import com.mycompany.gamezone.service.AccessoryService;
import com.mycompany.gamezone.service.MonthlyBalanceReport;
import com.mycompany.gamezone.service.PersonService;
import com.mycompany.gamezone.service.ProductService;
import com.mycompany.gamezone.service.PromotionService;
import com.mycompany.gamezone.service.ReturnService;
import com.mycompany.gamezone.service.SaleService;
import com.mycompany.gamezone.service.WarrantyService;

/**
 * Console-based user interface for GameZone Unicesar.
 * Displays the main menu, reads the user's selections and dispatches
 * each option to the menu class of its module. All the messages
 * shown to the user are written in Spanish, while identifiers and
 * documentation follow the English convention required by the project.
 */
public class ConsoleMenu {

    private final ConsoleIO io;
    private final ProductMenu productMenu;
    private final PersonMenu personMenu;
    private final SaleMenu saleMenu;
    private final AccessoryMenu accessoryMenu;
    private final PromotionMenu promotionMenu;
    private final ReturnMenu returnMenu;
    private final WarrantyMenu warrantyMenu;

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
     * @param monthlyBalanceReport report that computes the monthly balance
     * @param personFactory    factory that builds new customers
     * @param promotionFactory factory that builds new promotions
     */
    public ConsoleMenu(ProductService productService,
                       PersonService personService,
                       SaleService saleService,
                       AccessoryService accessoryService,
                       PromotionService promotionService,
                       ReturnService returnService,
                       WarrantyService warrantyService,
                       MonthlyBalanceReport monthlyBalanceReport,
                       PersonFactory personFactory,
                       PromotionFactory promotionFactory) {
        this.io = new ConsoleIO();
        this.productMenu = new ProductMenu(productService, io);
        this.personMenu = new PersonMenu(personService, personFactory, io);
        this.saleMenu = new SaleMenu(productService, personService, saleService, accessoryService, io);
        this.accessoryMenu = new AccessoryMenu(accessoryService, io);
        this.promotionMenu = new PromotionMenu(promotionService, promotionFactory, io);
        this.returnMenu = new ReturnMenu(personService, saleService, returnService,
                monthlyBalanceReport, io);
        this.warrantyMenu = new WarrantyMenu(warrantyService, io);
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
            int option = io.readInt();
            switch (option) {
                case 1 -> productMenu.registerVideoGame();
                case 2 -> productMenu.registerConsole();
                case 3 -> productMenu.listProducts();
                case 4 -> personMenu.registerCustomer();
                case 5 -> personMenu.listCustomers();
                case 6 -> personMenu.listSellers();
                case 7 -> saleMenu.registerSale();
                case 8 -> saleMenu.viewAllSales();
                case 9 -> saleMenu.viewSalesByCustomer();
                case 10 -> saleMenu.viewSalesBySeller();
                case 11 -> accessoryMenu.start();
                case 12 -> promotionMenu.start();
                case 13 -> returnMenu.start();
                case 14 -> warrantyMenu.start();
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
}
