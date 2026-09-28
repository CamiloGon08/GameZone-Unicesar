package com.mycompany.gamezone.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Person;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.Seller;
import com.mycompany.gamezone.service.PersonService;
import com.mycompany.gamezone.service.ProductService;

import utilities.FilePath;

/**
 * Repository responsible for persisting sales to a plain text file and
 * for loading them back into the system. Each sale is stored in a single
 * line using a pipe-separated format that includes the sale identifier,
 * the date, the customer ID, the seller ID, the product IDs, the total
 * amount, the applied promotion name and the discount amount.
 *
 * To reconstruct a Sale from disk, this repository depends on
 * ProductService and PersonService so it can resolve the references to
 * products, customers and sellers by their identifiers. This is a
 * documented layering exception.
 */
public class SaleRepository {

    private static final String FILE_PATH = FilePath.SALES;
    private static final String SEPARATOR = "\\|";

    private final ProductService productService;
    private final PersonService personService;

    /**
     * Creates a SaleRepository with the services required to resolve
     * references to products, customers and sellers when loading sales.
     *
     * @param productService service used to look up products by ID
     * @param personService  service used to look up people by ID
     */
    public SaleRepository(ProductService productService,
                          PersonService personService) {
        this.productService = productService;
        this.personService = personService;
    }

    private String toLine(Sale sale) {
        StringBuilder productIds = new StringBuilder();
        List<Product> products = sale.getProducts();
        for (int i = 0; i < products.size(); i++) {
            if (i > 0) {
                productIds.append(";");
            }
            productIds.append(products.get(i).getId());
        }

        String promotionName = sale.getAppliedPromotionName() == null
                ? "" : sale.getAppliedPromotionName();

        return sale.getId() + "|" + sale.getDate() + "|"
                + sale.getCustomer().getiD() + "|"
                + sale.getSeller().getiD() + "|"
                + productIds + "|" + sale.getTotal() + "|"
                + promotionName + "|" + sale.getDiscountAmount();
    }

    /**
     * Saves the given list of sales to the sales file. Each sale is
     * written on its own line using the format produced by
     * {@link #toLine(Sale)}.
     *
     * @param sales list of sales to persist
     */
    public void save(List<Sale> sales) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Sale sale : sales) {
                writer.write(toLine(sale));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving sales: " + e.getMessage());
        }
    }

    /**
     * Loads the sales stored in the sales file and reconstructs each sale
     * by resolving the references to products, customers and sellers.
     *
     * @return list of sales recovered from the file, or an empty list
     *         if the file does not exist or contains no data
     */
    public List<Sale> load() {
        List<Sale> sales = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] data = line.split(SEPARATOR, -1);
                Sale sale = createSale(data);
                if (sale != null) {
                    sales.add(sale);
                }
            }
        } catch (IOException e) {
            return sales;
        }
        return sales;
    }

    private Sale createSale(String[] data) {
        if (data.length < 6) {
            return null;
        }

        String id = data[0];
        LocalDate date = LocalDate.parse(data[1]);
        String customerId = data[2];
        String sellerId = data[3];
        String productIdsRaw = data[4];

        Customer customer = findCustomerById(customerId);
        Seller seller = findSellerById(sellerId);
        if (customer == null || seller == null) {
            return null;
        }

        List<Product> products = new ArrayList<>();
        if (!productIdsRaw.trim().isEmpty()) {
            for (String productId : productIdsRaw.split(";")) {
                Product product = productService.findById(productId);
                if (product != null) {
                    products.add(product);
                }
            }
        }
        if (products.isEmpty()) {
            return null;
        }

        Sale sale = new Sale(date, id, products, seller, customer);

        if (data.length >= 8) {
            String promotionName = data[6];
            String discountRaw = data[7];

            if (promotionName != null && !promotionName.isEmpty()) {
                sale.setAppliedPromotionName(promotionName);
            }
            try {
                double discount = Double.parseDouble(discountRaw);
                sale.setDiscountAmount(discount);
            } catch (NumberFormatException ignored) {
                // ignore malformed numeric value
            }
        }

        return sale;
    }

    private Customer findCustomerById(String id) {
        for (Person person : personService.listPersons()) {
            if (person instanceof Customer customer
                    && customer.getiD().equals(id)) {
                return customer;
            }
        }
        return null;
    }

    private Seller findSellerById(String id) {
        for (Person person : personService.listPersons()) {
            if (person instanceof Seller seller
                    && seller.getiD().equals(id)) {
                return seller;
            }
        }
        return null;
    }
}