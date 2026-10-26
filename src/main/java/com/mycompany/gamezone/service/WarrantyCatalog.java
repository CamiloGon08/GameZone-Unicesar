package com.mycompany.gamezone.service;

import com.mycompany.gamezone.factory.WarrantyFactory;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.Warranty;
import com.mycompany.gamezone.persistence.WarrantyData;
import com.mycompany.gamezone.persistence.WarrantyRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps the list of warranties of the system. It loads the raw records from
 * {@link WarrantyRepository} the first time they are needed, resolves their
 * product and sale, and saves the list after every change.
 * 
 * @author EstefaniaMarquez
 * 
 */
public class WarrantyCatalog {

    private final WarrantyRepository repository;
    private final SaleLookup saleLookup;
    private final ProductLookup productLookup;
    private final WarrantyFactory warrantyFactory;

    /**
     * Loaded on first use (see warranties()), after every sale can be found.
     */
    private List<Warranty> warranties;

    /**
     * Creates the catalog. The stored warranties are loaded on first use.
     *
     * @param repository repository used to save and load warranties
     * @param saleLookup lookup used to resolve the sale of a warranty
     * @param productLookup lookup used to resolve the covered product
     * @param warrantyFactory factory that builds each warranty type
     */
    public WarrantyCatalog(WarrantyRepository repository, SaleLookup saleLookup,
            ProductLookup productLookup, WarrantyFactory warrantyFactory) {
        this.repository = repository;
        this.saleLookup = saleLookup;
        this.productLookup = productLookup;
        this.warrantyFactory = warrantyFactory;
    }

    /**
     * Returns the list of every warranty.
     *
     * @return the list of warranties
     */
    public List<Warranty> findAll() {
        return warranties();
    }

    /**
     * Finds a warranty by its identifier.
     *
     * @param id identifier of the warranty
     * @return the warranty, or null if it does not exist
     */
    public Warranty findById(String id) {
        for (Warranty warranty : warranties()) {
            if (warranty.getId().equals(id)) {
                return warranty;
            }
        }
        return null;
    }

    /**
     * Returns how many warranties exist.
     *
     * @return the number of warranties
     */
    public int size() {
        return warranties().size();
    }

    /**
     * Adds a new warranty and saves every warranty.
     *
     * @param warranty the warranty to add
     */
    public void add(Warranty warranty) {
        warranties().add(warranty);
        save();
    }

    /**
     * Removes the given warranties and saves the list.
     *
     * @param removed the warranties to remove
     */
    public void removeAll(List<Warranty> removed) {
        warranties().removeAll(removed);
        save();
    }

    private List<Warranty> warranties() {
        if (warranties == null) {
            warranties = loadWarranties();
        }
        return warranties;
    }

    private List<Warranty> loadWarranties() {
        List<Warranty> loadedWarranties = new ArrayList<>();

        for (WarrantyData data : repository.loadAll()) {

            Product product = productLookup.findById(data.getProductId());
            Sale sale = saleLookup.findById(data.getSaleId());

            if (product == null || sale == null) {
                continue;
            }

            Warranty warranty = warrantyFactory.create(
                    data.getType(), data.getId(), product, sale, data.getStartDate());

            if (warranty != null) {
                loadedWarranties.add(warranty);
            }
        }

        return loadedWarranties;
    }

    private void save() {
        repository.saveAll(warranties());
    }
}
