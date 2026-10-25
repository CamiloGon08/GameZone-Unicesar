# GRASP Analysis

Answers to the five guiding questions of Requirement 7. Each answer names its author.

## 1. Criteria for violation, correct and not applicable verdicts
**Author:** Technical Lead

A fragment is a **violation** when we can point to concrete code where a responsibility is placed against the pattern and explain a real consequence, today or under a foreseeable change (a new product type, a new storage, a new rule). Examples: a public setter that lets another class compute a value the owner should compute (Information Expert), or an `instanceof` chain that has to grow with every new subtype (Polymorphism).

A pattern is **correct** when we can name the class and method where it is applied and say why the responsibility is in the right place. For example, `Promotion.calculateDiscount()` is abstract and each promotion computes its own discount.

A pattern is **not applicable** when the audited code has no point where the pattern's question arises. For example, a class that receives no system events has no Controller decision to make.

Minimum evidence accepted in the consolidation:
- class, method and exact line numbers in the audited develop commit (`bf35aa2`);
- the code fragment;
- the explanation and a concrete consequence.

A finding without line numbers or without a consequence is returned to its author before it is consolidated.

## 2. Boundary findings between members
**Author:** Technical Lead

We found several violations whose code belongs to two members. Two examples:

- **Stock routing between product families.** `SaleService.registerSale` (Technical Lead) and `ReturnService.restoreItemStock` (Developer 2) decide with `instanceof Accessory` whether to call `AccessoryService` (Developer 2) or `ProductService` (Developer 1). It is registered once in the consolidated audit as an Indirection violation. The Technical Lead creates the new `InventoryService` and updates `SaleService`, Developer 2 updates `ReturnService` and `AccessoryService`, and Developer 1 updates `ProductService`.
- **Use-case control split between UI and services.** `ConsoleMenu` (Technical Lead) loads the lists of persons, accessories and promotions, passes them to the services (Developer 2) and calls `saveAll`. It is registered once as a Controller violation. Developer 2 makes the services keep and persist their lists, and the Technical Lead then removes the list handling from `ConsoleMenu`.

Each boundary finding appears once in the "Boundary findings" section of `consolidated-audit.md`, with the source reports and the part of the correction each member owns.

## 3. Code that breaks more than one pattern
**Author:**

Yes. For example, `SaleRepository` depends on `ProductService` and `PersonService`. That breaks Low Coupling (the persistence layer depends on the service layer) and Indirection (there is no intermediary to resolve references). Likewise, the `instanceof` chains in the repositories break both Polymorphism and Protected Variations.

To avoid duplicates, each fragment was registered **once**, under the pattern whose solution fixes the root cause. The related patterns were mentioned in the explanation and in the "Proposed solution" field (for example, "Pattern applied: Low Coupling + Indirection"). The guiding pattern was chosen with one question: which pattern's solution, applied alone, removes the problem? For `SaleRepository`, removing the dependency on services is the fix, so Low Coupling guides it.

## 4. Strategy to preserve external behaviour
**Author:**

1. Before the first correction, we run a fixed script of every menu operation on develop: register and list products, persons, accessories and promotions; register sales with and without promotion and warranty; returns; the monthly balance; and the warranty queries. Each run uses a fixed copy of the `data/` folder. We save the console output and the resulting `data/*.txt` files as the reference.
2. Each Fase 2 Pull Request re-runs the operations it affects and compares the console output and the data files with the reference (`diff`). It also runs `mvn clean compile` and `mvn test`.
3. At the end of the phase we run the full script again on develop and compare everything with the reference.

The evidence is recorded in `docs/grasp-refactor-log.md`: the operations, the input data, the expected result, the initial and final results, and the `mvn` output. Known functional defects (out-of-scope findings) are expected to appear identically before and after, because they are not fixed in this requirement.

## 5. Correction order and coordination
**Author:**

Violations are corrected from the lowest layer to the highest: model, then persistence, then services, then UI. Upper layers depend on lower ones, so changing the model first (for example, `Sale` computing its own total) lets services and UI adapt in a single later step instead of twice. Within a layer, boundary violations go first, because other corrections build on them (for example, `InventoryService` before cleaning `SaleService` and `ReturnService`).

For boundary violations, the Technical Lead defines the order of commits in the consolidated audit. The member who owns the lower layer commits and pushes first. The others pull before starting their part. Each member only edits their own classes, every commit is pushed right away, and `ConsoleMenu`, which everyone depends on, is changed last and only by the Technical Lead.
