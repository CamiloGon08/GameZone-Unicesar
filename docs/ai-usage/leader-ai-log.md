# AI Usage Log — Technical Lead

This document records every use of AI tools during the development of GameZone Unicesar, following the template defined in the taller specification.

### Entry 1

**Date:** 2026-09-13
**Tool used:** Claude (web)

**Reason for use:**
Understand the exact Git workflow required by the taller and resolve a merge issue between feature branches.

**Problem faced:**
I was working on `feature/sale-module` and needed to bring the changes from `develop` into my branch, but I was not sure whether the workflow required updating `develop` locally first or merging directly from the remote. I also had doubts about whether keeping multiple feature branches open at the same time could produce conflicts when merging to `develop`.

**Prompt used:**
"Cómo hago para bajar los cambios de develop a mi rama feature/sale-module sin que se mezclen las otras ramas? Y si abro varios PR al tiempo, se genera conflicto?"

**Solution obtained and decision taken:**
The AI explained that each branch is independent and that the recommended flow is to update `develop` locally, switch back to the feature branch, and merge `develop` into it. It also clarified that multiple PRs can coexist but only the first one merges cleanly — the rest must be rebased after `develop` moves. I followed this workflow for the rest of the project and coordinated with my teammates to merge PRs one at a time.

### Entry 2

**Date:** 2026-09-17
**Tool used:** Claude (web)

**Reason for use:**
Translate the documentation files from Spanish to English, as required by the taller for analysis documents under `docs/`.

**Problem faced:**
The orienting questions in each requirement were written in Spanish and the answers had to be delivered in English. Some technical terms (like "delegar", "vigencia", "reembolso") did not have obvious direct translations in the context of the project.

**Prompt used:**
"Ayúdame a traducir las respuestas de este análisis al inglés manteniendo el lenguaje técnico y usando los nombres exactos de las clases."

**Solution obtained and decision taken:**
The AI helped produce consistent English translations for `accessory-analysis.md`, `promotion-analysis.md`, `return-analysis.md` and `warranty-analysis.md`, keeping the exact class and method names from the code. I reviewed each translation and adjusted the parts where the meaning did not match the intended design decision.

### Entry 3

**Date:** 2026-09-17
**Tool used:** Claude (web)

**Reason for use:**
Understand the existing code flow before implementing a new method in `SaleService` for the accessory module.

**Problem faced:**
Before modifying `SaleService.registerSale` to accept accessories, I needed to understand how the current stock update worked. I noticed it called `product.adjustStock(-1)` directly, but I was not sure whether that was persisting the change or whether I was supposed to delegate to a service instead.

**Prompt used:**
"Explícame cómo funciona el flujo actual de SaleService.registerSale y por qué el cambio de stock con adjustStock no se guarda."

**Solution obtained and decision taken:**
The AI explained that `adjustStock` only modifies the in-memory object and does not persist it to the file, and that the requirement asked for delegation to `ProductService` or `AccessoryService` depending on the item type. I refactored the method to split the loop with `instanceof Accessory` and call the appropriate service, then confirmed the change compiled and the file was written correctly.

### Entry 4

**Date:** 2026-09-17
**Tool used:** Claude (web)

**Reason for use:**
Diagnose a compilation error that appeared after applying a change to `Sale.java`.

**Problem faced:**
When I added the promotion attributes to `Sale` and then switched to `SaleService`, VS Code reported around 136 errors across multiple files, including errors in classes I had not modified. I was not sure whether the errors were caused by my change or by something else.

**Prompt used:**
"Cambié Sale y SaleService y me sale un montón de errores, ¿por qué?"

**Solution obtained and decision taken:**
The AI explained that changing the constructor of `SaleService` breaks every class that instantiates it, because `Main` and `ConsoleMenu` were still calling the old signature. I updated both files in the same commit so the branch would compile as a single functional unit, avoiding a broken state in the repository history.

### Entry 5

**Date:** 2026-09-18
**Tool used:** Claude (web)

**Reason for use:**
Understand how to break a circular dependency between `SaleService` and `WarrantyRepository`.

**Problem faced:**
`WarrantyRepository` requires a `SaleService` reference to reconstruct warranties from the file, and `SaleService` needs a `WarrantyService` to assign warranties during sale registration. Instantiating both in `Main` produced a circular construction that could not compile.

**Prompt used:**
"Cómo puedo romper una dependencia circular entre SaleService y WarrantyRepository si ambos se necesitan entre sí?"

**Solution obtained and decision taken:**
The AI suggested removing `WarrantyService` from the `SaleService` constructor and instead exposing a setter that `Main` calls after both objects are created. I applied that pattern, verified it preserved the layer rules, and confirmed the project compiled.

### Entry 6

**Date:** 2026-09-18
**Tool used:** Claude (web)

**Reason for use:**
Rewrite the project README to include the four extended modules.

**Problem faced:**
The original README only described the baseline system and used a package path that did not match the actual project (`com.gamezone` vs `com.mycompany.gamezone`). It also did not mention the accessory, promotion, return or warranty modules.

**Prompt used:**
"Ayúdame a reescribir el README en inglés para incluir los cuatro módulos extendidos, con los números de opción del menú y la ruta del paquete correctos."

**Solution obtained and decision taken:**
I rewrote the README with one section per module, the correct menu option numbers (11 through 14), the correct main class, and a project layout section that reflects the real structure. I committed it as a documentation-only change.

### Entry 7

**Date:** 2026-09-27
**Tool used:** Claude (Claude Code)
**Phase and branch:** Phase 3 (Warranties) — refactor/unified-sale-registration

**Reason for use:**
Reorganize SaleService.registerSale into the unified 8-step flow required by A3, and fix compilation errors introduced after merging fix/warranty-circular-dependency (A2) and fix/return-accessory-stock (A4) into the branch.

**Problem faced:**
registerSale mixed validation, promotion, warranty and inventory logic in one long method with no explicit step order, and Sale had no field to report the extended warranty cost separately on the receipt. After merging A2, three files broke: SaleRepository had duplicate imports and an undeclared variable, and ConsoleMenu called two WarrantyService methods with outdated signatures. Additionally, WarrantyService allowed a console to have only one warranty per sale, which silently blocked assigning an extended warranty to a console that already had the automatic basic one.

**Prompt used:**
"Ayúdame a reorganizar registerSale en los 8 pasos que pide el Requerimiento 5, revisando la firma real de SaleService, y a corregir los errores que aparecieron después de fusionar los cambios de mis compañeros."

**Solution obtained and decision taken:**
Split registerSale into five named private methods (validateItems, validateStock, applyBestPromotion, assignWarranties, updateInventory), added Sale.warrantyCost with its getter/setter, and rewrote generateReceipt to show subtotal, discount, warranty cost and final total. Fixed the duplicate imports and undefined variable in SaleRepository, and updated ConsoleMenu's calls to viewActiveWarranties(LocalDate) and viewWarrantiesExpiringSoon(int) to match the new signatures. Identified that WarrantyService's duplicate-warranty check did not distinguish warranty type, and reported it to Developer 2, who fixed it using a type-aware check (hasWarrantyOfType). All changes were accepted as proposed; no part was discarded.

**Commit related:**
refactor: split generateReceipt into header, products and totals in Sale
refactor: split registerSale into named steps for the unified sale flow
fix: remove duplicate imports and undefined variable in SaleRepository
fix: update ConsoleMenu warranty calls to match new WarrantyService signatures



### Entry 8

**Date:** 2026-10-24
**Tool used:** Claude (Claude Code)
**Phase and branch:** Requirement 6 (Validations and Exceptions) — feature/console-exception-handling

**Reason for use:**
Refactor ConsoleMenu so that each kind of failure shows its own Spanish message, and document the new exception layer in the README.

**Problem faced:**
ConsoleMenu had twelve identical catch (IllegalArgumentException) blocks that printed a generic "Error: ..." message. The new GameZoneException hierarchy and the validators were already merged, but the menu did not tell the four kinds of failure apart. Accessories, promotions, returns and warranties still throw IllegalArgumentException, so the old exception could not simply be removed.

**Prompt used:**
"Quisiera que me dieras el comando para bajar los cambios y me deje en la rama que debo trabajar, una vez hecho eso me pasas completo lo que debo cambiar en el R6."

**Solution obtained and decision taken:**
Added a private handleException method with one message per exception type and replaced the twelve catch blocks with catch (GameZoneException | IllegalArgumentException e). I kept IllegalArgumentException in the catch on purpose, as a transition until the four remaining modules are migrated. The first proposal included a long Javadoc on handleException; I shortened it to one line to match the rest of the class. I also asked for the complete class so I could paste it, and compiled it before committing. The README section and the class diagram were drafted with the assistant and reviewed by me before committing.

**Commit related:**
refactor: handle GameZone exceptions with differentiated messages in ConsoleMenu
docs: update README with exception handling and validation
docs: add exception class diagram
docs: log leader AI usage for requirement 6

## Requirement 7 — GRASP Audit and Correction

### Phase 1 — Audit

### Entry 9

**Date:** 2026-10-25
**Tool used:** Claude (Claude Code)
**Phase and branch:** Requirement 7, Phase 1 (Audit) — feature/grasp-audit

**Reason for use:**
Review the code under my responsibility against the nine GRASP patterns before writing my individual audit. The professor confirmed that, in Phase 1, using AI to review our own code and identify possible improvements is allowed.

**Problem faced:**
`Sale`, `SaleService` and `ConsoleMenu` received changes in every requirement (accessories, promotions, returns, warranties and exceptions). I needed to check, pattern by pattern, which responsibilities were badly placed and which were correct, with exact line numbers from develop (`bf35aa2`).

**Prompt used:**
"Tu tarea ahora es cumplir las labores de líder técnico, el cual está encargado de realizar un informe estilo auditoría en donde se identifiquen las violaciones de los principios GRASP [...] Si ves un error, DÉJALO ASÍ, solo debes indicar que ese error o falla existe con documentación."

**Solution obtained and decision taken:**
The AI produced a review of the whole system with 31 possible violations. I kept only the ones that belong to my classes according to `TEAM.md` (`Sale`, `SaleRepository`, `SaleService`, `ConsoleMenu`, `Main` and `ProductService.restoreStock`), which gave 9 violations (V-01 to V-09), plus Correct verdicts for Creator and Pure Fabrication. The findings that involve code of Developer 1 or Developer 2 were marked as boundary findings for the consolidation instead of being audited by me. Functional defects (for example, loaded sales losing their discount) were moved to the out-of-scope section and not fixed. I checked each line number against the code before committing.

**Commit related:**
docs: add technical lead GRASP audit

### Entry 10

**Date:** 2026-10-25
**Tool used:** Claude (Claude Code)
**Phase and branch:** Requirement 7, Phase 1 (Audit) — feature/grasp-audit

**Reason for use:**
Draft the answers to the five guiding questions in `docs/grasp-analysis.md`. The professor confirmed that the AI may draft the guiding question answers and documentation as long as the team verifies them.

**Problem faced:**
The questions ask about our audit criteria, boundary findings, code that breaks more than one pattern, the non-regression strategy and the correction order. The answers had to use concrete cases from our own repository.

**Prompt used:**
"Ahora hazme lo que piden los requerimientos 7 y 8."

**Solution obtained and decision taken:**
The AI drafted the five answers using cases from our audit (stock routing with `instanceof Accessory` in `SaleService` and `ReturnService`, and `ConsoleMenu` loading and saving the lists of other modules). I signed answers 1 and 2 as Technical Lead and left 3, 4 and 5 to be assigned in the team meeting. I reviewed the draft and checked that the cases it cites match my audit before committing.

**Commit related:**
docs: add guiding question answers to grasp analysis

### Entry 11

**Date:** 2026-10-26
**Tool used:** Claude (Claude Code)
**Phase and branch:** Requirement 7, Phase 1 (Audit) — feature/grasp-audit

**Reason for use:**
Consolidate the three individual GRASP audits into `docs/grasp-audit/consolidated-audit.md`.

**Problem faced:**
The three reports had 43 local findings with their own IDs (9 from my audit, 2 from Developer 1 and 32 from Developer 2). Several of them described the same problem from both sides of a boundary, and some corrections would change messages that the user sees.

**Prompt used:**
"Revisa lo que hay en el develop para ver ahora qué falta del R7 para que me ayudes."

**Solution obtained and decision taken:**
The AI drafted the consolidation: coverage table, team verdict per pattern, 37 final violations (C-01 to C-37) with their source report and owner, the boundary findings with the commit order for each, and the out-of-scope section. Duplicates were merged (for example, my L-V08 and Developer 2's D2-V23 became C-27). D2-V13, D2-V17, D2-V29 and D2-V30 were moved out of scope because fixing them would change the messages shown in the console. I reviewed the owners and the merged findings before committing.

**Commit related:**
docs: add consolidated GRASP audit

## Requirement 8 - Phase 1 (Audit)

### Entry R8-1

**Date:** 2026-10-26
**Tool used:** Claude

**Reason for use:**
Review my own code (Sale, SaleItem, SaleRepository, SaleRecord, SaleCatalog, SaleLookup, SaleService, InventoryService, ConsoleMenu, ConsoleIO, the module menus, Main and ProductService.restoreStock) against the five SOLID principles, as allowed by the professor's update to the Phase 1 AI policy.

**Problem faced:**
After the GRASP correction my classes changed a lot, and I needed to check which of the new classes (InventoryService, SaleCatalog, the module menus) introduced new SOLID problems and which ones already comply.

**Prompt used:**
"Revisa mi código del líder frente a SOLID y arma un borrador de leader-audit.md con el mismo formato del R8."

**Solution obtained and decision taken:**
The AI proposed a draft with 8 violations (SRP: receipt text in Sale, record format in SaleRepository; OCP: instanceof routing in InventoryService, exception chain in ConsoleIO, duplicated options in ConsoleMenu; ISP: ReturnMenu using the full SaleService; DIP: SaleCatalog and SaleService depending on concrete classes) and LSP as correct. I checked each line number against develop, kept the findings I can explain, and moved the ReturnService and MonthlyBalanceReport dependencies to the boundary findings because that code belongs to Developer 2.

## Requirement 8 - Phase 2 (Correction)

### Entry R8-2

**Date:** 2026-10-26
**Tool used:** Claude

**Reason for use:**
Review my design for the eight violations assigned to me in the consolidated audit and get reference code to compare with my own changes.

**Problem faced:**
My corrections touch `Main` and the menus, which also had to wire the new classes of Developer 1 and Developer 2, so the order of the commits and the wiring had to be right for the project to compile.

**Prompt used:**
"Dame el código de referencia de mis correcciones SOLID y verifica que no cambie el comportamiento."

**Solution obtained and decision taken:**
The AI proposed `SaleReceiptFormatter`, `SaleRecordMapper` with `FileSaleRepository`, the `SaleStore` interface, `StockHandler` implementations, the exception map in `ConsoleIO` and `MenuOption`. I reviewed each change, applied them as one commit per violation, and checked that the receipt text, messages and data files did not change. Final evidence: 127 tests passing and `verify-grasp.sh` 11/11 SAME on develop.

### Entry R8-3

**Date:** 2026-10-26
**Tool used:** Claude

**Reason for use:**
Help with the consolidation, the guiding questions, the before/after diagram and the README, as allowed by the professor's update to the AI policy.

**Problem faced:**
The three reports used local IDs and some findings were the same problem seen from two sides, so I needed one final list with owners.

**Prompt used:**
"Arma la auditoría consolidada, solid-analysis, el diagrama antes/después, el log y el README con lo que ya está en develop."

**Solution obtained and decision taken:**
The AI drafted the documents. I checked the coverage table against `TEAM.md`, merged leader V-06 and developer 2 V-06 into one boundary violation (V-16), confirmed that the 20 final violations appear in the refactor log with their commits, and reviewed the answers before committing.
