# Return Analysis – GameZone Unicesar

### Q1:  A return is a new system entity that refers to an existing sale. What type of relationship exists between the Return class and the Sale class? Is this relationship one of inheritance, association, aggregation, or composition? Justify your answer.

It is a unidirectional association: Return references Sale, but Sale does not reference Return. It is not inheritance, aggregation, or composition because the return does not contain the sale; the sale exists independently. The association is needed to validate products, the 30-day period, and the refund.

### Q2:  A return may contain only some of the products from the original sale, not necessarily all of them. How is this situation represented in the attributes of the Return class? What is stored in the returned products attribute?

Return has a List<Product> returnedProducts (or List<String> returnedProductIds). It stores only the specific products being returned, which may be a subset of the sale. It is an association/aggregation with Product, not a many-to-many relationship.

### Q3: The business rule states that returns can only be registered within 30 days of the sale. In which system layer is this validation located, and why? What Java mechanism is used to calculate the difference between two dates?

It is in the service layer (ReturnService.registerReturn()), because it is a business rule. Java uses LocalDate and ChronoUnit.DAYS.between(saleDate, now) <= 30 or ! now.isAfter(saleDate.plusDays(30)). Period can also work, but ChronoUnit.DAYS is more precise for an exact day limit.

### Q4:  Returning products increases stock levels. Which existing method in the Workshop 1 system is reused for this operation, and in which class is it invoked from the returns module? Why is it important to reuse existing methods rather than duplicating the stock update logic?

The method is restoreStock(String productId, int quantity) in ProductService. It is invoked from ReturnService. Reusing it avoids duplicating stock logic, keeps a single source of truth, ensures persistence, and maintains layer separation.

### Q5: The monthly balance report requires consolidating information from two distinct modules (sales and returns). In which type of service is this report located, and why is this placement consistent with the layered architecture? What dependencies does this class require to generate it?

It is in ReturnService.generateMonthlyBalance(int month, int year). The service layer coordinates business logic across modules. It depends on SaleService (or SaleRepository) and ReturnRepository. It calculates totalSales - totalReturns and returns the net balance.
