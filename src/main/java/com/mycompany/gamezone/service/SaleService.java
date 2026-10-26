package com.mycompany.gamezone.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gamezone.model.Accessory;
import com.mycompany.gamezone.model.Console;
import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Promotion;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.Seller;
import com.mycompany.gamezone.model.Warranty;
import com.mycompany.gamezone.persistence.SaleRepository;
import com.mycompany.gamezone.validation.ProductValidator;
import com.mycompany.gamezone.validation.SaleValidator;

/**
 * Service that handles the business rules related to sales.
 * Coordinates the creation of new sales, the validation of stock,
 * the update of the inventory, the application of the best promotion,
 * the automatic assignment of warranties, and the queries over the
 * sales registered in the system. The checks on the sale data and on the
 * stock of each item are delegated to {@link SaleValidator} and
 * {@link ProductValidator}, which report failures with typed exceptions
 * from the GameZone exception hierarchy.
 * 
 * @author EstefaniaMarquez
 * 
 */

public class SaleService {

    private SaleRepository repository;
    private List<Sale> sales;
    private ProductService productService;
    private AccessoryService accessoryService;
    private PromotionService promotionService;
    private WarrantyService warrantyService;

    /**
     * Creates a SaleService with the repositories and services required
     * to register sales, update inventory, apply promotions and assign
     * warranties.
     *
     * @param repository       repository used to save and load sales
     * @param productService   service used to update product stock
     * @param accessoryService service used to update accessory stock
     * @param promotionService service used to find the best promotion for a sale
     * @param warrantyService  service used to assign warranties
     */
    public SaleService(SaleRepository repository,
                       ProductService productService,
                       AccessoryService accessoryService,
                       PromotionService promotionService,
                       WarrantyService warrantyService) {
        this.repository = repository;
        this.sales = repository.load();
        this.productService = productService;
        this.accessoryService = accessoryService;
        this.promotionService = promotionService;
        this.warrantyService = warrantyService;
    }

    /**
     * Registers a new sale for the given customer and seller with the
     * list of items provided. Products that are consoles automatically
     * receive a basic warranty, and products listed in the extended
     * warranty list receive an extended warranty whose additional cost
     * is added to the total of the sale.
     *
     * @param customer                       customer who makes the purchase
     * @param seller                         seller who attends the sale
     * @param products                       list of items included in the sale
     * @param productIdsWithExtendedWarranty IDs of products that should receive extended warranty (may be null or empty)
     * @return the newly created sale
     * @throws com.mycompany.gamezone.exceptions.InvalidDataException if the
     *         customer or the seller is missing
     * @throws com.mycompany.gamezone.exceptions.BusinessRuleException if the
     *         item list is null or empty, or if any item has insufficient stock
     * @throws com.mycompany.gamezone.exceptions.ResourceNotFoundException if an
     *         item to be deducted from the inventory no longer exists
     * @throws com.mycompany.gamezone.exceptions.PersistenceException if the
     *         inventory or the sales cannot be saved
     */
    public Sale registerSale(Customer customer, Seller seller, List<Product> products,
                             List<String> productIdsWithExtendedWarranty) {
        SaleValidator.validateSaleData(customer, seller, products);

        for (Product product : products) {
            ProductValidator.validateStockAvailability(product, 1);
        }

        String saleId = "SALE-" + (sales.size() + 1);
        Sale sale = new Sale(LocalDate.now(), saleId, products, seller, customer);

        Promotion bestPromotion = promotionService.findBestPromotionFor(sale);
        if (bestPromotion != null) {
            double discount = bestPromotion.calculateDiscount(sale);
            if (discount > 0) {
                sale.applyPromotion(bestPromotion.getName(), discount);
            }
        }

        for (Product product : products) {
            if (product instanceof Console) {
                warrantyService.assignBasicWarranty(product, sale);
            }
            if (productIdsWithExtendedWarranty != null
                    && productIdsWithExtendedWarranty.contains(product.getId())) {
                Warranty extended = warrantyService.assignExtendedWarranty(product, sale);
                if (extended != null) {
                    sale.addWarrantyCost(extended.getAdditionalCost());
                }
            }
        }

        List<Accessory> accessoriesToPersist = null;
        for (Product product : products) {
            if (product instanceof Accessory) {
                if (accessoriesToPersist == null) {
                    accessoriesToPersist = accessoryService.listAllAccessories();
                }
                accessoryService.updateStock(accessoriesToPersist, product.getId(), -1);
            } else {
                productService.updateStock(product.getId(), -1);
            }
        }
        if (accessoriesToPersist != null) {
            accessoryService.saveAll(accessoriesToPersist);
        }

        sales.add(sale);
        repository.save(sales);

        return sale;
    }

    /**
     * Returns the list of all sales registered in the system.
     *
     * @return list containing every sale
     */
    public List<Sale> viewAllSales() {
        return sales;
    }

    /**
     * Returns every sale made by the given customer.
     *
     * @param customer customer whose sales will be filtered
     * @return list of sales belonging to the customer
     */
    public List<Sale> viewSalesByCustomer(Customer customer) {
        List<Sale> result = new ArrayList<>();
        for (Sale sale : sales) {
            if (sale.getCustomer().equals(customer)) {
                result.add(sale);
            }
        }
        return result;
    }

    /**
     * Finds a sale using its identification number.
     *
     * @param id the identification number of the sale
     * @return the Sale with the specified ID, or null if it does not exist
     */
    public Sale findById(String id) {
        for (Sale sale : sales) {
            if (sale.getId().equals(id)) {
                return sale;
            }
        }
        return null;
    }

    /**
     * Returns every sale attended by the given seller.
     *
     * @param seller seller whose sales will be filtered
     * @return list of sales attended by the seller
     */
    public List<Sale> viewSalesBySeller(Seller seller) {
        List<Sale> result = new ArrayList<>();
        for (Sale sale : sales) {
            if (sale.getSeller().equals(seller)) {
                result.add(sale);
            }
        }
        return result;
    }
}