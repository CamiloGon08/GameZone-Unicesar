package com.mycompany.gamezone.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gamezone.exceptions.PersistenceException;
import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Person;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.SaleItem;
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
 *
 * Any {@link IOException} raised while accessing the file is caught here and
 * rethrown as a {@link PersistenceException} that identifies the file and the
 * operation that failed.
 * 
 * @author EstefaniaMarquez
 * 
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
        List<SaleItem> items = sale.getItems();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                productIds.append(";");
            }
            productIds.append(items.get(i).getProduct().getId());
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
     * @throws PersistenceException if the file cannot be written
     */
    public void save(List<Sale> sales) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Sale sale : sales) {
                writer.write(toLine(sale));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenceException(
                    "Write error: could not save sales to file '" + FILE_PATH + "'", e);
        }
    }

    /**
     * Loads the sales stored in the sales file and reconstructs each sale
     * by resolving the references to products, customers and sellers.
     *
     * @return list of sales recovered from the file, or an empty list
     *         if the file does not exist or contains no data
     * @throws PersistenceException if the file cannot be read or contains a
     *         malformed record
     */
    public List<Sale> load() {
        List<Sale> sales = new ArrayList<>();

        if (!Files.exists(Paths.get(FILE_PATH))) {
            return sales;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] data = line.split(SEPARATOR, -1);
                Sale sale = parseRecord(data, lineNumber);
                if (sale != null) {
                    sales.add(sale);
                }
            }
        } catch (IOException e) {
            throw new PersistenceException(
                    "Read error: could not load sales from file '" + FILE_PATH + "'", e);
        }
        return sales;
    }

    /**
     * Converts one record of the sales file into a sale, translating any
     * format problem into a {@link PersistenceException} that identifies the
     * file and the offending line.
     *
     * @param data       the fields of the record
     * @param lineNumber the 1-based number of the line in the file
     * @return the reconstructed sale, or null if its references cannot be resolved
     * @throws PersistenceException if the record is malformed
     */
    private Sale parseRecord(String[] data, int lineNumber) {
        try {
            return createSale(data);
        } catch (DateTimeParseException e) {
            throw new PersistenceException(
                    "Read error: malformed sale record at line " + lineNumber
                            + " of file '" + FILE_PATH + "'", e);
        }
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
    
    /**
     * * Finds a sale by its identifier. * * @param id sale identifier * @return
     * matching sale, or null if no sale with the given identifier * exists
     * @param id
     * @return 
     */
    
    public Sale findById(String id) {
        for (Sale sale : load()) {
            if (sale.getId().equals(id)) {
                return sale;
            }
        }
        return null;
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