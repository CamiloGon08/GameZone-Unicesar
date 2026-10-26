package com.mycompany.gamezone.factory;

import com.mycompany.gamezone.model.BasicWarranty;
import com.mycompany.gamezone.model.ExtendedWarranty;
import com.mycompany.gamezone.model.Product;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.model.Warranty;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/** Builds each warranty from the stored type token (BASIC / EXTENDED). */
public class WarrantyFactory {

    public static final String BASIC_TYPE = "BASIC";
    public static final String EXTENDED_TYPE = "EXTENDED";

    @FunctionalInterface
    private interface WarrantyBuilder {
        Warranty build(String id, Product product, Sale sale, LocalDate startDate);
    }

    private final Map<String, WarrantyBuilder> builders = new HashMap<>();

    public WarrantyFactory() {
        builders.put(BASIC_TYPE, BasicWarranty::new);
        builders.put(EXTENDED_TYPE, ExtendedWarranty::new);
    }

    /** @return the warranty, or null if the type token is unknown. */
    public Warranty create(String typeCode, String id, Product product,
            Sale sale, LocalDate startDate) {

        WarrantyBuilder builder = builders.get(typeCode);

        return builder == null ? null : builder.build(id, product, sale, startDate);
    }
}