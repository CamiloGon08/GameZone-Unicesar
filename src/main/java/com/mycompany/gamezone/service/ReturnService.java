package com.mycompany.gamezone.service;

import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Return;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.persistence.ReturnData;
import com.mycompany.gamezone.persistence.ReturnRepository;
import com.mycompany.gamezone.validation.ReturnValidator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReturnService {

    private static final String RETURN_PREFIX = "RETURN";

    private final ReturnRepository repository;
    private final SaleService saleService;
    private final InventoryService inventoryService;
    private final WarrantyService warrantyService;
    private final IdGenerator idGenerator;
    private final List<Return> returns;

    public ReturnService(
            ReturnRepository repository,
            SaleService saleService,
            InventoryService inventoryService,
            WarrantyService warrantyService,
            IdGenerator idGenerator) {

        this.repository = repository;
        this.saleService = saleService;
        this.inventoryService = inventoryService;
        this.warrantyService = warrantyService;
        this.idGenerator = idGenerator;
        this.returns = loadReturns();
    }

    public Return registerReturn(String saleId, List<String> productIds, String reason) {

        ReturnValidator.validateReturnRequest(saleId, productIds, reason);

        Sale sale = saleService.findById(saleId);
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
                RETURN_PREFIX, returns.size(), this::containsReturnId);

        Return returnRecord = new Return(
                returnId, currentDate, sale, returnedProducts,
                reason, warrantyRefundAmount);

        returns.add(returnRecord);
        repository.saveAll(returns);

        return returnRecord;
    }

    public List<Return> viewAllReturns() {
        return returns;
    }

    public List<Return> viewReturnsByCustomer(Customer customer) {
        List<Return> result = new ArrayList<>();

        for (Return returnRecord : returns) {
            if (returnRecord.belongsTo(customer)) {
                result.add(returnRecord);
            }
        }

        return result;
    }

    public List<Return> viewReturnsBySale(String saleId) {
        List<Return> result = new ArrayList<>();

        for (Return returnRecord : returns) {
            if (returnRecord.getOriginalSale().getId().equals(saleId)) {
                result.add(returnRecord);
            }
        }

        return result;
    }

    private boolean containsReturnId(String id) {
        for (Return returnRecord : returns) {
            if (returnRecord.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /** Resolves the raw records into domain objects. Records whose sale no longer exists are skipped. */
    private List<Return> loadReturns() {
        List<Return> loaded = new ArrayList<>();

        for (ReturnData data : repository.loadAll()) {
            Return returnRecord = toReturn(data);

            if (returnRecord != null) {
                loaded.add(returnRecord);
            }
        }

        return loaded;
    }

    private Return toReturn(ReturnData data) {
        Sale originalSale = saleService.findById(data.getSaleId());

        if (originalSale == null) {
            return null;
        }

        List<Product> returnedProducts = new ArrayList<>();

        for (String productId : data.getProductIds()) {
            Product product = inventoryService.findAnyById(productId);

            if (product != null) {
                returnedProducts.add(product);
            }
        }

        Return returnRecord = new Return(
                data.getId(), data.getDate(), originalSale, returnedProducts,
                data.getReason(), data.getWarrantyRefundAmount());

        returnRecord.setRefundAmount(data.getRefundAmount());

        return returnRecord;
    }
}