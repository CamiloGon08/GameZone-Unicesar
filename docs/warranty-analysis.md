# Warranty Analysis – GameZone Unicesar

### Q1: The two warranty types share common attributes (dates, associated product) but also have different attributes and behaviors (duration, coverage, cost). How is this reflected in the class hierarchy design? What object-oriented programming mechanism allows each warranty type to have its own duration without duplicating code?

The design uses an abstract base class `Warranty` with common attributes (id, product, sale, startDate, endDate) and common methods (isActive, generateWarrantyCertificate). Each concrete type (BasicWarranty, ExtendedWarranty) extends Warranty and overrides the abstract methods getDurationInMonths(), getWarrantyType(), and getAdditionalCost(). The mechanism is inheritance with polymorphism: the base constructor calls the abstract getDurationInMonths() to compute endDate, so each subclass defines its own duration (6 or 12 months) without duplicating the date calculation logic.

### Q2: The business rule states that only consoles generate automatic basic warranty, not video games. In which layer is this decision located, and what Java mechanism is used to verify the real type of a product? Justify.

The decision is located in the service layer, specifically in SaleService.registerSale(), because it is a business rule. Java uses the instanceof operator to check the real type of the product: if (product instanceof Console). This is appropriate because the rule depends on the concrete type of product, and instanceof allows the service to apply the rule without adding warranty logic to the Product hierarchy. The model classes stay clean and focused on their own behavior.

### Q3: The duration of each warranty type is different (6 or 12 months). How is the expiration date calculated in each subclass? Should this calculation be done in the warranty constructor or in a separate method? Justify.

The expiration date is calculated in the base `Warranty` constructor, which calls the abstract method getDurationInMonths() and does endDate = startDate.plusMonths(getDurationInMonths()). Each subclass only implements getDurationInMonths() returning 6 or 12. Doing the calculation in the constructor guarantees that every warranty is created with a valid endDate from the start and avoids duplicating the plusMonths
logic in each subclass. A separate method would risk forgetting to call it or producing an inconsistent object.

### Q4: The extended warranty adds a cost of 10% of the product price to the sale total. At what point in the sale registration flow is this additional cost calculated and applied? What modifications are necessary in the `SaleService.registerSale` method?

The cost is calculated and applied inside SaleService.registerSale(), after validating stock and before finalizing the sale. For each product: if it is a Console, call WarrantyService.assignBasicWarranty(...) automatically. If the user requested an extended warranty for that product, call WarrantyService.assignExtendedWarranty(...) and add the returned getAdditionalCost() to the sale total. This requires adding a new parameter to registerSale (for example, List<String> extendedWarrantyProductIds) and injecting WarrantyService into SaleService. Existing behavior for sales without extended warranty remains unchanged.

### Q5: The "warranties expiring soon" query requires iterating over all warranties and filtering those whose end date falls within the next 30 days. In which class is this method located, and what dependencies does it need? Why is this placement consistent with the layered architecture?

It is located in `WarrantyService`, in the method listWarrantiesExpiringSoon(int daysAhead). It depends on WarrantyRepository to load all warranties. It iterates over them and compares warranty.getEndDate() with LocalDate.now().plusDays(daysAhead) using isBefore or isEqual. This placement is consistent with the layered architecture because the service layer applies business rules and coordinates persistence, while the UI only displays the results and the model only holds data and behavior.
