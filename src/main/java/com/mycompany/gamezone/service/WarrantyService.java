package com.mycompany.gamezone.service;

import com.mycompany.gamezone.factory.WarrantyFactory;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.Warranty;
import com.mycompany.gamezone.validation.WarrantyValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Assigns, cancels and queries warranties. Loading and saving the warranties
 * belongs to {@link WarrantyCatalog}.
 * 
 * @author EstefaniaMarquez
 * 
 */
public class WarrantyService {

    private static final String WARRANTY_PREFIX = "WAR";

    private final WarrantyCatalog warrantyCatalog;
    private final WarrantyFactory warrantyFactory;
    private final IdGenerator idGenerator;

    /**
     * Creates the service with its collaborators.
     *
     * @param warrantyCatalog catalog that keeps and saves the warranties
     * @param warrantyFactory factory that builds each warranty type
     * @param idGenerator generator of warranty identifiers
     */
    public WarrantyService(
            WarrantyCatalog warrantyCatalog,
            WarrantyFactory warrantyFactory,
            IdGenerator idGenerator) {

        if (warrantyCatalog == null) {
            throw new IllegalArgumentException("WarrantyCatalog cannot be null.");
        }
        if (warrantyFactory == null) {
            throw new IllegalArgumentException("WarrantyFactory cannot be null.");
        }
        if (idGenerator == null) {
            throw new IllegalArgumentException("IdGenerator cannot be null.");
        }

        this.warrantyCatalog = warrantyCatalog;
        this.warrantyFactory = warrantyFactory;
        this.idGenerator = idGenerator;
    }

    /**
     * Assigns the warranties a sale needs: basic for products that require it,
     * extended for the product IDs the customer asked for.
     *
     * @return additional cost of the extended warranties created now, for the
     * caller to add to the sale
     */
    public double assignWarrantiesFor(Sale sale, Collection<String> extendedProductIds) {
        double extendedCost = 0.0;

        for (Product product : sale.getProducts()) {

            if (product.requiresBasicWarranty()) {
                assignBasicWarranty(product, sale);
            }

            if (product.allowsExtendedWarranty()
                    && extendedProductIds != null
                    && extendedProductIds.contains(product.getId())) {

                Warranty extended = assignExtendedWarranty(product, sale);

                if (extended != null) {
                    extendedCost += extended.getAdditionalCost();
                }
            }
        }

        return extendedCost;
    }

    public Warranty assignBasicWarranty(Product product, Sale sale) {
        WarrantyValidator.validateWarrantyData(product, sale);

        if (hasWarrantyOfType(product.getId(), sale.getId(), WarrantyFactory.BASIC_TYPE)) {
            return findByProductAndSale(product.getId(), sale.getId());
        }

        return createWarranty(WarrantyFactory.BASIC_TYPE, product, sale);
    }

    public Warranty assignExtendedWarranty(Product product, Sale sale) {
        WarrantyValidator.validateWarrantyData(product, sale);

        if (hasWarrantyOfType(product.getId(), sale.getId(), WarrantyFactory.EXTENDED_TYPE)) {
            return null;
        }

        return createWarranty(WarrantyFactory.EXTENDED_TYPE, product, sale);
    }

    private Warranty createWarranty(String typeCode, Product product, Sale sale) {
        String warrantyId = generateWarrantyId();

        Warranty warranty = warrantyFactory.create(
                typeCode, warrantyId, product, sale, LocalDate.now());

        warrantyCatalog.add(warranty);

        return warranty;
    }

    /**
     * Case-insensitive match, as before.
     */
    private boolean hasWarrantyOfType(String productId, String saleId, String typeCode) {
        for (Warranty warranty : warrantyCatalog.findAll()) {
            if (warranty.getTypeCode().equals(typeCode)
                    && warranty.matchesIgnoreCase(productId, saleId)) {
                return true;
            }
        }
        return false;
    }

    public Warranty findById(String id) {
        return warrantyCatalog.findById(id);
    }

    public Warranty findByProductAndSale(String productId, String saleId) {
        for (Warranty warranty : warrantyCatalog.findAll()) {
            if (warranty.matches(productId, saleId)) {
                return warranty;
            }
        }
        return null;
    }

    public double cancelWarranties(String productId, String saleId) {
        WarrantyValidator.validateCancellationData(productId, saleId);

        double refundableCost = 0.0;
        List<Warranty> cancelled = new ArrayList<>();

        for (Warranty warranty : warrantyCatalog.findAll()) {
            if (warranty.matches(productId, saleId)) {
                refundableCost += warranty.getAdditionalCost();
                cancelled.add(warranty);
            }
        }

        if (!cancelled.isEmpty()) {
            warrantyCatalog.removeAll(cancelled);
        }

        return refundableCost;
    }

    public List<Warranty> viewAllWarranties() {
        return new ArrayList<>(warrantyCatalog.findAll());
    }

    public List<Warranty> viewActiveWarranties(LocalDate date) {
        List<Warranty> activeWarranties = new ArrayList<>();

        for (Warranty warranty : warrantyCatalog.findAll()) {
            if (warranty.isActive(date)) {
                activeWarranties.add(warranty);
            }
        }

        return activeWarranties;
    }

    public List<Warranty> viewWarrantiesExpiringSoon(int days) {
        LocalDate today = LocalDate.now();
        LocalDate limitDate = today.plusDays(days);

        List<Warranty> expiringWarranties = new ArrayList<>();

        for (Warranty warranty : warrantyCatalog.findAll()) {
            LocalDate expirationDate = warranty.getEndDate();

            if (!expirationDate.isBefore(today) && !expirationDate.isAfter(limitDate)) {
                expiringWarranties.add(warranty);
            }
        }

        return expiringWarranties;
    }

    private String generateWarrantyId() {
        return idGenerator.nextId(
                WARRANTY_PREFIX, warrantyCatalog.size(), id -> warrantyCatalog.findById(id) != null);
    }

}
