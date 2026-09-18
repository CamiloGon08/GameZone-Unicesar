package com.mycompany.gamezone.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.Seller;
import com.mycompany.gamezone.persistence.SaleRepository;

public class SaleService {

    private SaleRepository repository;
    private List<Sale> sales;

    public SaleService(SaleRepository repository){
        this.repository = repository;
        this.sales = repository.load();
    }
    
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

    public List<Sale> viewAllSales() {
        return sales;
    }

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