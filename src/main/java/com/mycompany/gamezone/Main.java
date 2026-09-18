package com.mycompany.gamezone;

import com.mycompany.gamezone.persistence.AccessoryRepository;
import com.mycompany.gamezone.persistence.ProductRepository;
import com.mycompany.gamezone.persistence.PromotionRepository;
import com.mycompany.gamezone.persistence.ReturnRepository;
import com.mycompany.gamezone.persistence.SaleRepository;
import com.mycompany.gamezone.persistence.WarrantyRepository;
import com.mycompany.gamezone.service.AccessoryService;
import com.mycompany.gamezone.service.PersonService;
import com.mycompany.gamezone.service.ProductService;
import com.mycompany.gamezone.service.PromotionService;
import com.mycompany.gamezone.service.ReturnService;
import com.mycompany.gamezone.service.SaleService;
import com.mycompany.gamezone.service.WarrantyService;
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
        PromotionRepository promotionRepository = new PromotionRepository();

        ProductService productService = new ProductService(productRepository);
        AccessoryService accessoryService = new AccessoryService(accessoryRepository);
        PersonService personService = new PersonService();
        PromotionService promotionService = new PromotionService(promotionRepository);
        SaleService saleService = new SaleService(saleRepository, productService, accessoryService, promotionService);

        ReturnRepository returnRepository = new ReturnRepository("data/returns.txt", saleService, productService);
        ReturnService returnService = new ReturnService(returnRepository, saleService, productService);

        WarrantyRepository warrantyRepository = new WarrantyRepository(saleService, productService);
        WarrantyService warrantyService = new WarrantyService(warrantyRepository);
        saleService.setWarrantyService(warrantyService);

        ConsoleMenu menu = new ConsoleMenu(productService, personService, saleService,
                accessoryService, promotionService, returnService, warrantyService);
        menu.start();
    }
}