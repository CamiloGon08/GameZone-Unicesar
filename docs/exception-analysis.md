# Exception Analysis – GameZone Unicesar

### Q1: The root class GameZoneException can be declared as Exception (checked) or as RuntimeException (unchecked). Which of the two options is more appropriate for this project and why? What consequence does it have on the signatures of the service and repository methods?

We declared `GameZoneException` as an abstract subclass of `RuntimeException` (unchecked), as the requirement specifies.

The errors we report are not situations the caller can usually recover from in the middle of the call chain. A missing product, a broken business rule or an unreadable file must travel up to `ConsoleMenu`, which is the only class that knows how to talk to the user. With a checked exception, every method in between (validators, services and repositories) would need a `throws` clause, even when it only passes the error along. The same clauses would also leak into `SaleRepository`, `ReturnRepository` and other classes that call services. An unchecked exception lets the error go directly from where it is detected to where it is handled.

Consequence on the signatures: service and repository methods do not declare `throws`. To keep the contract visible anyway, every refactored public method documents the exceptions it can throw with `@throws` in its JavaDoc. For example, `ProductService.updateStock` documents `InvalidDataException`, `ResourceNotFoundException`, `BusinessRuleException` and `PersistenceException`. The trade-off is that the compiler no longer forces callers to handle the errors, so the UI must catch them deliberately.

### Q2: Why is it preferable to have a hierarchy of custom exceptions with a common root class (GameZoneException) instead of throwing a generic IllegalArgumentException in every case? Which design principles are applied?

With a single `IllegalArgumentException`, the only way to tell a missing product from insufficient stock or a corrupted file was to read the message text. The UI could not show different messages, and a change in wording could break the handling. With the hierarchy, the type itself says which category of failure happened:

| Exception | Category | Example in our code |
| --- | --- | --- |
| `ResourceNotFoundException` | The requested resource does not exist | `ProductValidator.validateProductExists` when `findById` returns `null` |
| `BusinessRuleException` | A business rule is violated | Insufficient stock in `validateStockAvailability`, duplicated person in `validateUniquePerson`, sale without products |
| `InvalidDataException` | Input with a wrong format or range | Empty title, price ≤ 0, email without `@` and `.` |
| `PersistenceException` | Reading or writing a data file failed | `ProductRepository.saveAll` wrapping an `IOException` with the file name and operation |

The common root also means that code needing to handle "any GameZone error" can catch one type without catching unrelated runtime errors. In addition, every exception carries a stable `errorCode` (`RESOURCE_NOT_FOUND`, `BUSINESS_RULE`, `INVALID_DATA`, `PERSISTENCE_ERROR`) that does not depend on the message wording.

Principles applied:

- **Abstraction and polymorphism:** `GameZoneException` is abstract and only its subclasses are thrown, but a handler can treat all of them through the root type.
- **Separation of concerns:** the layer that detects an error decides its category, and the UI decides how to present it to the user.
- **Open for extension:** a new category of error can be added as a new subclass without changing the existing ones.
- **Information preservation:** `PersistenceException` keeps the original `IOException` as its cause, so technical detail is not lost even though the user never sees it.

### Q3: The validators form a new layer (com.gamezone.validation). At what point in the flow are they invoked, and why is this placement consistent with the layered architecture of the system? Can validators access the persistence package? Justify.

In our project the package is `com.mycompany.gamezone.validation`. The validators are invoked by the services at the start of each operation, before any object is created or any data is changed:

- `ProductService.registerVideoGame` / `registerConsole` call `ProductValidator.validateProductData` before creating the product.
- `SaleService.registerSale` calls `SaleValidator.validateSaleData` and then `ProductValidator.validateStockAvailability` for every item before building the `Sale`.
- `PersonService.registerCustomer` calls `PersonValidator.validatePersonData` and `validateUniquePerson` before adding the customer.

The flow is `ui → service → validation → model`, and `exceptions` is usable from any layer. This keeps the services focused on coordinating the business operation while the checks live in one place and can be reused. The UI never calls a validator directly, so it cannot bypass the service.

Validators must not access the persistence package (or the services). They are final classes with a private constructor and only static methods. They receive everything they need as parameters: `validateUniquePerson` receives the list of existing persons and does not load it. This makes them stateless, independent of files and easy to test in isolation. `ValidatorTest` checks them without creating any repository or data file. If a validator read files, it would mix data access with rule checking and break the dependency direction between layers.

### Q4: The refactor must preserve the external behavior of the system. What strategy does the team follow to guarantee that the 10 menu operations keep working the same? What evidence is shown at the end to demonstrate that nothing was broken?

Strategy:

1. **Same conditions, different type.** Each `throw new IllegalArgumentException(...)` in `ProductService`, `PersonService` and `SaleService` was replaced by a validator call that fails under exactly the same condition. Only the exception type and message changed, not the condition. Example: stock is still rejected when `stock < requested`, now with `BusinessRuleException`.
2. **Small, separate commits per class.** Exceptions and validators (Developer 1), then services and repositories (Developer 2), then `ConsoleMenu` (Technical Lead). Each step can be verified and reverted on its own.
3. **Unit tests for the new code.** `ValidatorTest` has 20 JUnit tests covering the failure of each rule and the valid boundaries (for example, stock 0 is accepted and stock −1 is rejected). `mvn clean test` must pass before every Pull Request.
4. **Menu comparison against a reference build.** `scripts/verify-menu.ps1` runs the same scripted input through all 10 base operations and the read-only paths of submenus 11–14. It runs on clean data files against the reference classes (develop before Requirement 6) and the candidate classes (after the refactor). It checks the expected success messages and compares the console output and the seven data files byte for byte.

Evidence shown at the end:

- `mvn clean test` output: BUILD SUCCESS, 20 tests, 0 failures.
- `verify-menu.ps1` output with both reference and candidate: "PASS: console output and seven data files match the reference byte for byte."
- A manual demonstration of one error of each category in the console, showing the four differentiated messages.

> **TODO (team):** paste here the date, the two commits compared and the actual output of `verify-menu.ps1` and `mvn clean test` from the final run.

### Q5: The ConsoleMenu class used to catch a single generic exception. Now it must catch four different types. Is this done with a single try block with multiple catch blocks, with cascading catch, or with a general catch (GameZoneException)? Justify the choice considering code clarity and the differentiation of messages to the user.

Each menu operation keeps a single `try` block around the service call. The `catch (GameZoneException | IllegalArgumentException e)` passes the exception to one private method, `handleException`. That method checks the concrete type from the most specific category to the most generic one and prints a different Spanish message for each:

| Type | Message shown to the user |
| --- | --- |
| `ResourceNotFoundException` | "No se encontró el recurso solicitado: <detail>" |
| `BusinessRuleException` | "No se puede completar la operación: <detail>" |
| `InvalidDataException` | "Los datos ingresados no son válidos: <detail>" |
| `PersistenceException` | "Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador." |
| `IllegalArgumentException` (modules not migrated yet) | "Error: <detail>" |

We chose this instead of writing four separate `catch` blocks in each operation because `ConsoleMenu` has twelve operations with a `try` around a service call. Repeating four catch blocks in each one would mean 48 catch blocks with the same messages, and a change in a message would have to be repeated in every operation. With `handleException`, the differentiation by type is still explicit, the order of the categories is visible in one place, and each message is defined only once. For persistence errors, the user receives a generic message without file names or stack traces, as the requirement asks.

A general `catch (GameZoneException e)` that prints only `e.getMessage()` was rejected because it would not differentiate the messages by category. The requirement literally describes "múltiples bloques catch diferenciados". If the professor requires that exact form, the change is mechanical: replace the call to `handleException` with four `catch` blocks, ordered from the most specific to the most generic, that call the same message methods.
