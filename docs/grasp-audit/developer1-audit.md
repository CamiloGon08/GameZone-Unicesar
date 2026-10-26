# GRASP Audit - Developer 1

**Audited commit (develop):** `bf35aa2875cd9cc0818ad8cef8089d9a29f84fca`
**Author:** Jesus David Dominguez Theran (Developer 1, `jdaviddominguez`)
**Product review date:** 2026-10-25
**Accessory review date:** 2026-10-25
**Promotion review date:** 2026-10-25

## Status

The first three incremental blocks cover products, the accessory domain hierarchy and
the promotion domain hierarchy. Returns, warranties, exceptions and validations are pending.
The reference matches the team's existing audit templates. The Java sources were rechecked
after synchronizing with develop at `06fd7c6` and are unchanged relative to the audited commit.
R6 service/repository and console integration is now present; the earlier pre-integration
product, accessory and promotion drafts are superseded by these reviews.

Finding IDs are local to this individual report until the lead consolidates the findings.
No correction is implemented by this document. Proposed changes must preserve current
inputs, outputs, messages, exception categories and persisted record formats.

## Scope

| Module (requirement) | Class | Methods audited / current progress |
|---|---|---|
| Products (Workshop 1) | Product | Constructor; getDescription declaration; all ID/title/price/stock getters and setters; adjustStock |
| Products (Workshop 1) | VideoGame | Constructor; getDescription; platform, genre and ageRating getters/setters |
| Products (Workshop 1) | Console | Constructor; getDescription; brand, model and generation getters/setters |
| Products (Workshop 1) | ProductRepository | Constructor, saveAll, loadAll, toLine, toProduct; R6 parseLine is a collaboration boundary |
| Products (Workshop 1) | ProductService | Constructor, registerVideoGame, registerConsole, listProducts, findById, hasSufficientStock, updateStock; R6 validation helpers are a collaboration boundary; restoreStock excluded |
| Accessories (Req. 1) | Accessory | Constructor; addCompatibleConsole; getCompatibleConsoleIds; isCompatibleWith |
| Accessories (Req. 1) | Controller | Constructor; getDescription; connectionType getter/setter |
| Accessories (Req. 1) | Cable | Constructor; getDescription; lengthInMeters and connectorType getters/setters |
| Accessories (Req. 1) | Memory | Constructor; getDescription; capacityInGb and memoryType getters/setters |
| Promotions (Req. 2) | Promotion | Constructor; ID/name/startDate/endDate getters and setters; isActive; calculateDiscount declaration |
| Promotions (Req. 2) | PercentageDiscount | Constructor; percentage getter/setter; calculateDiscount |
| Promotions (Req. 2) | CategoryDiscount | Constructor; percentage and targetCategory getters/setters; calculateDiscount |
| Promotions (Req. 2) | BulkPurchaseDiscount | Constructor; minimumQuantity and percentage getters/setters; calculateDiscount |
| Returns (Req. 3) | Return, Sale.canBeReturned | Pending |
| Warranties (Req. 4) | Warranty, BasicWarranty, ExtendedWarranty | Pending |
| Exceptions and validations (Req. 6) | exceptions and validation packages | Pending |

Ownership follows TEAM.md and history, not a file-level author tag alone. Original product
work is recorded in 634a85b, 50827ee, 174ec1d, 2e9dc6a, d5c0216 and 2b3ae2d.
The R6 changes in ProductRepository (7741ab5) and ProductService (1f165cc) belong to
Developer 2 and are considered only at their boundary with this original code.
restoreStock is outside Developer 1's scope; the lead's report covers it, while its original
implementation is attributed to EstefaniaMarquez in 70ee2b3. Final team ownership is for consolidation.

The original accessory domain commits are d620e9f (Accessory), b241149 (Controller),
091e3bb (Cable) and ab6426d (Memory), authored by Developer 1. AccessoryService and
AccessoryRepository belong to Developer 2 and are considered only as consumers at the
domain boundary, not added to this report's owned scope.

The original promotion commits are d1f9844 (Promotion), 68a2765 (PercentageDiscount),
aab57ab (CategoryDiscount), dae0e5e (BulkPurchaseDiscount) and abcf6e4 (ACCESSORY
category support), authored by Developer 1. PromotionService and PromotionRepository
belong to Developer 2. TEAM.md also lists a targeted Sale promotion edit, but the current
promotion fields/accessors and setTotal were implemented by the lead in ac40006 and are
explicitly covered in the lead's report. Their collaboration boundary is recorded below.

## Summary of verdicts - products, accessories and promotions

| Pattern | Products | Accessory domain | Promotion domain | Local violations |
|---|---|---|---|---|
| Information Expert | Correct in the inspected product responsibilities | Correct for compatibility and subtype data | Correct for activity dates and calculation data; invariant proposal remains a team boundary | - |
| Creator | Correct | Correct for the contained compatibility list; external use-case creation not applicable | Not applicable to external object creation | - |
| Controller | Correct for ProductService; not applicable to product entities | Not applicable to domain entities | Not applicable to domain policies | - |
| Low Coupling | Correct at the inspected layer boundary | Correct at the domain boundary | Violation at category-to-family dependencies | V-02 (promotions) |
| High Cohesion | Correct for product persistence and coordination | Correct for shared compatibility and hardware attributes | Correct for shared activity data and individual discount policies | - |
| Polymorphism | Violation in record conversion; correct for descriptions | Correct for descriptions | Correct for policy overrides; violation in category selection | V-01 (products), V-02 (promotions) |
| Pure Fabrication | Correct | Not applicable within these domain entities | Not applicable within these domain policies | - |
| Indirection | Correct | Not applicable within these domain entities | Not applicable within the owned hierarchy | - |
| Protected Variations | Violation at the persisted-family variation point | Correct for the shared compatibility and description contracts | Correct for discount-policy callers; violation at category selection | V-01 (products), V-02 (promotions) |

These are not final verdicts for all Developer 1 modules; the summary will be extended
as each remaining module is reviewed.
Accessory verdicts concern the responsibilities declared in these four classes. The inherited
stock invariant and consumer-side proposals remain team boundaries, as recorded below.
V-01 and V-02 are two local findings; their related pattern references do not increase that count.

## 1. Information Expert

**Verdict for products: Correct.**

Product owns stock and changes it through adjustStock (Product.java:107-109).
ProductService.hasSufficientStock (136-145) coordinates lookup and queries that state.
VideoGame.getDescription (40-43) and Console.getDescription (41-44) use their own data.
The data-holder responsibility is therefore located with the object that has the information.

This does not establish that every path enforces the nonnegative-stock invariant.
Developer 2's V-09 proposes changing that boundary; it must be discussed in consolidation
rather than counted here as an already agreed change.

**Verdict for the accessory domain: Correct for compatibility and subtype data.**

Accessory owns compatibleConsoleIds (Accessory.java:8), avoids duplicate entries in
addCompatibleConsole (16-19), answers isCompatibleWith from that list (27-28), and returns
a copy from getCompatibleConsoleIds (22-23). Callers do not need to inspect its private list
to answer the compatibility question. Each concrete getDescription uses its own hardware
attributes: Controller.java:16-18, Cable.java:19-21 and Memory.java:18-20.

**Verdict for promotions: Correct for activity dates and calculation data.**

Promotion owns startDate/endDate (Promotion.java:9-10) and answers isActive from that
state with inclusive limits (68-69). PercentageDiscount uses its percentage and Sale's
total query (PercentageDiscount.java:36-37); BulkPurchaseDiscount uses its threshold,
percentage and Sale queries (BulkPurchaseDiscount.java:50-54). CategoryDiscount owns
the target and rate and computes the matching sum (CategoryDiscount.java:56-67); its
knowledge of product families is the separate V-02 boundary below.

This verdict does not claim constructors/setters enforce all date, rate or quantity
invariants. Developer 2's V-18 already proposes changing that responsibility; preserve its
original ID and agree on the invariant/validation boundary before implementing a correction.

## 2. Creator

**Verdict for products: Correct.**

ProductService.registerVideoGame (59-70) and registerConsole (87-98) have the required
initialization data and retain the objects they create (67-69 and 95-97).
ProductRepository.toProduct (154-175) has the persisted fields needed for reconstruction.
Creating an object during loading is appropriate; V-01 concerns how variant selection is structured.

**Verdict for the accessory domain: Correct for the contained collection; external
use-case creation is not applicable in these entities.**

Accessory creates and contains its compatibility list (Accessory.java:11-13).
The concrete constructors initialize their own attributes and delegate common product
initialization through super (Controller.java:9-11, Cable.java:10-14, Memory.java:9-13).
Choosing and creating accessories for a registration use case belongs to the consuming
service, outside this block's owned scope; no additional factory is required here.

**Verdict for promotions: Not applicable to external object creation.**

The four constructors initialize their own data and delegate shared initialization through
super (Promotion.java:20-24, PercentageDiscount.java:19-21, CategoryDiscount.java:22-26,
BulkPurchaseDiscount.java:21-25). None creates or contains Sale/Product aggregates.
Promotion registration and reconstruction occur in Developer 2's service/repository;
this domain hierarchy is not responsible for selecting or creating those external objects.

## 3. Controller

**Verdict for products: Correct for ProductService; not applicable to product entities.**

ProductService coordinates registration and inventory operations without reading console input.
Registration delegates common checks to ProductValidator, constructs an item, updates its list
and delegates storage (59-98). updateStock (159-170) coordinates identification, checking,
mutation and persistence. Product, VideoGame and Console do not receive system events.

**Verdict for the accessory domain: Not applicable.**

These four classes represent products and receive no system events or console input.
Controller (Controller.java:4) represents a physical game controller; its name does not
make it a GRASP application controller. Moving menu coordination into it would mix
responsibilities rather than satisfy this pattern.

**Verdict for promotions: Not applicable.**

isActive and calculateDiscount answer domain questions. These classes neither receive
system events nor read console input, coordinate registration or persist promotions.
No application controller is needed inside a discount policy.

## 4. Low Coupling

**Verdict for products: Correct at the inspected layer boundary.**

ProductService receives ProductRepository through its constructor (36-42) and contains no
file-opening operations. Product and its two inspected subtypes import neither persistence
nor service/UI classes. Constructor injection provides an existing collaboration boundary;
it is not presented as proof of dependency inversion. V-01 separately records the repository's
central knowledge of concrete persisted families.

**Verdict for the accessory domain: Correct at the domain boundary.**

Accessory extends Product and uses only List/ArrayList (Accessory.java:3-8).
Controller, Cable and Memory extend Accessory and use the inherited product API for
their descriptions. None imports services, repositories or UI classes. The compatibility
getter returns a copy (Accessory.java:22-23), keeping callers from mutating the stored list.

**Verdict for promotions: Violation at the category-to-family dependency, V-02.**

The hierarchy has no repository/service/UI dependency, and PercentageDiscount and
BulkPurchaseDiscount query Sale through its public API. CategoryDiscount, however,
also names VideoGame, Console and Accessory to decide eligibility (59-64). A new
independently categorized family requires changing an existing discount implementation.
V-02 records that coupling and its extension consequence once, under Polymorphism.

## 5. High Cohesion

**Verdict for products: Correct for the current responsibilities.**

Product, VideoGame and Console hold product state and product descriptions.
ProductService coordinates inventory use cases. ProductRepository is focused on product
persistence; IO and record translation both serve that technical responsibility.
The demonstrable extension problem in its two type-selection chains is recorded once under
Polymorphism as V-01, rather than forcing a separate cohesion finding for the same code.
A converter may be useful in the correction design without making its absence a second violation.

**Verdict for the accessory domain: Correct.**

Accessory concentrates the common compatibility responsibility (Accessory.java:8-28).
Controller holds connectionType (6); Cable holds lengthInMeters and connectorType (6-7);
Memory holds capacityInGb and memoryType (6-7). Their constructors, accessors and
descriptions use those attributes. No file persistence, registration workflow or menu
coordination is mixed into these entities.

**Verdict for promotions: Correct.**

Promotion holds shared identity, validity dates and the calculation contract. Each subclass
holds one discount policy's parameters and implements its formula. No persistence,
registration validation workflow or console coordination is added to these classes.
The category extension problem is V-02, not a second cohesion finding for the same code.

## 6. Polymorphism

**Verdict for products: Violation in record conversion; correct for descriptions.**

Product.getDescription (38) is implemented by VideoGame (40) and Console (41), allowing a
caller to obtain a subtype-specific description through the Product contract.

**Verdict for the accessory domain: Correct for descriptions.**

Controller.getDescription (16-18), Cable.getDescription (19-21) and
Memory.getDescription (18-20) override Product.getDescription (Product.java:38).
Each subtype supplies its own behavior through the inherited contract; these four domain
classes contain no subtype-selection chain. Developer 2's V-10/V-11 concern selection in
the external service/repository, not a second finding in this owned implementation.

ProductRepository.toLine (125-145) instead selects encoders with instanceof VideoGame and
instanceof Console; toProduct (154-175) selects constructors with string comparisons.
A new persisted family requires editing both existing methods.

### V-01 - Centralized product-family record conversion

| Field | Content |
|---|---|
| Pattern | Polymorphism, related to Protected Variations |
| Module | Products (Workshop 1): ProductRepository |
| Evidence | toLine:125-145, branches at 126/134; toProduct:154-175, branches at 166/170 |
| Code fragment | `if (product instanceof VideoGame)`; `if (product instanceof Console)`; `if (type.equals("VIDEOGAME"))`; `if (type.equals("CONSOLE"))` |
| Explanation | The repository decides each family's conversion behavior instead of delegating that behavior through a stable conversion contract |
| Consequence | Adding a persisted product family requires modifying both current read/write selection paths; one side can be missed |
| Proposed solution | Introduce persistence-side family converters behind a common contract and a small dispatch/factory boundary; keep existing field order and byte representation |
| Pattern applied | Polymorphism plus Protected Variations, using a technical pure fabrication rather than file-format methods on Product |
| Other member's code | Coordinate with Developer 2: preserve the R6 parseLine boundary and PersistenceException behavior added in the same repository |
| Responsible original code | Developer 1; final correction assignment remains subject to consolidation |
| Status | Proposed finding, not an approved correction or a new implementation |

Moving the same growing type switch unchanged into another class would isolate its location
but would not, by itself, resolve the proposed polymorphism finding. The correction design
must address both read and write dispatch without adding file access to model classes.

**Verdict for promotions: Correct for policy overrides; violation in category selection.**

Promotion.calculateDiscount (72) is overridden by PercentageDiscount (36-37),
CategoryDiscount (56-67) and BulkPurchaseDiscount (50-54). Existing consumers invoke
the contract without selecting a formula by concrete promotion type
(PromotionService.java:211-213; SaleService.java:106-108).

Inside CategoryDiscount, eligibility is still selected with a growing family-type chain.
The existing abcf6e4 commit demonstrates the extension consequence: adding ACCESSORY
support required a new branch in this method. A new subtype of an existing family already
matches through inheritance; the finding concerns a new independently categorized family,
not every subclass addition.

### V-02 - Product-category selection inside a discount policy

| Field | Content |
|---|---|
| Pattern | Polymorphism, related to Low Coupling and Protected Variations |
| Module | Promotions (Req. 2): CategoryDiscount; Product family collaboration boundary |
| Evidence | CategoryDiscount.calculateDiscount:56-67; family checks at 59/61/63; historical extension in abcf6e4 |
| Code fragment | `"VIDEOGAME".equals(targetCategory) && product instanceof VideoGame`; corresponding CONSOLE/Console and ACCESSORY/Accessory branches |
| Explanation | The discount policy identifies each product family itself rather than querying a stable domain category contract |
| Consequence | Supporting a new independently categorized family requires editing the existing discount's selection logic; category knowledge is tied to Java family types |
| Proposed solution | Introduce a domain category-matching query on Product, implemented at VideoGame, Console and Accessory family roots; have CategoryDiscount query that contract and retain the existing sum/rate calculation |
| Pattern applied | Polymorphism plus Protected Variations, reducing direct family dependencies in the discount policy |
| Other member's code | These model classes belong to Developer 1. Coordinate the contract with the lead's proposed SaleItem boundary (lead V-09) and Developer 2's service/category validation before assigning the final correction |
| Responsible original code | Developer 1; final correction assignment remains subject to consolidation |
| Status | Proposed finding; no method, interface, category enum or behavior change is implemented |

Keep the current exact category strings and case sensitivity, all Accessory subtypes
matching ACCESSORY, and zero contribution from null/unmatched product entries or an
unknown/null target. A default false category match for unclassified Product families is
one possible compatibility rule. Moving the same type chain into a helper would not, by
itself, establish polymorphic behavior. Final method naming and design require consolidation.

## 7. Pure Fabrication

**Verdict for products: Correct.**

ProductRepository keeps technical file persistence outside Product and its subclasses.
ProductValidator separately holds common validation rules in the R6 layer.
The possible converter contract for V-01 is another technical fabrication; it is not an
existing class in this reference and is not counted as a separate finding.

**Verdict for the accessory domain: Not applicable within these entities.**

Accessory, Controller, Cable and Memory represent domain concepts. They do not implement
file handling or another technical responsibility that needs a fabricated collaborator.
The existing external AccessoryRepository is outside this block's owned scope. No helper
class is proposed solely to make this pattern appear in the model hierarchy.

**Verdict for promotions: Not applicable within these domain policies.**

Promotion and its three variants represent domain discount policies. They do not perform
technical file handling or another responsibility requiring a fabricated infrastructure
class. The external PromotionRepository and Developer 2's proposed PromotionValidator
are separate scope; V-02 does not require adding persistence or a technical mapper here.

## 8. Indirection

**Verdict for products: Correct.**

ProductService delegates load/save through ProductRepository (constructor:41,
registration:69/97, stock update:169). It does not call FileReader/FileWriter directly.
The lead's proposed InventoryService concerns collaboration between product families;
this report records that boundary below without auditing another member's implementation.

**Verdict for the accessory domain: Not applicable within these entities.**

Compatibility operations use the contained list directly (Accessory.java:16-28), and
subtype descriptions use the inherited product accessors. No direct infrastructure
interaction or service-to-service coordination appears here that would justify an
additional mediator. Consumer-side coordination remains outside the owned model scope.

**Verdict for promotions: Not applicable within the owned hierarchy.**

Calculations query the supplied Sale and its Product data directly. No service-to-service
or infrastructure interaction here justifies an additional mediator. Consumer calls through
Promotion's abstract contract are recorded under Polymorphism and Protected Variations;
that contract alone is not used to assert a separate indirection responsibility.

## 9. Protected Variations

**Verdict for products: Violation at the persisted-family variation point, V-01.**

The abstract description contract already protects product-description callers.
The two record conversion paths still know every currently stored concrete family
(ProductRepository.java:125-175). V-01 proposes stable conversion collaborators to protect
those paths from subtype additions. It is one finding with related patterns, not a duplicate.

**Verdict for the accessory domain: Correct for the shared contracts.**

Accessory provides one compatibility API to all three variants (Accessory.java:16-28),
while Product.getDescription provides the stable description contract implemented by
each subtype. Adding a description implementation does not require editing those shared
compatibility methods. These local contracts do not establish that every external
consumer is protected against new types; Developer 2's proposals at that boundary are
listed below and remain subject to consolidation.

**Verdict for promotions: Correct for discount-policy callers; violation in category
selection, V-02.**

Promotion.calculateDiscount allows an existing consumer to invoke different formulas
through one contract. It does not protect CategoryDiscount from new independently
categorized Product families: calculateDiscount:59-64 still names their Java types.
V-02 proposes a domain category query at that variation point. Service registration and
repository conversion are Developer 2's separate findings, not duplicate local findings.

---

## Findings reported to the Technical Lead

- **ProductRepository / R6 boundary:** V-01 affects Developer 1's original conversion policy.
  Preserve Developer 2's try-with-resources, malformed-line context (parseLine:107-115)
  and typed read/write failures (saveAll:51-62; loadAll:72-95).
- **ProductService / R6 boundary:** validator calls and private validation helpers added
  in 1f165cc belong to Developer 2's R6 contribution. This block reviews the original
  inventory coordination, without reassigning those checks to Developer 1.
- **Already reported team boundaries:** the lead's V-07 mentions Product/Console warranty
  capability queries, and V-08 mentions ProductService inventory routing; Developer 2's
  V-09 mentions the Product stock invariant. Keep their original report IDs and consolidate
  each shared issue once before assigning any part of a correction.
- **Static functional observation, outside GRASP correction:** registration and updateStock
  mutate the service's objects before saveAll (ProductService:67-69, 95-97, 168-169).
  A failed save can leave memory changed. This has not been failure-injection tested and
  must not be silently corrected while preserving existing behavior.
- **Accessory model / consumer boundary:** Developer 2's V-09 proposes enforcing the
  inherited stock invariant in Product; V-10 proposes an Accessory.getTypeName contract
  for service filtering. Preserve those report IDs and agree on ownership before changing
  the domain API. V-11 concerns record conversion in Developer 2's AccessoryRepository;
  persistence formatting should remain outside the accessory entities.
- **Controller model / connection-type boundary:** Developer 2's V-14 proposes a
  ConnectionType enum and identifies Controller as affected code. This is a proposal,
  not an agreed model change. Any later design must preserve accepted strings, public
  callers, descriptions and persisted records, or explicitly resolve the behavior change
  during consolidation. No enum or new model method is introduced in this audit.
- **Accessory behavior preserved:** compatibility currently uses exact String equality,
  preserves insertion order, avoids duplicate IDs and returns a copied list. Direct model
  methods do not validate blank/null IDs; consuming service/repository helpers filter them.
  This observation does not justify silently changing validation or normalization during
  the audit. No additional local GRASP violation is asserted for these four classes.
- **Promotion category boundary:** local V-02 belongs to Developer 1's CategoryDiscount
  and Product families. Developer 2's V-15 concerns PromotionRepository conversion and
  V-19 concerns service registration; do not merge these different variation points into
  one implementation merely because they all mention promotion types.
- **Promotion invariants / Sale boundary:** Developer 2's V-18 proposes constructor
  invariants; the lead's V-01 proposes changing Sale total management and mentions
  PercentageDiscount/BulkPurchaseDiscount as affected callers. The lead's V-09 proposes
  SaleItem and affects CategoryDiscount's product iteration. Retain the original IDs and
  coordinate those contracts before assigning any correction. In particular, replacing
  getTotal with getSubtotal requires verifying the new Sale contract and all call sites;
  it is not applied as an unconditional change in this audit.
- **Promotion behavior preserved:** isActive uses the supplied date and includes both
  endpoints (Promotion:68-69). PercentageDiscount uses sale.getTotal; BulkPurchaseDiscount
  compares the number of list entries with minimumQuantity using >= and then uses that
  same total. CategoryDiscount sums the current prices of matching entries. The formulas
  use existing double arithmetic with no new rounding, and calculateDiscount does not
  itself check activity dates. Adding invariant checks, normalizing category strings,
  changing quantities or replacing prices with sale snapshots would require coordinated
  decisions and behavior verification, not an undocumented audit edit.
- Shared guiding answers, consolidated findings, diagrams and team verification retain
  their assigned authors/coordination. No shared file is modified by this individual report.
