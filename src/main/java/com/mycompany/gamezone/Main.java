package com.mycompany.gamezone;

import java.util.List;

import com.mycompany.gamezone.factory.AccessoryFactory;
import com.mycompany.gamezone.factory.PersonFactory;
import com.mycompany.gamezone.factory.PromotionFactory;
import com.mycompany.gamezone.factory.WarrantyFactory;
import com.mycompany.gamezone.persistence.AccessoryRecordMapper;
import com.mycompany.gamezone.persistence.AccessoryRepository;
import com.mycompany.gamezone.persistence.FileAccessoryRepository;
import com.mycompany.gamezone.persistence.FilePersonRepository;
import com.mycompany.gamezone.persistence.FileProductRepository;
import com.mycompany.gamezone.persistence.FilePromotionRepository;
import com.mycompany.gamezone.persistence.FileReturnRepository;
import com.mycompany.gamezone.persistence.FileSaleRepository;
import com.mycompany.gamezone.persistence.FileWarrantyRepository;
import com.mycompany.gamezone.persistence.PersonRecordMapper;
import com.mycompany.gamezone.persistence.PersonRepository;
import com.mycompany.gamezone.persistence.ProductRepository;
import com.mycompany.gamezone.persistence.PromotionRecordMapper;
import com.mycompany.gamezone.persistence.PromotionRepository;
import com.mycompany.gamezone.persistence.ReturnRecordMapper;
import com.mycompany.gamezone.persistence.SaleRecordMapper;
import com.mycompany.gamezone.persistence.WarrantyRecordMapper;
import com.mycompany.gamezone.service.AccessoryService;
import com.mycompany.gamezone.service.AccessoryStockHandler;
import com.mycompany.gamezone.service.IdGenerator;
import com.mycompany.gamezone.service.InventoryService;
import com.mycompany.gamezone.service.MonthlyBalanceReport;
import com.mycompany.gamezone.service.PersonService;
import com.mycompany.gamezone.service.ProductService;
import com.mycompany.gamezone.service.ProductStockHandler;
import com.mycompany.gamezone.service.PromotionService;
import com.mycompany.gamezone.service.ReturnCatalog;
import com.mycompany.gamezone.service.ReturnService;
import com.mycompany.gamezone.service.SaleCatalog;
import com.mycompany.gamezone.service.SaleService;
import com.mycompany.gamezone.service.WarrantyCatalog;
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
        ProductRepository productRepository = new FileProductRepository(FilePath.PRODUCTS);
        AccessoryRepository accessoryRepository = new FileAccessoryRepository(new AccessoryRecordMapper());
        PromotionRepository promotionRepository = new FilePromotionRepository(new PromotionRecordMapper());
        PersonRepository personRepository = new FilePersonRepository(
                new PersonRecordMapper(new PersonFactory()));

        ProductService productService = new ProductService(productRepository);
        AccessoryService accessoryService = new AccessoryService(accessoryRepository, new AccessoryFactory());
        PersonService personService = new PersonService(personRepository);
        PromotionService promotionService = new PromotionService(promotionRepository);
        InventoryService inventoryService = new InventoryService(List.of(
                new ProductStockHandler(productService), new AccessoryStockHandler(accessoryService)));
        IdGenerator idGenerator = new IdGenerator();

        SaleCatalog saleCatalog = new SaleCatalog(new FileSaleRepository(new SaleRecordMapper()),
                productService, personService);
        WarrantyCatalog warrantyCatalog = new WarrantyCatalog(
                new FileWarrantyRepository(new WarrantyRecordMapper()), saleCatalog,
                productService, new WarrantyFactory());
        WarrantyService warrantyService = new WarrantyService(warrantyCatalog,
                new WarrantyFactory(), idGenerator);
        SaleService saleService = new SaleService(saleCatalog, inventoryService,
                promotionService, warrantyService, idGenerator);

        ReturnCatalog returnCatalog = new ReturnCatalog(
                new FileReturnRepository(FilePath.RETURNS, new ReturnRecordMapper()),
                saleCatalog, inventoryService);
        ReturnService returnService = new ReturnService(returnCatalog, saleCatalog,
                inventoryService, warrantyService, idGenerator);
        MonthlyBalanceReport monthlyBalanceReport = new MonthlyBalanceReport(saleCatalog, returnService);

        ConsoleMenu menu = new ConsoleMenu(productService, personService, saleService, saleCatalog,
                accessoryService, promotionService, returnService, warrantyService,
                monthlyBalanceReport, new PersonFactory(), new PromotionFactory());
        menu.start();
    }
}