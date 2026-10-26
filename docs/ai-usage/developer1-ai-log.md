# Developer 1 AI Usage Log

## 2026-10-18 — Requirement 6

Developer: Jesus David Dominguez Theran. GitHub: `jdaviddominguez`.
Role: Developer 1, as recorded in TEAM.md.

### Project background and assigned contribution

This requirement extends an existing project that I have been working on in NetBeans.
My previous commits cover the product module and domain classes for accessories,
promotions, returns and warranties. Requirement 6 builds on that existing work.

My assigned implementation consists of the GameZoneException hierarchy and the
ProductValidator, PersonValidator and SaleValidator classes. Service/repository
integration belongs to Developer 2; menu integration belongs to the technical lead.

### How I used AI assistance

I used OpenAI Codex as a programming and review assistant to interpret the requirements,
check the existing repository against them, discuss the implementation and verify its behavior.
The assistance included code and test drafts for the exceptions and validators,
JavaDoc and error-message wording, explanations of design choices, and local build/test checks.
It also included a disposable console verification script and a Maven output ignore correction.

The implementation retains the existing com.mycompany.gamezone package structure,
uses the required unchecked exception root, preserves persistence failure causes,
and keeps validators independent of file access and repositories. Integration notes
identify existing model differences such as Person.getiD(), numeric phone values,
seller fields and the current uniqueness rules.

### My decisions and repository work

I requested that work remain within my Developer 1 responsibilities and continue in
small, explainable blocks. I asked for the Git commands and personally executed each
commit and push. I also clarified that shared analysis and diagram documents should
remain drafts for the team rather than become part of my individual delivery.
The assistant explained each block and checked that my local and remote commits matched.

### Verification results

The 20 validation test cases passed. The console scenario exercised the ten original
main operations and read-only paths through the four extended submenus using temporary
data. A differential check matched the saved baseline's console output and seven data files.
The new validators still require the team's assigned service/repository/menu integration.

### Commits I created and pushed on feature/exception-handling

| Commit | Contribution |
| --- | --- |
| 40836e5 | Abstract GameZoneException root |
| 5870ca2 | ResourceNotFoundException and BusinessRuleException |
| df0f3b7 | InvalidDataException and PersistenceException |
| 5a97f68 | ProductValidator |
| 0891d82 | PersonValidator |
| 86a8842 | SaleValidator |
| c750f74 | ValidatorTest |
| 4fa7aed | Disposable console verification scenario |
| 3fe65f5 | Maven output ignore rule |
| 84e1797 | Initial personal AI usage entry |
| bd48a61 | Clarified personal contribution and AI support |

### Integration status verified on 2026-10-24

My PR #50 was approved by CamiloGon08 and merged into develop at
f84b2fcf2ddf740f05fd2690709354504a612b6f. My individual R6 contribution is integrated.
The team's assigned service/repository/menu work is still pending in that snapshot.

## Requirement 7 — GRASP

### Phase 1 — Audit

#### 2026-10-24 — Product audit preparation

I began reviewing my own product module against the nine GRASP patterns, using develop
f84b2fcf2ddf740f05fd2690709354504a612b6f as a recorded reference.
The inspected scope includes Product, VideoGame, Console, ProductRepository and
ProductService, excluding restoreStock.

I used AI support to check method references and ownership, discuss the verdicts, and
prepare English report drafts. The main proposed finding is the combination of file lifecycle
and record-conversion policy in ProductRepository. Correct responsibility assignments
are recorded as well; a conditional alone is not classified as a violation.

The product report is a working draft. I will recheck affected evidence after my teammates
integrate their R6 work before closing the audit. Other module blocks remain pending
in this incremental review. No team consolidation or approved correction is claimed.
The local feature/grasp-audit branch was prepared without publishing it.

Verification of the current baseline passed 20 validation cases and the existing console
smoke scenario. This review changes documentation only. I will personally review, commit
and push each agreed block; no audit commit or push has been made at this point.


#### 2026-10-25 — Product review updated

After the team's R6 integration was merged, I resumed the product review against the shared
audit reference bf35aa2875cd9cc0818ad8cef8089d9a29f84fca. The current audit branch has the
same Java source as that reference.

I use AI responsibly to check possible errors, discuss suggestions, compare GRASP criteria
and verify evidence against my existing code. This support also includes suggestions for
the English wording of the report draft. I will review each proposed verdict and retain
responsibility for my Git commits and the explanation of my contribution.

The product block identifies the repository's read/write subtype branches as one proposed
Polymorphism finding, related to Protected Variations. Correct applications are documented
as well. The earlier tentative cohesion classification was revised after considering the
current code and the concrete consequence of adding another product family.

Verification of this updated baseline passed 20 validation tests and the existing console
smoke scenario. No product code, shared analysis answers or teammates' reports were modified.

PR #55 for Developer 2 was merged into develop at 06fd7c6 on 2026-10-25.
The local audit branch was fast-forwarded to that integrated reference. I then created and
pushed 87b85cd (docs: add product module GRASP audit), containing my individual report's
product block and products.md. That product block was verified on the remote branch.
No team consolidation or approved correction is claimed.

#### 2026-10-25 — Accessory model review

I continued with my original Accessory, Controller, Cable and Memory classes. AI assistance
helped check every declared method against the nine patterns, compare evidence and
prepare the English report wording. The report records correct applications and explains
patterns that do not apply to these domain entities, without requiring an artificial violation.

The review also identifies Developer 2's proposals that could affect my model contracts.
Those proposals need the lead's consolidation before any correction is assigned or applied.
No domain API, validation behavior or persistence format has been changed by this review.

The synchronization check confirmed that PR #55 is already included in my audit branch.
My local develop branch was also fast-forwarded to 06fd7c6, and work resumed on
feature/grasp-audit. I then created and pushed 6d6b6a6 (docs: add accessory hierarchy
GRASP audit), containing the individual report update and accessories.md. The commit
was verified on the remote branch. No additional runtime tests were run for these
documentation-only changes.

#### 2026-10-25 — Promotion model review

I continued with my Promotion, PercentageDiscount, CategoryDiscount and
BulkPurchaseDiscount classes. I used AI support to check method references and history,
compare the nine GRASP criteria, discuss improvement proposals and prepare English
audit wording for my review.

The proposed local finding V-02 concerns product-family selection inside CategoryDiscount.
My earlier ACCESSORY support commit provides a concrete example of that extension
point. Correct assignments are also documented, including the common calculation
contract, ownership of activity dates and the separate discount formulas.

The report preserves current behavior as the reference and records the model boundaries
affected by my teammates' proposals. No Java implementation, shared team analysis or
other member's report was changed. I then created and pushed 5411ef6 (docs: add promotion
module GRASP audit), containing the individual report update and promotions.md. The
commit was verified on the remote branch. No additional runtime tests were run for this
documentation change, and no GRASP correction is claimed before the team's consolidation.

#### 2026-10-25 — Return model review

I continued with my Return class and assigned Sale.canBeReturned method. I used AI
support to verify method references and authorship, compare the nine GRASP criteria,
discuss possible inconsistencies and prepare the English audit wording for my review.
The report identifies my proportional-refund contribution and preserves Developer 2's
later warranty-refund integration at their collaboration boundary.

The review adds evidence from canBeReturned to Developer 2's existing V-24: the model
query and registration service independently implement the return window and differ for
future-dated sales. This is recorded as a shared issue for consolidation, without creating
a duplicate local finding or applying an unapproved behavior change. The report also
records correct responsibility assignments and the current refund/receipt behavior.

I then created and pushed 3c77b74 (docs: add return module GRASP audit), containing the
individual report update and returns.md. The commit was verified on the remote branch.
No Java implementation, team analysis or other member's report was changed. No additional
runtime tests were run for this documentation change; the date discrepancy is a static
code observation. GRASP corrections remain pending the team's consolidation.

#### 2026-10-25 — Warranty model review

I continued with my Warranty, BasicWarranty and ExtendedWarranty classes. I used AI
support to verify the methods and my original commits, compare the nine GRASP criteria,
discuss improvement proposals and prepare the English report wording for my review.
The audit records the existing polymorphic duration/type/cost behavior and the scope of
patterns that do not require an additional model responsibility.

The report also records team proposals that affect the contracts: the lead's historical-price
finding and Developer 2's type-code and matching-query proposals. It distinguishes display
labels from persisted type tokens and checks the current case-sensitive/case-insensitive
matching differences in consumers before recommending any coordinated correction.
No duplicate local finding or implementation is introduced.

I then created and pushed 646c56e (docs: add warranty hierarchy GRASP audit), containing
the individual report update and warranties.md. The commit was verified on the remote
branch. No Java code, data, shared analysis or other member's report was changed. No
additional runtime tests were run for this documentation change. GRASP corrections still
require the team's consolidation.

#### 2026-10-25 — Exception and validator review

I completed the remaining code-audit draft for my R6 exception hierarchy and three
validators. I used AI support to verify constructors, public methods and the private person
validation helper, compare the nine GRASP criteria, discuss the consumer boundaries and
prepare the English audit wording for my review. The existing 20-case ValidatorTest report
was inspected as baseline evidence; the tests were not rerun for this documentation change.

The report documents contextual exception creation, stateless validation, service delegation
and the common typed-failure contract. It also records Developer 2's proposal involving
PersonValidator and distinguishes consumer-side correction proposals from my existing R6
responsibilities. No duplicate local finding, new validator or unapproved rule change is added.

At this preparation checkpoint, all six assigned code-audit blocks are covered in the
individual draft. The exception/validator block and this log are ready for my review and
publication as separate commits. The five audit commits listed above have been verified
on the remote; the final commits and PR status will be checked after I execute the Git
commands. No Java source, data or other member's report was changed. Shared guiding
answers, PR review and the team's consolidation remain separate completion steps;
this entry does not claim that the whole requirement or its correction phase is finished.

I clarified that the lead currently prioritizes code and the individual audit, with the
five shared guiding answers to be handled later. Those answers remain pending for team
coordination and do not block my current individual audit delivery. No shared answer is
claimed as assigned or completed in this entry.

### Phase 2 — Not started

Corrections will follow the team's merged consolidated GRASP audit and assigned finding IDs.
Requirement 8's formal audit will use develop after those GRASP corrections are integrated.
