package com.mycompany.gamezone.service;

import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Return;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.validation.ReturnValidator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Registers returns and answers the queries over them. Loading and saving the
 * returns belongs to {@link ReturnCatalog}.
 * 
 * @author EstefaniaMarquez
 * 
 */
public class ReturnService {

    private static final String RETURN_PREFIX = "RETURN";

    private final ReturnCatalog returnCatalog;
    private final SaleLookup saleLookup;
    private final InventoryService inventoryService;
    private final WarrantyService warrantyService;
    private final IdGenerator idGenerator;

    /**
     * Creates the service with its collaborators.
     *
     * @param returnCatalog catalog that keeps and saves the returns
     * @param saleLookup lookup used to find the original sale
     * @param inventoryService service used to find items and restore stock
     * @param warrantyService service used to cancel warranties
     * @param idGenerator generator of return identifiers
     */
    public ReturnService(
            ReturnCatalog returnCatalog,
            SaleLookup saleLookup,
            InventoryService inventoryService,
            WarrantyService warrantyService,
            IdGenerator idGenerator) {

        this.returnCatalog = returnCatalog;
        this.saleLookup = saleLookup;
        this.inventoryService = inventoryService;
        this.warrantyService = warrantyService;
        this.idGenerator = idGenerator;
    }

    public Return registerReturn(String saleId, List<String> productIds, String reason) {

        ReturnValidator.validateReturnRequest(saleId, productIds, reason);

        Sale sale = saleLookup.findById(saleId);
        ReturnValidator.validateSaleExists(sale);

        LocalDate currentDate = LocalDate.now();
        ReturnValidator.validateReturnWindow(sale, currentDate);

        List<Product> returnedProducts = new ArrayList<>();

        for (String productId : productIds) {
            Product product = inventoryService.findAnyById(productId);

            ReturnValidator.validateProductExists(product, productId);
            ReturnValidator.validateProductBelongsToSale(sale, productId);

            returnedProducts.add(product);
        }

        double warrantyRefundAmount = 0.0;

        for (Product product : returnedProducts) {
            inventoryService.increaseStock(product.getId(), 1);

            if (product.requiresBasicWarranty()) {
                warrantyRefundAmount += warrantyService.cancelWarranties(
                        product.getId(), saleId);
            }
        }

        String returnId = idGenerator.nextId(
                RETURN_PREFIX, returnCatalog.size(), returnCatalog::containsId);

        Return returnRecord = new Return(
                returnId, currentDate, sale, returnedProducts,
                reason, warrantyRefundAmount);

        returnCatalog.add(returnRecord);

        return returnRecord;
    }

    public List<Return> viewAllReturns() {
        return returnCatalog.findAll();
    }

    public List<Return> viewReturnsByCustomer(Customer customer) {
        List<Return> result = new ArrayList<>();

        for (Return returnRecord : returnCatalog.findAll()) {
            if (returnRecord.belongsTo(customer)) {
                result.add(returnRecord);
            }
        }

        return result;
    }

    public List<Return> viewReturnsBySale(String saleId) {
        List<Return> result = new ArrayList<>();

        for (Return returnRecord : returnCatalog.findAll()) {
            if (returnRecord.getOriginalSale().getId().equals(saleId)) {
                result.add(returnRecord);
            }
        }

        return result;
    }
}
