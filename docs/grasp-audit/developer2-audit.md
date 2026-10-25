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
- **Other member's code:** ConsoleMenu
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
- **Other member's code:** Main
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
- **Other member's code:** Sale, SaleService
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
- **Other member's code:** ConsoleMenu
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
- **Other member's code:** Main
- **Verdict:** Violation

# Audit 2/6 — Accessory Module (AccessoryRepository, AccessoryService)

(`L###` = line number in the file)

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
- **Other member's code:** Accessory, Product
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
- **Other member's code:** Accessory
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
- **Other member's code:** ConsoleMenu
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
- **Other member's code:** Controller (model)
- **Verdict:** Violation

