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

/**
 * Service that handles the business rules related to sales.
 * Coordinates the creation of new sales, the validation of stock,
 * the update of the inventory, the application of the best promotion,
 * the automatic assignment of warranties, and the queries over the
 * sales registered in the system.
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
     * to register sales, update inventory and apply promotions. The
     * warranty service is set later to break the circular dependency
     * between SaleService and WarrantyRepository.
     *
     * @param repository       repository used to save and load sales
     * @param productService   service used to update product stock
     * @param accessoryService service used to update accessory stock
     * @param promotionService service used to find the best promotion for a sale
     */
    public SaleService(SaleRepository repository,
                       ProductService productService,
                       AccessoryService accessoryService,
                       PromotionService promotionService) {
        this.repository = repository;
        this.sales = repository.load();
        this.productService = productService;
        this.accessoryService = accessoryService;
        this.promotionService = promotionService;
    }

    /**
     * Sets the warranty service after construction to break the circular
     * dependency between SaleService and WarrantyRepository.
     *
     * @param warrantyService service used to assign warranties
     */
    public void setWarrantyService(WarrantyService warrantyService) {
        this.warrantyService = warrantyService;
    }

        /**
     * Registers a new sale following a single unified flow: it validates the
     * items, creates the sale, applies the best available promotion on the
     * subtotal, assigns warranties, computes the final total, updates the
     * inventory and persists the sale.
     *
     * @param customer                       customer who makes the purchase
     * @param seller                         seller who attends the sale
     * @param products                       list of items included in the sale
     * @param productIdsWithExtendedWarranty IDs of products that should receive extended warranty (may be null or empty)
     * @return the newly created sale
     * @throws IllegalArgumentException if the item list is null or empty,
     *         or if any item has insufficient stock
     */
    public Sale registerSale(Customer customer, Seller seller, List<Product> products,
                             List<String> productIdsWithExtendedWarranty) {

        validateItems(products);
        validateStock(products);

        String saleId = "SALE-" + (sales.size() + 1);
        Sale sale = new Sale(LocalDate.now(), saleId, products, seller, customer);
        double subtotal = sale.getSubtotal();

        double discount = applyBestPromotion(sale);
        double warrantyCost = assignWarranties(sale, products, productIdsWithExtendedWarranty);

        sale.setTotal(subtotal - discount + warrantyCost);

        updateInventory(products);

        sales.add(sale);
        repository.save(sales);

        return sale;
    }

    /**
     * Step 1: validates that the sale contains at least one item.
     *
     * @param products list of items included in the sale
     * @throws IllegalArgumentException if the list is null or empty
     */
    private void validateItems(List<Product> products) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("A sale must contain at least one product.");
        }
    }

    /**
     * Step 2: validates that every item (product or accessory) has stock available.
     *
     * @param products list of items included in the sale
     * @throws IllegalArgumentException if any item has insufficient stock
     */
    private void validateStock(List<Product> products) {
        for (Product product : products) {
            if (product.getStock() <= 0) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getTitle());
            }
        }
    }

    /**
     * Step 4: finds the best available promotion for the sale and registers the
     * discount, calculated only on the subtotal of the items.
     *
     * @param sale the sale being registered
     * @return the discount amount applied, or zero if no promotion applied
     */
    private double applyBestPromotion(Sale sale) {
        Promotion bestPromotion = promotionService.findBestPromotionFor(sale);
        if (bestPromotion == null) {
            return 0;
        }
        double discount = bestPromotion.calculateDiscount(sale);
        if (discount > 0) {
            sale.setAppliedPromotionName(bestPromotion.getName());
            sale.setDiscountAmount(discount);
            return discount;
        }
        return 0;
    }

    /**
     * Step 5: assigns the basic warranty to every console in the sale and the
     * extended warranty to each requested product, summing their additional cost.
     *
     * @param sale                            the sale being registered
     * @param products                        list of items included in the sale
     * @param productIdsWithExtendedWarranty  IDs of products that should receive extended warranty
     * @return the total additional cost of the extended warranties assigned
     */
    private double assignWarranties(Sale sale, List<Product> products,
                                     List<String> productIdsWithExtendedWarranty) {
        double warrantyCost = 0;
        if (warrantyService == null) {
            sale.setWarrantyCost(0);
            return 0;
        }
        for (Product product : products) {
            if (product instanceof Console) {
                warrantyService.assignBasicWarranty(product, sale);
            }
            if (productIdsWithExtendedWarranty != null
                    && productIdsWithExtendedWarranty.contains(product.getId())) {
                Warranty extended = warrantyService.assignExtendedWarranty(product, sale);
                if (extended != null) {
                    warrantyCost += extended.getAdditionalCost();
                }
            }
        }
        sale.setWarrantyCost(warrantyCost);
        return warrantyCost;
    }

    /**
     * Step 7: updates the inventory, delegating to ProductService for regular
     * products and to AccessoryService for accessories.
     *
     * @param products list of items included in the sale
     */
    private void updateInventory(List<Product> products) {
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