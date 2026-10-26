# GRASP Consolidated Audit

**Audited commit (develop):** `bf35aa2` (`bf35aa2875cd9cc0818ad8cef8089d9a29f84fca`)
**Consolidated by:** Camilo Andres Gonzalez Garcia (Technical Lead)
**Source reports:** `leader-audit.md` (L-V01 to L-V09), `developer1-audit.md` (D1-V01, D1-V02), `developer2-audit.md` (D2-V01 to D2-V32)

In this document `L-`, `D1-` and `D2-` prefix the original IDs of each individual report. `C-XX` are the final team IDs used in Phase 2.

## 1. Coverage

Every class of the system was audited once, by the member who wrote it according to `TEAM.md`.

| Class / method | Audited by | Source report |
|---|---|---|
| `Sale` (except `canBeReturned`), `SaleRepository`, `SaleService` | Technical Lead | leader-audit |
| `ConsoleMenu`, `Main`, `ProductService.restoreStock` | Technical Lead | leader-audit |
| `Product`, `VideoGame`, `Console`, `ProductRepository`, `ProductService` (except `restoreStock`) | Developer 1 | developer1-audit (+ products.md) |
| `Accessory`, `Controller`, `Cable`, `Memory` | Developer 1 | developer1-audit (+ accessories.md) |
| `Promotion`, `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount` | Developer 1 | developer1-audit (+ promotions.md) |
| `Return`, `Sale.canBeReturned` | Developer 1 | developer1-audit (+ returns.md) |
| `Warranty`, `BasicWarranty`, `ExtendedWarranty` | Developer 1 | developer1-audit (+ warranties.md) |
| `exceptions` package, `ProductValidator`, `PersonValidator`, `SaleValidator` | Developer 1 | developer1-audit (+ cross-cutting.md) |
| `Person`, `Customer`, `Seller`, `PersonRepository`, `PersonService` | Developer 2 | developer2-audit, block 1 |
| `AccessoryRepository`, `AccessoryService` | Developer 2 | developer2-audit, block 2 |
| `PromotionRepository`, `PromotionService` | Developer 2 | developer2-audit, block 3 |
| `ReturnRepository`, `ReturnService` (including monthly balance) | Developer 2 | developer2-audit, block 4 |
| `WarrantyRepository`, `WarrantyService` | Developer 2 | developer2-audit, block 5 |
| Use of exceptions and validators in services and repositories | Developer 2 | developer2-audit, block 6 |
| `utilities.FilePath` (constants only) | Technical Lead | Checked during consolidation: no responsibility to evaluate, all patterns not applicable |

No class was left unaudited and no class was audited twice. `ProductService` is split by method: `restoreStock` (Technical Lead, Req. 3) and the rest (Developer 1). `Sale` is split the same way: `canBeReturned` (Developer 1) and the rest (Technical Lead). `ProductRepository`/`ProductService` R6 additions (`parseLine`, validator calls) were reviewed by Developer 1 only at the boundary and are covered by Developer 2's block 6.

## 2. Team verdict per pattern

| Pattern | Technical Lead | Developer 1 | Developer 2 | Team verdict | Final violations |
|---|---|---|---|---|---|
| Information Expert | Violation | Correct | Violation | Violation | C-11, C-19, C-21, C-30, C-35 |
| Creator | Correct | Correct / N/A | Correct | Correct | - |
| Controller | Violation | Correct / N/A | N/A (related finding D2-V01 filed as Information Expert) | Violation | C-01 |
| Low Coupling | Violation | Violation (D1-V02, related) | Violation | Violation | C-03, C-04, C-22, C-23, C-29, C-33 |
| High Cohesion | Violation | Correct | Violation | Violation | C-02, C-09, C-14, C-18, C-24, C-25, C-31, C-37 |
| Polymorphism | Violation | Violation | Violation | Violation | C-05, C-06, C-10, C-12, C-13, C-16, C-17, C-26, C-34 |
| Pure Fabrication | Correct | Correct / N/A | Violation | Violation | C-07, C-36 |
| Indirection | Violation | Correct / N/A | Correct | Violation | C-27 |
| Protected Variations | Violation | Violation | Violation | Violation | C-08, C-15, C-20, C-28, C-32 |

Correct applications kept as reference: `Promotion.calculateDiscount` and `findBestPromotionFor` (Polymorphism), `Warranty` abstract duration/type/cost (Polymorphism), `WarrantyRepository.WarrantyData` (Indirection), `ProductValidator`/`PersonValidator`/`SaleValidator` (Pure Fabrication), `SaleService` and `ProductService` as use-case controllers, `Main` as composition root (Creator).

## 3. Final violation list
Order: Person, Product, Accessory, Promotion, Sale/UI, Return, Warranty. All corrections must keep the same inputs, outputs, Spanish messages and data files.

### C-01
| Field | Content |
|---|---|
| Pattern | Controller (related: Information Expert) |
| Evidence | `ConsoleMenu`:77, 209-210, 456-459, 619-621 loads the lists, passes them and calls `save`; `PersonService.registerCustomer(ArrayList<Person> persons, ...)`:96; `AccessoryService.registerController(List<Accessory> ...)`:50, `listAllAccessories()`:152 rereads the file; `PromotionService.listAllPromotions()`:157-159 |
| Explanation | Person, Accessory and Promotion services do not own their collections. The UI loads, passes and persists them, so the use case is controlled half by the UI and half by the service. |
| Consequence | A missing save loses data; `ConsoleMenu` keeps a separate copy of persons; every sale rereads accessories and promotions from disk. |
| Agreed solution | Each service loads its list in the constructor, keeps it private and persists inside each operation, like `ProductService`. `ConsoleMenu` only forwards input. No new class. |
| Source report | L-V02, D2-V01 (and the list part of D2-V09, D2-V18) |
| Responsible for correction | Developer 2: `PersonService`, `AccessoryService`, `PromotionService`. Technical Lead: `ConsoleMenu`, `Main`. |

### C-02
| Field | Content |
|---|---|
| Pattern | High Cohesion |
| Evidence | `PersonRepository.searchPerson`:184, `searchCustomer`:205, `searchSeller`:230; duplicate `PersonService.searchPerson`:303 |
| Explanation | The repository searches in-memory lists besides persisting; the search is also duplicated in the service. |
| Consequence | Search changes touch persistence; two copies can diverge. |
| Agreed solution | Move searches to `PersonService`; repository keeps only `listPersons`/`savePersons`. No new class. |
| Source report | D2-V02 |
| Responsible for correction | Developer 2 |

### C-03
| Field | Content |
|---|---|
| Pattern | Low Coupling |
| Evidence | `PersonService`:30 `new PersonRepository()` |
| Explanation | The service creates its own concrete repository; other services receive it by constructor. |
| Consequence | Cannot replace or test the repository in isolation. |
| Agreed solution | Constructor injection `PersonService(PersonRepository)`, wired in `Main`. No new class. |
| Source report | D2-V03 |
| Responsible for correction | Developer 2: `PersonService`. Technical Lead: `Main`. |

### C-04
| Field | Content |
|---|---|
| Pattern | Low Coupling |
| Evidence | `Customer.purchases`:15, `Seller.sales`:15; `addPurchase`/`addSale` never called |
| Explanation | Person and Sale reference each other, and the Person side is never filled. |
| Consequence | Two sources of truth; `getPurchases()` is always empty. |
| Agreed solution | Keep one direction (Sale -> Customer/Seller); query through `SaleService.viewSalesByCustomer/Seller`. No new class. |
| Source report | D2-V04 |
| Responsible for correction | Developer 2 |

### C-05
| Field | Content |
|---|---|
| Pattern | Polymorphism |
| Evidence | `PersonRepository.createPerson`:142-156 (`CUSTOMER_TYPE.equals(type)` ...), `instanceof Customer`:211 |
| Explanation | Reading branches on strings and `instanceof` while writing is polymorphic. |
| Consequence | A new person type needs edits in several repository methods. |
| Agreed solution | New class `PersonFactory` that builds each subtype by its type key. |
| Source report | D2-V05 |
| Responsible for correction | Developer 2 |

### C-06
| Field | Content |
|---|---|
| Pattern | Polymorphism |
| Evidence | `PersonService` register/update/delete/validate per type (96/130, 174/209, 243/262, 42/69); `customersOf`:359 |
| Explanation | Each operation is duplicated per subtype. |
| Consequence | Each new subtype multiplies service methods. |
| Agreed solution | `Person.getKey()` and `validate()`; generic `register/update/delete/search` in the service, keeping the current Spanish messages. No new class. |
| Source report | D2-V06 |
| Responsible for correction | Developer 2: `Person`, `PersonService`. Technical Lead: `ConsoleMenu` calls. |

### C-07
| Field | Content |
|---|---|
| Pattern | Pure Fabrication |
| Evidence | `Person.textFormat()`:92, `Customer`:82, `Seller`:100 |
| Explanation | Domain classes know the file format; reading lives in `PersonRepository`. |
| Consequence | Format changes touch model and repository. |
| Agreed solution | New class `PersonRecordMapper` (`toLine`/`toPerson`); remove `textFormat()` from the model. Same field order and separators. |
| Source report | D2-V07 |
| Responsible for correction | Developer 2 |

### C-08
| Field | Content |
|---|---|
| Pattern | Protected Variations |
| Evidence | `PersonService`:30 depends on concrete `PersonRepository`; fixed positions `infoPerson[1..5]`:144-162 |
| Explanation | No stable interface protects the service from the storage choice. |
| Consequence | Changing storage forces service changes. |
| Agreed solution | New interface `PersonRepository`; current class renamed `FilePersonRepository`. |
| Source report | D2-V08 |
| Responsible for correction | Developer 2. Technical Lead: `Main`. |

### C-09
| Field | Content |
|---|---|
| Pattern | High Cohesion |
| Evidence | `PersonService.validateSeller`:76, `requireNameWithoutDigits`:390, `requirePositiveContactNumber`:404 |
| Explanation | Person rules are split between `PersonValidator` and private service helpers. |
| Consequence | Rules divided in two places. |
| Agreed solution | Add seller, name and phone checks to `PersonValidator` (same exceptions and messages) and remove the helpers. No new class. |
| Source report | D2-V32 |
| Responsible for correction | Developer 2: `PersonService`. Developer 1: `PersonValidator`. |

### C-10
| Field | Content |
|---|---|
| Pattern | Polymorphism (related: Protected Variations) |
| Evidence | `ProductRepository.toLine`:125-145 (`instanceof VideoGame`/`Console`), `toProduct`:154-175 |
| Explanation | The repository selects each family's conversion with type checks. |
| Consequence | A new product family needs edits in both read and write paths. |
| Agreed solution | Persistence-side converters per family behind one contract, with a small dispatch; same field order. Keep `parseLine` and `PersistenceException` behavior. |
| Source report | D1-V01 |
| Responsible for correction | Developer 1 |

### C-11
| Field | Content |
|---|---|
| Pattern | Information Expert |
| Evidence | `AccessoryService.updateStock`:273-278 (`newStock < 0`); `Product.adjustStock`:107 has no invariant |
| Explanation | The stock rule belongs to the item that owns the stock, but services apply it. |
| Consequence | Rule duplicated in `ProductService`/`ProductValidator`; direct `adjustStock` can leave negative stock. |
| Agreed solution | Product exposes a guarded stock operation; services call it. Exception types and messages stay as today in each caller. No new class. |
| Source report | D2-V09 |
| Responsible for correction | Developer 1: `Product`. Developer 2: `AccessoryService`. |

### C-12
| Field | Content |
|---|---|
| Pattern | Polymorphism |
| Evidence | `AccessoryService.listAccessoriesByType`:175-189 (`instanceof Controller && "Controller"...`) |
| Explanation | Filter by type with `instanceof` and strings. |
| Consequence | A new accessory type changes the method. |
| Agreed solution | Polymorphic `Accessory.getTypeName()`; filter with `equalsIgnoreCase`. No new class. |
| Source report | D2-V10 |
| Responsible for correction | Developer 1: `Accessory` subclasses. Developer 2: `AccessoryService`. |

### C-13
| Field | Content |
|---|---|
| Pattern | Polymorphism |
| Evidence | `AccessoryRepository.formatAccessory`:106-136, `createAccessory`:150-207 |
| Explanation | Per-type `if` chains on save and load. |
| Consequence | Each new accessory type edits both methods. |
| Agreed solution | New class `AccessoryRecordMapper` (one mapper per type). Same file format. |
| Source report | D2-V11 |
| Responsible for correction | Developer 2 |

### C-14
| Field | Content |
|---|---|
| Pattern | High Cohesion |
| Evidence | `AccessoryService` `validateCommonFields`...`validateUniqueId`:346-458 |
| Explanation | Validation mixed with coordination; duplicates `ProductValidator`. |
| Consequence | Rules duplicated; service hard to maintain. |
| Agreed solution | New class `AccessoryValidator` in `validation`; same exceptions and messages. |
| Source report | D2-V12 |
| Responsible for correction | Developer 2 |

### C-15
| Field | Content |
|---|---|
| Pattern | Protected Variations |
| Evidence | `AccessoryService`:388-389 `"WIRELESS"`/`"WIRED"` |
| Explanation | Connection types hard-coded as text in the service. |
| Consequence | A new connection type edits the service. |
| Agreed solution | New enum `ConnectionType`, used for validation only; accepted strings, stored values and descriptions stay the same. |
| Source report | D2-V14 |
| Responsible for correction | Developer 2 |

### C-16
| Field | Content |
|---|---|
| Pattern | Polymorphism (related: Low Coupling, Protected Variations) |
| Evidence | `CategoryDiscount.calculateDiscount`:56-67 (checks at 59/61/63) |
| Explanation | The discount identifies each product family itself. |
| Consequence | A new independently categorized family edits the discount. |
| Agreed solution | Category-matching query on `Product`, implemented at `VideoGame`, `Console` and `Accessory`; same strings and case sensitivity. |
| Source report | D1-V02 |
| Responsible for correction | Developer 1 |

### C-17
| Field | Content |
|---|---|
| Pattern | Polymorphism |
| Evidence | `PromotionRepository.formatPromotion`:111-139, `createPromotion`:158-206 |
| Explanation | Per-type `if` chains on save and load. |
| Consequence | Each new promotion type edits the repository. |
| Agreed solution | New class `PromotionRecordMapper` (one per type). Same file format. |
| Source report | D2-V15 |
| Responsible for correction | Developer 2 |

### C-18
| Field | Content |
|---|---|
| Pattern | High Cohesion |
| Evidence | `PromotionService` validation helpers:266-361 |
| Explanation | Validation mixed with promotion logic. |
| Consequence | Service grows with each type; rules not reusable. |
| Agreed solution | New class `PromotionValidator` in `validation`; same exceptions and messages. |
| Source report | D2-V16 |
| Responsible for correction | Developer 2 |

### C-19
| Field | Content |
|---|---|
| Pattern | Information Expert |
| Evidence | `PromotionService.validatePercentage`:308 |
| Explanation | The percentage rule belongs to the promotion, but the service applies it. |
| Consequence | An invalid promotion can be built bypassing the service. |
| Agreed solution | Promotion classes expose their own validity check, called by the service before registering, so loading existing files keeps working. No new class. |
| Source report | D2-V18 |
| Responsible for correction | Developer 1: promotion classes. Developer 2: `PromotionService`. |

### C-20
| Field | Content |
|---|---|
| Pattern | Protected Variations |
| Evidence | `PromotionService.registerPercentageDiscount`:48, `registerCategoryDiscount`:85, `registerBulkPurchaseDiscount`:125 |
| Explanation | One register method per type; the type is not a protected variation point. |
| Consequence | A new type touches service, repository and menu. |
| Agreed solution | New class `PromotionFactory` and a single `register(Promotion)` in the service. |
| Source report | D2-V19 |
| Responsible for correction | Developer 2. Technical Lead: `ConsoleMenu` calls. |

### C-21
| Field | Content |
|---|---|
| Pattern | Information Expert |
| Evidence | `SaleService.registerSale`:112, 125 `sale.setTotal(...)`; `Sale.setTotal`:156; `Sale.warrantyCost`:22 never set |
| Explanation | Sale owns subtotal, discount and warranty cost, but the service builds the total. |
| Consequence | Rule split in two classes; new charges edit `SaleService`. |
| Agreed solution | `Sale.applyPromotion(name, amount)` and `addWarrantyCost(amount)`; `PercentageDiscount`/`BulkPurchaseDiscount` read `getSubtotal()` (same value at call time). No new class. |
| Source report | L-V01 |
| Responsible for correction | Technical Lead: `Sale`, `SaleService`. Developer 1: `PercentageDiscount`, `BulkPurchaseDiscount`. |

### C-22
| Field | Content |
|---|---|
| Pattern | Low Coupling |
| Evidence | `SaleService.setWarrantyService`:70, `if (warrantyService != null)`:116; `Main`:50 |
| Explanation | Optional collaborator set after construction (temporal coupling). |
| Consequence | Without the setter, warranties are silently skipped. |
| Agreed solution | Create `WarrantyService` first in `Main` and inject it through the constructor. No new class. |
| Source report | L-V03 |
| Responsible for correction | Technical Lead |

### C-23
| Field | Content |
|---|---|
| Pattern | Low Coupling (related: Indirection) |
| Evidence | `SaleRepository`:51-52 depends on `ProductService`/`PersonService`; `findCustomerById`:231, `findSellerById`:241 |
| Explanation | Persistence depends on the service layer. |
| Consequence | Persons file read twice per sale; service changes break loading. |
| Agreed solution | New class `SaleRecord`; repository returns raw records and `SaleService` resolves references. |
| Source report | L-V04 |
| Responsible for correction | Technical Lead |

### C-24
| Field | Content |
|---|---|
| Pattern | High Cohesion |
| Evidence | `SaleService.registerSale`:95-150 |
| Explanation | One method validates, generates IDs, applies discounts, assigns warranties, routes stock and persists. |
| Consequence | Every module change edits this method. |
| Agreed solution | Delegate to `Sale` (C-21), `InventoryService` (C-27), `IdGenerator` (C-36) and `WarrantyService.assignWarrantiesFor(sale, extendedIds)`. |
| Source report | L-V05 |
| Responsible for correction | Technical Lead: `SaleService`. Developer 2: `WarrantyService.assignWarrantiesFor`. |

### C-25
| Field | Content |
|---|---|
| Pattern | High Cohesion |
| Evidence | `ConsoleMenu` (989 lines); `persons`:48; `getCustomers`/`getSellers`:946-964 |
| Explanation | One class runs eight menus, holds domain state and filters persons. |
| Consequence | All members edit the same file. |
| Agreed solution | New classes `PersonMenu`, `ProductMenu`, `AccessoryMenu`, `SaleMenu`, `PromotionMenu`, `ReturnMenu`, `WarrantyMenu`; `PersonService.listCustomers()/listSellers()`. Same texts and option numbers. |
| Source report | L-V06 |
| Responsible for correction | Technical Lead: `ConsoleMenu`. Developer 2: `PersonService`. |

### C-26
| Field | Content |
|---|---|
| Pattern | Polymorphism |
| Evidence | `SaleService`:118 `instanceof Console`; `ConsoleMenu`:319 `instanceof Console`; `ReturnService`:157 `instanceof Console` |
| Explanation | Warranty eligibility is decided by concrete type in three places. |
| Consequence | A new product with warranty edits three classes. |
| Agreed solution | `Product.requiresBasicWarranty()` / `allowsExtendedWarranty()` (false by default, true in `Console`). |
| Source report | L-V07, D2-V23 (warranty part) |
| Responsible for correction | Developer 1: `Product`, `Console`. Technical Lead: `SaleService`, `ConsoleMenu`. Developer 2: `ReturnService`. |

### C-27
| Field | Content |
|---|---|
| Pattern | Indirection |
| Evidence | `SaleService`:131-144 `instanceof Accessory`; `ReturnService.findItemById`:189, `restoreItemStock`:206; `ReturnRepository`:208-212 |
| Explanation | Clients choose between `ProductService` and `AccessoryService` themselves. |
| Consequence | Each client repeats the branching; a new family needs a branch in all of them. |
| Agreed solution | New class `InventoryService` (`findAnyById`, `decreaseStock`, `increaseStock`) delegating to both services. Replaces the proposed `InventoryLookup`. |
| Source report | L-V08, D2-V23 (stock part) |
| Responsible for correction | Technical Lead: creates `InventoryService`, updates `SaleService`, `Main`. Developer 2: `ReturnService`, `ReturnRepository`. |

### C-28
| Field | Content |
|---|---|
| Pattern | Protected Variations |
| Evidence | `Sale.products`:18; `Return.calculateRefundAmount`:126, `ExtendedWarranty.getAdditionalCost`:36, `CategoryDiscount`:59-64 read live `getPrice()` |
| Explanation | Closed sales reference mutable products; nothing freezes the charged price. |
| Consequence | A price change alters receipts, refunds and warranty costs of past sales. |
| Agreed solution | New class `SaleItem` with the unit price at sale time. `sales.txt` format unchanged; items are rebuilt from the product on load, so current outputs stay the same. |
| Source report | L-V09 |
| Responsible for correction | Technical Lead: `Sale`, `SaleService`, `SaleRepository`. Developer 1: `Return`, `ExtendedWarranty`, `CategoryDiscount`. |

### C-29
| Field | Content |
|---|---|
| Pattern | Low Coupling |
| Evidence | `ReturnRepository`:8 imports services; `saleService.findById`:94 |
| Explanation | Persistence depends on the service layer. |
| Consequence | Service changes break return loading. |
| Agreed solution | New class `ReturnData` (like `WarrantyData`); `ReturnService` resolves references. |
| Source report | D2-V20 |
| Responsible for correction | Developer 2. Technical Lead: `Main`. |

### C-30
| Field | Content |
|---|---|
| Pattern | Information Expert |
| Evidence | `ReturnService`:134-140 walks `sale.getProducts()`; `:269-271` `getOriginalSale().getCustomer().equals(...)` |
| Explanation | The service reads the internals of Sale and Return instead of asking them. |
| Consequence | These checks get copied into services. |
| Agreed solution | `Sale.containsProduct(id)` and `Return.belongsTo(customer)`. No new class. |
| Source report | D2-V21 |
| Responsible for correction | Developer 2: `ReturnService`. Technical Lead: `Sale.containsProduct`. Developer 1: `Return.belongsTo`. |

### C-31
| Field | Content |
|---|---|
| Pattern | High Cohesion |
| Evidence | `ReturnService.calculateMonthlySales`:315, `generateMonthlyBalance`:375 |
| Explanation | The returns service also builds the sales balance. |
| Consequence | Report changes edit the returns service. |
| Agreed solution | New class `MonthlyBalanceReport` with the three calculations. |
| Source report | D2-V22 |
| Responsible for correction | Developer 2. Technical Lead: `ConsoleMenu`, `Main`. |

### C-32
| Field | Content |
|---|---|
| Pattern | Protected Variations |
| Evidence | `ReturnService`:107-117 (`daysSinceSale > 30`); `Sale.canBeReturned`:278-281 (`plusDays(30)`, unused) |
| Explanation | The return window is implemented twice with different edge rules (future dates). |
| Consequence | A window change needs synchronized edits; the two rules can disagree. |
| Agreed solution | One return-window predicate with an explicit evaluation date, used by both. It must keep the service's current rule (0 to 30 days inclusive, future dates rejected). New class `ReturnPolicy` only if the team agrees. |
| Source report | D2-V24 (+ D1 boundary evidence) |
| Responsible for correction | Developer 2: `ReturnService`. Developer 1: `Sale.canBeReturned`. |

### C-33
| Field | Content |
|---|---|
| Pattern | Low Coupling |
| Evidence | `WarrantyService`:8, 43 depends on `SaleRepository`; `saleRepository.findById`:88-89 |
| Explanation | A service reaches into another module's persistence. |
| Consequence | Any `SaleRepository` change breaks warranties. |
| Agreed solution | New interface `SaleLookup` (`findById`), implemented by `SaleService`. |
| Source report | D2-V25 |
| Responsible for correction | Developer 2: `WarrantyService`, `SaleLookup`. Technical Lead: `SaleService` implements it, `Main`. |

### C-34
| Field | Content |
|---|---|
| Pattern | Polymorphism |
| Evidence | `WarrantyRepository`:128-132 `instanceof BasicWarranty`; `WarrantyService`:95-105 `"BASIC".equals(...)` |
| Explanation | Type code decided by `instanceof` on write and strings on read. |
| Consequence | A new warranty edits repository and service. |
| Agreed solution | `Warranty.getTypeCode()` returning the stored tokens `BASIC`/`EXTENDED` (not the display labels) and new class `WarrantyFactory`. |
| Source report | D2-V26 |
| Responsible for correction | Developer 1: `Warranty` subclasses. Developer 2: `WarrantyRepository`, `WarrantyService`, `WarrantyFactory`. |

### C-35
| Field | Content |
|---|---|
| Pattern | Information Expert |
| Evidence | `WarrantyService`:169-170, 228-229, 272-273 repeat the product/sale match |
| Explanation | The match rule belongs to the warranty and is repeated three times. |
| Consequence | A rule change must be made in three places. |
| Agreed solution | `Warranty.matches(productId, saleId)`. `hasWarrantyOfType` uses `equalsIgnoreCase` and the others `equals`: keep both behaviors (two methods or a flag). No new class. |
| Source report | D2-V27 |
| Responsible for correction | Developer 1: `Warranty`. Developer 2: `WarrantyService`. |

### C-36
| Field | Content |
|---|---|
| Pattern | Pure Fabrication |
| Evidence | `WarrantyService.generateWarrantyId`:353-361; `ReturnService.generateReturnId`; `SaleService`:103 |
| Explanation | ID generation is reimplemented in three services. |
| Consequence | Three algorithms that can diverge. |
| Agreed solution | New class `IdGenerator` (prefix + uniqueness check). Sale IDs keep the current `SALE-n` sequence. |
| Source report | D2-V28 |
| Responsible for correction | Developer 2: creates `IdGenerator`, updates `ReturnService`, `WarrantyService`. Technical Lead: `SaleService`. |

### C-37
| Field | Content |
|---|---|
| Pattern | High Cohesion |
| Evidence | `ReturnService`:85, `validateMonth`:387; `WarrantyService.validateWarrantyData`:184 |
| Explanation | Both services validate inline instead of using the validation layer. |
| Consequence | Rules not reusable. |
| Agreed solution | New classes `ReturnValidator` and `WarrantyValidator`; same exception types and messages. |
| Source report | D2-V31 |
| Responsible for correction | Developer 2 |

## 4. Boundary findings

These violations involve code of more than one member. Each was registered once above; the correction is split as follows. The member who owns the lower layer commits first, the others pull before starting their part.

| ID | Source reports unified | Members | How the correction is split | Commit order |
|---|---|---|---|---|
| C-01 | L-V02 + D2-V01 | Dev 2, Lead | Dev 2 makes the three services own and persist their lists; Lead removes list handling from `ConsoleMenu` and `Main` | Dev 2, then Lead |
| C-09 | D2-V32 | Dev 2, Dev 1 | Dev 1 adds seller/name/phone checks to `PersonValidator`; Dev 2 removes the service helpers | Dev 1, then Dev 2 |
| C-11 | D2-V09 | Dev 1, Dev 2 | Dev 1 adds the guarded stock operation to `Product`; Dev 2 uses it in `AccessoryService` | Dev 1, then Dev 2 |
| C-12 | D2-V10 | Dev 1, Dev 2 | Dev 1 adds `getTypeName()`; Dev 2 filters with it | Dev 1, then Dev 2 |
| C-19 | D2-V18 | Dev 1, Dev 2 | Dev 1 adds the validity check to promotions; Dev 2 calls it | Dev 1, then Dev 2 |
| C-21 | L-V01 | Lead, Dev 1 | Lead adds `applyPromotion`/`addWarrantyCost` to `Sale`; Dev 1 switches the two discounts to `getSubtotal()` | Lead, then Dev 1 |
| C-24 | L-V05 | Lead, Dev 2 | Dev 2 adds `WarrantyService.assignWarrantiesFor`; Lead simplifies `registerSale` | after C-21, C-27, C-36 |
| C-25 | L-V06 | Lead, Dev 2 | Dev 2 adds `listCustomers()/listSellers()`; Lead splits `ConsoleMenu` | last of all |
| C-26 | L-V07 + D2-V23 | Dev 1, Lead, Dev 2 | Dev 1 adds the warranty queries to `Product`/`Console`; Lead updates `SaleService`/`ConsoleMenu`; Dev 2 updates `ReturnService` | Dev 1, then Lead and Dev 2 |
| C-27 | L-V08 + D2-V23 | Lead, Dev 2 | Lead creates `InventoryService` and updates `SaleService`/`Main`; Dev 2 updates `ReturnService`/`ReturnRepository` | Lead, then Dev 2 |
| C-28 | L-V09 | Lead, Dev 1 | Lead creates `SaleItem` and updates `Sale`/`SaleService`/`SaleRepository`; Dev 1 reads the item price in `Return`, `ExtendedWarranty`, `CategoryDiscount` | Lead, then Dev 1 |
| C-30 | D2-V21 | Dev 2, Lead, Dev 1 | Lead adds `Sale.containsProduct`; Dev 1 adds `Return.belongsTo`; Dev 2 uses both | Lead and Dev 1, then Dev 2 |
| C-32 | D2-V24 + D1 evidence | Dev 2, Dev 1 | Dev 1 adapts `Sale.canBeReturned` to the agreed predicate; Dev 2 uses it in `ReturnService` | Dev 1, then Dev 2 |
| C-33 | D2-V25 | Dev 2, Lead | Dev 2 creates `SaleLookup` and uses it; Lead makes `SaleService` implement it and updates `Main` | Dev 2, then Lead |
| C-34 | D2-V26 | Dev 1, Dev 2 | Dev 1 adds `getTypeCode()`; Dev 2 creates `WarrantyFactory` and updates repository/service | Dev 1, then Dev 2 |
| C-35 | D2-V27 | Dev 1, Dev 2 | Dev 1 adds `Warranty.matches`; Dev 2 replaces the three comparisons | Dev 1, then Dev 2 |
| C-36 | D2-V28 | Dev 2, Lead | Dev 2 creates `IdGenerator`; Lead uses it in `SaleService` | Dev 2, then Lead |

Simple dependencies on `Main` (C-03, C-08, C-22, C-23, C-29, C-31) are wired by the Technical Lead after the owner's commit.

Duplicates merged without a new ID: D2-V09 and D2-V18 also described services not owning their lists, which is part of C-01. D2-V23 described both stock routing (C-27) and warranty eligibility (C-26).

## 5. Out-of-scope findings

### 5.1 Violations moved out of scope
Correcting these would change what the user sees, so they are not corrected in Requirement 7.

| Source | Pattern | Evidence | Why it is out of scope |
|---|---|---|---|
| D2-V13 | High Cohesion | `AccessoryRepository`:52 prints `"Error: Failed to save the accessories records."`; `loadAll`:87-89 returns an empty list | Throwing `PersistenceException` would replace the current console message and the "registered" confirmation with the `handleException` message. |
| D2-V17 | High Cohesion | `PromotionRepository`:57 prints the error; `loadAll`:93-95 returns an empty list | Same reason as D2-V13. |
| D2-V29 | Protected Variations | `IllegalArgumentException` in `AccessoryService`:265, `PromotionService`:273, `ReturnService`:86, `WarrantyService`:49 | `ConsoleMenu.handleException` prints a different prefix for each typed exception, so the user would see different messages. |
| D2-V30 | Protected Variations | `ReturnRepository`:79, 115, 187; `WarrantyRepository`:63, 166 | Changing the exception type or the missing-file policy changes the messages and the result of loading corrupt files. |

### 5.2 Functional defects (not corrected)

| # | Evidence | Defect | Reported by |
|---|---|---|---|
| 1 | `SaleRepository.createSale`:194, `Sale` constructor:41 | The stored total is ignored on load; loaded sales lose discount and warranty, so the monthly balance changes after a restart. | Lead |
| 2 | `Sale.warrantyCost`:22 | Never assigned; the receipt never shows the extended warranty line. | Lead |
| 3 | `SaleRepository.createSale`:184 | Only `ProductService` is searched; accessories disappear from loaded sales and an accessory-only sale is dropped. | Lead |
| 4 | `SaleService.registerSale`:100 | Stock checked as 1 unit per list item; the same product twice with stock 1 fails halfway, after warranties were saved. | Lead |
| 5 | `SaleService.registerSale`:103 | `SALE-n` has no collision check; a dropped sale on load can make an ID repeat. | Lead |
| 6 | `SaleService.viewSalesByCustomer`:170, `ReturnService`:269 | Compared by reference; after a restart, history is not found for the selected customer. | Lead |
| 7 | `PersonService.registerSeller`:146 | Seller uniqueness checked only by employee code; a seller can share an ID with another person. | Lead |
| 8 | `AccessoryRepository.formatAccessory` (Cable branch) | Cable compatible consoles are not written, so they are lost on restart. | Lead |
| 9 | `ReturnService.registerReturn` | Nothing tracks returned items; the same product can be returned and refunded more than once. | Lead |
| 10 | `ProductService`:67-69, 95-97, 168-169 | Objects change in memory before `saveAll`; a failed save leaves memory changed. | Dev 1 |
| 11 | `Return`:54, 72-73 | The returned-products list is shared and the refund is cached; changes through the reference can leave amounts inconsistent. | Dev 1 |
| 12 | `ReturnService.viewAllReturns`:253-255 | Returns the internal mutable list. | Dev 2 |
| 13 | `AccessoryRepository`:100 vs `PromotionRepository`/`ReturnRepository` | Different separators (`|` vs `,`) for the same purpose; a name with a comma breaks promotion and product records. | Dev 2 |
| 14 | `WarrantyRepository.toWarranty`:165-168 | One corrupt record aborts the whole warranty load. | Dev 2 |

## 6. Correction order for Phase 2

1. Model (Dev 1 first): C-11, C-12, C-16, C-19, C-26, C-30 (`Return`), C-32, C-34, C-35, and `Sale` changes by the Lead for C-21, C-28, C-30.
2. Persistence: C-02, C-05, C-07, C-08, C-10, C-13, C-17, C-23, C-29.
3. Services: C-01, C-03, C-04, C-06, C-09, C-14, C-15, C-18, C-20, C-22, C-27, C-31, C-33, C-36, C-37, then C-24.
4. UI last, by the Lead only: C-25 and the `ConsoleMenu` parts of C-01, C-06, C-20, C-26, C-31.
