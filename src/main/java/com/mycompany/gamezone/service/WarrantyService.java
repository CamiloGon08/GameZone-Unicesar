package com.mycompany.gamezone.service;

import com.mycompany.gamezone.model.BasicWarranty;
import com.mycompany.gamezone.model.ExtendedWarranty;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.Warranty;
import com.mycompany.gamezone.persistence.SaleRepository;
import com.mycompany.gamezone.persistence.WarrantyRepository;
import com.mycompany.gamezone.persistence.WarrantyRepository.WarrantyData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for managing warranties.
 *
 * This service handles warranty creation, validation, searching, viewing,
 * persistence and loading. Product and Sale references stored by
 * WarrantyRepository are resolved here through ProductService and
 * SaleRepository.
 *
 * @author EstefaniaMarquez
 */
public class WarrantyService {

    private final WarrantyRepository repository;
    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final List<Warranty> warranties;

    /**
     * Creates a WarrantyService with the repositories and services required
     * to manage and resolve warranty references.
     *
     * @param repository repository responsible for warranty persistence
     * @param saleRepository repository used to resolve Sale references
     * @param productService service used to resolve Product references
     * @throws IllegalArgumentException if any dependency is null
     */
    public WarrantyService(
            WarrantyRepository repository,
            SaleRepository saleRepository,
            ProductService productService) {

        if (repository == null) {
            throw new IllegalArgumentException(
                    "WarrantyRepository cannot be null.");
        }

        if (saleRepository == null) {
            throw new IllegalArgumentException(
                    "SaleRepository cannot be null.");
        }

        if (productService == null) {
            throw new IllegalArgumentException(
                    "ProductService cannot be null.");
        }

        this.repository = repository;
        this.saleRepository = saleRepository;
        this.productService = productService;
        this.warranties = loadWarranties();
    }

    /**
     * Loads warranties from the persistence file and resolves their
     * Product and Sale references.
     *
     * WarrantyRepository only provides the identifiers stored in the file.
     * This service uses those identifiers to reconstruct the complete
     * Warranty objects.
     *
     * @return list of reconstructed warranties
     */
    private List<Warranty> loadWarranties() {

        List<Warranty> loadedWarranties = new ArrayList<>();

        for (WarrantyData data : repository.loadAll()) {

            Product product = productService.findById(
                    data.getProductId());

            Sale sale = saleRepository.findById(
                    data.getSaleId());

            if (product == null || sale == null) {
                continue;
            }

            Warranty warranty;

            if ("BASIC".equals(data.getType())) {

                warranty = new BasicWarranty(
                        data.getId(),
                        product,
                        sale,
                        data.getStartDate());

            } else if ("EXTENDED".equals(data.getType())) {

                warranty = new ExtendedWarranty(
                        data.getId(),
                        product,
                        sale,
                        data.getStartDate());

            } else {
                continue;
            }

            loadedWarranties.add(warranty);
        }

        return loadedWarranties;
    }

    /**
     * Assigns a basic warranty to a product associated with a sale.
     *
     * @param product product covered by the warranty
     * @param sale sale associated with the warranty
     * @return created basic warranty
     */
    public Warranty assignBasicWarranty(Product product, Sale sale) {
        validateWarrantyData(product, sale);

        if (hasWarrantyOfType(product.getId(), sale.getId(), BasicWarranty.class)) {
            return findByProductAndSale(product.getId(), sale.getId());
        }

        String warrantyId = generateWarrantyId();
        Warranty warranty = new BasicWarranty(warrantyId, product, sale, LocalDate.now());
        warranties.add(warranty);
        saveAll();
        return warranty;
    }

    /**
     * Assigns an extended warranty to a product associated with a sale.
     *
     * @param product product covered by the warranty
     * @param sale sale associated with the warranty
     * @return created extended warranty, or null if the product already has a
     * warranty for the sale
     */
    public Warranty assignExtendedWarranty(Product product, Sale sale) {
        validateWarrantyData(product, sale);

        if (hasWarrantyOfType(product.getId(), sale.getId(), ExtendedWarranty.class)) {
            return null;
        }

        String warrantyId = generateWarrantyId();
        Warranty warranty = new ExtendedWarranty(warrantyId, product, sale, LocalDate.now());
        warranties.add(warranty);
        saveAll();
        return warranty;
    }

    private boolean hasWarrantyOfType(String productId, String saleId, Class<? extends Warranty> type) {
        for (Warranty warranty : warranties) {
            if (type.isInstance(warranty)
                    && warranty.getProduct().getId().equalsIgnoreCase(productId)
                    && warranty.getSale().getId().equalsIgnoreCase(saleId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates the product and sale references required to create a warranty.
     *
     * @param product product associated with the warranty
     * @param sale sale associated with the warranty
     * @throws IllegalArgumentException if the product or sale is null
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
    }

    /**
     * Finds a warranty using its identifier.
     *
     * @param id warranty identifier
     * @return matching warranty, or null if it does not exist
     */
    public Warranty findById(String id) {

        for (Warranty warranty : warranties) {

            if (warranty.getId().equals(id)) {
                return warranty;
            }
        }

        return null;
    }

    /**
     * Finds a warranty associated with a specific product and sale.
     *
     * @param productId product identifier
     * @param saleId sale identifier
     * @return matching warranty, or null if none exists
     */
    public Warranty findByProductAndSale(
            String productId,
            String saleId) {

        for (Warranty warranty : warranties) {

            if (warranty.getProduct().getId().equals(productId)
                    && warranty.getSale().getId().equals(saleId)) {

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
     * Returns all warranties that are active on the specified date.
     *
     * @param date date used to determine whether a warranty is active
     * @return list of active warranties
     */
    public List<Warranty> viewActiveWarranties(LocalDate date) {

        List<Warranty> activeWarranties = new ArrayList<>();

        for (Warranty warranty : warranties) {

            if (warranty.isActive(date)) {
                activeWarranties.add(warranty);
            }
        }

        return activeWarranties;
    }

    /**
     * Returns warranties that expire within the specified number of days.
     *
     * @param days number of days used as the expiration window
     * @return list of warranties expiring within the specified period
     */
    public List<Warranty> viewWarrantiesExpiringSoon(int days) {

        LocalDate today = LocalDate.now();
        LocalDate limitDate = today.plusDays(days);

        List<Warranty> expiringWarranties = new ArrayList<>();

        for (Warranty warranty : warranties) {

            LocalDate expirationDate = warranty.getEndDate();

            if (!expirationDate.isBefore(today)
                    && !expirationDate.isAfter(limitDate)) {

                expiringWarranties.add(warranty);
            }
        }

        return expiringWarranties;
    }

    /**
     * Generates a unique warranty identifier.
     *
     * @return new warranty identifier
     */
    private String generateWarrantyId() {

        int nextNumber = warranties.size() + 1;

        String warrantyId = "WAR-" + nextNumber;

        while (findById(warrantyId) != null) {
            nextNumber++;
            warrantyId = "WAR-" + nextNumber;
        }

        return warrantyId;
    }

    /**
     * Saves all current warranties using the persistence repository.
     */
    private void saveAll() {
        repository.saveAll(warranties);
    }
}
