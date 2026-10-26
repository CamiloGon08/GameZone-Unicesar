# SOLID Audit - Developer 1

**Audited commit (develop):** `d2d7245`
**Author:** Jesus David Dominguez Theran (Developer 1, GitHub `jdaviddominguez`)
**Phase:** Requirement 8, Phase 1 - Audit
**Progress:** 3 of five principle sections recorded; remaining sections follow in separate commits.

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
| Open/Closed (OCP) | Violation | V-03, V-04 |
| Liskov Substitution (LSP) | Correct for the existing substitution points | No confirmed violation |
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

## 2. Open/Closed Principle

**Verdict:** Violation (V-03 and V-04).

Promotion calculations already vary through `Promotion.calculateDiscount`; selection can
use the base contract. Product warranty/category queries and `Accessory.getTypeName`
allow family-specific behavior without adding type checks at their consumers. Warranty
duration, cost and storage type code also vary polymorphically. The converter dispatch
introduced in C-10 is an extension point, but its construction remains closed internally.

### V-03 - Product registration is enumerated in the service

| Field | Content |
|---|---|
| Principle | Open/Closed |
| Module | Products (Workshop 1), Exceptions (Req. 6) |
| Evidence | `service/ProductService.java`:59-70 (`registerVideoGame`) and 87-98 (`registerConsole`) each validate common input, check uniqueness, validate family fields, construct a concrete family, append it and call `saveAll`. |
| Explanation | The generic product service also defines a separate creation path for each concrete family. Supporting another family requires modifying the service and repeating the common sequence. |
| Consequence | Family extensions repeatedly modify working registration code, and duplicated checks, persistence or error ordering can diverge. |
| Proposed solution | Separate family construction/validation behind a factory or domain hook and introduce a shared product-registration path for common checks, uniqueness and saving. Keep the current public registration methods as compatibility adapters while coordinating the extension contract. |
| Other member's code | The Technical Lead's `ui/ProductMenu.java`:47,71 uses the current methods. Keeping those adapters avoids an immediate menu change; removing signatures requires coordination. New helper ownership is agreed in consolidation. |

Preserve common-field validation before duplicate-ID rejection, and duplicate-ID rejection
before family-field errors. Keep the existing Spanish fields/messages, identifier matching,
save behavior and permissive loading/constructors. Simply moving all family checks into
constructors, or validating a new object before uniqueness, would change current behavior;
that is not a behavior-preserving solution to this finding.

### V-04 - Repository hard-codes the converter registry

| Field | Content |
|---|---|
| Principle | Open/Closed |
| Module | Products (Workshop 1), GRASP (Req. 7) |
| Evidence | `persistence/ProductRepository.java`:31-32 initializes `List.of(new VideoGameLineConverter(), new ConsoleLineConverter())`. `toLine` and `toProduct` (lines 125-163) already dispatch through the converter contract. |
| Explanation | Another family can implement `ProductLineConverter`, but its converter cannot be supplied without editing the repository's internal registry. |
| Consequence | A family extension still changes the file repository despite the existing polymorphic conversion boundary. |
| Proposed solution | Accept a defensively copied ordered converter collection in an additional constructor. Keep a compatibility constructor with the current two converters; custom composition can supply new families without modifying repository dispatch. Coordinate this with V-06. |
| Other member's code | The Technical Lead's `Main` needs changes when explicitly supplying the registry. Existing callers can retain the compatibility constructor. |

Preserve converter order and first-match dispatch, eight-field records, common parsing
before type dispatch, `split(",")`/trim behavior, case-sensitive type tokens, platform
line separators, and the exact contextual `PersistenceException` messages and causes.
Moving registration to composition does not justify changing malformed-record handling.

## 3. Liskov Substitution Principle

**Verdict:** Correct for the existing hierarchies and documented usage; no confirmed
substitution violation was found in this scope.

- `VideoGame`, `Console`, `Controller`, `Cable` and `Memory` retain the inherited
  `adjustStock` / `canAdjustStock` behavior. Their category and warranty-query overrides
  answer family policy without stronger input requirements. Their descriptions fulfill
  the base string-returning contract. Accessory compatibility remains an accessory API.
- `PercentageDiscount`, `CategoryDiscount` and `BulkPurchaseDiscount` implement
  `calculateDiscount(Sale)` using the supplied sale and their configured policy, without
  changing the sale. For a non-null sale and appropriate promotion data they remain usable
  through `Promotion` by `PromotionService.findBestPromotionFor`.
- This is **not** a claim that discounts are always nonnegative or never throw. Promotion
  constructors/setters are permissive: negative or NaN percentages and null sale inputs
  have legacy behavior. The abstract calculation does not promise universal nonnegative
  results for such inputs. These cases do not by themselves prove a subtype strengthened
  the base contract, and this audit does not add validation to change them.
- `BasicWarranty.getDurationInMonths`:21-23 returns 6; the extended implementation:23-25
  returns 12. The base constructor's virtual call (line 30) works with these constant
  implementations. A future override depending on uninitialized subclass fields would
  be a construction hazard to consider, rather than a present failure of either subtype.
  Type labels, storage codes, costs and matching retain their distinct contracts.
- Both `ProductLineConverter` implementations accept the products selected by `supports`
  and reconstruct their own family for the selected type token. The repository checks
  that contract before the implementation's family cast; the cast alone is not an LSP
  violation. Invalid records retain the repository's contextual failure behavior.
- The four concrete exceptions extend `GameZoneException` without removing its error-code,
  message or cause behavior. Different codes are intentional failure categories, not a
  broken base contract.

The static validators, `ProductService`, the concrete repository, `Return` and the isolated
`Sale.canBeReturned` method declare no overriding relationship of their own; LSP is not
applicable to those portions as standalone types. Their use of the hierarchies above was
reviewed. The 87-case baseline supports the exercised paths, including prices, discounts,
stock, return dates, validation and conversion; it is not a proof over all possible inputs.
