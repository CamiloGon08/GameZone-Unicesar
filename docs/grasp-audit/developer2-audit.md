# GRASP Audit - Developer 2

**Audited commit (develop):** `bf35aa2`
**Author:** Estefanía Márquez Martínez (Developer 2)

# Audit 1/6 — Person Module (Person, Customer, Seller, PersonService, PersonRepository)

(`L###` = line number in the .java)

## Verdict per pattern

| Pattern | Verdict | Violations |
|---|---|---|
| Information Expert | Violation | V-01 |
| Creator | Correct | — |
| Controller | N/A | — |
| High Cohesion | Violation | V-02 |
| Low Coupling | Violation | V-03, V-04 |
| Polymorphism | Violation | V-05, V-06 |
| Pure Fabrication | Violation | V-07 |
| Indirection | Correct | — |
| Protected Variations | Violation | V-08 |

---

## Dev2Audit - V-01
- **Pattern:** Information Expert
- **Module:** PersonService
- **Evidence:**
```java
public void registerCustomer(ArrayList<Person> persons, String eMail, ...)  // L96
public Person searchPerson(ArrayList<Person> persons, String iD) { ... }   // L303 (duplicated in PersonRepository L184)
```
- **Explanation:** The service does not own the person collection; callers pass it on every call. `searchPerson` is also implemented twice.
- **Consequence:** Uniqueness holds only for the list passed in, and the two `searchPerson` copies can diverge.
- **Proposed solution:** `PersonService` loads the list once from the repository and keeps it as state (Information Expert). Remove the duplicate `searchPerson`. No new class.
- **Other member's code:** ConsoleMenu (Leader)
- **Verdict:** Violation

## Dev2Audit - V-02
- **Pattern:** High Cohesion
- **Module:** PersonRepository
- **Evidence:**
```java
public Customer searchCustomer(ArrayList<Person> persons, String iD) { ... instanceof Customer ... }  // L205
public Seller searchSeller(ArrayList<Person> persons, String employeeCode) { ... }                   // L230
```
- **Explanation:** A repository should only persist, yet it also searches in-memory lists. `PersonService.searchCustomer/searchSeller` (L205-L230) only delegate.
- **Consequence:** Changing a search criterion forces edits in the persistence layer.
- **Proposed solution:** Move the three searches to `PersonService`, leaving the repository with only `listPersons`/`savePersons` (separation of responsibilities). No new class.
- **Other member's code:** None
- **Verdict:** Violation

## Dev2Audit - V-03
- **Pattern:** Low Coupling
- **Module:** PersonService
- **Evidence:**
```java
private final PersonRepository personRepository = new PersonRepository();  // L30
```
- **Explanation:** The service instantiates its own concrete dependency, unlike the other services, which receive it by constructor.
- **Consequence:** The repository cannot be replaced and the service cannot be tested in isolation.
- **Proposed solution:** Constructor injection (`PersonService(PersonRepository repo)`) (Low Coupling + Dependency Injection). No new class.
- **Other member's code:** Main (Leader)
- **Verdict:** Violation

## Dev2Audit - V-04
- **Pattern:** Low Coupling
- **Module:** Customer, Seller
- **Evidence:**
```java
private ArrayList<Sale> purchases;   // Customer L15
private ArrayList<Sale> sales;       // Seller L15
returnRecord.getOriginalSale().getCustomer()  // ReturnService L269: Sale knows Customer
```
- **Explanation:** Person and Sale depend on each other. Nothing in the provided scripts calls `addPurchase`/`addSale`, so these lists are redundant, likely empty state.
- **Consequence:** Two sources of truth that drift apart; changes to Sale ripple into Person.
- **Proposed solution:** Keep a single direction (Sale → Customer/Seller) and query sales through `SaleService.viewSalesByCustomer/Seller`. No new class.
- **Other member's code:** Sale, SaleService (Leader)
- **Verdict:** Violation

## Dev2Audit - V-05
- **Pattern:** Polymorphism
- **Module:** PersonRepository
- **Evidence:**
```java
if (CUSTOMER_TYPE.equals(type)) {...} else if (SELLER_TYPE.equals(type)) {...}  // createPerson L142-L156
if (currentPerson instanceof Customer customer)                                   // L211
```
- **Explanation:** Writing is polymorphic (`textFormat()`), but reading branches on a string and on `instanceof`.
- **Consequence:** A new Person type (e.g. Supplier) requires edits in several places in the repository.
- **Proposed solution:** Create a **`PersonFactory`** (Factory) that builds each subtype, and let each Person subclass report its own key. **New class: PersonFactory.**
- **Other member's code:** None
- **Verdict:** Violation

## Dev2Audit - V-06
- **Pattern:** Polymorphism
- **Module:** PersonService
- **Evidence:**
```java
registerCustomer/registerSeller (L96/L130), updateCustomer/updateSeller (L174/L209),
deleteCustomer/deleteSeller (L243/L262), validateCustomer/validateSeller (L42/L69)
currentPerson instanceof Customer   // customersOf L359
```
- **Explanation:** Every operation is duplicated per type instead of being resolved polymorphically.
- **Consequence:** Each new subtype multiplies the service methods.
- **Proposed solution:** `Person` declares `getKey()` (iD or employeeCode) and `validate()`; expose generic `register(Person)`, `update`, `delete` and `search(key)`. No new class.
- **Other member's code:** ConsoleMenu (Leader)
- **Verdict:** Violation

## Dev2Audit - V-07
- **Pattern:** Pure Fabrication
- **Module:** Person, Customer, Seller
- **Evidence:**
```java
public abstract String textFormat();                        // Person L92
return "CUSTOMER|" + getiD() + "|" + getName() + ...;       // Customer L82
return "SELLER|" + getiD() + "|" + getName() + ...;         // Seller L100
```
- **Explanation:** Domain classes know the file format while the reading side of that same format lives in `PersonRepository`. The format knowledge is split across two places.
- **Consequence:** A format change requires editing both the model and the repository.
- **Proposed solution:** Create **`PersonRecordMapper`** (`toLine(Person)` / `toPerson(String[])`) (Pure Fabrication) and remove `textFormat()` from the model. **New class: PersonRecordMapper.**
- **Other member's code:** None
- **Verdict:** Violation

## Dev2Audit - V-08
- **Pattern:** Protected Variations
- **Module:** PersonRepository, PersonService
- **Evidence:**
```java
new FileReader(FilePath.PERSONS) ... infoPerson[1], infoPerson[2], infoPerson[4]  // L79, L144-162
private final PersonRepository personRepository ...                                // concrete class, no interface (L30)
```
- **Explanation:** The service depends on a concrete file-based class, and the format uses hard-coded positions with no protective layer.
- **Consequence:** Switching storage (DB, JSON) forces service changes.
- **Proposed solution:** Create a **`PersonRepository` interface** and rename the current class to `FilePersonRepository` (Protected Variations via Indirection). **New class: PersonRepository interface.**
- **Other member's code:** Main (Leader)
- **Verdict:** Violation

# Audit 2/6 — Accessory Module (AccessoryRepository, AccessoryService)

(`L###` = line number in the .java)

## Verdict per pattern

| Pattern | Verdict | Violations |
|---|---|---|
| Information Expert | Violation | V-09 |
| Creator | Correct | — |
| Controller | N/A | — |
| High Cohesion | Violation | V-12, V-13 |
| Low Coupling | Correct | — |
| Polymorphism | Violation | V-10, V-11 |
| Pure Fabrication | Correct | — |
| Indirection | Correct | — |
| Protected Variations | Violation | V-14 |

---

## Dev2Audit - V-09
- **Pattern:** Information Expert
- **Module:** AccessoryService
- **Evidence:**
```java
int newStock = accessory.getStock() + quantity;
if (newStock < 0) { throw new IllegalArgumentException("Stock cannot be negative."); }  // L273-278
```
- **Explanation:** The rule "stock cannot go negative" belongs to the item that owns the stock, but the service applies it. Methods also receive the list from outside (L50, L259) and `listAllAccessories()` rereads the file on every call (L152).
- **Consequence:** The same rule is duplicated in `ProductService`, and the model can end up with invalid stock if `adjustStock` is called directly.
- **Proposed solution:** `adjustStock` enforces the invariant and throws `BusinessRuleException`; the service only calls it. No new class.
- **Other member's code:** Accessory, Product (Developer 1)
- **Verdict:** Violation

## Dev2Audit - V-10
- **Pattern:** Polymorphism
- **Module:** AccessoryService
- **Evidence:**
```java
if (accessory instanceof Controller && "Controller".equalsIgnoreCase(type)) ...  // L175-189
```
- **Explanation:** It filters by type using `instanceof` and strings instead of each accessory reporting its own type.
- **Consequence:** A new type (e.g. Headset) forces changes to the method.
- **Proposed solution:** Polymorphic `Accessory.getTypeName()` and filter with `equalsIgnoreCase`. No new class.
- **Other member's code:** Accessory (Developer 1)
- **Verdict:** Violation

## Dev2Audit - V-11
- **Pattern:** Polymorphism
- **Module:** AccessoryRepository
- **Evidence:**
```java
if (accessory instanceof Controller controller) {...} else if (accessory instanceof Cable cable) {...}  // L106-136
if (CONTROLLER_TYPE.equals(type)) {...} else if (CABLE_TYPE.equals(type)) {...}                         // L150-207
```
- **Explanation:** Both writing and reading are resolved with per-type `if` chains.
- **Consequence:** Every new accessory type requires editing both methods.
- **Proposed solution:** Create **`AccessoryRecordMapper`** (one mapper per type, Factory Method + Polymorphism). **New class: AccessoryRecordMapper.**
- **Other member's code:** None
- **Verdict:** Violation

## Dev2Audit - V-12
- **Pattern:** High Cohesion
- **Module:** AccessoryService
- **Evidence:**
```java
validateCommonFields, validateControllerFields, validateCableFields,
validateMemoryFields, validateUniqueId   // L346-458, ~112 lines
```
- **Explanation:** The service mixes business coordination with data validation, which already has its own layer in the project (`ProductValidator`, `PersonValidator`).
- **Consequence:** Rules are duplicated with `ProductValidator.validateProductData`, and the service is harder to maintain.
- **Proposed solution:** Create **`AccessoryValidator`** in the `validation` package (Pure Fabrication) and reuse `ProductValidator` for the common fields. **New class: AccessoryValidator.**
- **Other member's code:** None (`ProductValidator` is only called, not modified)
- **Verdict:** Violation

## Dev2Audit - V-13
- **Pattern:** High Cohesion
- **Module:** AccessoryRepository
- **Evidence:**
```java
System.out.println("Error: Failed to save the accessories records.");  // L52
} catch (IOException e) { return accessories; }                          // L87-89
```
- **Explanation:** The persistence layer prints to the console and hides read failures by returning an empty list.
- **Consequence:** It mixes persistence with presentation, and callers cannot tell "no data" from "corrupt file".
- **Proposed solution:** Throw `PersistenceException`, as the other refactored repositories already do, and let the UI show the message. No new class.
- **Other member's code:** ConsoleMenu (Leader)
- **Verdict:** Violation

## Dev2Audit - V-14
- **Pattern:** Protected Variations
- **Module:** AccessoryService
- **Evidence:**
```java
if (!connectionType.equalsIgnoreCase("WIRELESS") && !connectionType.equalsIgnoreCase("WIRED"))  // L388-389
```
- **Explanation:** A variation point (connection types) is hard-coded as text inside the service.
- **Consequence:** Adding a type (e.g. BLUETOOTH) forces service edits.
- **Proposed solution:** Create the enum **`ConnectionType`** and validate against it. **New class: ConnectionType.**
- **Other member's code:** Controller (model) (Developer 1)
- **Verdict:** Violation

# Audit 3/6 — Promotion Module (PromotionRepository, PromotionService)

(`L###` = line number in the .java)
**Strength:** `findBestPromotionFor` uses `promotion.isActive()` and `promotion.calculateDiscount()` polymorphically (L178, L213).

## Verdict per pattern

| Pattern | Verdict | Violations |
|---|---|---|
| Information Expert | Violation | V-18 |
| Creator | Correct | — |
| Controller | N/A | — |
| High Cohesion | Violation | V-16, V-17 |
| Low Coupling | Correct | — |
| Polymorphism | Violation | V-15 |
| Pure Fabrication | Correct | — |
| Indirection | Correct | — |
| Protected Variations | Violation | V-19 |

---

## Dev2Audit - V-15
- **Pattern:** Polymorphism
- **Module:** PromotionRepository
- **Evidence:**
```java
if (promotion instanceof PercentageDiscount ...) else if (promotion instanceof CategoryDiscount ...)  // L111-139
if (PERCENTAGE_TYPE.equals(type)) ... else if (CATEGORY_TYPE.equals(type)) ...                        // L158-206
```
- **Explanation:** Same per-type `if` chain on both save and load.
- **Consequence:** Each new promotion type requires repository edits.
- **Proposed solution:** Create **`PromotionRecordMapper`**, one per type (Factory + Polymorphism). **New class: PromotionRecordMapper.**
- **Other member's code:** None
- **Verdict:** Violation

## Dev2Audit - V-16
- **Pattern:** High Cohesion
- **Module:** PromotionService
- **Evidence:**
```java
validateCommonFields, validatePercentage, validateCategory,
validateMinimumQuantity, validateUniqueId   // L266-361
```
- **Explanation:** Validation is mixed with promotion logic inside the service.
- **Consequence:** The service grows with every type and the rules are not reusable.
- **Proposed solution:** Create **`PromotionValidator`** in the `validation` package. **New class: PromotionValidator.**
- **Other member's code:** None
- **Verdict:** Violation

## Dev2Audit - V-17
- **Pattern:** High Cohesion
- **Module:** PromotionRepository
- **Evidence:**
```java
System.out.println("Error: Failed to save the promotion records.");  // L57
} catch (IOException e) { return promotions; }                          // L93-95
```
- **Explanation:** Same as V-13: presentation inside persistence and a hidden error.
- **Consequence:** A corrupt file looks like "no promotions", silently dropping discounts.
- **Proposed solution:** Throw `PersistenceException`. No new class.
- **Other member's code:** ConsoleMenu, SaleService (uses `findBestPromotionFor`) (Developer 1)
- **Verdict:** Violation

## Dev2Audit - V-18
- **Pattern:** Information Expert
- **Module:** PromotionService
- **Evidence:**
```java
if (percentage <= 0 || percentage > 100) { throw ... }   // validatePercentage L308
public List<Promotion> listAllPromotions() { return promotionRepository.loadAll(); }  // L157-159
```
- **Explanation:** The percentage rule belongs to the promotion, yet the service applies it. Each query rereads the file and lists are passed in from outside.
- **Consequence:** An invalid promotion can be built bypassing the service, and every sale triggers disk reads (SaleService asks for the best promotion).
- **Proposed solution:** Each `Promotion` constructor protects its own invariants and the service keeps the list in memory. No new class.
- **Other member's code:** Promotion, PercentageDiscount, CategoryDiscount, BulkPurchaseDiscount (Developer 1)
- **Verdict:** Violation

## Dev2Audit - V-19
- **Pattern:** Protected Variations
- **Module:** PromotionService
- **Evidence:**
```java
registerPercentageDiscount (L48), registerCategoryDiscount (L85), registerBulkPurchaseDiscount (L125)
```
- **Explanation:** The "promotion type" variation point is unprotected: there is one register method per type.
- **Consequence:** A new type touches the service, the repository and the menu.
- **Proposed solution:** Create **`PromotionFactory`** and a single `register(Promotion)` in the service. **New class: PromotionFactory.**
- **Other member's code:** ConsoleMenu (Leader)
- **Verdict:** Violation

# Audit 4/6 — Return Module (ReturnRepository, ReturnService, including the monthly balance)

(`L###` = line number in the .java)

## Verdict per pattern

| Pattern | Verdict | Violations |
|---|---|---|
| Information Expert | Violation | V-21 |
| Creator | Correct | — |
| Controller | N/A | — |
| High Cohesion | Violation | V-22 |
| Low Coupling | Violation | V-20 |
| Polymorphism | Violation | V-23 |
| Pure Fabrication | Correct | — |
| Indirection | Correct | — |
| Protected Variations | Violation | V-24 |

---

## Dev2Audit - V-20
- **Pattern:** Low Coupling
- **Module:** ReturnRepository
- **Evidence:**
```java
import com.mycompany.gamezone.service.SaleService;   // L8 (also ProductService, AccessoryService)
Sale originalSale = saleService.findById(saleId);    // L94
```
- **Explanation:** The persistence layer depends on the service layer, inverting the intended ui → service → persistence direction.
- **Consequence:** Circular dependencies, and service changes that break the repository.
- **Proposed solution:** Make the repository return raw data (**`ReturnData`**, like `WarrantyData`) and let `ReturnService` resolve the references (Indirection). **New class: ReturnData.**
- **Other member's code:** Main (Leader)
- **Verdict:** Violation

## Dev2Audit - V-21
- **Pattern:** Information Expert
- **Module:** ReturnService
- **Evidence:**
```java
for (Product saleProduct : sale.getProducts()) { if (saleProduct.getId().equals(productId)) ... }  // L134-140
returnRecord.getOriginalSale().getCustomer().equals(customer)                                    // L269-271
```
- **Explanation:** The service walks through the internals of Sale and Return instead of asking them.
- **Consequence:** Every rule like "does this product belong to this sale?" gets copied into services.
- **Proposed solution:** Add `Sale.containsProduct(id)` and `Return.belongsTo(customer)`. No new class.
- **Other member's code:** Sale, Return (Leader and Develooper 1)
- **Verdict:** Violation

## Dev2Audit - V-22
- **Pattern:** High Cohesion
- **Module:** ReturnService
- **Evidence:**
```java
public double calculateMonthlySales(int month, int year) { ... saleService.viewAllSales() ... }  // L315
public double generateMonthlyBalance(int month, int year)                                        // L375
```
- **Explanation:** A returns service also computes sales and the monthly balance, and `registerReturn` does validation, stock, warranties, IDs and persistence (L80-178).
- **Consequence:** Changing the reports forces edits in the returns service.
- **Proposed solution:** Create **`MonthlyBalanceReport`** and move the three calculations there. **New class: MonthlyBalanceReport.**
- **Other member's code:** ConsoleMenu (Leader)
- **Verdict:** Violation

## Dev2Audit - V-23
- **Pattern:** Polymorphism
- **Module:** ReturnService
- **Evidence:**
```java
if (product instanceof Console) { warrantyService.cancelWarranties(...) }   // L157
if (item instanceof Accessory) { accessoryService.restoreStock(...) } else { productService.restoreStock(...) }  // L206
```
- **Explanation:** It decides by concrete type which service to use and whether a warranty applies. `findItemById` (L189) also repeats the double lookup found in `ReturnRepository` (L208-212).
- **Consequence:** A new product type forces edits across several services.
- **Proposed solution:** Polymorphic methods (e.g. `Product.hasWarranty()`) plus a single inventory lookup, **`InventoryLookup`**. **New class: InventoryLookup.**
- **Other member's code:** Product, Console, Accessory, ProductService (Developer 1)
- **Verdict:** Violation

## Dev2Audit - V-24
- **Pattern:** Protected Variations
- **Module:** ReturnService
- **Evidence:**
```java
if (daysSinceSale < 0 || daysSinceSale > 30) { throw ... }   // L114
```
- **Explanation:** The return-window policy is hard-coded.
- **Consequence:** Changing the window, or varying it per category, requires service edits.
- **Proposed solution:** Create **`ReturnPolicy`** (Strategy) with a configurable window. **New class: ReturnPolicy.**
- **Other member's code:** None
- **Verdict:** Violation

# Audit 5/6 — Warranty Module (WarrantyRepository, WarrantyService)

(`L###` = line number in the .java)
**Strength:** `WarrantyData` insulates the repository from the services (L185).

## Verdict per pattern
| Pattern | Verdict | Violations |
|---|---|---|
| Information Expert | Violation | V-27 |
| Creator | Correct | — |
| Controller | N/A | — |
| High Cohesion | Correct | — |
| Low Coupling | Violation | V-25 |
| Polymorphism | Violation | V-26 |
| Pure Fabrication | Violation | V-28 |
| Indirection | Correct | — |
| Protected Variations | Correct | — |

---

## Dev2Audit - V-25
- **Pattern:** Low Coupling
- **Module:** WarrantyService
- **Evidence:**
```java
import com.mycompany.gamezone.persistence.SaleRepository;   // L8
public WarrantyService(WarrantyRepository repository, SaleRepository saleRepository, ...)  // L43
Sale sale = saleRepository.findById(data.getSaleId());    // L88-89
```
- **Explanation:** The service reaches into another module's persistence instead of going through `SaleService`. This is exactly the point that broke when `findById` was removed from `SaleRepository`.
- **Consequence:** Any change in `SaleRepository` breaks Warranty, and `SaleService` and `WarrantyService` end up tied in a cycle (resolved with `setWarrantyService`).
- **Proposed solution:** Depend on a small **`SaleLookup`** interface (`findById`) implemented by `SaleService` (Indirection). **New class: SaleLookup interface.**
- **Other member's code:** SaleRepository, SaleService, Main (Leader)
- **Verdict:** Violation

## Dev2Audit - V-26
- **Pattern:** Polymorphism
- **Module:** WarrantyRepository, WarrantyService
- **Evidence:**
```java
if (warranty instanceof BasicWarranty) { type = "BASIC"; } else if (warranty instanceof ExtendedWarranty) ...  // L128-132
if ("BASIC".equals(data.getType())) {...} else if ("EXTENDED".equals(data.getType())) {...}                    // L95-105
hasWarrantyOfType(..., Class<? extends Warranty> type)                                                         // L166
```
- **Explanation:** The type code ("BASIC"/"EXTENDED") is decided with `instanceof` on write and with strings on read, duplicated across two classes. `assignBasicWarranty` and `assignExtendedWarranty` are also near-identical.
- **Consequence:** A new warranty type requires editing both the repository and the service.
- **Proposed solution:** Polymorphic `Warranty.getTypeCode()` and a **`WarrantyFactory`**. **New class: WarrantyFactory.**
- **Other member's code:** Warranty, BasicWarranty, ExtendedWarranty (Developer 1)
- **Verdict:** Violation

## Dev2Audit - V-27
- **Pattern:** Information Expert
- **Module:** WarrantyService
- **Evidence:**
```java
warranty.getProduct().getId().equals(productId) && warranty.getSale().getId().equals(saleId)  // L228, repeated at L169 and L272
```
- **Explanation:** The "does this warranty match this product and sale?" comparison is repeated three times in the service.
- **Consequence:** If the matching criterion changes, it must be fixed in three places.
- **Proposed solution:** Add `Warranty.matches(productId, saleId)`. No new class.
- **Other member's code:** Warranty (Developer 1)
- **Verdict:** Violation

## Dev2Audit - V-28
- **Pattern:** Pure Fabrication
- **Module:** WarrantyService
- **Evidence:**
```java
String warrantyId = "WAR-" + nextNumber; while (findById(warrantyId) != null) { nextNumber++; ... }  // L353-361
```
- **Explanation:** ID generation is reimplemented in each service: the same logic appears in `generateReturnId` (L2704-2715) and in the sale numbering of `SaleService`.
- **Consequence:** Three parallel algorithms that can diverge.
- **Proposed solution:** Create **`IdGenerator`** (Pure Fabrication) with a prefix and a uniqueness check. **New class: IdGenerator.**
- **Other member's code:** SaleService (Leader)
- **Verdict:** Violation

# Audit 6/6 — Use of Exceptions and Validators in Services and Repositories

(`L###` = line number in the .java)
**Scope note:** This is a cross-cutting audit of the five modules above. `ProductService`, `SaleService`, `ProductRepository`, `SaleRepository`, V-13 and V-17 (repositories that print and swallow errors) are already recorded and not repeated.

## Compliant code found
- `PersonRepository` rethrows `IOException` as `PersistenceException` with file and operation (L56, L101, L125) and wraps malformed records.
- `PersonService` uses typed exceptions (`BusinessRuleException`, L147) and calls `PersonValidator` for data, existence and uniqueness (L103, L183, L219, L249, L268).

## Verdict per pattern

| Pattern | Verdict | Violations |
|---|---|---|
| Information Expert | Correct | — |
| Creator | N/A | — |
| Controller | N/A | — |
| High Cohesion | Violation | V-31, V-32 |
| Low Coupling | Correct | — |
| Polymorphism | N/A | — |
| Pure Fabrication | Correct | — |
| Indirection | Correct | — |
| Protected Variations | Violation | V-29, V-30 |

---

## Dev2Audit - V-29
- **Pattern:** Protected Variations
- **Module:** AccessoryService, PromotionService, ReturnService, WarrantyService
- **Evidence:**
```java
throw new IllegalArgumentException("Accessory ID cannot be empty.");        // AccessoryService L265
throw new IllegalArgumentException("Promotion ID cannot be empty.");        // PromotionService L273
throw new IllegalArgumentException("The sale ID cannot be empty.");         // ReturnService L86
throw new IllegalArgumentException("WarrantyRepository cannot be null.");   // WarrantyService L49
```
- **Explanation:** These services still use the generic exception instead of the `GameZoneException` hierarchy, so failures cannot be told apart by category. Examples that would need other types: "was not found" → `ResourceNotFoundException`, "stock cannot be negative" and the 30-day rule → `BusinessRuleException`, empty or invalid fields → `InvalidDataException`.
- **Consequence:** Errors from these modules escape the differentiated handling that the UI applies by exception type, and the user may see a raw error.
- **Proposed solution:** Replace each generic throw with the matching typed exception, invoked from validators (see V-31 and V-12/V-16). No new class.
- **Other member's code:** None (the hierarchy is only used, not modified)
- **Verdict:** Violation

## Dev2Audit - V-30
- **Pattern:** Protected Variations
- **Module:** ReturnRepository, WarrantyRepository
- **Evidence:**
```java
throw new RuntimeException("Could not save returns to " + filePath, e);       // ReturnRepository L79
} catch (IOException e) { return returns; }                                    // ReturnRepository L115
double refundAmount = Double.parseDouble(fields[5]);                           // ReturnRepository L187 
throw new RuntimeException("Could not save warranties to " + filePath, e);    // WarrantyRepository L63
throw new IllegalArgumentException("Invalid warranty record: " + line);        // WarrantyRepository L166
```
- **Explanation:** These repositories do not use `PersistenceException`. They throw a generic `RuntimeException`, hide read errors by returning an empty list, and let raw parsing errors (`NumberFormatException`, `DateTimeParseException`) escape. `WarrantyRepository.loadAll` catches only `FileNotFoundException`. Missing-file handling differs from `PersonRepository` (which checks `Files.exists`).
- **Consequence:** An inconsistent persistence-failure policy: callers cannot tell "no data" from "corrupt file", and raw technical errors can reach the user.
- **Proposed solution:** Use `PersistenceException` with file and operation, wrap parsing errors with the line number, and treat a missing file as an empty list (same policy as `PersonRepository`). No new class.
- **Other member's code:** None
- **Verdict:** Violation

## Dev2Audit - V-31
- **Pattern:** High Cohesion
- **Module:** ReturnService, WarrantyService
- **Evidence:**
```java
if (saleId == null || saleId.trim().isEmpty()) { throw new IllegalArgumentException(...) }   // ReturnService L85
private void validateMonth(int month) { if (month < 1 || month > 12) ... }                    // ReturnService L387
private void validateWarrantyData(Product product, Sale sale) {...}                           // WarrantyService L184
```
- **Explanation:** Both services validate inline, whereas the project has a dedicated validation layer (`ProductValidator`, `PersonValidator`, `SaleValidator`).
- **Consequence:** Business logic mixed with validation, rules not reusable, and no typed-exception mapping.
- **Proposed solution:** Create **`ReturnValidator`** and **`WarrantyValidator`** in the `validation` package. **New classes: ReturnValidator, WarrantyValidator.**
- **Other member's code:** None
- **Verdict:** Violation

## Dev2Audit - V-32
- **Pattern:** High Cohesion
- **Module:** PersonService
- **Evidence:**
```java
requireNotBlank("código de empleado", employeeCode);   // validateSeller L76
if (name.matches(".*\\d.*")) {...}                      // requireNameWithoutDigits L390
if (contactNumber <= 0) {...}                           // requirePositiveContactNumber L404
```
- **Explanation:** Validation rules are split between `PersonValidator` and private helpers in `PersonService`. `PersonValidator.validatePersonData` requires an email, so it cannot validate sellers, and it does not check name digits or a positive phone number.
- **Consequence:** Rule knowledge is divided in two places, and the service keeps validation code that belongs to the validation layer.
- **Proposed solution:** Extend `PersonValidator` with a seller method and the name and phone rules, and remove the helpers from the service. No new class.
- **Other member's code:** PersonValidator (Developer 1)
- **Verdict:** Violation

---

## Non-GRASP observations 
- `viewAllReturns()` returns the internal mutable list (ReturnService L253-255).
- `AccessoryRepository` uses `|` as the separator and `,` inside a field (L100), while `PromotionRepository` and `ReturnRepository` use `,`: different formats for the same purpose.
- `WarrantyRepository.toWarranty` throws on a corrupt record and aborts the whole load (L165-168).

## Summary
32 violations: Person 8 (V-01 to V-08), Accessory 6 (V-09 to V-14), Promotion 5 (V-15 to V-19), Return 5 (V-20 to V-24), Warranty 4 (V-25 to V-28), exceptions and validators 4 (V-29 to V-32). Polymorphism is the most frequent pattern (7).