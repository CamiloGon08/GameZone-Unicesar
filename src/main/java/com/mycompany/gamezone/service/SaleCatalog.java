package com.mycompany.gamezone.service;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.Seller;
import com.mycompany.gamezone.persistence.SaleRecord;
import com.mycompany.gamezone.persistence.SaleRepository;

/**
 * Keeps the list of sales of the system. It loads the raw records from
 * {@link SaleRepository}, resolves their references to customers, sellers
 * and products, and saves the list after every new sale. It is the
 * {@link SaleLookup} used by the classes that only need to find a sale.
 */
public class SaleCatalog implements SaleLookup {

    private final SaleRepository repository;
    private final ProductService productService;
    private final PersonService personService;
    private final List<Sale> sales;

    /**
     * Creates the catalog and loads the sales stored in the sales file.
     *
     * @param repository     repository used to save and load sales
     * @param productService service used to resolve the products of a sale
     * @param personService  service used to resolve customers and sellers
     * @throws com.mycompany.gamezone.exceptions.PersistenceException if the
     *         sales cannot be loaded
     */
    public SaleCatalog(SaleRepository repository, ProductService productService,
            PersonService personService) {
        this.repository = repository;
        this.productService = productService;
        this.personService = personService;
        this.sales = loadSales();
    }

    /**
     * Returns the list of every sale of the system.
     *
     * @return list containing every sale
     */
    public List<Sale> findAll() {
        return sales;
    }

    /**
     * Finds a sale using its identification number.
     *
     * @param saleId the identification number of the sale
     * @return the sale with the specified ID, or null if it does not exist
     */
    @Override
    public Sale findById(String saleId) {
        for (Sale sale : sales) {
            if (sale.getId().equals(saleId)) {
                return sale;
            }
        }
        return null;
    }

    /**
     * Adds a new sale to the catalog and saves every sale.
     *
     * @param sale the sale to add
     * @throws com.mycompany.gamezone.exceptions.PersistenceException if the
     *         sales cannot be saved
     */
    public void add(Sale sale) {
        sales.add(sale);
        repository.save(sales);
    }

    /**
     * Returns how many sales exist.
     *
     * @return the number of sales
     */
    public int size() {
        return sales.size();
    }

    private List<Sale> loadSales() {
        List<Sale> loadedSales = new ArrayList<>();
        for (SaleRecord record : repository.loadAll()) {
            Sale sale = toSale(record);
            if (sale != null) {
                loadedSales.add(sale);
            }
        }
        return loadedSales;
    }

    private Sale toSale(SaleRecord record) {
        Customer customer = findCustomerById(record.getCustomerId());
        Seller seller = findSellerById(record.getSellerId());
        if (customer == null || seller == null) {
            return null;
        }

        List<Product> products = new ArrayList<>();
        for (String productId : record.getProductIds()) {
            Product product = productService.findById(productId);
            if (product != null) {
                products.add(product);
            }
        }
        if (products.isEmpty()) {
            return null;
        }

        Sale sale = new Sale(record.getDate(), record.getId(), products, seller, customer);

        String promotionName = record.getPromotionName();
        if (promotionName != null && !promotionName.isEmpty()) {
            sale.setAppliedPromotionName(promotionName);
        }
        if (record.getDiscountAmount() != null) {
            sale.setDiscountAmount(record.getDiscountAmount());
        }
        return sale;
    }

    private Customer findCustomerById(String id) {
        for (Customer customer : personService.listCustomers()) {
            if (customer.getiD().equals(id)) {
                return customer;
            }
        }
        return null;
    }

    private Seller findSellerById(String id) {
        for (Seller seller : personService.listSellers()) {
            if (seller.getiD().equals(id)) {
                return seller;
            }
        }
        return null;
    }
}
