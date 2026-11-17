# SOLID Analysis - Guiding Questions

Requirement 8. Answers discussed by the team and written by the Technical Lead, using the three individual audits (`docs/solid-audit/`) and the consolidated audit.

## 1. Criteria for a verdict and minimum evidence

- **Violation:** the code breaks the principle in a concrete place that can be pointed to in `develop` (`d2d7245`): a class with two independent reasons to change (SRP), a `switch`/`instanceof`/one-method-per-type that must be edited to add a variant (OCP), a subtype that strengthens the base contract (LSP), a client forced to depend on operations it does not use (ISP), or policy code that depends on a concrete low-level class (DIP).
- **Correct:** the principle has a real application point and the code respects it; we name the class and method where it holds (for example `PersonService` depends on the `PersonRepository` interface).
- **Not applicable:** there is no application point in the audited code (for example LSP on a class with no hierarchy).

Minimum evidence accepted in the consolidation: class, method and exact lines in the audited commit, an explanation of why the principle is broken, a concrete consequence today or under a foreseeable change, a proposed solution that preserves behavior, and whether the fix touches another member's code. A finding without lines or without a consequence would have been returned to its author; none was.

## 2. Boundary findings

The clearest case is **V-16 (ISP)**. The Lead reported that `ReturnMenu` uses the whole `SaleService` only to find a sale (leader V-06). Developer 2 reported, from the service side, that `ReturnService`, `MonthlyBalanceReport` and `WarrantyService` depend on whole services and use one method each (developer 2 V-06). It was registered once and split:

- Technical Lead: created `SaleStore` (with V-18) and changed `ReturnMenu` to `SaleLookup`.
- Developer 1: `ProductService` implements the new `ProductLookup`.
- Developer 2: `ReturnService` uses `SaleLookup`, `MonthlyBalanceReport` uses `SaleStore`, `WarrantyCatalog` uses `ProductLookup`.

Other boundary cases: V-15 (Developer 1 created `Promotion.isValid()`, Developer 2 changed `PromotionValidator` to call it) and V-03/V-04 (Developer 1 created the receipt and certificate formatters; the Lead changed `ReturnMenu` and `WarrantyMenu`). All new repositories, catalogs and factories are built in `Main`, which belongs to the Lead.

## 3. Relation with the GRASP audit (Requirement 7)

- **SOLID violations in code created by the GRASP correction.** `InventoryService` was created in GRASP (C-27, Indirection) to route stock between products and accessories, but it routes with `instanceof` (V-08, OCP). `SaleCatalog` was created in GRASP (C-23, Pure Fabrication) but depends on the concrete `SaleRepository` (V-17, DIP). The menus split in GRASP still had the `switch` in `ConsoleMenu` (V-10, OCP).
- **GRASP corrections that solved a SOLID problem in passing.** The record mappers for persons, accessories and promotions (GRASP Pure Fabrication) separated the line format from file access, so those repositories were already correct for SRP; the same idea was then applied to sales, returns and warranties (V-02, V-07). `SaleLookup` (GRASP C-33) already gave `WarrantyService` a small abstraction for sales, which is correct for ISP and DIP and was reused for V-16. The product line converters (GRASP C-10) gave OCP for the file format; only their construction remained closed (V-12).

## 4. Preserving behavior

Strategy: `scripts/verify-grasp.sh` compiles a reference build (develop before Phase 2) and the current build, runs the same menu script on both with fresh copies of the data files, and compares byte for byte the console output, the error output and the seven data files (`products`, `persons`, `sales`, `accessories`, `promotions`, `returns`, `warranties`). The script covers every option of the main menu and its submenus, including invalid inputs. In addition, `mvn clean test` must pass.

Evidence: the result was recorded before the first correction and after the final integration in `docs/solid-refactor-log.md`: 11 of 11 files SAME and 127 tests passing (87 before Phase 2 plus 40 new tests from Developer 1).

## 5. Order of the corrections and coordination

1. Abstractions first, so the others can depend on them: the Lead's `SaleStore`/`SaleLookup` (V-16, V-18) and Developer 1's `ProductLookup` and `Promotion.isValid()` (V-15).
2. Then each member's own violations, one commit per violation, in their own files only. Lead, Developer 1 and Developer 2 work on disjoint files, so they never edit the same file.
3. The Lead's `Main` commit wires every new class.

Coordination in the shared branch `feature/solid-refactor`: each member pulls before starting, pushes after each commit, and the boundary parts follow the order in section 4 of the consolidated audit. The project only compiles when the three parts are in, so the integration was verified with `mvn clean test` and `verify-grasp.sh` before merging to `develop`.
