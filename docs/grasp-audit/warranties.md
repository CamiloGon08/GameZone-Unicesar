# Warranties: Developer 1 GRASP evidence

Reviewed on 2026-10-25 against bf35aa2875cd9cc0818ad8cef8089d9a29f84fca.
The current Java sources match that reference after the team's R6 integration.
Scope: every constructor, abstract declaration and declared public method in Warranty,
BasicWarranty and ExtendedWarranty. Service/repository and UI callers were inspected
only at their collaboration boundary; those implementations belong to the other members.
The [individual report](developer1-audit.md) is the canonical audit and finding register.

Developer 1's original commits are cb42205 (Warranty), e52533e (BasicWarranty),
9f11b04 (ExtendedWarranty) and 5303cde (English certificate/type wording).

| Pattern | Verdict | Concrete evidence / justification |
| --- | --- | --- |
| Information Expert | Correct for coverage dates and current cost formulas | Warranty:25-30, 68-69 uses its dates; BasicWarranty:33-34 returns zero cost; ExtendedWarranty:7, 35-36 applies its rate to the associated product price. |
| Creator | Not applicable to external domain-object creation | Constructors receive existing Product/Sale references; deriving an end-date value alone does not assign warranty selection/creation to the model. |
| Controller | Not applicable | The hierarchy receives no system events, reads no console input and coordinates no registration/cancellation services. |
| Low Coupling | Correct at the domain/infrastructure boundary | Uses Product/Sale and LocalDate without repository/service/UI imports; the separate live-price boundary remains open. |
| High Cohesion | Correct for the inspected warranty responsibility | Coverage state, policy queries and its returned certificate describe the same warranty; no IO or registration workflow is added. |
| Polymorphism | Correct for duration, display type and cost | Warranty:34-40 declares contracts overridden in BasicWarranty:21-34 and ExtendedWarranty:23-36; the constructor/certificate invoke them without a type chain. |
| Pure Fabrication | Not applicable within the owned hierarchy | These domain classes implement no file handling or technical coordination requiring a helper. |
| Indirection | Not applicable within the owned hierarchy | Abstract dispatch is recorded under Polymorphism/Protected Variations; no additional mediator responsibility is demonstrated. |
| Protected Variations | Correct for current behavior dispatch; shared price-history violation remains | Lead V-09 names ExtendedWarranty's live-price query; Developer 2 V-26/V-27 propose external type/matching contracts. |

No new local finding is introduced. The lead's V-09 affects ExtendedWarranty's monetary
basis, Developer 2's V-26 proposes getTypeCode/WarrantyFactory, and V-27 proposes
Warranty.matches. Keep those original report IDs and coordinate the correction assignment.

Current display labels are "Basic warranty" and "Extended warranty"; persisted tokens
are BASIC and EXTENDED. Reusing getWarrantyType as a stored type code would change
the record format. Matching behavior also differs among consumers: hasWarrantyOfType
uses equalsIgnoreCase (WarrantyService:169-170), while findByProductAndSale and
cancelWarranties use equals (228-229, 272-273). Do not silently change those semantics
when considering a common matching query.

Preserve calendar-month end dates, inclusive coverage endpoints, six months/zero cost
for BasicWarranty and twelve months/current price times 0.10 for ExtendedWarranty.
The base constructor calls the overridable duration method; both current overrides return
constants without reading subtype fields. Future construction designs must respect that
constraint. Historical-price changes require the lead's coordinated V-09 decision.

Keep certificate wording, literal quotation marks at both ends, platform %n newlines,
default-locale %.2f formatting and existing display labels. This review introduces no Java
source/data change, runtime test result, new validator, snapshot, rounding or persistence
format. Final contracts and corrections require team consolidation.
