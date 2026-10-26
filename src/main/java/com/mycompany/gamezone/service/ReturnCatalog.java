package com.mycompany.gamezone.service;

import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Return;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.persistence.ReturnData;
import com.mycompany.gamezone.persistence.ReturnRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * Keeps the list of returns of the system. It loads the raw records from
 * {@link ReturnRepository}, resolves their sale and products, and saves the
 * list after every new return.
 * 
 * @author EstefaniaMarquez
 * 
 */
public class ReturnCatalog {

    private final ReturnRepository repository;
    private final SaleLookup saleLookup;
    private final InventoryService inventoryService;
    private final List<Return> returns;

    /**
     * Creates the catalog and loads the stored returns.
     *
     * @param repository repository used to save and load returns
     * @param saleLookup lookup used to resolve the original sale
     * @param inventoryService service used to resolve the returned items
     */
    public ReturnCatalog(ReturnRepository repository, SaleLookup saleLookup,
            InventoryService inventoryService) {
        this.repository = repository;
        this.saleLookup = saleLookup;
        this.inventoryService = inventoryService;
        this.returns = loadReturns();
    }

    /**
     * Returns the list of every return.
     *
     * @return the list of returns
     */
    public List<Return> findAll() {
        return returns;
    }

    /**
     * Adds a new return and saves every return.
     *
     * @param returnRecord the return to add
     */
    public void add(Return returnRecord) {
        returns.add(returnRecord);
        repository.saveAll(returns);
    }

    /**
     * Returns how many returns exist.
     *
     * @return the number of returns
     */
    public int size() {
        return returns.size();
    }

    /**
     * Checks whether a return already uses the given identifier.
     *
     * @param id identifier to check
     * @return true if a return has that identifier
     */
    public boolean containsId(String id) {
        for (Return returnRecord : returns) {
            if (returnRecord.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Resolves the raw records into domain objects. Records whose sale no
     * longer exists are skipped.
     */
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
        Sale originalSale = saleLookup.findById(data.getSaleId());

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
