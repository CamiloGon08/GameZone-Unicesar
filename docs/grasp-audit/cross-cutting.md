# Exceptions and validation: Developer 1 GRASP evidence

Reviewed on 2026-10-25 against bf35aa2875cd9cc0818ad8cef8089d9a29f84fca.
The current Java sources match that reference and include the team's R6 integration.
Scope: both GameZoneException constructors/getErrorCode, all four subclass constructors,
the three validators' private constructors and every public method, plus PersonValidator's
private requireText helper. Service/repository/UI use is inspected only at the boundary.
The [individual report](developer1-audit.md) is the canonical audit and finding register.

Developer 1's source commits are 40836e5, 5870ca2, df0f3b7, 5a97f68, 0891d82 and
86a8842. These classes are integrated R6 code, not unmerged local additions.

| Pattern | Verdict | Concrete evidence / justification |
| --- | --- | --- |
| Information Expert | Correct for supplied data and failure context | ProductValidator:20-63, PersonValidator:21-67 and SaleValidator:23-51 inspect caller-supplied data; the caller selects/loads it. PersistenceException:9-11 retains the supplied cause. |
| Creator | Correct for contextual failure objects | Validators select and construct field/resource/business exceptions using the failed check's context; no exception factory is required. |
| Controller | Not applicable | Stateless checks and error objects receive no system events or workflow/menu responsibility. |
| Low Coupling | Correct at the inspected boundary | Uses supplied values, model queries, typed exceptions and List, without services, repositories, UI or hidden IO. |
| High Cohesion | Correct | Checks are grouped by product/person/sale responsibilities; each error category carries its context without recovery/logging. |
| Polymorphism | Not applicable to varying operations here | Static validators have no algorithm variants; exception subclasses override no behavior. Common typed handling is available, but inheritance alone is not treated as GRASP dispatch. |
| Pure Fabrication | Correct for dedicated validation helpers | Non-domain helper classes separate checking from workflow coordination and file access. |
| Indirection | Correct for service-to-validation delegation | ProductService:61/89/162/165, PersonService:48/105/183 and SaleService:97/100 delegate checks through dedicated validation APIs. |
| Protected Variations | Correct at the exposed typed-failure boundary | GameZoneException:4-23 provides a common unchecked root and String errorCode; PersistenceException preserves its cause. Universal consumer adoption is not claimed. |

No new local finding is introduced. Developer 2's V-32 proposes extending PersonValidator
for seller/name/phone checks. Coordinate any rule transfer and new methods with its
consumer owner, preserving field requirements, exact ID equality, caller-provided list
scope and current messages. Do not make seller validation require a customer email.

Developer 2 V-29/V-30 concern generic errors in extended service/repository consumers;
V-31 proposes ReturnValidator/WarrantyValidator. Their package location alone does not
assign those new classes to Developer 1. Consolidation determines the final responsibilities.

Preserve the existing exception categories/codes, Spanish messages and cause (including
call sites supplying null), positive finite prices, nonnegative stock, positive requested
quantity, and the different invalid-input/business-rule/not-found classifications. Keep the
required person email/phone checks and sale null-versus-empty distinctions. These helpers
do not mutate model/list state, load data or impose additional normalization/validation.

The existing recorded ValidatorTest baseline reports 20 cases and zero failures/errors/skips;
its report was inspected without rerunning it for this documentation change. No Java source,
data, runtime-test result, new validator/interface or approved correction is introduced by
this block. The individual report now covers all six assigned code-audit blocks; publication,
assigned guiding answers, review and team consolidation remain separate completion steps.
