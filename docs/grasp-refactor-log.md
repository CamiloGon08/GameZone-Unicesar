# GRASP Refactor Log

Record of the Phase 2 corrections of Requirement 7. Each entry references a final violation (`C-xx`) of `docs/grasp-audit/consolidated-audit.md`.

## Non-regression strategy

The external behavior must not change: same inputs, same outputs, same Spanish messages and same data files.

1. **Reference build.** `develop` at commit `8382d85` (consolidated audit merged, before any correction) is compiled once and kept as the reference.
2. **Fixed script.** `scripts/verify-grasp.sh` runs the same menu script on the reference build and on the build under review. Each run starts from its own fresh `data/` folder with only the seller `S1 Luis`.
   - **Session 1** registers and exercises every menu: video game, duplicated video game (error), console, controller, cable, memory, accessory listings by type and by console, the three promotion types, active promotions, invalid customer phone (error), customer, sale with three items and extended warranty, sale with one item, sales by customer and by seller, return of a non-existent sale (error), return of `G1` from `SALE-1`, returns by customer and by sale, monthly balance, warranty queries, invalid option (error) and exit.
   - **Session 2** restarts the program and reads everything back from the files (products, customers, sellers, sales, accessories, promotions, returns, warranties).
3. **Comparison.** The script compares byte for byte the two console outputs, the two error outputs and the seven data files. Any difference fails the check and prints the `diff`.
4. **Build and tests.** `mvn clean compile` and `mvn test` must succeed.
5. **When.** Every correction commit is checked before it is pushed. Every Phase 2 Pull Request lists the result. At the end of the phase the full check runs again on `develop`.

Known functional defects (out-of-scope findings of the consolidated audit, section 5) are not fixed in this requirement, so they must appear identically before and after. For example, in session 2 the reloaded `SALE-2` shows `Total : $100.00` instead of `$90.00` because the discount is lost when sales are loaded.

### How to run it (Git Bash)

```bash
# once: build the reference from develop before Phase 2
git worktree add ../gamezone-reference 8382d85
cd ../gamezone-reference && mvn -q clean compile && cd -

# on each check: build the current branch and compare
mvn -q compile
scripts/verify-grasp.sh ../gamezone-reference/target/classes
mvn test
```

The run must end with `PASS: console output and the seven data files match the reference.`

## Baseline

**Date:** 2026-10-26
**Commit:** `8382d85` (develop) / `6f05df8` (feature/grasp-refactor, documentation only)

| Check | Result |
|---|---|
| `mvn clean compile` | BUILD SUCCESS |
| `mvn test` | 20 tests, 0 failures, 0 errors |
| `verify-grasp.sh` reference vs `feature/grasp-refactor` | PASS, 11 of 11 files identical |

Expected results recorded in the baseline (session 1):

| Operation | Expected result |
|---|---|
| Register `G1` twice | Second time: `No se puede completar la operación: Ya existe un producto con el identificador G1.` |
| Customer with phone `abc` | `Teléfono inválido.` |
| Sale `SALE-1`: Game + Console + Pad, extended warranty | Promotion `Bulk`, discount `$70.00`, total `$300.00` |
| Sale `SALE-2`: Game | Promotion `Promo10`, discount `$10.00`, total `$90.00` |
| Return of `SALE-99` | `No existe una venta con ese ID.` |
| Return of `G1` from `SALE-1` | Refund `$80.00` (list price `$100.00`, proportional discount `$20.00`) |
| Monthly balance of the current month | Sales `$390.00`, returns `$80.00`, net `$310.00` |
| Warranties of `SALE-1` | `WAR-1` basic and `WAR-2` extended (cost `$20.00`) for `C1` |

Data files after session 1:

```text
products.txt     VIDEOGAME,G1,Game,100.0,4,PC,RPG,E
                 CONSOLE,C1,Console,200.0,2,Sony,PS5,9
sales.txt        SALE-1|<date>|U1|S1|G1;C1;CT1|300.0|Bulk|70.0
                 SALE-2|<date>|U1|S1|G1|90.0|Promo10|10.0
returns.txt      RETURN-1,<date>,SALE-1,G1,Defectuoso,80.0,0.0
warranties.txt   BASIC,WAR-1,C1,SALE-1,<date>
                 EXTENDED,WAR-2,C1,SALE-1,<date>
```

## Corrections

One entry per correction commit, in the order of section 6 of the consolidated audit.

<!--
### C-xx — <short title>

**Member:** <name>
**Commit:** refactor: <description> (C-xx)
**Classes changed:** <list>
**Change:** <what moved where, and which pattern it applies>
**Verification:** mvn clean compile OK · mvn test 20/20 · verify-grasp.sh PASS
-->

### C-21 — Sale computes its own total

**Member:** Technical Lead
**Commit:** refactor: let Sale apply promotion and warranty cost to its total (C-21)
**Classes changed:** `Sale`, `SaleService`
**Change:** `SaleService.registerSale` no longer computes the total with `sale.setTotal(sale.getTotal() - discount)` and `sale.setTotal(sale.getTotal() + cost)`. `Sale` now has `applyPromotion(name, amount)` and `addWarrantyCost(amount)`, and the public `setTotal` was removed, so only `Sale` changes its total (Information Expert). `warrantyCost` is still not assigned, because filling it would add a new line to the receipt (out-of-scope defect 2). The part of Developer 1 (`PercentageDiscount` and `BulkPurchaseDiscount` reading `getSubtotal()`) is a separate commit.
**Verification:** `mvn compile` OK · `mvn test` 20/20 · `verify-grasp.sh` PASS (11 of 11 files identical)

### C-22 — WarrantyService injected through the constructor

**Member:** Technical Lead
**Commit:** refactor: inject WarrantyService into SaleService constructor (C-22)
**Classes changed:** `SaleService`, `Main`
**Change:** `SaleService` received `WarrantyService` through `setWarrantyService` after construction and skipped warranties when it was `null`. `Main` now creates `WarrantyService` first and passes it to the `SaleService` constructor; the setter and the `null` check were removed, so the collaboration is explicit and cannot be forgotten (Low Coupling).
**Verification:** `mvn compile` OK · `mvn test` 20/20 · `verify-grasp.sh` PASS (11 of 11 files identical)

### C-27 — InventoryService routes stock operations (Technical Lead part)

**Member:** Technical Lead
**Commit:** refactor: add InventoryService to route stock operations (C-27)
**Classes changed:** `InventoryService` (new), `SaleService`, `Main`
**Change:** `SaleService.registerSale` decided with `instanceof Accessory` whether to update stock through `AccessoryService` or `ProductService`. The new `InventoryService` (`findAnyById`, `decreaseStock`, `increaseStock`) is the only class that makes that routing decision, and `SaleService` now depends on it instead of both services (Indirection). The save order is kept: products are saved one by one and accessories once at the end. The part of Developer 2 (`ReturnService` and `ReturnRepository` using `InventoryService`) is a separate commit.
**Verification:** `mvn compile` OK · `mvn test` 20/20 · `verify-grasp.sh` PASS (11 of 11 files identical)

### C-28 — SaleItem freezes the price of each item (Technical Lead part)

**Member:** Technical Lead
**Commit:** refactor: add SaleItem to keep the unit price of each sale item (C-28)
**Classes changed:** `SaleItem` (new), `Sale`, `SaleRepository`
**Change:** `Sale` kept a list of `Product` and read the live `getPrice()` for its subtotal and receipt, so a later price change would alter past sales. The new `SaleItem` stores the product and the unit price charged at sale time; `Sale` now keeps a list of `SaleItem`, computes its subtotal and receipt from it, and exposes `getItems()`. `getProducts()` is kept for the classes that still need the products. `SaleRepository` writes the product ids from the items. The `sales.txt` format did not change, and loaded sales rebuild their items from the current product, so current outputs stay the same (Protected Variations). The part of Developer 1 (`Return`, `ExtendedWarranty` and `CategoryDiscount` reading the item price) is a separate commit.
**Verification:** `mvn compile` OK · `mvn test` 20/20 · `verify-grasp.sh` PASS (11 of 11 files identical)

## Final verification

To be completed when every correction is merged into `develop`.
