# GRASP Refactor Log

Record of the Phase 2 corrections of Requirement 7. Each entry references a final violation (`C-xx`) of `docs/grasp-audit/consolidated-audit.md`.

## Non-regression strategy

The external behavior must not change: same inputs, same outputs, same Spanish messages and same data files.

1. **Reference build.** `develop` at commit `8382d85` (consolidated audit merged, before any correction) is compiled once and kept as the reference.
2. **Fixed script.** `scripts/verify-grasp.sh` runs the same menu script on the reference build and on the build under review. Each run starts from its own fresh `data/` folder with only the seller `S1 Luis`.
   - **Session 1** registers and exercises every menu: video game, duplicated video game (error), console, controller, cable, memory, accessory listings by type and by console, the three promotion types, active promotions, invalid customer phone (error), customer, sale with three items and extended warranty, sale with one item, sales by customer and by seller, return of a non-existent sale (error), return of `G1` from `SALE-1`, returns by customer and by sale, monthly balance, warranty queries, invalid option (error) and exit.
   - **Session 2** restarts the program and reads everything back from the files (products, customers, sellers, sales, accessories, promotions, returns, warranties).
3. **Comparison.** The script compares byte for byte the two console outputs, the two error outputs and the seven data files. Any difference fails the check and prints the `diff`.
4. **Build and tests.** `mvn clean compile` and `mvn test` must succeed.
5. **When.** Every correction commit is checked before it is pushed. Every Phase 2 Pull Request lists the result. At the end of the phase the full check runs again on `develop`.

Known functional defects (out-of-scope findings of the consolidated audit, section 5) are not fixed in this requirement, so they must appear identically before and after. For example, in session 2 the reloaded `SALE-2` shows `Total : $100.00` instead of `$90.00` because the discount is lost when sales are loaded.

### How to run it (Git Bash)

```bash
# once: build the reference from develop before Phase 2
git worktree add ../gamezone-reference 8382d85
cd ../gamezone-reference && mvn -q clean compile && cd -

# on each check: build the current branch and compare
mvn -q clean compile
scripts/verify-grasp.sh ../gamezone-reference/target/classes
mvn test
```

The run must end with `PASS: console output and the seven data files match the reference.`

## Baseline

**Date:** 2026-10-26
**Commit:** `8382d85` (develop) / `6f05df8` (feature/grasp-refactor, documentation only)

| Check | Result |
|---|---|
| `mvn clean compile` | BUILD SUCCESS |
| `mvn test` | 20 tests, 0 failures, 0 errors |
| `verify-grasp.sh` reference vs `feature/grasp-refactor` | PASS, 11 of 11 files identical |

Expected results recorded in the baseline (session 1):

| Operation | Expected result |
|---|---|
| Register `G1` twice | Second time: `No se puede completar la operación: Ya existe un producto con el identificador G1.` |
| Customer with phone `abc` | `Teléfono inválido.` |
| Sale `SALE-1`: Game + Console + Pad, extended warranty | Promotion `Bulk`, discount `$70.00`, total `$300.00` |
| Sale `SALE-2`: Game | Promotion `Promo10`, discount `$10.00`, total `$90.00` |
| Return of `SALE-99` | `No existe una venta con ese ID.` |
| Return of `G1` from `SALE-1` | Refund `$80.00` (list price `$100.00`, proportional discount `$20.00`) |
| Monthly balance of the current month | Sales `$390.00`, returns `$80.00`, net `$310.00` |
| Warranties of `SALE-1` | `WAR-1` basic and `WAR-2` extended (cost `$20.00`) for `C1` |

Data files after session 1:

```text
products.txt     VIDEOGAME,G1,Game,100.0,4,PC,RPG,E
                 CONSOLE,C1,Console,200.0,2,Sony,PS5,9
sales.txt        SALE-1|<date>|U1|S1|G1;C1;CT1|300.0|Bulk|70.0
                 SALE-2|<date>|U1|S1|G1|90.0|Promo10|10.0
returns.txt      RETURN-1,<date>,SALE-1,G1,Defectuoso,80.0,0.0
warranties.txt   BASIC,WAR-1,C1,SALE-1,<date>
                 EXTENDED,WAR-2,C1,SALE-1,<date>
```

## Corrections

One entry per correction commit, in the order of section 6 of the consolidated audit.

<!--
### C-xx — <short title>

**Member:** <name>
**Commit:** refactor: <description> (C-xx)
**Classes changed:** <list>
**Change:** <what moved where, and which pattern it applies>
**Verification:** mvn clean compile OK · mvn test 20/20 · verify-grasp.sh PASS
-->

## Final verification

To be completed when every correction is merged into `develop`.
