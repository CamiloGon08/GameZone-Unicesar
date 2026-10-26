package com.mycompany.gamezone.ui;

import com.mycompany.gamezone.model.Warranty;

/**
 * Builds the certificate text shown to the user for a warranty. The
 * coverage rules stay in {@link Warranty}; this class only lays out the values.
 */
public class WarrantyCertificateFormatter {

    /**
     * Builds the certificate with the type, product, dates and additional cost.
     *
     * @param warranty the warranty to describe
     * @return the formatted certificate
     */
    public String format(Warranty warranty) {
        return String.format(
                "\"Warranty Certificate%nType: %s%nProduct: %s%nStart Date: %s%nExpiration Date: %s%nAdditional Cost: $%.2f\"",
                warranty.getWarrantyType(), warranty.getProduct().getTitle(),
                warranty.getStartDate(), warranty.getEndDate(), warranty.getAdditionalCost());
    }
}
