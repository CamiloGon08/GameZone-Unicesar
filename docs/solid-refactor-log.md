# SOLID Refactor Log

## Developer 1 - Requirement 8, Phase 2

**Author:** Jesus David Dominguez Theran (`jdaviddominguez`).
**Audit baseline:** `d2d7245`. **Integrated implementation baseline:** `011922c`.
**Reference package:** `r8-fase2-jesus.zip` supplied by the Technical Lead.

The IDs below refer to `developer1-audit.md`; they are not global team IDs. At this
checkpoint, the individual audit PR #64 is merged, but the consolidated SOLID audit
is not present in develop. The Technical Lead must confirm final IDs and the consolidated
audit before the final Phase 2 review. These changes follow the assigned reference
package and preserve the behavior constraints recorded in the individual report.

The commit column is populated by the author's publication script after each code
commit exists; no commit hash is invented before publication.

| Finding | Principle | Before | After | Main files | Code commit |
|---|---|---|---|---|---|
| Developer 1 V-01 | SRP | Return calculated refunds and assembled the receipt. | Return exposes charged-price and proportional-discount queries; ReturnReceiptFormatter owns the receipt layout. | `model/Return.java`, `ui/ReturnReceiptFormatter.java` | `89a45d9` |
| Developer 1 V-02 | SRP | Warranty held coverage rules and certificate layout. | WarrantyCertificateFormatter reads the public warranty queries and assembles the certificate. | `model/Warranty.java`, `ui/WarrantyCertificateFormatter.java` | `79b3de9` |
| Developer 1 V-03 | OCP | ProductService repeated registration logic per concrete family. | A common register(Product) path validates common data, uniqueness and polymorphic family details before adding/saving. Existing family methods delegate. | `model/Product.java`, `model/VideoGame.java`, `model/Console.java`, `validation/ProductValidator.java`, `service/ProductService.java` | `34e96d9` |
| Developer 1 V-04 | OCP | ProductRepository fixed the converter registry internally. | FileProductRepository accepts an ordered, defensively copied converter list; the compatibility constructor keeps both existing families. | `persistence/FileProductRepository.java` | `d5778d2` |
| Developer 1 V-05 | ISP | General Promotion required hasValidPercentage from every subtype. | General isValid delegates the existing direct-registration rule to each promotion type, matching Developer 2's updated validator. | `model/Promotion.java`, `model/PercentageDiscount.java`, `model/CategoryDiscount.java`, `model/BulkPurchaseDiscount.java` | `6e906d3` |
| Developer 1 V-06 | DIP | ProductService depended on the concrete text-file repository. | ProductRepository is a load/save interface and FileProductRepository supplies file storage; ProductService receives the abstraction. | `persistence/ProductRepository.java`, `persistence/FileProductRepository.java`, `service/ProductService.java` | `d5778d2` |

### Cross-member integration

- ProductService also implements the narrow ProductLookup interface required by
  Developer 2's WarrantyCatalog boundary. Existing findById semantics remain intact.
- The Technical Lead's Main already constructs FileProductRepository, and the return
  and warranty menus already call the new formatters. Those team files are downloaded,
  rather than changed as part of this Developer 1 delivery.
- Developer 2 owns PromotionValidator, which already calls Promotion.isValid. The
  missing domain method reported by the teammate is supplied in this delivery.
- ProductService.restoreStock remains the Technical Lead's method; its implementation
  was not changed by these corrections.

### Behavior preserved and reference adjustments

- Return receipts retain the exact labels, newline behavior, locale-dependent money,
  first matching charged price, unmatched fallback, cached total and warranty refund.
  Calculation still uses price*(1-ratio), while the displayed item amount uses
  price-price*ratio. The formatter does not recalculate the stored total.
- Warranty certificates retain their literal surrounding quotes, English labels,
  platform line separators, cost policy and two-decimal formatting.
- Registration still checks common product fields before duplicate IDs, and duplicate
  IDs before family details. The existing Spanish fields/messages and trim semantics
  remain intact. Constructors/loading stay permissive. The new register(Product) API
  reports a typed InvalidDataException for a null product.
- The ZIP combined category/minimum-quantity rules into isValid. This was adjusted:
  direct promotion registration keeps its existing percentage-only rule, including NaN
  behavior, while additional factory checks retain their original paths. Public concrete
  category/quantity queries remain available. This avoids changing accepted inputs or
  returning a percentage error for an unrelated category/quantity condition.
- File storage retains field order, split/trim behavior, converter precedence, default
  encoding, line separators, contextual exceptions and causes. Known memory-before-save
  and cached-refund defects from Requirement 7 are preserved rather than silently fixed.

### Validation evidence

- Actual integrated project at 011922c plus this delivery: Maven test BUILD SUCCESS,
  **127 tests, zero failures, errors or skips**. This includes the previous 87 cases
  and 40 cases covering registration contracts, extension points and exact formatting.
- Maven clean compile: BUILD SUCCESS on the complete project, including Main.
- scripts/verify-grasp.sh compared the current classes against a freshly compiled
  d2d7245 reference using separate fresh data fixtures: all **11 console/error/data
  files matched byte for byte**. The script name is historical; it exercises the menu
  behavior retained by this SOLID correction as well.
- Source and evidence backups are outside the repository under
  GameZone-Unicesar-local-backups/r8-phase2-20261026. Runtime data were not modified.
- The first incoming phase2 snapshot at 4506057 had 14 compilation errors. After these
  changes and downloading Developer 2's warranty integration at 011922c, the successful
  results above supersede that checkpoint. No passing partial build is presented as a
  complete integration result.

Publication, final consolidated IDs, peer approval and the team's remaining shared
documentation are workflow steps; this entry does not claim that the entire requirement
or the team's consolidated audit is finished.
