# SOLID Refactor Log

Requirement 8, Phase 2. Every violation of `docs/solid-audit/consolidated-audit.md` appears at least once in this log. IDs V-01 to V-20 are the consolidated IDs; the individual sections also show each member's local report ID.

## Non-regression strategy

Defined before the first correction (answer to guiding question 4).

- **Tool:** `scripts/verify-grasp.sh <reference-classes> target/classes`. It runs the same scripted menu session on the reference build and on the current build, with fresh copies of the data files, and compares byte for byte the console output, the error output and the seven data files.
- **Reference:** develop `d2d7245` (start of Requirement 8).
- **Menu operations covered:** all main-menu options and their submenus: products (register video game and console, list, search), persons (register, update, delete, search, list customers and sellers), sales (register with promotion and warranties, list all, by customer, by seller), accessories (register controller, cable and memory, list, compatible with a console, stock), promotions (register the three types, list active), returns (register, list, by customer, by sale, monthly balance), warranties (list, active, expiring soon, certificate) and exit, plus invalid inputs that show error messages.
- **Expected result:** every compared file is SAME, and `mvn clean test` passes.

| Run | Commit | Tests | verify-grasp.sh |
|---|---|---|---|
| Initial (reference) | `d2d7245` | 87 passing | 11/11 SAME (reference run) |
| Lead PR (`feature/solid-refactor`) | `6781532` | compiles only with the developers' parts (shared integration) | run on the integration |
| Final (all members) | develop `2f1dc97` | **127 passing**, 0 failures | **11/11 SAME - PASS** |

## Change log

| Class | Change made | Violation ID | Principle applied | Commit / PR | Owner |
|---|---|---|---|---|---|
| `Sale`, `SaleReceiptFormatter`, `SaleMenu` | Receipt text moved out of the model to a UI formatter | V-01 | SRP | `52888c1` / #65 | Technical Lead |
| `SaleRecordMapper`, `FileSaleRepository`, `SaleRepository` | Line format separated from file access; `SaleRepository` is an interface | V-02, V-17 | SRP, DIP | `d2b6260` / #65 | Technical Lead |
| `SaleStore`, `SaleCatalog`, `SaleService` | `SaleService` depends on the `SaleStore` abstraction | V-18, V-16 (Lead part) | DIP, ISP | `f78b395` / #65 | Technical Lead |
| `StockHandler`, `ProductStockHandler`, `AccessoryStockHandler`, `InventoryService` | `instanceof` routing replaced by a list of handlers | V-08 | OCP | `21d42f4` / #65 | Technical Lead |
| `ConsoleIO` | If-chain replaced by an ordered exception-to-message map | V-09 | OCP | `f617e6e` / #65 | Technical Lead |
| `MenuOption`, `ConsoleMenu`, `ReturnMenu` | Options built as a list; `ReturnMenu` depends on `SaleLookup` | V-10, V-16 (Lead part) | OCP, ISP | `7cc0f71` / #65 | Technical Lead |
| `ReturnMenu`, `WarrantyMenu` | Use `ReturnReceiptFormatter` and `WarrantyCertificateFormatter` (boundary) | V-03, V-04 (Lead part) | SRP | `7cc0f71`, `6a1b58a` / #65 | Technical Lead |
| `Main` | Builds file repositories, mappers, catalogs, handlers and factories | V-05, V-06, V-07, V-12, V-13, V-19, V-20 (Lead part) | DIP | `6781532` / #65 | Technical Lead |
| `Person`, `Customer`, `Seller` | `updateFrom` checks the type and delegates to `copyFrom` | V-14 | LSP | `b899cd0` / #65 | Developer 2 |
| `AccessoryFactory`, `AccessoryService` | One `register(Accessory, List<String>)` plus a factory per type | V-13 | OCP | `b899cd0`, `6afcadc` / #65 | Developer 2 |
| `AccessoryRepository`, `FileAccessoryRepository` | Repository interface and file implementation | V-20 | DIP | `6afcadc` / #65 | Developer 2 |
| `PromotionRepository`, `FilePromotionRepository`, `PromotionValidator` | Repository interface; validator calls `Promotion.isValid()` | V-20, V-15 (Dev 2 part) | DIP, ISP | `f937895` / #65 | Developer 2 |
| `ReturnRepository`, `FileReturnRepository`, `ReturnRecordMapper` | Format separated from file access; interface | V-07, V-20 | SRP, DIP | `bcaa740`, `4506057` / #65 | Developer 2 |
| `ReturnCatalog`, `ReturnService` | Catalog loads and saves returns; service keeps the rules and uses `SaleLookup` | V-05, V-16 (Dev 2 part) | SRP, ISP | `bcaa740` / #65 | Developer 2 |
| `MonthlyBalanceReport` | Depends on `SaleStore` | V-16 (Dev 2 part) | ISP | `bcaa740` / #65 | Developer 2 |
| `WarrantyRepository`, `FileWarrantyRepository`, `WarrantyRecordMapper`, `WarrantyData` | Format separated from file access; interface | V-07, V-20 | SRP, DIP | `011922c` / #65 | Developer 2 |
| `WarrantyCatalog`, `WarrantyService` | Catalog loads, caches and saves warranties; uses `ProductLookup` | V-06, V-16 (Dev 2 part) | SRP, ISP | `011922c` / #65 | Developer 2 |
| `Promotion` and subclasses | `hasValidPercentage` replaced by `isValid()` | V-15 | ISP | `6e906d3` / #66 | Developer 1 |
| `Return`, `ReturnReceiptFormatter` | Receipt moved to a UI formatter | V-03 | SRP | `89a45d9` / #66 | Developer 1 |
| `Warranty`, `WarrantyCertificateFormatter` | Certificate moved to a UI formatter | V-04 | SRP | `79b3de9` / #66 | Developer 1 |
| `Product`, `VideoGame`, `Console`, `ProductService`, `ProductLookup` | `register(Product)` with `validateDetails`; `ProductLookup` | V-11, V-16 (Dev 1 part) | OCP, ISP | `34e96d9` / #66 | Developer 1 |
| `ProductRepository`, `FileProductRepository` | Interface and injected converter list | V-12, V-19 | OCP, DIP | `d5778d2` / #66 | Developer 1 |

No violation was moved to out of scope: all 20 were corrected.

## Technical Lead - Requirement 8, Phase 2

**Author:** Camilo Andres Gonzalez Garcia. Local IDs: leader-audit V-01 to V-08 = consolidated V-01, V-02, V-08, V-09, V-10, V-16, V-17, V-18.

- Behavior preserved: the receipt text, exception messages, menu texts and option numbers are the same; `InventoryService` falls back to the first handler exactly as the old final `else` did; the sales file keeps its field order.
- The Lead's commits are disjoint from the developers' files, so the three parts were merged without conflicts. The branch compiles only with the three parts, so the final verification was run on the integrated develop.

## Developer 2 - Requirement 8, Phase 2

**Author:** Estefanía Márquez Martínez. Local IDs: developer2-audit V-01 to V-07 = consolidated V-05, V-06, V-07, V-13, V-14, V-16, V-20.

- Behavior preserved: return and warranty files keep their format; warranties are still loaded lazily; `updateFrom` with a different type now throws the existing `BusinessRuleException` instead of `ClassCastException`, a path that the menu never reaches because `PersonService.update` searches by the same type.

## Developer 1 - Requirement 8, Phase 2

**Author:** Jesus David Dominguez Theran (`jdaviddominguez`).
**Audit baseline:** `d2d7245`. **Integrated implementation baseline:** `011922c`.
**Reference package:** `r8-fase2-jesus.zip` supplied by the Technical Lead.

The IDs below refer to `developer1-audit.md`; they are not global team IDs. At this
checkpoint, the individual audit PR #64 is merged, but the consolidated SOLID audit
is not present in develop. The Technical Lead must confirm final IDs and the consolidated
audit before the final Phase 2 review. These changes follow the assigned reference
package and preserve the behavior constraints recorded in the individual report.

The commit column is populated by the author's publication script after each code
commit exists; no commit hash is invented before publication.

| Finding | Principle | Before | After | Main files | Code commit |
|---|---|---|---|---|---|
| Developer 1 V-01 | SRP | Return calculated refunds and assembled the receipt. | Return exposes charged-price and proportional-discount queries; ReturnReceiptFormatter owns the receipt layout. | `model/Return.java`, `ui/ReturnReceiptFormatter.java` | `89a45d9` |
| Developer 1 V-02 | SRP | Warranty held coverage rules and certificate layout. | WarrantyCertificateFormatter reads the public warranty queries and assembles the certificate. | `model/Warranty.java`, `ui/WarrantyCertificateFormatter.java` | `79b3de9` |
| Developer 1 V-03 | OCP | ProductService repeated registration logic per concrete family. | A common register(Product) path validates common data, uniqueness and polymorphic family details before adding/saving. Existing family methods delegate. | `model/Product.java`, `model/VideoGame.java`, `model/Console.java`, `validation/ProductValidator.java`, `service/ProductService.java` | `34e96d9` |
| Developer 1 V-04 | OCP | ProductRepository fixed the converter registry internally. | FileProductRepository accepts an ordered, defensively copied converter list; the compatibility constructor keeps both existing families. | `persistence/FileProductRepository.java` | `d5778d2` |
| Developer 1 V-05 | ISP | General Promotion required hasValidPercentage from every subtype. | General isValid delegates the existing direct-registration rule to each promotion type, matching Developer 2's updated validator. | `model/Promotion.java`, `model/PercentageDiscount.java`, `model/CategoryDiscount.java`, `model/BulkPurchaseDiscount.java` | `6e906d3` |
| Developer 1 V-06 | DIP | ProductService depended on the concrete text-file repository. | ProductRepository is a load/save interface and FileProductRepository supplies file storage; ProductService receives the abstraction. | `persistence/ProductRepository.java`, `persistence/FileProductRepository.java`, `service/ProductService.java` | `d5778d2` |

### Cross-member integration

- ProductService also implements the narrow ProductLookup interface required by
  Developer 2's WarrantyCatalog boundary. Existing findById semantics remain intact.
- The Technical Lead's Main already constructs FileProductRepository, and the return
  and warranty menus already call the new formatters. Those team files are downloaded,
  rather than changed as part of this Developer 1 delivery.
- Developer 2 owns PromotionValidator, which already calls Promotion.isValid. The
  missing domain method reported by the teammate is supplied in this delivery.
- ProductService.restoreStock remains the Technical Lead's method; its implementation
  was not changed by these corrections.

### Behavior preserved and reference adjustments

- Return receipts retain the exact labels, newline behavior, locale-dependent money,
  first matching charged price, unmatched fallback, cached total and warranty refund.
  Calculation still uses price*(1-ratio), while the displayed item amount uses
  price-price*ratio. The formatter does not recalculate the stored total.
- Warranty certificates retain their literal surrounding quotes, English labels,
  platform line separators, cost policy and two-decimal formatting.
- Registration still checks common product fields before duplicate IDs, and duplicate
  IDs before family details. The existing Spanish fields/messages and trim semantics
  remain intact. Constructors/loading stay permissive. The new register(Product) API
  reports a typed InvalidDataException for a null product.
- The ZIP combined category/minimum-quantity rules into isValid. This was adjusted:
  direct promotion registration keeps its existing percentage-only rule, including NaN
  behavior, while additional factory checks retain their original paths. Public concrete
  category/quantity queries remain available. This avoids changing accepted inputs or
  returning a percentage error for an unrelated category/quantity condition.
- File storage retains field order, split/trim behavior, converter precedence, default
  encoding, line separators, contextual exceptions and causes. Known memory-before-save
  and cached-refund defects from Requirement 7 are preserved rather than silently fixed.

### Validation evidence

- Actual integrated project at 011922c plus this delivery: Maven test BUILD SUCCESS,
  **127 tests, zero failures, errors or skips**. This includes the previous 87 cases
  and 40 cases covering registration contracts, extension points and exact formatting.
- Maven clean compile: BUILD SUCCESS on the complete project, including Main.
- scripts/verify-grasp.sh compared the current classes against a freshly compiled
  d2d7245 reference using separate fresh data fixtures: all **11 console/error/data
  files matched byte for byte**. The script name is historical; it exercises the menu
  behavior retained by this SOLID correction as well.
- Source and evidence backups are outside the repository under
  GameZone-Unicesar-local-backups/r8-phase2-20261026. Runtime data were not modified.
- The first incoming phase2 snapshot at 4506057 had 14 compilation errors. After these
  changes and downloading Developer 2's warranty integration at 011922c, the successful
  results above supersede that checkpoint. No passing partial build is presented as a
  complete integration result.

Publication, final consolidated IDs, peer approval and the team's remaining shared
documentation are workflow steps; this entry does not claim that the entire requirement
or the team's consolidated audit is finished.
