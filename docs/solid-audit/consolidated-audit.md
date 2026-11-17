# SOLID Consolidated Audit

**Audited commit (develop):** `d2d7245`
**Consolidated by:** Camilo Andres Gonzalez Garcia (Technical Lead)
**Source reports:** `leader-audit.md` (L), `developer1-audit.md` (D1), `developer2-audit.md` (D2)

This document is the reference for Phase 2. Every violation below has a unique ID (V-01 to V-20), its source report and the member responsible for the correction, following section 4 of the requirement, `TEAM.md` and `docs/grasp-refactor-log.md`.

## 1. Coverage

Every class of the system was audited by exactly one member. Methods added by another member are audited by their author.

| Package | Class | Audited by |
|---|---|---|
| model | `Sale` (except `canBeReturned`), `SaleItem` | Technical Lead |
| model | `Sale.canBeReturned` | Developer 1 |
| model | `Product`, `VideoGame`, `Console` | Developer 1 |
| model | `Accessory`, `Controller`, `Cable`, `Memory` | Developer 1 |
| model | `Promotion`, `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount` | Developer 1 |
| model | `Return` | Developer 1 |
| model | `Warranty`, `BasicWarranty`, `ExtendedWarranty` | Developer 1 |
| model | `Person`, `Customer`, `Seller`, `ConnectionType` | Developer 2 |
| persistence | `SaleRepository`, `SaleRecord` | Technical Lead |
| persistence | `ProductRepository`, `ProductLineConverter`, `VideoGameLineConverter`, `ConsoleLineConverter` | Developer 1 |
| persistence | `PersonRepository`, `FilePersonRepository`, `PersonRecordMapper` | Developer 2 |
| persistence | `AccessoryRepository`, `AccessoryRecordMapper`, `PromotionRepository`, `PromotionRecordMapper` | Developer 2 |
| persistence | `ReturnRepository`, `ReturnData`, `WarrantyRepository` (with `WarrantyData`) | Developer 2 |
| service | `SaleService`, `SaleCatalog`, `InventoryService` | Technical Lead |
| service | `SaleLookup` (interface) | Developer 2 (implementation `SaleCatalog`: Technical Lead) |
| service | `ProductService` (except `restoreStock`) | Developer 1 |
| service | `ProductService.restoreStock` | Technical Lead |
| service | `PersonService`, `AccessoryService`, `PromotionService`, `ReturnService`, `WarrantyService`, `MonthlyBalanceReport`, `IdGenerator` | Developer 2 |
| factory | `PersonFactory`, `PromotionFactory`, `WarrantyFactory` | Developer 2 |
| ui | `ConsoleMenu`, `ConsoleIO`, `ProductMenu`, `PersonMenu`, `SaleMenu`, `AccessoryMenu`, `PromotionMenu`, `ReturnMenu`, `WarrantyMenu` | Technical Lead |
| root | `Main` | Technical Lead |
| exceptions | `GameZoneException` and its four subclasses | Developer 1 |
| validation | `ProductValidator`, `PersonValidator`, `SaleValidator` | Developer 1 |
| validation | `AccessoryValidator`, `PromotionValidator`, `ReturnValidator`, `WarrantyValidator` | Developer 2 (author by commit history) |
| utilities | `FilePath` | Developer 2 |

Evidence validation: every finding cites class, method and lines of `d2d7245`. No finding was returned to its author. The validators created by Developer 2 were moved from Developer 1's scope to Developer 2's scope by commit history (`4fecd01`, `7a9078c`, `ad88ecb`, `0033171`), so no class was audited twice.

## 2. Team verdict per principle

| Principle | L | D1 | D2 | Team verdict | Violations |
|---|---|---|---|---|---|
| Single Responsibility (SRP) | Violation | Violation | Violation | **Violation** | V-01 to V-07 |
| Open/Closed (OCP) | Violation | Violation | Violation | **Violation** | V-08 to V-13 |
| Liskov Substitution (LSP) | Correct | Correct | Violation | **Violation** | V-14 |
| Interface Segregation (ISP) | Violation | Violation | Violation | **Violation** | V-15, V-16 |
| Dependency Inversion (DIP) | Violation | Violation | Violation | **Violation** | V-17 to V-20 |

LSP is a team violation because one confirmed case (V-14) is enough; the hierarchies audited by the Lead and Developer 1 comply.

## 3. Final list of violations

| ID | Principle | Evidence (d2d7245) | Explanation | Consequence | Agreed solution | Source | Owner |
|---|---|---|---|---|---|---|---|
| V-01 | SRP | `Sale.generateReceipt` 273-279 and `append*` 281-316 | The sale computes totals and also builds the receipt text | A layout change edits the domain class | `ui/SaleReceiptFormatter`; `SaleMenu` uses it | L V-01 | Technical Lead |
| V-02 | SRP | `SaleRepository.toLine`, `parseRecord`, `createRecord` with file I/O in `save`/`loadAll` | Line format and file access in one class | A new field edits the class that handles file errors | `SaleRecordMapper` + `FileSaleRepository` | L V-02 | Technical Lead |
| V-03 | SRP | `Return.generateReturnReceipt` 162-189; ratio repeated 163-165 | Refund rules and receipt layout in the model | Layout change edits the model; duplicated ratio can diverge | `ui/ReturnReceiptFormatter`; `Return` exposes read queries | D1 V-01 | Developer 1 (+ Lead: `ReturnMenu`) |
| V-04 | SRP | `Warranty.generateWarrantyCertificate` 102-106 | Coverage rules and certificate layout in the base class | Label change edits the domain hierarchy | `ui/WarrantyCertificateFormatter` | D1 V-02 | Developer 1 (+ Lead: `WarrantyMenu`) |
| V-05 | SRP | `ReturnService.registerReturn` 40-83 with `loadReturns` 123-135, `toReturn` 137-161 | Return rules and record resolution in one class | File or resolution change edits the use case | `service/ReturnCatalog` | D2 V-01 | Developer 2 (+ Lead: `Main`) |
| V-06 | SRP | `WarrantyService` cache 60-65, load 67-88, rules 97-205 | Loading, cache, rules and queries in one class | Any storage change edits the rules | `service/WarrantyCatalog` | D2 V-02 | Developer 2 (+ Lead: `Main`) |
| V-07 | SRP | `ReturnRepository` 23-106, `WarrantyRepository` 24-111 | Record format mixed with file access | Format cannot change or be tested without the file | `ReturnRecordMapper`, `WarrantyRecordMapper`, standalone `WarrantyData` | D2 V-03 | Developer 2 |
| V-08 | OCP | `InventoryService.findAnyById`/`decreaseStock`/`increaseStock` with `instanceof` | Routing by concrete type | A new sellable item edits the service | `StockHandler` + `ProductStockHandler`, `AccessoryStockHandler` | L V-03 | Technical Lead |
| V-09 | OCP | `ConsoleIO.handleException` if-chain | One branch per exception type | A new exception edits the method | Ordered exception-to-message map | L V-04 | Technical Lead |
| V-10 | OCP | `ConsoleMenu.start` switch | One case per option, numbers repeated in `printMainMenu` | A new option edits two places | `MenuOption` list | L V-05 | Technical Lead |
| V-11 | OCP | `ProductService.registerVideoGame` 59-70, `registerConsole` 87-98 | One method per family | A new family edits the service | `register(Product)` + `Product.validateDetails` | D1 V-03 | Developer 1 |
| V-12 | OCP | `ProductRepository` 31-32 fixed converter list | Converters cannot be supplied | A new family edits the repository | Constructor with ordered converter list | D1 V-04 | Developer 1 (+ Lead: `Main`) |
| V-13 | OCP | `AccessoryService.registerController` 23-36, `registerCable` 38-48, `registerMemory` 50-63 | One method per accessory type | A new type edits the service | `AccessoryFactory` + `register(Accessory, List<String>)` | D2 V-04 | Developer 2 (+ Lead: `Main`) |
| V-14 | LSP | `Customer.updateFrom` 71, `Seller.updateFrom` 95 cast the argument | Subclasses strengthen the base precondition | A mixed call throws `ClassCastException` | `Person.updateFrom` checks the type, then `copyFrom` | D2 V-05 | Developer 2 |
| V-15 | ISP | `Promotion.hasValidPercentage` 80; consumer `PromotionValidator` 47 | Every promotion must expose a percentage query | A fixed-amount promotion would fake it | Neutral `Promotion.isValid()` | D1 V-05 | Developer 1 (+ Dev 2: `PromotionValidator`) |
| V-16 | ISP | `ReturnMenu` (whole `SaleService`), `ReturnService` 19, `MonthlyBalanceReport` 11, `WarrantyService` 23 | Clients depend on whole services and use one method | Unrelated service changes reach them | `SaleLookup`, `SaleStore`, `ProductLookup` | L V-06 + D2 V-06 | Lead + Dev 2 + Dev 1 (see section 4) |
| V-17 | DIP | `SaleCatalog` depends on concrete `SaleRepository` | Policy depends on file class | Storage change edits the catalog | `SaleRepository` interface | L V-07 | Technical Lead |
| V-18 | DIP | `SaleService` depends on concrete `SaleCatalog` | Policy depends on a concrete holder | Service cannot use another store | `SaleStore` interface | L V-08 | Technical Lead |
| V-19 | DIP | `ProductService` 25, 36 concrete `ProductRepository` | Policy depends on file class | Another backend edits the service | `ProductRepository` interface + `FileProductRepository` | D1 V-06 | Developer 1 (+ Lead: `Main`) |
| V-20 | DIP | `AccessoryService` 15, `PromotionService` 14, `ReturnService` 18, `WarrantyService` 21 | Services depend on file classes | Storage change edits four services | Interfaces + `File...Repository` | D2 V-07 | Developer 2 (+ Lead: `Main`) |

## 4. Boundary findings

| Violation | Members | How the correction was split | Commit order |
|---|---|---|---|
| V-16 ISP | L V-06 and D2 V-06 reported the same problem from both sides | Lead: `SaleStore` (V-18) and `ReturnMenu` -> `SaleLookup`. Dev 2: `ReturnService` -> `SaleLookup`, `MonthlyBalanceReport` -> `SaleStore`, `WarrantyCatalog` -> `ProductLookup`. Dev 1: `ProductService implements ProductLookup` | Lead first (`SaleStore`), then Dev 1 (`ProductLookup`), then Dev 2 |
| V-15 ISP | D1 V-05 and D2 boundary note | Dev 1: `Promotion.isValid()`. Dev 2: `PromotionValidator` calls it | Dev 1 and Dev 2 in the same integration |
| V-03, V-04 SRP | D1 V-01, V-02 | Dev 1 creates the formatters; Lead changes `ReturnMenu` and `WarrantyMenu` | Lead wiring together with Dev 1 |
| V-05, V-06, V-07, V-12, V-13, V-19, V-20 | D1 and D2 | Each owner creates the new classes; Lead builds them in `Main` | Lead `Main` commit last |

## 5. Out-of-scope findings (functional defects, not fixed)

Defects already registered in Requirement 7 are not registered again.

| # | Evidence | Defect | Source |
|---|---|---|---|
| 1 | `SaleCatalog.findAll` 48-50 | Returns the internal list; a caller could change sales without saving them | L |
| 2 | `Product.setStock` 92 | Accepts a negative stock without the `canAdjustStock` guard; no production caller today | D1 |

Fixing them would change behavior, so they stay for a later requirement.
