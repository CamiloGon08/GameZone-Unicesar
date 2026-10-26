package com.mycompany.gamezone;

import com.mycompany.gamezone.factory.PersonFactory;
import com.mycompany.gamezone.factory.PromotionFactory;
import com.mycompany.gamezone.factory.WarrantyFactory;
import com.mycompany.gamezone.persistence.AccessoryRecordMapper;
import com.mycompany.gamezone.persistence.AccessoryRepository;
import com.mycompany.gamezone.persistence.FilePersonRepository;
import com.mycompany.gamezone.persistence.PersonRecordMapper;
import com.mycompany.gamezone.persistence.PersonRepository;
import com.mycompany.gamezone.persistence.ProductRepository;
import com.mycompany.gamezone.persistence.PromotionRecordMapper;
import com.mycompany.gamezone.persistence.PromotionRepository;
import com.mycompany.gamezone.persistence.ReturnRepository;
import com.mycompany.gamezone.persistence.SaleRepository;
import com.mycompany.gamezone.persistence.WarrantyRepository;
import com.mycompany.gamezone.service.AccessoryService;
import com.mycompany.gamezone.service.IdGenerator;
import com.mycompany.gamezone.service.InventoryService;
import com.mycompany.gamezone.service.MonthlyBalanceReport;
import com.mycompany.gamezone.service.PersonService;
import com.mycompany.gamezone.service.ProductService;
import com.mycompany.gamezone.service.PromotionService;
import com.mycompany.gamezone.service.ReturnService;
import com.mycompany.gamezone.service.SaleCatalog;
import com.mycompany.gamezone.service.SaleService;
import com.mycompany.gamezone.service.WarrantyService;
import com.mycompany.gamezone.ui.ConsoleMenu;

import utilities.FilePath;

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
        ProductRepository productRepository = new ProductRepository(FilePath.PRODUCTS);
        AccessoryRepository accessoryRepository = new AccessoryRepository(new AccessoryRecordMapper());
        PromotionRepository promotionRepository = new PromotionRepository(new PromotionRecordMapper());
        PersonRepository personRepository = new FilePersonRepository(
                new PersonRecordMapper(new PersonFactory()));

        ProductService productService = new ProductService(productRepository);
        AccessoryService accessoryService = new AccessoryService(accessoryRepository);
        PersonService personService = new PersonService(personRepository);
        PromotionService promotionService = new PromotionService(promotionRepository);
        InventoryService inventoryService = new InventoryService(productService, accessoryService);
        IdGenerator idGenerator = new IdGenerator();

        SaleCatalog saleCatalog = new SaleCatalog(new SaleRepository(), productService, personService);
        WarrantyService warrantyService = new WarrantyService(new WarrantyRepository(), saleCatalog,
                productService, new WarrantyFactory(), idGenerator);
        SaleService saleService = new SaleService(saleCatalog, inventoryService,
                promotionService, warrantyService, idGenerator);

        ReturnService returnService = new ReturnService(new ReturnRepository(FilePath.RETURNS),
                saleService, inventoryService, warrantyService, idGenerator);
        MonthlyBalanceReport monthlyBalanceReport = new MonthlyBalanceReport(saleService, returnService);

        ConsoleMenu menu = new ConsoleMenu(productService, personService, saleService,
                accessoryService, promotionService, returnService, warrantyService,
                monthlyBalanceReport, new PersonFactory(), new PromotionFactory());
        menu.start();
    }
}