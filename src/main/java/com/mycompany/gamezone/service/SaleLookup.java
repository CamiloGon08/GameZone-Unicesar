package com.mycompany.gamezone.service;

import com.mycompany.gamezone.model.Sale;

@FunctionalInterface
public interface SaleLookup {

    Sale findById(String saleId);
}