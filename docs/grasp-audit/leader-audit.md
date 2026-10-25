# GRASP Audit - Technical Lead

**Audited commit (develop):** `bf35aa2`
**Author:** Camilo Andres Gonzalez Garcia (Technical Lead)

## Scope

| Module (requirement) | Class | Methods audited |
|---|---|---|
| Sales (Workshop 1), Promotions (Req. 2), Warranties (Req. 4) | `Sale` | constructor, `calculateTotal`, `getSubtotal`, `setTotal`, `setProducts`, discount and warranty getters/setters, `generateReceipt`, `appendHeader`, `appendProducts`, `appendTotals`, `toString` (`canBeReturned` is audited by Developer 1) |
| Sales (Workshop 1) | `SaleRepository` | `save`, `load`, `toLine`, `parseRecord`, `createSale`, `findById`, `findCustomerById`, `findSellerById` |
| Sales (Workshop 1), Accessories (Req. 1), Promotions (Req. 2), Warranties (Req. 4) | `SaleService` | constructor, `setWarrantyService`, `registerSale`, `viewAllSales`, `viewSalesByCustomer`, `viewSalesBySeller`, `findById` |
| UI and startup (Workshop 1), Req. 1-4 integration, Exceptions (Req. 6) | `ConsoleMenu` | constructor, `start`, every register/list/view method of each module menu, `getCustomers`, `getSellers`, `parseCommaSeparated`, `handleException`, `readInt` |
| UI and startup (Workshop 1) | `Main` | `main` |
| Returns (Req. 3) | `ProductService` | `restoreStock` |

## Summary of verdicts

| Pattern | Verdict | Violations |
|---|---|---|
| Information Expert | Violation | V-01 |
| Creator | Correct | - |
| Controller | Violation | V-02 |
| Low Coupling | Violation | V-03, V-04 |
| High Cohesion | Violation | V-05, V-06 |
| Polymorphism | Violation | V-07 |
| Pure Fabrication | Correct | - |
| Indirection | Violation | V-08 |
| Protected Variations | Violation | V-09 |

---

## 1. Information Expert
**Verdict:** Violation

Correctly applied in `Sale.generateReceipt()` and `Sale.getSubtotal()`, which only use data the sale owns, and in `ProductService.restoreStock()`, which delegates the stock change to `Product.adjustStock()`.

### V-01
| Field | Content |
|---|---|
| Pattern | Information Expert |
| Module | Sales (Workshop 1), Promotions (Req. 2), Warranties (Req. 4) |
| Evidence | `SaleService.registerSale` lines 112 and 125: `sale.setTotal(sale.getTotal() - discount);` and `sale.setTotal(sale.getTotal() + extended.getAdditionalCost());`. `Sale` line 156: `public void setTotal(double total)`. `Sale` line 22: `private double warrantyCost;` (never assigned). `Sale.setProducts` lines 139-141 recalculates `total` and drops the discount. |
| Explanation | `Sale` owns the subtotal, the discount and the warranty cost, so it is the expert for its total. Instead, `SaleService` builds the total from outside through a public setter, and `Sale` cannot guarantee the rule total = subtotal - discount + warranty cost. |
| Consequence | The rule is spread over two classes. `warrantyCost` is never set, so the receipt never prints the warranty line. When a sale is loaded, the constructor recalculates the total from the products and loses the discount (see out-of-scope findings). Any new charge or discount type requires editing `SaleService`. |
| Proposed solution | Add `applyPromotion(String name, double amount)` and `addWarrantyCost(double amount)` to `Sale`, compute `getTotal()` from its own fields and remove `setTotal`. Pattern applied: Information Expert. |
| Other member's code | Yes: `PercentageDiscount` and `BulkPurchaseDiscount` (Developer 1) read `sale.getTotal()`; they must read `getSubtotal()` so the result does not change. |

## 2. Creator
**Verdict:** Correct

- `SaleService.registerSale` (line 104) creates `Sale`: it records the sales list (`sales.add(sale)`, line 146) and has the initializing data (customer, seller, products).
- `SaleRepository.createSale` (line 194) creates `Sale` when loading, because it has the stored data.
- `Main.main` creates and wires every repository, service and `ConsoleMenu` (composition root).
- `ProductService.restoreStock` does not create objects.

## 3. Controller
**Verdict:** Violation

Correctly applied in `SaleService.registerSale`: it receives the "register sale" system event from `ConsoleMenu.registerSale`, coordinates the collaborators and persists.

### V-02
| Field | Content |
|---|---|
| Pattern | Controller |
| Module | UI and startup (Workshop 1), Accessories (Req. 1), Promotions (Req. 2) |
| Evidence | `ConsoleMenu` line 77: `this.persons = personService.listPersons();`. Lines 209-210: `personService.registerCustomer(persons, ...); personService.savePersons(persons);`. Lines 456-459: `listAllAccessories()`, `registerController(accessories, ...)`, `saveAll(accessories)`. Lines 619-621: same sequence for promotions. |
| Explanation | For persons, accessories and promotions the UI loads the list, passes it to the service and decides when to save. The use-case controller is split between `ConsoleMenu` and the service, so the UI coordinates domain state instead of only forwarding the event. |
| Consequence | If a menu option forgets the save call, data is lost on exit. `ConsoleMenu` keeps its own copy of `persons`, a different set of objects from the ones `SaleRepository` loads. Every new operation in these modules must repeat the load/modify/save sequence in the UI. |
| Proposed solution | `PersonService`, `AccessoryService` and `PromotionService` keep their list internally, load it in the constructor and persist inside each operation (as `ProductService` already does). `ConsoleMenu` only sends the input data. Pattern applied: Controller. |
| Other member's code | Yes: `PersonService`, `AccessoryService`, `PromotionService` (Developer 2). Boundary finding. |

## 4. Low Coupling
**Verdict:** Violation

Correctly applied in `ProductService.restoreStock` (depends only on `ProductRepository` and `ProductValidator`) and in `Main`, the only place that knows concrete constructors.

### V-03
| Field | Content |
|---|---|
| Pattern | Low Coupling |
| Module | Warranties (Req. 4), UI and startup (Workshop 1) |
| Evidence | `SaleService` line 70: `public void setWarrantyService(WarrantyService warrantyService)`; line 116: `if (warrantyService != null) { ... }`. `Main` line 50: `saleService.setWarrantyService(warrantyService);` |
| Explanation | `SaleService` depends on five collaborators, and one of them is optional and set after construction. This adds temporal coupling: the object only works correctly if someone remembers to call the setter. `WarrantyService` does not depend on `SaleService`, so there is no cycle that requires it. |
| Consequence | If the setter is not called (a test, another `main`), consoles are sold without a basic warranty and extended warranties are ignored, with no error. |
| Proposed solution | Create `WarrantyService` before `SaleService` in `Main` and pass it through the constructor as a required dependency. Pattern applied: Low Coupling. |
| Other member's code | No. |

### V-04
| Field | Content |
|---|---|
| Pattern | Low Coupling |
| Module | Sales (Workshop 1) |
| Evidence | `SaleRepository` lines 51-52: `private final ProductService productService; private final PersonService personService;`. Lines 231 and 241: `for (Person person : personService.listPersons())` inside `findCustomerById` and `findSellerById`, called once per sale. Line 184: `productService.findById(productId)`. |
| Explanation | The persistence layer depends on the service layer, which inverts the required layer direction (ui -> service -> persistence -> model). The repository is coupled to two other modules to rebuild references. |
| Consequence | `persons.txt` is read twice for every sale loaded. The `Customer` and `Seller` objects are new instances, different from the ones in `ConsoleMenu`. Any change in `PersonService` or `ProductService` can break sale loading. |
| Proposed solution | `SaleRepository` returns flat records (new class `SaleRecord`, like `WarrantyRepository.WarrantyData`) and `SaleService` resolves customer, seller and products. Pattern applied: Low Coupling + Indirection. |
| Other member's code | No (only `Main` changes the constructor call). |

## 5. High Cohesion
**Verdict:** Violation

Correctly applied in `SaleRepository.toLine/createSale` (only file format) and `ProductService.restoreStock` (one responsibility).

### V-05
| Field | Content |
|---|---|
| Pattern | High Cohesion |
| Module | Sales (Workshop 1), Req. 1, 2 and 4 integration |
| Evidence | `SaleService.registerSale` lines 95-150: validation (lines 97-101), id generation `"SALE-" + (sales.size() + 1)` (line 103), promotion and discount (lines 106-114), warranty assignment (lines 116-129), stock routing (lines 131-144) and persistence (lines 146-147). |
| Explanation | One method validates, generates the id, applies the discount, decides warranties, updates stock in two services and persists. Each requirement added its piece to the same method. |
| Consequence | Any change in promotions, warranties or inventory touches this method, which increases merge conflicts and regression risk. There is no rollback: if a step fails after warranties are saved, partial effects remain. |
| Proposed solution | `Sale` applies the discount (V-01); `WarrantyService.assignWarrantiesFor(sale, extendedIds)` handles warranties; stock goes through a new `InventoryService` (V-08); `registerSale` only coordinates. Pattern applied: High Cohesion. |
| Other member's code | Yes: `WarrantyService` (Developer 2). Boundary finding. |

### V-06
| Field | Content |
|---|---|
| Pattern | High Cohesion |
| Module | UI and startup (Workshop 1), Req. 1-6 integration |
| Evidence | `ConsoleMenu` (989 lines, 7 service fields). Line 48: `private final ArrayList<Person> persons;`. Lines 946-964: `getCustomers()` and `getSellers()` filter persons with `instanceof`. |
| Explanation | A single class handles the menus of eight areas, keeps domain state and runs business queries (filtering customers and sellers) that belong to `PersonService`. |
| Consequence | Every change in any module edits this file, so all three members work on it at the same time. The class is hard to read and to verify. |
| Proposed solution | Split it into new classes per module (`PersonMenu`, `ProductMenu`, `AccessoryMenu`, `SaleMenu`, `PromotionMenu`, `ReturnMenu`, `WarrantyMenu`) coordinated by `ConsoleMenu`, and move the filters to `PersonService.listCustomers()` and `listSellers()`. Pattern applied: High Cohesion. |
| Other member's code | Yes: `PersonService` (Developer 2). |

## 6. Polymorphism
**Verdict:** Violation

Correctly applied in `Sale.appendProducts`, which only uses `Product` methods, and in `ConsoleMenu.handleException`, where differentiated handling of the project's exception hierarchy is a presentation decision required by Req. 6.

### V-07
| Field | Content |
|---|---|
| Pattern | Polymorphism |
| Module | Warranties (Req. 4) |
| Evidence | `SaleService.registerSale` line 118: `if (product instanceof Console) { warrantyService.assignBasicWarranty(product, sale); }`. `ConsoleMenu.registerSale` line 319: `if (item instanceof Console) { ... "¿Desea agregar garantía extendida ..." }`. |
| Explanation | "Does this product get a warranty" varies by product type and is decided with `instanceof` in two places, instead of asking the product. |
| Consequence | A new product that needs a warranty requires editing `SaleService` and `ConsoleMenu`, and the two checks can drift apart. |
| Proposed solution | Add polymorphic methods `requiresBasicWarranty()` and `allowsExtendedWarranty()` to `Product`, overridden in `Console`, and use them in both places. Pattern applied: Polymorphism. |
| Other member's code | Yes: `Product`, `Console` (Developer 1). Boundary finding. |

## 7. Pure Fabrication
**Verdict:** Correct

`SaleRepository` holds the file persistence of sales, and `SaleValidator` holds input validation; neither responsibility belongs to a domain class. `Main` is the startup fabrication.

## 8. Indirection
**Verdict:** Violation

### V-08
| Field | Content |
|---|---|
| Pattern | Indirection |
| Module | Accessories (Req. 1) |
| Evidence | `SaleService.registerSale` lines 131-144: `if (product instanceof Accessory) { accessoryService.updateStock(accessoriesToPersist, product.getId(), -1); } else { productService.updateStock(product.getId(), -1); }` followed by `accessoryService.saveAll(accessoriesToPersist)`. |
| Explanation | `SaleService` talks directly to two stock services and decides which one to call. There is no intermediary that hides where each kind of product lives. |
| Consequence | Every client that changes stock (`SaleService`, `ReturnService`) repeats this branching. A new product family needs a third branch in every client. |
| Proposed solution | Create a new class `InventoryService` with `decreaseStock(Product, int)` and `increaseStock(Product, int)` that delegates to `ProductService` or `AccessoryService`. `SaleService` depends only on it. Pattern applied: Indirection (+ Pure Fabrication). |
| Other member's code | Yes: `AccessoryService` and `ReturnService` (Developer 2), `ProductService` (Developer 1). Boundary finding. |

## 9. Protected Variations
**Verdict:** Violation

### V-09
| Field | Content |
|---|---|
| Pattern | Protected Variations |
| Module | Sales (Workshop 1) |
| Evidence | `Sale` line 18: `private List<Product> products;`. `Sale.calculateTotal` and `Sale.appendProducts` read `product.getPrice()` every time. |
| Explanation | The sale keeps references to `Product`, which is mutable (`setPrice`, `adjustStock`). Nothing freezes the price that was charged, so a change in a product leaks into closed sales. |
| Consequence | If a price changes, a reprinted receipt, a return refund and the extended warranty cost use the new price, not the price charged. Quantities cannot be represented without repeating the object. |
| Proposed solution | Create a new class `SaleItem` (product id, title, unit price at sale time, quantity) and store `List<SaleItem>` in `Sale`. Pattern applied: Protected Variations. |
| Other member's code | Yes: `Return`, `ExtendedWarranty`, `CategoryDiscount` (Developer 1). Boundary finding. |

---

## Findings reported to the consolidation

### Boundary findings
V-02, V-05, V-07, V-08 and V-09 involve code of Developer 1 or Developer 2 and must be registered once in `consolidated-audit.md` with the split of the correction.

### Out-of-scope findings (functional defects, not fixed)
| # | Evidence | Defect |
|---|---|---|
| 1 | `SaleRepository.createSale` line 194 and `Sale` constructor line 41 | The stored total (`data[5]`) is ignored; a loaded sale recalculates its total without discount or warranty, so totals and the monthly balance change after a restart. |
| 2 | `Sale` line 22 | `warrantyCost` is never assigned, so the receipt never shows the extended warranty line. |
| 3 | `SaleRepository.createSale` line 184 | Products are only searched in `ProductService`; accessories disappear from loaded sales, and a sale with only accessories is discarded. |
| 4 | `SaleService.registerSale` line 100 | Stock is validated as 1 unit per list item; the same product added twice with stock 1 passes validation and fails halfway, after warranties were saved. |
| 5 | `SaleService.registerSale` line 103 | `"SALE-" + (sales.size() + 1)` has no collision check; if a stored sale is discarded on load, an existing id can be reused. |
| 6 | `SaleService.viewSalesByCustomer` line 170 | Comparison by reference (`Person` has no `equals`); after a restart, sales loaded from file are not found for the customer selected in the menu. |
