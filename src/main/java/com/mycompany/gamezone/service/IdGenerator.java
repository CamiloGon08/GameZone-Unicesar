package com.mycompany.gamezone.service;

import java.util.function.Predicate;

/** Generates PREFIX-n identifiers, skipping the ones already used. */
public class IdGenerator {

    /**
     * @param prefix        e.g. "WAR", "RETURN", "SALE"
     * @param existingCount how many records exist (the sequence starts at count + 1)
     * @param alreadyUsed   uniqueness check (the Lead passes id -> false to keep the SALE-n sequence unchanged)
     */
    public String nextId(String prefix, int existingCount, Predicate<String> alreadyUsed) {
        int nextNumber = existingCount + 1;
        String id = prefix + "-" + nextNumber;

        while (alreadyUsed.test(id)) {
            nextNumber++;
            id = prefix + "-" + nextNumber;
        }

        return id;
    }
}