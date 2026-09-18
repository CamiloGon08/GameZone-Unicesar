# Accessory Analysis – GameZone Unicesar

### Q1:Should accessories be integrated into the existing product hierarchy (extending Product) or form an independent hierarchy? Justify your decision considering code reuse and model coherence.

Accessories should extend Product because they are sellable items with the same basic attributes (id, title, price, stock) and behaviors (getDescription, stock management). This avoids duplicating code and allows Sale to handle products and accessories uniformly through polymorphism. An independent hierarchy would duplicate common fields and force SaleService to treat them differently, breaking model coherence

### Q2: What attributes are common to all three accessory types, and which are specific to each type? How is this distinction reflected in the class hierarchy?

* Common attributes: 

id, title, price, stock (inherited from Product), and compatibleConsoles (defined in Accessory).

* Specific attributes:

- Controller: connectionType (wireless/wired).
- Cable: length (double), connectorType (String).
- Memory: capacityGB (int), memoryType (String).

This is reflected with an abstract Accessory class that extends Product and holds the common accessory attribute.
Controller, Cable, and Memory extend Accessory and add their own fields, overriding getDescription().

### Q3: Compatibility between an accessory and a console is a relationship between two entities. How is this represented in the design and in persistence? Is compatibility an attribute of the accessory, of the console, or of both?

Compatibility is a unidirectional association from Accessory to Console. It is represented in Accessory as a List<Console> compatibleConsoles. In persistence, the accessories.csv file stores a list of compatible
console IDs for each accessory. It is an attribute of the accessory, not the console, because the accessory knows which consoles it works with. The console does not need to store a list of compatible accessories.

### Q4:  What modifications are necessary in the sales service class (SaleService) so that sales can include accessories without breaking existing behavior with video games and consoles?
 
Since Accessory extends Product, SaleService can still accept a List<Product> that includes accessories. Modify registerSale to inject AccessoryService. When validating stock and updating inventory, check if the item is an instance of Accessory: if so, delegate to AccessoryService.updateStock; otherwise use ProductService.updateStock. Total calculation remains the same (sum of prices). This keeps existing behavior for VideoGame and Console intact.

### Q5: In which layer of the system architecture should the new accessory module classes be located? Justify your decision based on the responsibilities of each layer.

- Model layer: Accessory, Controller, Cable, Memory (domain entities).
- Persistence layer: AccessoryRepository (file reading/writing).
- Service layer: AccessoryService (business rules, stock, compatibility queries).
- UI layer: ConsoleMenu extensions for accessory operations.

This respects the four-layer architecture: ui → service → persistence → model. The model holds domain data,
persistence handles files, service applies business rules, and UI interacts with the user. The model layer must not
contain file access logi
