package com.mycompany.gamezone.service;

import com.mycompany.gamezone.factory.WarrantyFactory;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.Warranty;
import com.mycompany.gamezone.persistence.WarrantyRepository;
import com.mycompany.gamezone.persistence.WarrantyRepository.WarrantyData;
import com.mycompany.gamezone.validation.WarrantyValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class WarrantyService {

    private static final String WARRANTY_PREFIX = "WAR";

    private final WarrantyRepository repository;
    private final SaleLookup saleLookup;
    private final ProductService productService;
    private final WarrantyFactory warrantyFactory;
    private final IdGenerator idGenerator;

    /** Loaded on first use (see warranties()). */
    private List<Warranty> warranties;

    public WarrantyService(
            WarrantyRepository repository,
            SaleLookup saleLookup,
            ProductService productService,
            WarrantyFactory warrantyFactory,
            IdGenerator idGenerator) {

        if (repository == null) {
            throw new IllegalArgumentException("WarrantyRepository cannot be null.");
        }
        if (saleLookup == null) {
            throw new IllegalArgumentException("SaleLookup cannot be null.");
        }
        if (productService == null) {
            throw new IllegalArgumentException("ProductService cannot be null.");
        }
        if (warrantyFactory == null) {
            throw new IllegalArgumentException("WarrantyFactory cannot be null.");
        }
        if (idGenerator == null) {
            throw new IllegalArgumentException("IdGenerator cannot be null.");
        }

        this.repository = repository;
        this.saleLookup = saleLookup;
        this.productService = productService;
        this.warrantyFactory = warrantyFactory;
        this.idGenerator = idGenerator;
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

            Product product = productService.findById(data.getProductId());
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

    /**
     * Assigns the warranties a sale needs: basic for products that require it,
     * extended for the product IDs the customer asked for.
     *
     * @return additional cost of the extended warranties created now,
     *         for the caller to add to the sale
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

        warranties().add(warranty);
        saveAll();

        return warranty;
    }

    /** Case-insensitive match, as before. */
    private boolean hasWarrantyOfType(String productId, String saleId, String typeCode) {
        for (Warranty warranty : warranties()) {
            if (warranty.getTypeCode().equals(typeCode)
                    && warranty.matchesIgnoreCase(productId, saleId)) {
                return true;
            }
        }
        return false;
    }

    public Warranty findById(String id) {
        for (Warranty warranty : warranties()) {
            if (warranty.getId().equals(id)) {
                return warranty;
            }
        }
        return null;
    }

    public Warranty findByProductAndSale(String productId, String saleId) {
        for (Warranty warranty : warranties()) {
            if (warranty.matches(productId, saleId)) {
                return warranty;
            }
        }
        return null;
    }

    public double cancelWarranties(String productId, String saleId) {
        WarrantyValidator.validateCancellationData(productId, saleId);

        double refundableCost = 0.0;
        boolean removed = false;

        Iterator<Warranty> iterator = warranties().iterator();

        while (iterator.hasNext()) {
            Warranty warranty = iterator.next();

            if (warranty.matches(productId, saleId)) {
                refundableCost += warranty.getAdditionalCost();
                iterator.remove();
                removed = true;
            }
        }

        if (removed) {
            saveAll();
        }

        return refundableCost;
    }

    public List<Warranty> viewAllWarranties() {
        return new ArrayList<>(warranties());
    }

    public List<Warranty> viewActiveWarranties(LocalDate date) {
        List<Warranty> activeWarranties = new ArrayList<>();

        for (Warranty warranty : warranties()) {
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

        for (Warranty warranty : warranties()) {
            LocalDate expirationDate = warranty.getEndDate();

            if (!expirationDate.isBefore(today) && !expirationDate.isAfter(limitDate)) {
                expiringWarranties.add(warranty);
            }
        }

        return expiringWarranties;
    }

    private String generateWarrantyId() {
        return idGenerator.nextId(
                WARRANTY_PREFIX, warranties().size(), id -> findById(id) != null);
    }

    private void saveAll() {
        repository.saveAll(warranties());
    }
}