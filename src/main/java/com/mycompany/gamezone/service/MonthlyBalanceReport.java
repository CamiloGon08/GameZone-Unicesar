package com.mycompany.gamezone.service;

import com.mycompany.gamezone.model.Return;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.validation.ReturnValidator;
import java.time.LocalDate;

/** Monthly sales, returns and balance. */
public class MonthlyBalanceReport {

    private final SaleService saleService;
    private final ReturnService returnService;

    public MonthlyBalanceReport(SaleService saleService, ReturnService returnService) {
        this.saleService = saleService;
        this.returnService = returnService;
    }

    public double calculateMonthlySales(int month, int year) {
        ReturnValidator.validateMonth(month);

        double totalSales = 0.0;

        for (Sale sale : saleService.viewAllSales()) {
            LocalDate saleDate = sale.getDate();

            if (saleDate.getMonthValue() == month && saleDate.getYear() == year) {
                totalSales += sale.getTotal();
            }
        }

        return totalSales;
    }

    public double calculateMonthlyReturns(int month, int year) {
        ReturnValidator.validateMonth(month);

        double totalReturns = 0.0;

        for (Return returnRecord : returnService.viewAllReturns()) {
            LocalDate returnDate = returnRecord.getDate();

            if (returnDate.getMonthValue() == month && returnDate.getYear() == year) {
                totalReturns += returnRecord.getRefundAmount();
            }
        }

        return totalReturns;
    }

    public double generateMonthlyBalance(int month, int year) {
        return calculateMonthlySales(month, year)
                - calculateMonthlyReturns(month, year);
    }
}