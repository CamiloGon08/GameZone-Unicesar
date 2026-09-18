package com.mycompany.gamezone;

import com.mycompany.gamezone.persistence.AccessoryRepository;
import com.mycompany.gamezone.persistence.ProductRepository;
import com.mycompany.gamezone.persistence.SaleRepository;
import com.mycompany.gamezone.service.AccessoryService;
import com.mycompany.gamezone.service.PersonService;
import com.mycompany.gamezone.service.ProductService;
import com.mycompany.gamezone.service.SaleService;
import com.mycompany.gamezone.ui.ConsoleMenu;

/**
 * Main entry point of the GameZone Unicesar application.
 * Creates the repositories and services required by the system and starts
 * the console-based user interface.
 */
public class Main {

    /**
     * Starts the GameZone Unicesar application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        ProductRepository productRepository = new ProductRepository("data/products.txt");
        SaleRepository saleRepository = new SaleRepository();
        AccessoryRepository accessoryRepository = new AccessoryRepository();

        ProductService productService = new ProductService(productRepository);
        AccessoryService accessoryService = new AccessoryService(accessoryRepository);
        PersonService personService = new PersonService();
        SaleService saleService = new SaleService(saleRepository, productService, accessoryService);

        ConsoleMenu menu = new ConsoleMenu(productService, personService, saleService, accessoryService);
        menu.start();
    }
}