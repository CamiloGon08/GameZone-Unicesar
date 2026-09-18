package com.mycompany.gamezone.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gamezone.model.Accessory;
import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Promotion;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.Seller;
import com.mycompany.gamezone.persistence.SaleRepository;

/**
 * Service that handles the business rules related to sales.
 * Coordinates the creation of new sales, the validation of stock,
 * the update of the inventory after a sale, the application of the
 * best promotion, and the queries over the sales registered in the system.
 */
public class SaleService {

    private SaleRepository repository;
    private List<Sale> sales;
    private ProductService productService;
    private AccessoryService accessoryService;
    private PromotionService promotionService;

    /**
     * Creates a SaleService with the repositories and services required
     * to register sales, to update the inventory, and to apply the best
     * promotion available for each sale.
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
     * Registers a new sale for the given customer and seller with the
     * list of items provided. The list may contain both products and
     * accessories. Validates that the sale contains at least one item,
     * that every item has enough stock, creates the sale, applies the
     * best active promotion, delegates the stock update to the
     * appropriate service (product or accessory), and persists the
     * updated list of sales.
     *
     * @param customer customer who makes the purchase
     * @param seller   seller who attends the sale
     * @param products list of items (products and accessories) included in the sale
     * @return the newly created sale
     * @throws IllegalArgumentException if the item list is null or empty,
     *         or if any item has insufficient stock
     */
    public Sale registerSale(Customer customer, Seller seller, List<Product> products) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("A sale must contain at least one product.");
        }

        for (Product product : products) {
            if (product.getStock() <= 0) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getTitle());
            }
        }

        String saleId = "SALE-" + (sales.size() + 1);
        Sale sale = new Sale(LocalDate.now(), saleId, products, seller, customer);

        Promotion bestPromotion = promotionService.findBestPromotionFor(sale);
        if (bestPromotion != null) {
            double discount = bestPromotion.calculateDiscount(sale);
            if (discount > 0) {
                sale.setAppliedPromotionName(bestPromotion.getName());
                sale.setDiscountAmount(discount);
                sale.setTotal(sale.getTotal() - discount);
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