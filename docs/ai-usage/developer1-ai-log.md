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

### Phase 2 — Corrections

#### 2026-10-26 — Assignment review and C-11 preparation

I resumed the work using the leader's correcciones-jesus-r7.zip and the merged consolidated
audit. The assigned scope contains 13 corrections: C-11, C-26, C-16, C-12, C-19, C-21,
C-28, C-30, C-32, C-34, C-35, C-09 and C-10. The package's reference sources were kept
outside the project for comparison, rather than copied over the working code.

I used AI support to compare that assignment with current Git history, inspect the proposed
stock guard, discuss its behavior and prepare the implementation, focused test cases and
verification notes for my review. My audit PR #56 and the lead's consolidated audit are
merged. I updated develop and continued on feature/grasp-refactor at d3c9744. Old local
diagram/refactor-log drafts were preserved outside the project before synchronizing.

The downloaded integration branch failed compilation before my changes: it already
contained consumers of pending model APIs and UI calls to changed service signatures.
Those integration dependencies were recorded without modifying other members' classes.

The first prepared correction is my Product part of C-11: a nonmutating canAdjustStock
query and a guard before stock mutation. Its exact source change and focused tests were
checked in an isolated worktree based on 6012971, whose original Product matches the
current integration base. All 28 cases passed, and the team's menu comparison against
8382d85 matched all 11 console/error/data files. These are isolated verification results;
the current integrated branch is not claimed to compile or pass full non-regression yet.

I then created and pushed be2fd99 (refactor: guard product stock adjustments (C-11)). The
commit was verified on origin/feature/grasp-refactor. At that checkpoint, the other 12
assigned corrections remained pending. No completed phase, final PR or Requirement 8
implementation is claimed by this entry.

#### 2026-10-26 — C-26 warranty eligibility queries

I continued with the assigned Product/Console part of C-26. I used AI support to compare
the leader's proposal with the consolidated audit, review the default and console-specific
coverage rules, prepare the methods/JavaDoc and check the existing verification scenario.
The remote check confirmed C-11 was published and found no additional incoming changes.

Product now supplies false defaults for requiresBasicWarranty/allowsExtendedWarranty,
and Console overrides both as true. This exposes the eligibility decision through the
Product contract. Service and menu changes remain with the assigned team members.

The exact Product/Console sources were checked with C-11 in the isolated 6012971-based
validation worktree: 28 existing cases passed and all 11 console/error/data files matched
reference 8382d85. This does not claim that the latest integrated branch compiles or that
every pending consumer has been updated. Full integration verification remains pending.
I then created and pushed c4247ad (refactor: add product warranty eligibility queries
(C-26)). The commit was verified on the remote. At that checkpoint, the other 11 assigned
corrections remained pending.

#### 2026-10-26 — C-16 category matching

I continued with my C-16 category-selection correction. I used AI support to review the
existing family checks, compare the agreed contract with the code, prepare the implementation
and meaningful discount-calculation tests, and verify the behavior against the reference.
Another fetch/pull confirmed the shared branch was up to date and included develop.

Product exposes matchesCategory with a false default; VideoGame, Console and Accessory
implement the existing exact category labels. CategoryDiscount delegates matching through
that contract, retaining its sum/rate calculation and nonmatching null-product behavior.
The separate C-28 price-snapshot correction has not been applied in this block.

The exact C-16 source changes with C-11/C-26 were checked in the isolated 6012971-based
worktree. All 40 cases passed, including 12 category-policy cases, and the menu comparison
matched all 11 console/error/data files against 8382d85. Full verification of the latest
integration branch remains pending the other model APIs and team wiring. I then created
and pushed 5eeaac9 (refactor: delegate category matching to product families (C-16)),
verified on the remote. At that checkpoint, the other 10 corrections remained pending.
No service, UI or other member's implementation was changed by that block.

#### 2026-10-26 — C-12 accessory type names

I continued with my Accessory/Controller/Cable/Memory part of C-12. I used AI support
to compare the agreed contract and existing labels, prepare the methods/JavaDoc and
check compatibility with the current variants and reference verification scenario. Another
fetch/pull confirmed that the branch was up to date before editing.

Accessory now declares getTypeName; each concrete variant returns its existing label.
The change preserves descriptions, console compatibility and persistence representation.
Consumer filtering remains the assigned service owner's implementation.

The exact sources were validated with my previous blocks in the isolated 6012971-based
worktree: all 40 existing cases passed and all 11 console/error/data files matched 8382d85.
These results do not claim a passing build of the latest integration branch. I then created
and pushed 11383ee (refactor: expose accessory type names polymorphically (C-12)), verified
on the remote. At that checkpoint, the other nine assigned corrections remained pending.
Full integration verification and the phase's final PR are still pending.

#### 2026-10-26 — C-19 promotion validity queries

I continued with my Promotion/PercentageDiscount/CategoryDiscount/BulkPurchaseDiscount
part of C-19. I used AI support to compare the proposed contract with the original service,
review the numeric and text boundaries, prepare the query methods and English notes, and
check compatibility with existing records. Another fetch/pull confirmed that C-12 was
published and that the branch had no incoming changes.

Promotion now declares hasValidPercentage and shares the original percentage rejection
rule through a protected helper. The discount types implement that query; category and
bulk discounts also expose their assigned target/quantity checks. The original handling
of NaN is retained as part of behavior preservation. Constructors and setters still accept
existing stored values, and the discount calculations are unchanged by this block.
Consumer changes remain the assigned service owner's responsibility.

The exact sources were checked with my earlier blocks in the isolated 6012971-based
worktree: all 40 existing cases passed. An external compatibility check matched 55 query
results with the unchanged legacy service, checked loading seven invalid stored records
without rewriting the file, and confirmed that queries use updated field values. The menu
comparison matched all 11 console/error/data files against 8382d85. These results do not
claim a passing build of the latest integrated branch. I then created and pushed 8bf508a
(refactor: expose promotion validity queries (C-19)), verified on the remote. At that
checkpoint, eight other assigned corrections remained pending. Full integration verification
and the phase's final PR are still pending.

#### 2026-10-26 — C-21 discount subtotal queries

I continued with my PercentageDiscount/BulkPurchaseDiscount part of C-21. I used AI
support to compare the assignment with the merged Sale contract, discuss the effect of
promotion and warranty adjustments, prepare the two method changes and regression cases,
and check the result against the reference scenario. Another fetch/pull confirmed that
C-19 was published and that the branch had no incoming changes.

Both calculations now use Sale.getSubtotal, preserving percentage arithmetic and the
bulk quantity threshold. The query no longer depends on previous changes to the final
sale total. This uses the leader's already merged prerequisite without changing Sale,
service consumers or another member's implementation.

The exact sources and new regression cases were validated with my earlier blocks in the
isolated 6012971-based worktree. All 43 cases passed, including three checks for stable
discounts after promotion/warranty adjustments, nonmutation during repeated calculation,
and the bulk threshold with repeated product entries. The menu comparison matched all
11 console/error/data files against 8382d85. These are isolated results; full verification
of the latest integrated branch remains pending the remaining contracts and team wiring.
I then created and pushed 84106ba (refactor: calculate discounts from sale subtotal
(C-21)), verified on the remote. At that checkpoint, seven other assigned corrections
remained pending, along with full integration verification and the phase's final PR.

#### 2026-10-26 — C-28 charged sale item prices

I continued with my Return/ExtendedWarranty/CategoryDiscount part of C-28. I used AI
support to compare the assigned lookup rules with SaleItem, discuss compatibility and
persistence boundaries, prepare the implementation and regression cases, and verify the
result against the reference. Another fetch/pull confirmed that C-21 was published and
that the branch had no incoming changes.

Return and ExtendedWarranty now use the first charged item price matching the product
ID, retaining the assigned fallback for an unmatched product. CategoryDiscount uses each
matching SaleItem's own unit price and keeps the existing family contract. Receipt wording,
discount arithmetic and warranty refunds are preserved. The sales file format is unchanged:
the in-memory snapshots protect against later catalog mutations, while reloading still
rebuilds prices from the catalog as specified by the consolidated audit.

The exact sources and new regression cases were validated with my earlier blocks in the
isolated 6012971-based worktree. All 50 cases passed, including seven covering changed
catalog prices, refund/receipt and warranty/certificate stability, matching another instance
by ID, category family selection, repeated IDs and the agreed fallback. The menu comparison
matched all 11 console/error/data files against 8382d85. These results do not claim a passing
build of the latest integrated branch. I then created and pushed 70c46cb (refactor: use
charged sale item prices (C-28)), verified on the remote. At that checkpoint, six other
assigned corrections remained pending, along with full integration verification and the
phase's final PR.

#### 2026-10-26 — C-30 return customer ownership

I continued with my Return.belongsTo part of C-30. I used AI support to compare the
assigned query with the previous service predicate, confirm existing equality behavior,
prepare the method/JavaDoc and verification notes, and review the isolated build results.
Another fetch/pull confirmed that C-28 was published and that no incoming changes existed.

The query delegates to the original sale customer's equals comparison, preserving the
same identity behavior because Customer/Person have no equality override. The separate
equality defect remains outside the agreed correction. The service consumer and the
leader's Sale.containsProduct implementation retain their assigned owners.

The exact Return source compiled with my earlier blocks in the isolated 6012971-based
worktree. All 50 existing tests passed and all 11 console/error/data files matched the
8382d85 reference. The predicate was compared statically with the old service and the
current consumer call; these results do not claim full integration coverage of the shared
service or a passing build of the latest branch. I then created and pushed 073374c
(refactor: expose return customer ownership query (C-30)), verified on the remote. At that
checkpoint, five other assigned corrections remained pending, along with full integration
verification and the phase's final PR.

#### 2026-10-26 — C-32 explicit return-window evaluation

I continued with my Sale.canBeReturned part of C-32. I used AI support to compare the
model query with the old service rule and current validator call, discuss calendar boundaries,
prepare the method/JavaDoc and focused regression cases, and review the verification results.
Another fetch/pull confirmed that C-30 was published and the branch had no incoming changes.

The query now receives an explicit evaluation date and accepts from 0 through 30 calendar
days after the sale, inclusive. A date before the sale is rejected, matching the existing
service rule. My changes in Sale are limited to this assigned method and its required
ChronoUnit import and named constant. Other members' Sale and consumer code is unchanged.

The exact source/test was checked with my earlier blocks in the isolated 6012971-based
worktree. All 59 cases passed, including nine fixed-date checks covering future sales,
same-day eligibility, days 29/30/31, year boundaries and leap-year February. The menu
comparison matched all 11 console/error/data files against 8382d85. These isolated results
do not establish a passing build of the latest shared branch: the remaining model contracts
and team integration dependencies, including Sale.containsProduct, are still pending.
I then created and pushed 68e1f47 (refactor: evaluate sale return window with explicit date
(C-32)), verified on the remote. At that checkpoint, four other assigned corrections
remained pending, along with full integration verification and the phase's final PR.

#### 2026-10-26 — C-34 warranty type codes

I continued with my Warranty/BasicWarranty/ExtendedWarranty part of C-34. I used AI
support to compare the assigned codes with stored tokens and current consumer references,
prepare the methods/JavaDoc, and review the compatibility evidence. Another fetch/pull
confirmed that C-32 was published and that the branch had no incoming changes.

Warranty now declares getTypeCode, with BASIC and EXTENDED returned by the respective
variants. These storage codes remain separate from the existing display names used in
certificates. Durations, costs and consumer implementation retain their previous scope.

The exact three sources compiled with my earlier blocks in the isolated 6012971-based
worktree. All 59 existing cases passed and all 11 console/error/data files matched 8382d85.
The current WarrantyRepository and WarrantyFactory also compiled separately against the
tested model classes. These results do not claim runtime integration coverage or a passing
build of the latest shared branch. I then created and pushed be44d2e (refactor: expose
warranty type codes polymorphically (C-34)), verified on the remote. At that checkpoint,
three other assigned corrections remained pending, along with full integration verification
and the phase's final PR.

#### 2026-10-26 — C-35 warranty matching queries

I continued with my Warranty part of C-35. I used AI support to compare the old service's
case-sensitive and case-insensitive checks, review the current consumer calls, prepare the
methods/JavaDoc and verification notes, and check compilation compatibility. Another
fetch/pull confirmed that C-34 was published and the branch had no incoming changes.

Warranty now exposes matches for exact product/sale IDs and matchesIgnoreCase for both
IDs without case sensitivity. The distinction preserves the prior type-check, lookup and
cancellation behavior. Consumer implementation remains with the assigned service owner.

The exact source compiled with my earlier blocks in the isolated 6012971-based worktree.
All 59 existing cases passed and all 11 console/error/data files matched 8382d85. The
predicates were compared statically with the previous checks; the current warranty service
and its selected dependencies also compiled separately against the tested model classes.
These checks do not claim full runtime integration coverage or a passing build of the latest
shared branch. I then created and pushed 761b190 (refactor: expose warranty matching
queries (C-35)), verified on the remote. At that checkpoint, C-09 and C-10 remained pending,
along with full integration verification and the phase's final PR.

#### 2026-10-26 — C-09 seller, name and contact validation

I continued with my PersonValidator part of C-09. I used AI support to compare the
original service rules/messages with the assignment and current consumer references,
review validation order and boundary cases, prepare the methods and regression cases,
and check the verification evidence. Another fetch/pull confirmed that C-35 was published
and that the branch had no incoming changes.

The validator now exposes the assigned seller, name and contact-number checks while
preserving the service's order, exception codes and Spanish messages. Its new trim-based
seller helper stays separate from the existing R6 isBlank checks. The existing name regex
and positive-long rule retain their behavior. Because Seller.validate already calls
validateSeller, a thin adapter forwards that name to the assigned validateSellerData
method; another member's consumer class is unchanged.

The exact source/test compiled with my earlier blocks in the isolated 6012971-based
worktree. All 75 cases passed, including sixteen new cases for both entry points, first-error
precedence, Spanish messages/codes, long boundaries and existing text/regex behavior.
The menu comparison matched all 11 console/error/data files against 8382d85. These are
isolated results; current Person/Customer/Seller integration and a full-branch passing build
are still pending. I then created and pushed 8d4e06b (refactor: centralize seller name and
phone validation (C-09)), verified on the remote. At that checkpoint, C-10 remained as my
last assigned code correction, followed by integration verification, the remaining
documentation and the phase's final PR.

#### 2026-10-26 — C-10 product persistence converters and integration checkpoint

I continued with my ProductRepository part of C-10. I used AI support to compare the
assigned converter contract with the current repository, review field order and error
handling, prepare the persistence-side classes and regression cases, and inspect the build
evidence. Another fetch/pull confirmed that C-09 was published and that the branch had no
incoming changes.

ProductLineConverter now defines the common conversion contract; VideoGameLineConverter
and ConsoleLineConverter provide the existing family-specific record layout. The repository
delegates through a small dispatch while retaining its shared parsing, file access and
contextual exceptions. The original split/trim behavior, field ordering, newline behavior and
error messages are preserved, and the domain classes do not acquire file-format knowledge.

The exact source/test compiled with my earlier blocks in the isolated 6012971-based
worktree. All 87 cases passed, including twelve persistence cases for exact bytes, round-trip
attributes, blank/trimmed lines, malformed records/causes, unsupported products and I/O.
The menu comparison matched all 11 console/error/data files against 8382d85.

I also checked the actual shared branch with Maven test. It still fails compilation with
32 integration errors before tests can run. The remaining points include startup/menu
wiring, person textFormat implementations, stock API alignment, inventory signatures and
the lead's Sale.containsProduct. Those contracts need team coordination; the isolated test
result is not a passing full-branch build. The compiler output was preserved outside the
project and a local integration checklist records the evidence without changing other
members' code or adding unrelated fixes to this correction.

I then created and pushed 5629953 (refactor: delegate product persistence to family
converters (C-10)), verified on the remote. All thirteen assigned Developer 1 code blocks
are published as separate commits. Integration, publication of the remaining documentation,
full non-regression and the phase's final PR are still pending. This entry does not claim
that Requirement 7 is complete or that Requirement 8 has started.

#### 2026-10-26 — Individual code delivery and PR preparation

I checked that my thirteen correction commits are on origin/feature/grasp-refactor and
that the branch includes the current develop reference. I used AI support to verify the
published scope, organize the evidence and draft the PR title/description for my review.
No open PR for this branch into develop was found at this checkpoint.

The PR information distinguishes the 87 passing isolated test cases and eleven matching
reference files from the actual shared branch's 32 compilation errors. The assignment
requires a successful clean compilation before opening the PR, so that readiness check
and the remaining integration work are still pending. The shared guiding answers remain
deferred according to the team's decision. I continue to execute my own commits and
pushes, and will request another team member's review when the PR is ready.

## Requirement 8 - Phase 1 (Audit)

### 2026-10-26 - Repository update and audit scope

**Tool used:** OpenAI Codex.

**Reason for use:** Check the leader's r8-jesus.zip against the updated repository,
confirm my Developer 1 scope and prepare the English scope section for my review.

**Request:** "Mira lo que mandó el lider. OJO recuerda que debemos bajar todo lo nuevo
que haya en repositorio".

I received a leader-provided audit draft and six cumulative parts. I used AI support to
inspect the package, compare its scope with Requirement 8, TEAM.md and class-creation
history, check references and discuss statements that need qualification before publication.
The package's sample AI entry names Claude; my own assistance in this session is OpenAI
Codex, so I record the tool actually used instead of copying that attribution.

I updated the GRASP feature and develop branches, then continued on feature/solid-audit
at d2d7245, the same revision specified by the package. Ten old untracked SOLID draft
files were preserved and hash-verified outside the repository before preparing the new
report. The downloaded code includes the team's GRASP integration fixes.

Verification on the actual updated project passed mvn clean test with all 87 cases and
zero failures/errors/skips. The team's menu comparison against 8382d85 matched all 11
console/error/data files using external fixtures. The former 32-error integration result
is a historical checkpoint, superseded by these results on d2d7245.

The scope and executable-baseline block was subsequently published as c1e5eea after
integrating changes on the shared audit branch. That checkpoint preceded the detailed
principle sections below. No Phase 2 correction is claimed by this entry.

### 2026-10-26 - Complete individual SOLID review and prepare six documentation commits

**Tool used:** OpenAI Codex.

**Reason for use:** Review the leader's candidate findings against my code, discuss
behavior-preserving proposals and prepare the English report and publication commands.

**Request:** "Osea quiero que me des los seis commits pero que trabajes todo de una vez".

I asked for the whole review to be prepared together, with six separate commits that I
will execute: SRP, OCP, LSP, ISP, DIP with consolidation boundaries, and this personal log.
I used AI support to inspect source and consumer references, compare the draft with the
existing contracts, identify overbroad claims, and organize the report for my review.
The report now covers all five principles in my assigned Developer 1 scope.

The review retains six local candidate findings: receipt/certificate formatting in models
(V-01/V-02), family-specific product registration and a fixed converter registry
(V-03/V-04), the percentage-specific obligation on the general promotion contract
(V-05), and the concrete product-storage dependency (V-06). The ISP finding explicitly
distinguishes the current percentage-based subclasses from the future extension example.
LSP is recorded as correct for the existing substitution points; the draft's universal
claim of nonnegative discounts and no exceptions was removed.

Other suggestions were qualified against the actual code: preserve validation order and
messages, keep factory checks separate from direct promotion registration, preserve NaN
and permissive loading behavior, distinguish the two refund arithmetic expressions, and
acknowledge that the current repository can be subclassed for a fake. These are design
review constraints rather than new functional fixes. The report also records cross-member
coordination, including Developer 2's ProductLookup proposal, without changing their code.

A fresh fetch and fast-forward update downloaded the team's new audit documents through
f5d06e2. Production source, tests, scripts and data still match the verified d2d7245
baseline: 87 passing test cases and eleven matching scenario files. Because this delivery
changes documentation only, that evidence is reused without claiming a new test run.

AI support helped with error checking, alternatives and drafting; I remain responsible for
reviewing the deliverable and executing my commits and pushes. The six atomic patches and
Git commands are prepared locally, not published by the assistant. This entry does not
claim that I have already committed them, received PR approval, answered unassigned
guiding questions, or finished the team's consolidated audit or Phase 2.

## Requirement 8 - Phase 2 (Corrections)

### 2026-10-26 - Review the assigned reference and prepare the complete correction delivery

**Tool used:** OpenAI Codex.

**Reason for use:** Compare the leader's r8-fase2-jesus.zip with the updated team branch,
review the missing promotion contract, discuss behavior-preserving adaptations, and
prepare code, regression checks and publication commands for my review.

**Request:** "aplica todos los cambios y me das los commits todos de un solo por cuestión de tiempo".

I used AI support to inspect the assigned reference files, check their ownership and
consumer contracts, adapt the English JavaDoc and implementation, and prepare regression
tests and separate commit groups. The package is a leader-provided reference; its sample
entry names Claude, but the tool I used in this session is OpenAI Codex. I do not copy
unperformed verification or another tool's attribution into my personal record.

My correction covers Return and Warranty formatter extraction, generic product
registration with family validation, ProductLookup, the product repository interface and
injectable file converters, and Promotion.isValid. The latter matches the method already
called by Developer 2's PromotionValidator; I retain responsibility for the promotion
model classes while that validator remains her contribution.

The reference was checked against the individual audit's preservation constraints.
Category and bulk promotions retain the original direct-registration percentage rule;
the additional factory checks are not silently added to that entry point. Existing NaN
behavior, validation ordering/messages, permissive loading, charged-price fallback,
receipt arithmetic and cached totals were also considered. The new generic product API
adds a typed null-product input error without changing the existing menu methods.

I downloaded the team's updates through 011922c, including Developer 2's warranty
integration. The complete actual project then passed 127 tests with zero failures,
errors or skips, and a clean compilation succeeded. The menu scenario compared against
freshly compiled d2d7245 classes matched all eleven console/error/data files using
external fixtures. This is a passing full-project result, not an isolated substitute.

The work is prepared as seven commits: five code/test groups, the refactor log and this
personal entry. Promotions are first so the teammate's existing validator receives its
missing contract promptly. I execute my own commits and pushes; the assistant has not
published this delivery. The publication commands record the real code commit hashes in
the refactor log when those commits exist.

The individual audit PR #64 is merged. The consolidated SOLID audit is still absent from
develop at this checkpoint, so final consolidated IDs, coordination and the final Phase 2
PR/peer review remain team workflow items. This entry describes review assistance and
observed evidence without claiming that I have already committed, obtained approval or
completed every team deliverable.
