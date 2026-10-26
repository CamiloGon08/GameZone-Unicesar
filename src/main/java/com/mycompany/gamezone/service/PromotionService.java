package com.mycompany.gamezone.service;

import com.mycompany.gamezone.model.Promotion;
import com.mycompany.gamezone.model.Sale;
import com.mycompany.gamezone.persistence.PromotionRepository;
import com.mycompany.gamezone.validation.PromotionValidator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Owns the promotions list and persists after every change. */
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final List<Promotion> promotions;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
        this.promotions = new ArrayList<>(promotionRepository.loadAll());
    }

    /**
     * Registers a promotion built by PromotionFactory. The percentage rule is
     * asked to the promotion itself, so loading existing files keeps working.
     */
    public void register(Promotion promotion) {
        if (promotion == null) {
            throw new IllegalArgumentException("Promotion cannot be null.");
        }

        PromotionValidator.validatePercentage(promotion);
        PromotionValidator.validateUniqueId(promotions, promotion.getId());

        promotions.add(promotion);
        saveAll();
    }

    public List<Promotion> listAllPromotions() {
        return new ArrayList<>(promotions);
    }

    public List<Promotion> listActivePromotions() {
        List<Promotion> activePromotions = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        for (Promotion promotion : promotions) {
            if (promotion.isActive(currentDate)) {
                activePromotions.add(promotion);
            }
        }

        return activePromotions;
    }

    public Promotion findBestPromotionFor(Sale sale) {
        if (sale == null) {
            return null;
        }

        Promotion bestPromotion = null;
        double maximumDiscount = 0.0;

        for (Promotion promotion : listActivePromotions()) {
            double discount = promotion.calculateDiscount(sale);

            if (discount > maximumDiscount) {
                maximumDiscount = discount;
                bestPromotion = promotion;
            }
        }

        return bestPromotion;
    }

    public Promotion findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        for (Promotion promotion : promotions) {
            if (promotion.getId().equals(id)) {
                return promotion;
            }
        }

        return null;
    }

    private void saveAll() {
        promotionRepository.saveAll(promotions);
    }
}