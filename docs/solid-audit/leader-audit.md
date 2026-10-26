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
