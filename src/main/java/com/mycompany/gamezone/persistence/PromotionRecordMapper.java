package com.mycompany.gamezone.persistence;

import com.mycompany.gamezone.model.BulkPurchaseDiscount;
import com.mycompany.gamezone.model.CategoryDiscount;
import com.mycompany.gamezone.model.PercentageDiscount;
import com.mycompany.gamezone.model.Promotion;
import java.time.LocalDate;
import java.util.List;

public class PromotionRecordMapper {

    private static final String SEPARATOR = ",";
    private static final String PERCENTAGE_TYPE = "PERCENTAGE";
    private static final String CATEGORY_TYPE = "CATEGORY";
    private static final String BULK_TYPE = "BULK";

    private final List<TypeMapper<?>> mappers = List.of(
            new PercentageMapper(), new CategoryMapper(), new BulkMapper());

    public String toLine(Promotion promotion) {
        for (TypeMapper<?> mapper : mappers) {
            if (mapper.supports(promotion)) {
                return mapper.toLine(promotion);
            }
        }
        return "";
    }

    /** @return the promotion, or null if the type key is unknown. */
    public Promotion toPromotion(String[] data) {
        for (TypeMapper<?> mapper : mappers) {
            if (mapper.typeKey.equals(data[0])) {
                return mapper.toPromotion(data);
            }
        }
        return null;
    }

    private static String join(Object... values) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                line.append(SEPARATOR);
            }
            line.append(String.valueOf(values[i]));
        }
        return line.toString();
    }

    private abstract static class TypeMapper<T extends Promotion> {
        private final String typeKey;
        private final Class<T> promotionClass;

        private TypeMapper(String typeKey, Class<T> promotionClass) {
            this.typeKey = typeKey;
            this.promotionClass = promotionClass;
        }

        boolean supports(Promotion promotion) {
            return promotionClass.isInstance(promotion);
        }

        String toLine(Promotion promotion) {
            return typeKey + SEPARATOR + fieldsOf(promotionClass.cast(promotion));
        }

        abstract String fieldsOf(T promotion);

        abstract Promotion toPromotion(String[] data);
    }

    private static final class PercentageMapper extends TypeMapper<PercentageDiscount> {
        private PercentageMapper() {
            super(PERCENTAGE_TYPE, PercentageDiscount.class);
        }

        @Override
        String fieldsOf(PercentageDiscount p) {
            return join(p.getId(), p.getName(), p.getStartDate(), p.getEndDate(),
                    p.getPercentage());
        }

        @Override
        Promotion toPromotion(String[] data) {
            return new PercentageDiscount(
                    data[1], data[2],
                    LocalDate.parse(data[3]), LocalDate.parse(data[4]),
                    Double.parseDouble(data[5]));
        }
    }

    private static final class CategoryMapper extends TypeMapper<CategoryDiscount> {
        private CategoryMapper() {
            super(CATEGORY_TYPE, CategoryDiscount.class);
        }

        @Override
        String fieldsOf(CategoryDiscount p) {
            return join(p.getId(), p.getName(), p.getStartDate(), p.getEndDate(),
                    p.getTargetCategory(), p.getPercentage());
        }

        @Override
        Promotion toPromotion(String[] data) {
            return new CategoryDiscount(
                    data[1], data[2],
                    LocalDate.parse(data[3]), LocalDate.parse(data[4]),
                    Double.parseDouble(data[6]),
                    data[5]);
        }
    }

    private static final class BulkMapper extends TypeMapper<BulkPurchaseDiscount> {
        private BulkMapper() {
            super(BULK_TYPE, BulkPurchaseDiscount.class);
        }

        @Override
        String fieldsOf(BulkPurchaseDiscount p) {
            return join(p.getId(), p.getName(), p.getStartDate(), p.getEndDate(),
                    p.getMinimumQuantity(), p.getPercentage());
        }

        @Override
        Promotion toPromotion(String[] data) {
            return new BulkPurchaseDiscount(
                    data[1], data[2],
                    LocalDate.parse(data[3]), LocalDate.parse(data[4]),
                    Integer.parseInt(data[5]),
                    Double.parseDouble(data[6]));
        }
    }
}