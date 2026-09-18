package com.mycompany.gamezone.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.Seller;
import com.mycompany.gamezone.persistence.SaleRepository;

/**
 * Service that handles the business rules related to sales.
 * Coordinates the creation of new sales, the validation of stock,
 * the update of the inventory after a sale, and the queries over
 * the sales registered in the system.
 */
public class SaleService {

    private SaleRepository repository;
    private List<Sale> sales;

    /**
     * Creates a SaleService with the repository used for sale persistence
     * and loads the sales already stored.
     *
     * @param repository repository used to save and load sales
     */
    public SaleService(SaleRepository repository) {
        this.repository = repository;
        this.sales = repository.load();
    }

    /**
     * Registers a new sale for the given customer and seller with the
     * list of products provided. Validates that the sale contains at
     * least one product, that every product has enough stock, creates
     * the sale, decreases the stock of each product and persists the
     * updated list of sales.
     *
     * @param customer customer who makes the purchase
     * @param seller   seller who attends the sale
     * @param products list of products included in the sale
     * @return the newly created sale
     * @throws IllegalArgumentException if the product list is null or
     *         empty, or if any product has insufficient stock
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

        for (Product product : products) {
            product.adjustStock(-1);
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