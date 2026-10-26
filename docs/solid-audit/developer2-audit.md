# SOLID Audit - Developer 2

**Audited commit (develop):** `d2d7245`
**Author:** Estefanía Márquez Martínez (Developer 2)

Line numbers refer to the audited commit of `develop`.

## Scope

| Module (requirement) | Class | Methods audited |
|---|---|---|
| Persons (Workshop 1), GRASP (Req. 7) | `Person`, `Customer`, `Seller` | constructors, getters/setters, `getKey`, `validate`, `requireUniqueAmong`, `updateFrom` |
| Persons (Workshop 1), GRASP (Req. 7) | `PersonRepository`, `FilePersonRepository`, `PersonRecordMapper` | `savePersons`, `listPersons`, `parseRecord`, `toLine`, `toPerson`, `RecordLayout` |
| Persons (Workshop 1), GRASP (Req. 7) | `PersonService` | constructor, `register`, `update`, `delete`, `search`, `searchPerson`, `listPersons`, `listCustomers`, `listSellers`, `listByType`, `personsOfSameType`, `persistPersons` |
| GRASP (Req. 7) | `PersonFactory` | constructor, `create`, `createCustomer`, `createSeller` |
| Accessories (Req. 1), GRASP (Req. 7) | `AccessoryRepository`, `AccessoryRecordMapper` | `saveAll`, `loadAll`, `toLine`, `toAccessory`, type mappers |
| Accessories (Req. 1), GRASP (Req. 7) | `AccessoryService` | constructor, `registerController`, `registerCable`, `registerMemory`, `listAllAccessories`, `listAccessoriesByType`, `findAccessoriesCompatibleWith`, `findById`, `updateStock`, `restoreStock`, `requireById`, `addCompatibleConsoles`, `saveAll` |
| Accessories (Req. 1) | `ConnectionType` | `isValid`, `optionsDescription` |
| Promotions (Req. 2), GRASP (Req. 7) | `PromotionRepository`, `PromotionRecordMapper`, `PromotionService`, `PromotionFactory` | every public method and the type mappers |
| Returns (Req. 3), GRASP (Req. 7) | `ReturnRepository`, `ReturnData`, `ReturnService`, `MonthlyBalanceReport` | every public method, `loadReturns`, `toReturn`, `containsReturnId` |
| Warranties (Req. 4), GRASP (Req. 7) | `WarrantyRepository` (with `WarrantyData`), `WarrantyService`, `WarrantyFactory` | every public method, `warranties`, `loadWarranties`, `createWarranty`, `hasWarrantyOfType`, `generateWarrantyId` |
| GRASP (Req. 7) | `IdGenerator`, `SaleLookup` | `nextId`, `findById` |
| Exceptions and validation in services and repositories (Req. 6) | `AccessoryValidator`, `PromotionValidator`, `ReturnValidator`, `WarrantyValidator`, and their use in the services above | every public static method |
| Infrastructure | `utilities.FilePath` | constants |

`SaleLookup` is implemented by `SaleCatalog` (Technical Lead); only the interface is audited here.

## Summary of verdicts

| Principle | Verdict | Violations |
|---|---|---|
| Single Responsibility (SRP) | Violation | V-01, V-02, V-03 |
| Open/Closed (OCP) | Violation | V-04 |
| Liskov Substitution (LSP) | Violation | V-05 |
| Interface Segregation (ISP) | Violation | V-06 |
| Dependency Inversion (DIP) | Violation | V-07 |

---

## 1. Single Responsibility Principle
**Verdict:** Violation

Correctly applied in `FilePersonRepository` + `PersonRecordMapper`, `AccessoryRepository` + `AccessoryRecordMapper` and `PromotionRepository` + `PromotionRecordMapper` (file access separated from the line format), in `PersonService` (only person rules and persistence calls), in `PromotionService`, in the three factories (only object creation) and in `IdGenerator` (only the `PREFIX-n` sequence). `MonthlyBalanceReport` only computes the balance (C-31).

### V-01
| Field | Content |
|---|---|
| Principle | Single Responsibility |
| Module | Returns (Req. 3), GRASP (Req. 7) |
| Evidence | `ReturnService.registerReturn` lines 40-83 (return use case) and, in the same class, `loadReturns` lines 123-135 and `toReturn` lines 137-161, which resolve the stored records into `Sale` and `Product` objects (`saleService.findById`, `inventoryService.findAnyById`, `setRefundAmount`). |
| Explanation | `ReturnService` has two reasons to change: the rules of a return and the way stored return records are rebuilt into domain objects. The Technical Lead already separated these two concerns for sales (`SaleCatalog`, C-23). |
| Consequence | A change in the returns file (a new field) or in how sales are resolved forces a change in the class that registers returns, and the constructor reads the file before the service can be used. |
| Proposed solution | Create a `ReturnCatalog` in `service` that loads the `ReturnData` records, resolves them, keeps the list and saves it; `ReturnService` keeps only the use case and the queries and receives the catalog. |
| Other member's code | Yes: `Main` (Technical Lead) creates the new catalog. |

### V-02
| Field | Content |
|---|---|
| Principle | Single Responsibility |
| Module | Warranties (Req. 4), GRASP (Req. 7) |
| Evidence | `WarrantyService`: lazy cache `warranties()` lines 60-65, record resolution `loadWarranties` lines 67-88, assignment `assignWarrantiesFor` / `assignBasicWarranty` / `assignExtendedWarranty` lines 97-139, cancellation `cancelWarranties` lines 182-205, queries lines 164-238, and ID generation lines 240-243. |
| Explanation | The class loads and resolves records, keeps the in-memory cache, applies the assignment and cancellation rules and answers every query. Those are several reasons to change in one class of 247 lines. |
| Consequence | Changing the warranty file, the cache policy or a business rule all edit the same class; the lazy loading exists only because loading is mixed with the service (C-22/C-33). |
| Proposed solution | Same as V-01: a `WarrantyCatalog` that loads, resolves, keeps and saves the warranties; `WarrantyService` keeps assignment, cancellation and queries. |
| Other member's code | Yes: `Main` (Technical Lead) creates the new catalog. |

### V-03
| Field | Content |
|---|---|
| Principle | Single Responsibility |
| Module | Returns (Req. 3), Warranties (Req. 4) |
| Evidence | `ReturnRepository.toLine` lines 58-78 and `toReturnData` lines 80-106 together with file access in `saveAll` / `loadAll` lines 23-56. `WarrantyRepository.toLine` lines 66-72, `toWarranty` lines 74-88 and the nested `WarrantyData` lines 90-111, together with `saveAll` / `loadAll` lines 24-64. |
| Explanation | Both repositories mix the text format of the record with the file access, while persons, accessories and promotions already use a separate record mapper. |
| Consequence | Adding a field to these files edits the class that handles the file errors, and the format cannot be tested without a file. |
| Proposed solution | Create `ReturnRecordMapper` and `WarrantyRecordMapper` (and move `WarrantyData` to its own class, like `ReturnData`), injected in the repositories as `AccessoryRepository` does. The file format and the current error behavior must stay the same. |
| Other member's code | Yes: `Main` (Technical Lead) creates the repositories with the mappers. |

## 2. Open/Closed Principle
**Verdict:** Violation

Correctly applied in `PersonService.register(Person)` and `PromotionService.register(Promotion)`, which accept any subtype, in `PersonFactory` / `WarrantyFactory` (builders registered by type key) and in the record mappers (one mapper per type).

### V-04
| Field | Content |
|---|---|
| Principle | Open/Closed |
| Module | Accessories (Req. 1) |
| Evidence | `AccessoryService.registerController` lines 23-36, `registerCable` lines 38-48 and `registerMemory` lines 50-63: one method per accessory type, each repeating `validateCommonFields`, a type-specific check, `validateUniqueId`, `new ...`, `accessories.add`, `saveAll`. |
| Explanation | A new accessory type requires a new method in `AccessoryService`, while persons and promotions already register the built object through one method. |
| Consequence | Every new accessory type modifies the service and repeats the same steps. |
| Proposed solution | Add `register(Accessory accessory, List<String> compatibleConsoleIds)` with the common steps, and an `AccessoryFactory` with one `create...` method per type that runs the type-specific validation (as `PromotionFactory` does). The three current methods delegate to them, so the messages stay the same. |
| Other member's code | Yes: `AccessoryMenu` (Technical Lead) lines 80, 103 and 130 can keep calling the old methods while they only delegate. |

## 3. Liskov Substitution Principle
**Verdict:** Violation

Correctly applied in the repositories: `FilePersonRepository` keeps the contract of `PersonRepository` (empty list when the file does not exist).

### V-05
| Field | Content |
|---|---|
| Principle | Liskov Substitution |
| Module | Persons (Workshop 1), GRASP (Req. 7) |
| Evidence | `Person.updateFrom(Person changes)` line 113 accepts any person. `Customer.updateFrom` line 71: `Customer other = (Customer) changes;` and `Seller.updateFrom` line 95: `Seller other = (Seller) changes;`. |
| Explanation | The subclasses strengthen the precondition of the base method: `updateFrom` declared on `Person` only works if the argument is of the same subclass, otherwise it throws `ClassCastException`. A client that only knows `Person` cannot use the method safely. |
| Consequence | Today `PersonService.update` (line 42) avoids it by searching with `changes.getClass()`, but any new caller that passes a different subtype fails at run time instead of at compile time. |
| Proposed solution | Make the base contract explicit and safe: `Person` checks `getClass() == changes.getClass()` and throws the existing `BusinessRuleException` otherwise, then calls a protected `copyFrom` implemented by each subclass with its own type (generic `Person<T>` is not needed). |
| Other member's code | No. |

## 4. Interface Segregation Principle
**Verdict:** Violation

Correctly applied in `PersonRepository` (two methods) and `SaleLookup` (one method, used by `WarrantyService`).

### V-06
| Field | Content |
|---|---|
| Principle | Interface Segregation |
| Module | Returns (Req. 3), Warranties (Req. 4) |
| Evidence | `ReturnService` line 19: `private final SaleService saleService;`, used only at lines 44 and 138 (`saleService.findById`). `MonthlyBalanceReport` line 11: `private final SaleService saleService;`, used only at line 24 (`viewAllSales`). `WarrantyService` line 23: `private final ProductService productService;`, used only at line 72 (`findById`). |
| Explanation | These classes depend on whole services but use one operation each. `ReturnService` already could use the existing `SaleLookup`. |
| Consequence | Any change to `SaleService` or `ProductService` affects classes that do not use the changed methods, and they cannot be built without all the collaborators of those services. |
| Proposed solution | `ReturnService` depends on `SaleLookup`; `MonthlyBalanceReport` depends on the sales abstraction the Technical Lead creates for `SaleService` (`SaleStore`, leader V-08); `WarrantyService` depends on a small `ProductLookup` (`findById`) implemented by `ProductService`. |
| Other member's code | Yes: boundary with the Technical Lead (leader-audit V-06 and V-08, wiring in `Main`) and with Developer 1 (`ProductService implements ProductLookup`). |

## 5. Dependency Inversion Principle
**Verdict:** Violation

Correctly applied in `PersonService` (depends on the `PersonRepository` interface, line 19) and in `WarrantyService` for sales (depends on `SaleLookup`, line 22).

### V-07
| Field | Content |
|---|---|
| Principle | Dependency Inversion |
| Module | Accessories (Req. 1), Promotions (Req. 2), Returns (Req. 3), Warranties (Req. 4) |
| Evidence | `AccessoryService` line 15 `private final AccessoryRepository accessoryRepository;`, `PromotionService` line 14 `private final PromotionRepository promotionRepository;`, `ReturnService` line 18 `private final ReturnRepository repository;`, `WarrantyService` line 21 `private final WarrantyRepository repository;` - all concrete file classes (`FilePath.ACCESSORIES`, `FilePath.PROMOTIONS`, `FilePath.RETURNS`, `FilePath.WARRANTIES`). |
| Explanation | The services hold the business rules and depend directly on the file implementations. The persons module already shows the expected design (`PersonRepository` interface + `FilePersonRepository`). |
| Consequence | Changing the storage or testing a service without its data file requires editing the service. |
| Proposed solution | Extract an interface per repository (`AccessoryRepository`, `PromotionRepository`, `ReturnRepository`, `WarrantyRepository`) and rename the current classes `File...Repository`, created in `Main`. |
| Other member's code | Yes: `Main` (Technical Lead) creates the concrete repositories. |

---

## Findings reported to the consolidation

### Boundary findings
- V-06 is the service side of leader-audit V-06 (`ReturnMenu` -> `SaleLookup`) and V-08 (`SaleStore`); it should be one consolidated violation with parts for the Technical Lead and Developer 2, plus `ProductLookup` for Developer 1.
- V-05 of developer1-audit (`Promotion.isValid`) requires changing `PromotionValidator.validatePercentage` here.
- V-01, V-02, V-03, V-04 and V-07 need the Technical Lead to update `Main` (and `AccessoryMenu` only if the old methods are removed).

### Out-of-scope findings (functional defects, not fixed)
- No new defects. The validators still throw `IllegalArgumentException` and the accessory and promotion repositories still print their own error message; both are already registered as out of scope in Requirement 7 (D2-V13, D2-V17, D2-V29, D2-V30) and are not registered again.
