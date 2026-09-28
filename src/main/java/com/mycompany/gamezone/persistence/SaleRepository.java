package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Person;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.Seller;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDate;
import utilities.FilePath;

/**
 * Repository responsible for persisting sales to a plain text file and for
 * loading them back into the system. Each sale is stored in a single line using
 * a comma-separated format that includes the sale identifier, the date, the
 * customer name, the seller name, the product titles and the total amount.
 */
public class SaleRepository {

    private static final String FILE_PATH = FilePath.SALES;

    private String toLine(Sale sale) {
        StringBuilder productNames = new StringBuilder();
        List<Product> products = sale.getProducts();
        for (int i = 0; i < products.size(); i++) {
            if (i > 0) {
                productNames.append(";");
            }
            productNames.append(products.get(i).getTitle());
        }
        return sale.getId() + "," + sale.getDate() + ","
                + sale.getCustomer().getName() + "," + sale.getSeller().getName()
                + "," + productNames + "," + sale.getTotal();
    }

    /**
     * Saves the given list of sales to the sales file. Each sale is written on
     * its own line using the format produced by {@link #toLine(Sale)}. If an
     * input/output error occurs, the error message is printed to the standard
     * output.
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
     *
     * Loads the sales stored in the sales file.
     *
     * Customer and seller references are resolved using PersonRepository.
     * Product references are resolved using ProductRepository and
     * AccessoryRepository because a sale may contain regular products or
     * accessories.
     *
     * @return list of sales recovered from the file, or an empty list if the
     * file does not exist
     */
    public List<Sale> load() {
        List<Sale> sales = new ArrayList<>();
        PersonRepository personRepository = new PersonRepository();
        ProductRepository productRepository = new ProductRepository(FilePath.PRODUCTS);
        AccessoryRepository accessoryRepository = new AccessoryRepository();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Sale sale = toSale(line, personRepository, productRepository, accessoryRepository);
                if (sale != null) {
                    sales.add(sale);
                }
            }
        } catch (FileNotFoundException e) {
            return sales;
        } catch (IOException e) {
            throw new RuntimeException("Could not load sales from " + FILE_PATH, e);
        }
        return sales;
    }

    /**
     * * Finds a sale by its identifier. * * @param id sale identifier * @return
     * matching sale, or null if no sale with the given identifier * exists
     */
    public Sale findById(String id) {
        for (Sale sale : load()) {
            if (sale.getId().equals(id)) {
                return sale;
            }
        }
        return null;
    }

    /**
     * 
     * Converts a persisted sale record into a Sale object. 
     * 
     * The method resolves customer and seller names through 
     * PersonRepository and product titles through ProductRepository 
     * and AccessoryRepository. 
     * 
     * @param line persisted sale record 
     * @param personRepository repository used to resolve customers 
     * and sellers 
     * @param productRepository repository used to resolve products 
     * @param accessoryRepository repository used to resolve accessories 
     * @return reconstructed Sale, or null if one of its
     * references cannot be resolved
     */
    
    private Sale toSale(String line, PersonRepository personRepository, ProductRepository productRepository, AccessoryRepository accessoryRepository) {
        String[] fields = line.split(",", -1);
        if (fields.length < 6) {
            throw new IllegalArgumentException("Invalid sale record: " + line);
        }
        String saleId = fields[0].trim();
        LocalDate date = LocalDate.parse(fields[1].trim());
        String customerName = fields[2].trim();
        String sellerName = fields[3].trim();
        String productField = fields[4].trim();
        double total = Double.parseDouble(fields[5].trim());
        Customer customer = null;
        Seller seller = null;
        for (Person person : personRepository.listPersons()) {
            if (person instanceof Customer && person.getName().equals(customerName)) {
                customer = (Customer) person;
            }
            if (person instanceof Seller && person.getName().equals(sellerName)) {
                seller = (Seller) person;
            }
        }
        if (customer == null || seller == null) {
            return null;
        }
        List<Product> products = new ArrayList<>();
        if (!productField.isEmpty()) {
            String[] productTitles = productField.split(";");
            List<Product> regularProducts = productRepository.loadAll();
            List<com.mycompany.gamezone.model.Accessory> accessories = accessoryRepository.loadAll();
            for (String productTitle : productTitles) {
                String title = productTitle.trim();
                Product foundProduct = null;
                for (Product product : regularProducts) {
                    if (product.getTitle().equals(title)) {
                        foundProduct = product;
                        break;
                    }
                }
                if (foundProduct == null) {
                    for (com.mycompany.gamezone.model.Accessory accessory : accessories) {
                        if (accessory.getTitle().equals(title)) {
                            foundProduct = accessory;
                            break;
                        }
                    }
                }
                if (foundProduct == null) {
                    return null;
                }
                products.add(foundProduct);
            }
        }
        Sale sale = new Sale(date, saleId, products, seller, customer);
        sale.setTotal(total);
        return sale;
    }
}
