package com.mycompany.gamezone.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.gamezone.model.Customer;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Return;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.persistence.ReturnRepository;

/**
 * Service responsible for managing product returns.
 *
 * This class contains the business rules related to returns, including
 * the 30-day return period, validation of products against the original
 * sale, stock restoration and monthly balance calculation.
 *
 * @author EstefaniaMarquez
 */
public class ReturnService {

    private final ReturnRepository repository;
    private final SaleService saleService;
    private final ProductService productService;
    private final List<Return> returns;

    /**
     * Creates a ReturnService with the required dependencies.
     *
     * @param repository repository used to persist returns
     * @param saleService service used to find original sales
     * @param productService service used to find products and restore stock
     */
    public ReturnService(
            ReturnRepository repository,
            SaleService saleService,
            ProductService productService) {

        this.repository = repository;
        this.saleService = saleService;
        this.productService = productService;
        this.returns = repository.loadAll();
    }

    /**
     * Registers a return for one or more products from an original sale.
     *
     * A return is only allowed within 30 calendar days from the original
     * sale date. Every returned product must belong to the original sale.
     * When the return is successful, the corresponding product stock is
     * restored and the return is persisted.
     *
     * @param saleId identification number of the original sale
     * @param productIds identification numbers of the products to return
     * @param reason reason for the return
     * @return the registered Return
     * @throws IllegalArgumentException if the sale does not exist, the
     *         return period has expired, a product does not belong to the
     *         sale, or the provided data is invalid
     */
    public Return registerReturn(
            String saleId,
            List<String> productIds,
            String reason) {

        if (saleId == null || saleId.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "The sale ID cannot be empty.");
        }

        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "The return must contain at least one product.");
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "The return reason cannot be empty.");
        }

        Sale sale = saleService.findById(saleId);

        if (sale == null) {
            throw new IllegalArgumentException(
                    "The specified sale does not exist.");
        }

        LocalDate saleDate = sale.getDate();
        LocalDate currentDate = LocalDate.now();

        long daysSinceSale = ChronoUnit.DAYS.between(
                saleDate,
                currentDate);

        if (daysSinceSale < 0 || daysSinceSale > 30) {
            throw new IllegalArgumentException(
                    "The return cannot be processed because more than "
                    + "30 calendar days have passed since the sale.");
        }

        List<Product> returnedProducts = new ArrayList<>();

        for (String productId : productIds) {

            Product product = productService.findById(productId);

            if (product == null) {
                throw new IllegalArgumentException(
                        "The specified product does not exist: "
                        + productId);
            }

            boolean belongsToSale = false;

            for (Product saleProduct : sale.getProducts()) {

                if (saleProduct.getId().equals(productId)) {
                    belongsToSale = true;
                    break;
                }
            }

            if (!belongsToSale) {
                throw new IllegalArgumentException(
                        "The product " + productId
                        + " does not belong to the original sale.");
            }

            returnedProducts.add(product);
        }

        String returnId = generateReturnId();

        Return returnRecord = new Return(
                returnId,
                currentDate,
                sale,
                returnedProducts,
                reason);

        for (Product product : returnedProducts) {
            productService.restoreStock(product.getId(), 1);
        }

        returns.add(returnRecord);
        repository.saveAll(returns);

        return returnRecord;
    }

    /**
     * Generates a unique identification number for a return.
     *
     * @return generated return ID
     */
    private String generateReturnId() {

        int nextNumber = returns.size() + 1;
        String returnId = "RETURN-" + nextNumber;

        while (containsReturnId(returnId)) {
            nextNumber++;
            returnId = "RETURN-" + nextNumber;
        }

        return returnId;
    }

    /**
     * Checks whether a return ID is already being used.
     *
     * @param id return identification number
     * @return true if the ID already exists
     */
    private boolean containsReturnId(String id) {

        for (Return returnRecord : returns) {
            if (returnRecord.getId().equals(id)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns all registered returns.
     *
     * @return list containing all returns
     */
    public List<Return> viewAllReturns() {
        return returns;
    }

    /**
     * Returns all returns associated with a specific customer.
     *
     * @param customer customer whose returns should be searched
     * @return list of returns made by the specified customer
     */
    public List<Return> viewReturnsByCustomer(Customer customer) {

        List<Return> result = new ArrayList<>();

        for (Return returnRecord : returns) {

            if (returnRecord.getOriginalSale()
                    .getCustomer()
                    .equals(customer)) {

                result.add(returnRecord);
            }
        }

        return result;
    }

    /**
     * Returns all returns associated with a specific sale.
     *
     * @param saleId identification number of the original sale
     * @return list of returns associated with the sale
     */
    public List<Return> viewReturnsBySale(String saleId) {

        List<Return> result = new ArrayList<>();

        for (Return returnRecord : returns) {

            if (returnRecord.getOriginalSale()
                    .getId()
                    .equals(saleId)) {

                result.add(returnRecord);
            }
        }

        return result;
    }

    /**
     * Calculates the monthly balance for the specified month and year.
     *
     * The balance is calculated as the total amount of sales made during
     * the month minus the total refund amount of returns registered
     * during the same month.
     *
     * @param month month to calculate, from 1 to 12
     * @param year year to calculate
     * @return monthly balance
     */
    public double generateMonthlyBalance(int month, int year) {

        if (month < 1 || month > 12) {
            throw new IllegalArgumentException(
                    "The month must be between 1 and 12.");
        }

        double totalSales = 0.0;
        double totalReturns = 0.0;

        for (Sale sale : saleService.viewAllSales()) {

            LocalDate saleDate = sale.getDate();

            if (saleDate.getMonthValue() == month
                    && saleDate.getYear() == year) {

                totalSales += sale.getTotal();
            }
        }

        for (Return returnRecord : returns) {

            LocalDate returnDate = returnRecord.getDate();

            if (returnDate.getMonthValue() == month
                    && returnDate.getYear() == year) {

                totalReturns += returnRecord.getRefundAmount();
            }
        }

        return totalSales - totalReturns;
    }
}