# Developer 1 AI Usage log:

## 2026-10-18 — Requirement 6

- Tool: OpenAI Codex, used interactively in the existing NetBeans Maven workspace.
- User request: inspect and update the repository, identify jdaviddominguez's previous contributions, and help implement the requirements within the Developer 1 role.
- User constraints: the user creates every commit and push; review and upload one logical block at a time. Explicitly announce the transition before beginning requirement 7.
- Assistance: read the local handouts, checked Git history and TEAM.md, fetched origin, updated develop and created local feature/exception-handling.
- Generated contribution: abstract exception root, four concrete exceptions, three stateless validators, English JavaDoc, Spanish error details and 20 validation test cases.
- Decisions proposed: preserve com.mycompany.gamezone, use the specified unchecked root, retain original causes, and keep validators independent of persistence.
- Integration differences reviewed: actual Person.getiD(), long phone values, seller data, additional existing service checks and the person-uniqueness policy.
- Supporting verification: the assistant prepared a disposable console scenario and corrected the Maven output ignore rule.
- Results: the 20 validation cases passed. The console scenario exercised main operations 1-10 and read-only submenu paths; the differential check matched the saved baseline's output and seven files.
- Scope: existing service, repository and menu code remains unchanged in this contribution. Developer 2 and the technical lead integrate their assigned parts.
- Shared documents: exception-analysis.md and exception-class-diagram.md remain local team-review drafts; the user chose not to include them in this personal contribution.
- Incremental review: the assistant explained each block and provided commands; the user reported completing them. Individual understanding must still be demonstrated by the user.
- Version control: all commits and pushes below were executed by the user. The assistant checked that the branch's local and remote heads matched after each block.
- Pending: user review/commit of this log, PR creation, another member's review, team integration and merge. Requirement 7 has not started in the user's incremental workflow.
- The assistant has created no commits, pushes, PRs, approvals or merges.

### User-created commits verified on feature/exception-handling

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
