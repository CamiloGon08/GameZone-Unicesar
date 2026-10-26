# SOLID Audit - Technical Lead

**Audited commit (develop):** `d2d7245`
**Author:** Camilo Andres Gonzalez Garcia (Technical Lead)

Line numbers refer to the audited commit of `develop`.

## Scope

| Module (requirement) | Class | Methods audited |
|---|---|---|
| Sales (Workshop 1), Promotions (Req. 2), Warranties (Req. 4), GRASP (Req. 7) | `Sale` | constructor, `toItems`, `calculateTotal`, getters/setters, `getItems`, `containsProduct`, `setProducts`, `applyPromotion`, `addWarrantyCost`, `getSubtotal`, `generateReceipt`, `appendHeader`, `appendProducts`, `appendTotals`, `toString` (`canBeReturned` is audited by Developer 1) |
| GRASP (Req. 7) | `SaleItem` | constructor, `getProduct`, `getUnitPrice` |
| Sales (Workshop 1), GRASP (Req. 7) | `SaleRepository` | `save`, `toLine`, `loadAll`, `parseRecord`, `createRecord` |
| GRASP (Req. 7) | `SaleRecord` | constructor and getters |
| GRASP (Req. 7) | `SaleCatalog` | constructor, `findAll`, `findById`, `add`, `size`, `loadSales`, `toSale`, `findCustomerById`, `findSellerById` |
| GRASP (Req. 7) | `SaleLookup` | `findById` |
| Sales (Workshop 1), Accessories (Req. 1), Promotions (Req. 2), Warranties (Req. 4), GRASP (Req. 7) | `SaleService` | constructor, `registerSale`, `applyBestPromotion`, `viewAllSales`, `viewSalesByCustomer`, `viewSalesBySeller`, `findById` |
| GRASP (Req. 7) | `InventoryService` | constructor, `findAnyById`, `decreaseStock`, `increaseStock` |
| UI and startup (Workshop 1), Req. 1-4 integration, Exceptions (Req. 6), GRASP (Req. 7) | `ConsoleMenu` | constructor, `start`, `printMainMenu` |
| GRASP (Req. 7) | `ConsoleIO` | `readLine`, `readInt`, `parseCommaSeparated`, `handleException` |
| GRASP (Req. 7) | `ProductMenu`, `PersonMenu`, `SaleMenu`, `AccessoryMenu`, `PromotionMenu`, `ReturnMenu`, `WarrantyMenu` | constructors, `start` and every register/list/view method |
| UI and startup (Workshop 1) | `Main` | `main` |
| Returns (Req. 3) | `ProductService` | `restoreStock` |

## Summary of verdicts

| Principle | Verdict | Violations |
|---|---|---|
| Single Responsibility (SRP) | Violation | V-01, V-02 |
| Open/Closed (OCP) | Violation | V-03, V-04, V-05 |
| Liskov Substitution (LSP) | Correct | - |
| Interface Segregation (ISP) | Violation | V-06 |
| Dependency Inversion (DIP) | Violation | V-07, V-08 |

---

## 1. Single Responsibility Principle
**Verdict:** Violation

Correctly applied in `SaleItem` (only keeps the product and its frozen price), `SaleRecord` (only carries stored data), `SaleLookup`, `InventoryService` (only routes stock operations), `ConsoleIO` (only console input and error output), each module menu class (one module each), `Main` (only builds the object graph) and `ProductService.restoreStock` (validates and delegates to `Product.adjustStock`).

### V-01
| Field | Content |
|---|---|
| Principle | Single Responsibility |
| Module | Sales (Workshop 1), Promotions (Req. 2), Warranties (Req. 4) |
| Evidence | `Sale.generateReceipt` lines 273-279 and `appendHeader`, `appendProducts`, `appendTotals` lines 281-316: `sb.append("===== RECIBO DE VENTA =====\n");`, `sb.append("Subtotal:  $").append(String.format("%.2f", getSubtotal()))`, `sb.append("Garantía extendida: $")...`. The same class holds the sale rules (`applyPromotion` line 201, `addWarrantyCost` line 212, `calculateTotal` line 56). |
| Explanation | `Sale` has two reasons to change: the business rules of a sale (total, discount, warranty cost) and the layout of the printed receipt (Spanish labels, separators, number format). The receipt text is presentation, which belongs to the console layer. |
| Consequence | Changing a label, adding a column or printing the receipt in another format (file, PDF) forces a change in a model class and recompiles everything that uses `Sale`. Model classes also end up holding user-facing Spanish text. |
| Proposed solution | Move the receipt text to a new `SaleReceiptFormatter` in the `ui` package with `format(Sale)`, built from the public getters of `Sale` (`getItems`, `getSubtotal`, `getDiscountAmount`, `getAppliedPromotionName`, `getWarrantyCost`, `getTotal`). `SaleMenu` prints `formatter.format(sale)`. The output must be identical character by character. |
| Other member's code | No. |

### V-02
| Field | Content |
|---|---|
| Principle | Single Responsibility |
| Module | Sales (Workshop 1), GRASP (Req. 7) |
| Evidence | `SaleRepository.toLine` lines 45-63 (builds the pipe-separated line), `createRecord` lines 141-172 (parses fields and `;`-separated product IDs) and `save` / `loadAll` lines 73-119 (opens the file with `FileWriter` / `FileReader`, translates `IOException`). |
| Explanation | The repository has two reasons to change: the text format of a sale record and the way the file is accessed. Developer 2 already separated these two concerns for accessories, promotions and persons (`AccessoryRecordMapper`, `PromotionRecordMapper`, `PersonRecordMapper`), so sales are now the only module that mixes them. |
| Consequence | Adding a field to the sales file (for example the warranty cost) means editing the same class that handles file errors, with the risk of breaking the read/write code. The format cannot be tested without a real file. |
| Proposed solution | Create `SaleRecordMapper` in `persistence` with `toLine(Sale)` and `toRecord(String[] data)`, keeping the same format and the same `< 6` fields rule. `SaleRepository` only opens, reads, writes and translates `IOException`, and receives the mapper in its constructor (as `AccessoryRepository` does). |
| Other member's code | No. |

## 2. Open/Closed Principle
**Verdict:** Violation

Correctly applied in `SaleService.applyBestPromotion` (lines 101-109): it calls `Promotion.calculateDiscount(sale)` polymorphically, so a new promotion type does not change `SaleService`. `SaleMenu.registerSale` line 121 asks `item.allowsExtendedWarranty()` instead of checking the product type, so a new product type does not change the menu.

### V-03
| Field | Content |
|---|---|
| Principle | Open/Closed |
| Module | GRASP (Req. 7), Returns (Req. 3) |
| Evidence | `InventoryService.decreaseStock` lines 57-65: `if (item instanceof Accessory) { accessoryService.updateStock(...); } else { productService.updateStock(...); }`. `increaseStock` lines 78-84: `if (findAnyById(itemId) instanceof Accessory) { accessoryService.restoreStock(...); } else { productService.restoreStock(...); }`. `findAnyById` lines 39-45 hardcodes the search order. |
| Explanation | The class decides by concrete type which service owns an item. A new family of sellable items (for example gift cards with their own service) requires editing the three methods of `InventoryService`. This routing was introduced by C-27 in Requirement 7. |
| Consequence | Every new item family modifies working code that sales and returns depend on, and the same type check is repeated in three places that can drift apart. |
| Proposed solution | Define a `StockHandler` interface in `service` (`Product findById(String id)`, `boolean handles(Product item)`, `void decrease(String id)`, `void restore(String id, int quantity)`) with two adapters created by the Lead, `ProductStockHandler` (wraps `ProductService`) and `AccessoryStockHandler` (wraps `AccessoryService`). `InventoryService` receives a `List<StockHandler>` from `Main` and loops over it, keeping the current order (products first). A new family only adds a handler. |
| Other member's code | No. The adapters only call public methods of `ProductService` (Developer 1) and `AccessoryService` (Developer 2). |

### V-04
| Field | Content |
|---|---|
| Principle | Open/Closed |
| Module | Exceptions (Req. 6), GRASP (Req. 7) |
| Evidence | `ConsoleIO.handleException` lines 71-85: `if (e instanceof ResourceNotFoundException) {...} else if (e instanceof BusinessRuleException) {...} else if (e instanceof InvalidDataException) {...} else if (e instanceof PersistenceException) {...} else {...}`. |
| Explanation | The method chooses the message with a chain of type checks. A new exception type of the GameZone hierarchy requires editing this method. |
| Consequence | Adding an exception changes the code that every menu uses to report errors; a missing branch silently falls into the generic `"Error: "` message. |
| Proposed solution | Keep an ordered `Map<Class<? extends RuntimeException>, Function<RuntimeException, String>>` (`LinkedHashMap`, same order as today) built in the `ConsoleIO` constructor, and look up the first type that matches with `isInstance`. A new exception only adds one entry. The messages stay exactly the same. |
| Other member's code | No. The exception classes (Developer 1) are not changed. |

### V-05
| Field | Content |
|---|---|
| Principle | Open/Closed |
| Module | UI and startup (Workshop 1), Req. 1-4 integration |
| Evidence | `ConsoleMenu.start` lines 78-95: `switch (option) { case 1 -> productMenu.registerVideoGame(); ... case 14 -> warrantyMenu.start(); ... }` and `printMainMenu` lines 100-118, which repeats the same 14 options as text. |
| Explanation | Each option is written twice, once in the `switch` and once in the printed menu. Adding a module (as Requirements 1 to 4 did) requires editing both places in `ConsoleMenu`. |
| Consequence | The number printed and the action executed can get out of sync, and every new module changes the class that runs the whole program loop. |
| Proposed solution | Build an ordered list of `MenuOption` (number, label, `Runnable` action) in the constructor; `printMainMenu` prints the labels from the list and `start` runs the action of the selected number. Option 0 and the invalid-option message stay as they are, so the console output does not change. |
| Other member's code | No. |

## 3. Liskov Substitution Principle
**Verdict:** Correct

The Lead's code has no class hierarchies of its own, but it has two substitution points:

- `SaleCatalog implements SaleLookup` (class line 19, `findById` lines 58-66): it keeps the contract the interface documents (returns the sale or `null`, no new exceptions), so `WarrantyService` works the same with any `SaleLookup`.
- `SaleService.registerSale`, `SaleMenu.registerSale` and `Sale` receive `List<Product>` that mixes `VideoGame`, `Console` and `Accessory` subclasses. They only use members defined in `Product` (`getId`, `getTitle`, `getPrice`, `getStock`, `allowsExtendedWarranty`), and every subclass answers them without changing the expected result. The `instanceof Accessory` check in `InventoryService` is not caused by a subclass breaking the contract of `Product`; it decides which service stores the item, and it is recorded as V-03 (OCP).

## 4. Interface Segregation Principle
**Verdict:** Violation

Correctly applied in `SaleLookup` (one method, `findById`), which `WarrantyService` uses instead of the full repository (C-33, Requirement 7).

### V-06
| Field | Content |
|---|---|
| Principle | Interface Segregation |
| Module | Returns (Req. 3), GRASP (Req. 7) |
| Evidence | `ReturnMenu` line 22: `private final SaleService saleService;` and its only use, line 83: `Sale sale = saleService.findById(saleId);`. |
| Explanation | `ReturnMenu` depends on the whole `SaleService` (register sale, list sales, list by customer, list by seller) but only needs to find a sale. The narrow abstraction `SaleLookup` already exists. |
| Consequence | Any change to the methods of `SaleService` touches a menu that does not use them, and the return menu cannot be built without the full sale service and all its collaborators. |
| Proposed solution | Change the field and constructor parameter of `ReturnMenu` to `SaleLookup`; `ConsoleMenu` passes the `SaleCatalog`, received from `Main`. |
| Other member's code | Reported to the consolidation: `ReturnService` (Developer 2) has the same dependency (fields line 19, uses lines 44 and 138 only call `findById`). See "Findings reported to the consolidation". |

## 5. Dependency Inversion Principle
**Verdict:** Violation

Correctly applied in `Main`, which is the composition root and the only place that creates concrete repositories and services. `WarrantyService` depends on the `SaleLookup` abstraction and `PersonService` on the `PersonRepository` interface, both wired in `Main` (lines 48-49, 53 and 59-60).

### V-07
| Field | Content |
|---|---|
| Principle | Dependency Inversion |
| Module | GRASP (Req. 7) |
| Evidence | `SaleCatalog` line 21: `private final SaleRepository repository;` and constructor line 35: `public SaleCatalog(SaleRepository repository, ...)`; `SaleRepository` is the concrete file class (line 42: `FILE_PATH = FilePath.SALES`). |
| Explanation | `SaleCatalog` is a service-layer class (keeps and resolves the sales) and depends directly on the concrete text-file implementation. Persons already use an interface (`PersonRepository`) with a file implementation (`FilePersonRepository`). |
| Consequence | Changing the storage (another file, a database) or testing the catalog without touching `data/sales.txt` requires editing `SaleCatalog`. |
| Proposed solution | Extract a `SaleRepository` interface in `persistence` (`save(List<Sale>)`, `loadAll()`), rename the current class to `FileSaleRepository implements SaleRepository`, and create it in `Main`. |
| Other member's code | No. |

### V-08
| Field | Content |
|---|---|
| Principle | Dependency Inversion |
| Module | Sales (Workshop 1), GRASP (Req. 7) |
| Evidence | `SaleService` lines 33-37: `private final SaleCatalog saleCatalog;` and the constructor line 49 receive the concrete `SaleCatalog`; `registerSale` lines 90 and 96 call `saleCatalog.size()` and `saleCatalog.add(sale)`, and the view methods call `saleCatalog.findAll()`. |
| Explanation | `SaleService` is the high-level policy of the sales use case and depends on a concrete class that also loads and resolves records. It only needs "list, add and count sales". |
| Consequence | `SaleService` cannot be tested or reused with another way of keeping sales without changing its code, and any change in how `SaleCatalog` loads records recompiles the sales policy. |
| Proposed solution | Define a `SaleStore` interface in `service` (`List<Sale> findAll()`, `void add(Sale sale)`, `int size()`) extending `SaleLookup`; `SaleCatalog` implements it and `SaleService` depends on `SaleStore`. |
| Other member's code | No. |

