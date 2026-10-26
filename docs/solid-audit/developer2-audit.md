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
