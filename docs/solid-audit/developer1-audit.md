# SOLID Audit - Developer 1

**Audited commit (develop):** `d2d7245`
**Author:** Jesus David Dominguez Theran (Developer 1, GitHub `jdaviddominguez`)
**Phase:** Requirement 8, Phase 1 - Audit
**Progress:** 1 of five principle sections recorded; remaining sections follow in separate commits.

Evidence and line numbers refer to the audited commit of develop. The leader's
`r8-jesus.zip` supplies candidate findings to verify against that commit. The audit is
prepared as separate documentation blocks for the author to review, commit and publish.
Finding IDs are local to this report (for example, Developer 1 V-01); the Technical Lead
assigns consolidated IDs. Proposed corrections are Phase 2 work, not changes made here.

## Scope

| Module (requirement) | Class | Methods covered by this audit |
|---|---|---|
| Products (Workshop 1), GRASP (Req. 7) | `Product` | constructor, getters/setters, `getDescription`, `adjustStock`, `canAdjustStock`, `requiresBasicWarranty`, `allowsExtendedWarranty`, `matchesCategory` |
| Products (Workshop 1), GRASP (Req. 7) | `VideoGame`, `Console` | constructors, getters/setters, `getDescription`, overridden family queries |
| Products (Workshop 1), GRASP (Req. 7) | `ProductRepository` | constructor, `saveAll`, `loadAll`, `parseLine`, `toLine`, `toProduct` |
| GRASP (Req. 7) | `ProductLineConverter`, `VideoGameLineConverter`, `ConsoleLineConverter` | `getTypeKey`, `supports`, `toLine`, `toProduct` |
| Products (Workshop 1), Exceptions (Req. 6) | `ProductService` | constructor, `registerVideoGame`, `registerConsole`, `listProducts`, `findById`, `hasSufficientStock`, `updateStock`, `requireUniqueId`, `requireProductId`, `requireNotBlank`; `restoreStock` belongs to the Technical Lead's audit |
| Accessories (Req. 1), GRASP (Req. 7) | `Accessory`, `Controller`, `Cable`, `Memory` | constructors, getters/setters, `getDescription`, `addCompatibleConsole`, `getCompatibleConsoleIds`, `isCompatibleWith`, `matchesCategory`, `getTypeName` |
| Promotions (Req. 2), GRASP (Req. 7) | `Promotion`, `PercentageDiscount`, `CategoryDiscount`, `BulkPurchaseDiscount` | constructors, getters/setters, `isActive`, `calculateDiscount`, `hasValidPercentage`, `isValidPercentage`, `hasTargetCategory`, `hasValidMinimumQuantity` |
| Returns (Req. 3), GRASP (Req. 7) | `Return` | constructors, getters/setters, `belongsTo`, `calculateRefundAmount`, `unitPriceOf`, `generateReturnReceipt` |
| Returns (Req. 3), GRASP (Req. 7) | `Sale` | `canBeReturned` only; other Sale responsibilities belong to the Technical Lead's audit |
| Warranties (Req. 4), GRASP (Req. 7) | `Warranty`, `BasicWarranty`, `ExtendedWarranty` | constructors, `getDurationInMonths`, `getWarrantyType`, `getTypeCode`, `matches`, `matchesIgnoreCase`, `getAdditionalCost`, `chargedPrice`, `isActive`, `generateWarrantyCertificate` |
| Exceptions (Req. 6) | `GameZoneException`, `InvalidDataException`, `BusinessRuleException`, `ResourceNotFoundException`, `PersistenceException` | constructors and `getErrorCode` where declared |
| Validation (Req. 6), GRASP (Req. 7) | `ProductValidator`, `SaleValidator`, `PersonValidator` | constructors, all public static checks, and PersonValidator's private `requireText` / `requireNotBlank` helpers |

This distribution follows Requirement 8 section 4, TEAM.md and the repository history.
The creation commits for `AccessoryValidator` (`4fecd01`), `PromotionValidator`
(`7a9078c`), `ReturnValidator` (`ad88ecb`) and `WarrantyValidator` (`0033171`)
identify Developer 2 as their author. Those classes are reviewed in Developer 2's report.
Consumer references in those classes and the menus may be cited as boundary evidence;
their implementations remain under the corresponding member's audit responsibility.

## Verification at audit start

- GRASP changes are integrated in develop through merge `d2d7245` (PR #61).
- `mvn clean test` on this actual project revision compiled successfully and passed
  87 cases, with zero failures, errors or skipped cases.
- The team's `verify-grasp.sh` scenario against reference `8382d85` matched all
  11 console/error/data files byte for byte using fresh external data fixtures.
- The audit-start verification changes no Java source, menu text or project data file.

## Summary of verdicts

| Principle | Verdict | Findings |
|---|---|---|
| Single Responsibility (SRP) | Violation | V-01, V-02 |
| Open/Closed (OCP) | Pending | - |
| Liskov Substitution (LSP) | Pending | - |
| Interface Segregation (ISP) | Pending | - |
| Dependency Inversion (DIP) | Pending | - |

## 1. Single Responsibility Principle

**Verdict:** Violation (V-01 and V-02).

The stock checks in `Product.adjustStock` / `canAdjustStock` (lines 108-124), the
accessory compatibility queries and the promotion calculations concern their own domain
state. `Sale.canBeReturned` (line 335) owns the explicit-date return-window rule, rather
than the whole return workflow. Each product converter owns one family's record format;
the repository owns file access and contextual persistence failures. `ProductService`
coordinates registration and stock use cases without printing menus or opening files.

The five exception classes carry typed failure information and, where relevant, causes.
`ProductValidator`, `SaleValidator` and `PersonValidator`, including its private helpers,
perform checks without loading repositories or changing inventory. The seller alias
delegates to the same validation sequence. These are cohesive responsibilities.

`VideoGame`, `Console`, `Controller`, `Cable` and `Memory` also implement the established
`Product.getDescription` domain-summary contract. Their strings describe family attributes.
That is a presentation boundary to keep consistent with the team, but a returned string
alone does not establish an independent responsibility. The two findings below concern
complete customer documents with separately changing layout and business policy.

### V-01 - Return calculation and receipt layout share a model class

| Field | Content |
|---|---|
| Principle | Single Responsibility |
| Module | Returns (Req. 3), GRASP (Req. 7) |
| Evidence | `model/Return.java`:129-140 calculates and stores the refund. Lines 162-189 build the receipt, including `===== Recibo de Devolucion =====`, product labels and formatted money. Lines 163-165 repeat the discount-ratio calculation from lines 130-132. |
| Explanation | Refund policy and customer-document layout have separate reasons to change, but both require editing `Return`. The duplicated ratio also couples a printed breakdown to a second copy of the rule. |
| Consequence | A layout change touches a business model; a later policy change can update the stored refund without updating the receipt breakdown. |
| Proposed solution | Move document assembly to a UI-side `ReturnReceiptFormatter` reading public, non-mutating domain queries. Keep charged-price lookup and refund rules in the domain; expose any additional read queries needed without moving file or UI knowledge into `Return`. |
| Other member's code | Yes. The Technical Lead's `ui/ReturnMenu.java`:106,120,149,164 calls `generateReturnReceipt`; coordinate formatter wiring and ownership during consolidation. |

**Behavior to preserve:** The calculation uses `price * (1 - ratio)` whereas the receipt
uses `price - price * ratio`; replacing one expression with the other can change floating
point results. Preserve both observable calculations, the first matching charged price and
fallback, the existing cached total printed by the receipt, warranty refund, labels, default
locale, two-decimal formatting and newline behavior. Extracting the layout must not silently
repair the shared-list/cached-refund defect already recorded in Requirement 7.

### V-02 - Warranty coverage and certificate layout share the base class

| Field | Content |
|---|---|
| Principle | Single Responsibility |
| Module | Warranties (Req. 4) |
| Evidence | `model/Warranty.java`:25-30 establishes dates; duration, cost and matching queries implement coverage information. Lines 102-106 assemble `Warranty Certificate`, type, product, dates and additional cost through `String.format`. |
| Explanation | Coverage rules and the certificate's customer-facing layout are independent reasons to change the warranty base class. |
| Consequence | Changing labels or document layout requires editing the shared domain base class and reviewing its consumers, even when coverage rules are unchanged. This does not imply that every consumer must always be recompiled. |
| Proposed solution | Introduce a UI-side `WarrantyCertificateFormatter.format(Warranty)` using existing public getters; leave dates, duration, matching and cost in the hierarchy. |
| Other member's code | Yes. The Technical Lead's `ui/WarrantyMenu.java`:70,81,94,107 calls `generateWarrantyCertificate`; coordinate those callers and the new formatter's ownership. |

Preserve the current literal surrounding quotation marks, English labels, `%n` line
separators, default locale and `%.2f` formatting. `BasicWarranty` and `ExtendedWarranty`
continue to supply their existing duration, type token and additional-cost policies.
