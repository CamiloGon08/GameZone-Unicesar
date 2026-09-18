package com.mycompany.gamezone.service;

import com.mycompany.gamezone.model.Console;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.Warranty;
import com.mycompany.gamezone.model.BasicWarranty;
import com.mycompany.gamezone.model.ExtendedWarranty;
import com.mycompany.gamezone.persistence.WarrantyRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Business rules and validation for warranties.
 *
 * This service manages the creation and consultation of product warranties.
 * Basic warranties are automatically assigned to consoles, while extended
 * warranties can be assigned optionally to eligible products.
 *
 * @author EstefaniaMarquez
 */
public class WarrantyService {

    private final WarrantyRepository repository;
    private final List<Warranty> warranties;

    /**
     * Creates a WarrantyService with the required repository.
     *
     * @param repository repository used to persist warranties
     */
    public WarrantyService(WarrantyRepository repository) {

        if (repository == null) {
            throw new IllegalArgumentException(
                    "WarrantyRepository cannot be null.");
        }

        this.repository = repository;
        this.warranties = new ArrayList<>(repository.loadAll());
    }

    /**
     * Automatically creates a basic warranty for a console included
     * in a sale.
     *
     * Basic warranties are free and last six months from the sale date.
     *
     * @param product product covered by the warranty
     * @param sale sale in which the product was purchased
     * @return the newly created basic warranty
     */
    public Warranty assignBasicWarranty(Product product, Sale sale) {

        validateWarrantyData(product, sale);

        if (!(product instanceof Console)) {
            throw new IllegalArgumentException(
                    "Basic warranties are only available for consoles.");
        }

        if (findByProductAndSale(product.getId(), sale.getId()) != null) {
            throw new IllegalArgumentException(
                    "The product already has a warranty for this sale.");
        }

        String warrantyId = generateWarrantyId();

        Warranty warranty = new BasicWarranty(
                warrantyId,
                product,
                sale,
                sale.getDate());

        warranties.add(warranty);
        repository.saveAll(warranties);

        return warranty;
    }

    /**
     * Creates an extended warranty for a console included in a sale.
     *
     * Extended warranties last twelve months and have an additional cost
     * equivalent to ten percent of the product price.
     *
     * @param product product covered by the warranty
     * @param sale sale in which the product was purchased
     * @return the newly created extended warranty
     */
    public Warranty assignExtendedWarranty(Product product, Sale sale) {

        validateWarrantyData(product, sale);

        if (!(product instanceof Console)) {
            throw new IllegalArgumentException(
                    "Extended warranties are only available for consoles.");
        }

        if (findByProductAndSale(product.getId(), sale.getId()) != null) {
            throw new IllegalArgumentException(
                    "The product already has a warranty for this sale.");
        }

        String warrantyId = generateWarrantyId();

        Warranty warranty = new ExtendedWarranty(
                warrantyId,
                product,
                sale,
                sale.getDate());

        warranties.add(warranty);
        repository.saveAll(warranties);

        return warranty;
    }

    /**
     * Validates the common data required to create a warranty.
     *
     * @param product product covered by the warranty
     * @param sale sale associated with the warranty
     */
    private void validateWarrantyData(Product product, Sale sale) {

        if (product == null) {
            throw new IllegalArgumentException(
                    "Product cannot be null.");
        }

        if (sale == null) {
            throw new IllegalArgumentException(
                    "Sale cannot be null.");
        }

        boolean productBelongsToSale = false;

        for (Product saleProduct : sale.getProducts()) {

            if (saleProduct.getId().equals(product.getId())) {
                productBelongsToSale = true;
                break;
            }
        }

        if (!productBelongsToSale) {
            throw new IllegalArgumentException(
                    "The product does not belong to the specified sale.");
        }

        if (sale.getDate() == null) {
            throw new IllegalArgumentException(
                    "Sale date cannot be null.");
        }
    }

    /**
     * Finds a warranty using its identification number.
     *
     * @param id warranty identification number
     * @return the warranty with the specified ID, or null if it does not exist
     */
    public Warranty findById(String id) {

        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        for (Warranty warranty : warranties) {

            if (warranty.getId().equalsIgnoreCase(id.trim())) {
                return warranty;
            }
        }

        return null;
    }

    /**
     * Finds the warranty associated with a specific product within a
     * specific sale.
     *
     * @param productId product identification number
     * @param saleId sale identification number
     * @return the associated warranty, or null if none exists
     */
    public Warranty findByProductAndSale(
            String productId,
            String saleId) {

        if (productId == null || productId.trim().isEmpty()
                || saleId == null || saleId.trim().isEmpty()) {
            return null;
        }

        for (Warranty warranty : warranties) {

            boolean sameProduct = warranty.getProduct()
                    .getId()
                    .equalsIgnoreCase(productId.trim());

            boolean sameSale = warranty.getSale()
                    .getId()
                    .equalsIgnoreCase(saleId.trim());

            if (sameProduct && sameSale) {
                return warranty;
            }
        }

        return null;
    }

    /**
     * Returns all registered warranties.
     *
     * @return list containing all warranties
     */
    public List<Warranty> viewAllWarranties() {
        return new ArrayList<>(warranties);
    }

    /**
     * Returns all warranties that are active on the current date.
     *
     * @return list of currently active warranties
     */
    public List<Warranty> viewActiveWarranties() {

        LocalDate currentDate = LocalDate.now();
        List<Warranty> result = new ArrayList<>();

        for (Warranty warranty : warranties) {

            if (warranty.isActive(currentDate)) {
                result.add(warranty);
            }
        }

        return result;
    }

    /**
     * Returns warranties that will expire within the next 30 days.
     *
     * Only warranties that are still active and whose expiration date is
     * between today and thirty days from today are included.
     *
     * @return list of warranties expiring within the next 30 days
     */
    public List<Warranty> viewWarrantiesExpiringSoon() {

        LocalDate currentDate = LocalDate.now();
        LocalDate limitDate = currentDate.plusDays(30);

        List<Warranty> result = new ArrayList<>();

        for (Warranty warranty : warranties) {

            LocalDate endDate = warranty.getEndDate();

            if (warranty.isActive(currentDate)
                    && !endDate.isAfter(limitDate)) {

                result.add(warranty);
            }
        }

        return result;
    }

    /**
     * Generates a unique identification number for a new warranty.
     *
     * @return unique warranty identification number
     */
    private String generateWarrantyId() {

        int nextNumber = warranties.size() + 1;
        String warrantyId = "WARRANTY-" + nextNumber;

        while (findById(warrantyId) != null) {
            nextNumber++;
            warrantyId = "WARRANTY-" + nextNumber;
        }

        return warrantyId;
    }

    /**
     * Persists the current list of warranties.
     */
    public void saveAll() {
        repository.saveAll(warranties);
    }
}