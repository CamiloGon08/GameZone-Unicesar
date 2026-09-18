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
