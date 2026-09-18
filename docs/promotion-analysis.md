# Promotion Analysis – GameZone Unicesar

### Q1: The three promotions have different calculation rules but share common attributes and behaviors. How is this reflected in the class hierarchy design? What object-oriented programming mechanism allows each promotion type to calculate its discount differently without the rest of the system knowing the concrete types?

The design uses an abstract base class `Promotion` that holds the common attributes (id, name, startDate, endDate) and the common method isActive(LocalDate). Each concrete type (PercentageDiscount, CategoryDiscount, BulkPurchaseDiscount) extends Promotion and provides its own implementation of calculateDiscount(Sale). The mechanism is polymorphism: the rest of the system works with Promotion references and calls calculateDiscount without knowing the concrete class. This keeps the system extensible and decoupled.


### Q2: The base class `Promotion` cannot implement the discount calculation method because each type has a different logic. How is this method declared in the base class, and what does this declaration guarantee about the subclasses?

It is declared as an abstract method: public abstract double calculateDiscount(Sale sale);. This guarantees that every concrete subclass must implement the method, otherwise the compiler will throw an error. It also allows the base class to define a common contract, enabling polymorphic calls from the service layer.

### Q3: The business rule states that only the promotion with the highest discount is applied. In which class is this selection logic located, and why is this placement consistent with the layered architecture? Why should this logic NOT be in the `Sale` class or in the console menu?

It is located in `PromotionService`, specifically in findBestPromotionFor(Sale sale). This is consistent with the layered architecture because the service layer is responsible for business rules and coordinating multiple domain objects. It should not be in Sale because the sale should not know about all promotions or how to compare them; its responsibility is to represent a transaction. It should not be in the console menu because the UI layer must only handle user interaction, not business logic.

### Q4: What modifications are necessary in the `Sale` class and in the `generateReceipt` method so that the receipt shows the applied discount? Do these modifications break any existing behavior in the system?

Add two private attributes to Sale: appliedPromotionName (String) and discountAmount (double), with their getters and setters. Modify generateReceipt() to display subtotal, the applied promotion name, the discount amount, and the final total (subtotal - discount). These modifications are additive and do not break existing behavior because the new fields default to null and 0.0, and the receipt still works when no promotion is applied. Existing constructors and methods remain unchanged.

### Q5: Active promotions are determined by comparing the current date with the start and end dates of each promotion. Where is this validation performed (in the `Promotion` class, in `PromotionService`, or both)? Justify

It is performed in both, with clear responsibilities. The Promotion class provides the method isActive(LocalDate date) that encapsulates the date comparison for a single promotion. The PromotionService uses this method to filter the list of all promotions and obtain only the active ones. This is good design: the domain class knows how to determine its own validity, and the service coordinates the filtering logic. It avoids duplicating the date comparison in the service and keeps the rule close to the data.