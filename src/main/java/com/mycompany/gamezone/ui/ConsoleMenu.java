package com.mycompany.gamezone.ui;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.gamezone.factory.PersonFactory;
import com.mycompany.gamezone.factory.PromotionFactory;
import com.mycompany.gamezone.service.AccessoryService;
import com.mycompany.gamezone.service.MonthlyBalanceReport;
import com.mycompany.gamezone.service.PersonService;
import com.mycompany.gamezone.service.ProductService;
import com.mycompany.gamezone.service.PromotionService;
import com.mycompany.gamezone.service.ReturnService;
import com.mycompany.gamezone.service.SaleLookup;
import com.mycompany.gamezone.service.SaleService;
import com.mycompany.gamezone.service.WarrantyService;

/**
 * Console-based user interface for GameZone Unicesar.
 * Displays the main menu, reads the user's selections and runs the
 * {@link MenuOption} chosen, which belongs to the menu class of its module. All the messages
 * shown to the user are written in Spanish, while identifiers and
 * documentation follow the English convention required by the project.
 */
public class ConsoleMenu {

    private final ConsoleIO io;
    private final List<MenuOption> options = new ArrayList<>();

    /**
     * Creates a console menu with the services required to perform the
     * user operations.
     *
     * @param productService   service that handles product-related operations
     * @param personService    service that handles person-related operations
     * @param saleService      service that handles sale-related operations
     * @param saleLookup       lookup used by the return menu to find a sale
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
                       SaleLookup saleLookup,
                       AccessoryService accessoryService,
                       PromotionService promotionService,
                       ReturnService returnService,
                       WarrantyService warrantyService,
                       MonthlyBalanceReport monthlyBalanceReport,
                       PersonFactory personFactory,
                       PromotionFactory promotionFactory) {
        this.io = new ConsoleIO();
        ProductMenu productMenu = new ProductMenu(productService, io);
        PersonMenu personMenu = new PersonMenu(personService, personFactory, io);
        SaleMenu saleMenu = new SaleMenu(productService, personService, saleService, accessoryService, io);
        AccessoryMenu accessoryMenu = new AccessoryMenu(accessoryService, io);
        PromotionMenu promotionMenu = new PromotionMenu(promotionService, promotionFactory, io);
        ReturnMenu returnMenu = new ReturnMenu(personService, saleLookup, returnService,
                monthlyBalanceReport, io);
        WarrantyMenu warrantyMenu = new WarrantyMenu(warrantyService, io);

        options.add(new MenuOption(1, "Registrar videojuego", productMenu::registerVideoGame));
        options.add(new MenuOption(2, "Registrar consola", productMenu::registerConsole));
        options.add(new MenuOption(3, "Listar todos los productos", productMenu::listProducts));
        options.add(new MenuOption(4, "Registrar cliente", personMenu::registerCustomer));
        options.add(new MenuOption(5, "Listar clientes", personMenu::listCustomers));
        options.add(new MenuOption(6, "Listar vendedores", personMenu::listSellers));
        options.add(new MenuOption(7, "Registrar una nueva venta", saleMenu::registerSale));
        options.add(new MenuOption(8, "Ver todas las ventas", saleMenu::viewAllSales));
        options.add(new MenuOption(9, "Ver ventas por cliente", saleMenu::viewSalesByCustomer));
        options.add(new MenuOption(10, "Ver ventas por vendedor", saleMenu::viewSalesBySeller));
        options.add(new MenuOption(11, "Gestión de accesorios", accessoryMenu::start));
        options.add(new MenuOption(12, "Gestión de promociones", promotionMenu::start));
        options.add(new MenuOption(13, "Gestión de devoluciones", returnMenu::start));
        options.add(new MenuOption(14, "Gestión de garantías", warrantyMenu::start));
    }

    /**
     * Starts the main interaction loop of the console menu. Displays the
     * main menu repeatedly and runs the option the user selects until the
     * user chooses to exit.
     */
    public void start() {
        boolean running = true;
        while (running) {
            printMainMenu();
            int option = io.readInt();
            if (option == 0) {
                running = false;
            } else {
                MenuOption selected = findOption(option);
                if (selected != null) {
                    selected.run();
                } else {
                    System.out.println("Opción inválida. Intente de nuevo.");
                }
            }
        }
        System.out.println("¡Hasta luego!");
    }

    private MenuOption findOption(int number) {
        for (MenuOption option : options) {
            if (option.getNumber() == number) {
                return option;
            }
        }
        return null;
    }

    private void printMainMenu() {
        System.out.println("\n===== GameZone Unicesar =====");
        for (MenuOption option : options) {
            System.out.println(option.getNumber() + ". " + option.getLabel());
        }
        System.out.println("0. Salir");
        System.out.print("Elija una opción: ");
    }
}
