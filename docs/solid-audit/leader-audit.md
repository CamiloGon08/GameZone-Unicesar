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
