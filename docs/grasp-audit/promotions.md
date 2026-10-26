# Promotions: Developer 1 GRASP evidence

Reviewed on 2026-10-25 against bf35aa2875cd9cc0818ad8cef8089d9a29f84fca.
The current Java sources match that reference after the team's R6 integration.
Scope: constructors, all declared getters/setters, Promotion.isActive and the calculation
contract/overrides in Promotion, PercentageDiscount, CategoryDiscount and
BulkPurchaseDiscount. Service/repository and Sale integration were checked only at
their collaboration boundary; those implementations belong to the other team members.
The [individual report](developer1-audit.md) is the canonical audit and finding register.

| Pattern | Verdict | Concrete evidence / justification |
| --- | --- | --- |
| Information Expert | Correct for dates and calculation data | Promotion:68-69 queries its own dates; discount methods use their own parameters and Sale/Product queries. Constructor invariants are Developer 2's V-18 boundary. |
| Creator | Not applicable to external object creation | Constructors initialize their own state; they do not create Sale/Product aggregates or coordinate promotion registration/reconstruction. |
| Controller | Not applicable | Domain policies calculate values rather than receive system events or read console input. |
| Low Coupling | Violation V-02 at category selection | CategoryDiscount:59-64 names VideoGame, Console and Accessory; an independently categorized family requires editing that policy. |
| High Cohesion | Correct | Promotion holds shared identity/dates; each concrete subtype holds its own discount parameters and formula, without persistence or menu coordination. |
| Polymorphism | Correct for policy overrides; violation V-02 at category selection | All three variants implement Promotion.calculateDiscount; CategoryDiscount still selects product families with a type chain (56-67). |
| Pure Fabrication | Not applicable within these policies | These are domain policies, with no technical file-handling responsibility requiring a fabricated helper. |
| Indirection | Not applicable within the owned hierarchy | No additional mediator is required for these calculations; invoking an abstract contract is recorded under Polymorphism/Protected Variations. |
| Protected Variations | Correct for policy callers; violation V-02 at category selection | Promotion:72 gives clients a common calculation contract; CategoryDiscount still changes when another independent product category is supported. |

One new local finding is proposed: **V-02**, related to three patterns. V-01 already
identifies product record conversion in the canonical report and is not reused here.
The real extension in abcf6e4 added the ACCESSORY branch to CategoryDiscount.
Subtypes of an existing family already match through inheritance; not every new subtype
requires editing the method.

Proposed direction: a stable domain category-matching query on Product with family-root
implementations, then CategoryDiscount delegates matching and keeps its sum/rate formula.
Preserve exact category tokens/case, all Accessory subtypes matching ACCESSORY and no
matching-total contribution from unknown/null targets or null/unmatched product entries.
Moving the same type chain to a helper alone does not resolve the polymorphism finding.

Keep inclusive activity dates, current getTotal-based percentage calculations, the bulk
threshold based on list size, current product-price reads and existing double arithmetic.
Activity checks remain the caller's responsibility. No new validation, normalization,
rounding, quantity representation or persisted format is introduced by this review.

Developer 2's V-15/V-19 concern repository conversion and service registration; V-18
proposes model invariants. The lead's V-01 affects getTotal consumers, while lead V-09
proposes SaleItem and affects category iteration. Coordinate those existing findings
without duplicating their IDs or implementing them before consolidation.

This block changes documentation only. No Java source/data changes or new runtime test
results are claimed. Final team IDs, contracts and correction ownership require consolidation.
