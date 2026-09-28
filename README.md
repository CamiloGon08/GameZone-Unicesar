# GameZone Unicesar

A console-based management system for a small video game store, built in Java as the final project for **Programación de Computadores III (SS462)** at Universidad Popular del Cesar.

## What this project does

GameZone Unicesar keeps track of everything a retail video game store needs on a daily basis: which products are in stock, who buys them, who sells them, and what happens after a sale (returns, warranties, promotions). It runs entirely in the terminal and stores all information in plain text files, so records are not lost when the program closes.

The system started as the Taller 1 delivery — products, people and sales — and was later extended with four independent modules, each one documented in its own file under `docs/`.

## System architecture

Every class in the project belongs to one of four layers, and dependencies only flow downwards:


```mermaid
flowchart TD
    UI["ui"] --> SERVICE["service"]
    SERVICE --> PERSISTENCE["persistence"]
    SERVICE --> MODEL["model"]
    PERSISTENCE --> MODEL

    
|     Layer     |               Contains                              |
|-------|-------|-----------------------------------------------------|
|     `ui`      | The console menu that reads input and shows output  |
|   `service`   | The business rules and coordination between entities|
| `persistence` | The code that writes to and reads from files        |
|    `model`    | The domain entities (products, sales, people, etc.) |

The rule is enforced strictly: the `model` layer never imports anything from the other layers, and the `ui` layer never touches files directly.

## Compilation and execution

Requires **JDK 17** or newer and **Maven 3.8** or newer.

To compile:

```
mvn clean compile
```

To run the application:

mvn exec:java "-Dexec.mainClass=com.mycompany.gamezone.Main"


## Modules

### Accessories (v1.1.0)

The product catalog was extended with a new line of accessories. An abstract class `Accessory` extends `Product`, and three concrete subclasses represent what the store actually sells: `Controller`, `Cable` and `Memory`. Each accessory keeps a list of the console IDs it is compatible with, so the seller can quickly check compatibility before offering it to a customer.

Accessories are stored separately in `data/accessories.csv` and can be mixed with regular products in a single sale.

New menu option (11 — Accessory management):
1. Register a new controller
2. Register a new cable
3. Register a new memory
4. List all accessories
5. List accessories by type
6. Find accessories compatible with a console

### Promotions (v1.2.0)

Customers respond well to discounts, so the store needed a way to run promotional campaigns with different strategies. Three types of promotions were implemented, all extending the abstract class `Promotion`: percentage-based discounts over the whole sale, category-based discounts limited to video games or consoles, and bulk-purchase discounts that trigger above a minimum item count.

When a sale is registered, the system evaluates every currently active promotion, calculates the discount each one would produce, and applies **only the highest**. Promotions are never stacked. The receipt shows the promotion name and the discount amount whenever one was applied.

Promotions live in `data/promotions.csv`.

New menu option (12 — Promotion management):
1. Register a new percentage discount
2. Register a new category discount
3. Register a new bulk purchase discount
4. List all promotions
5. List active promotions

### Returns (v1.3.0)

Sometimes a customer brings a product back. The return module allows registering partial returns — a customer may return just one item out of several purchased in the same sale — but only within 30 days of the original purchase. Each return validates that the sale exists, that it is still within the return window (`Sale.canBeReturned()`), and that the products being returned actually belong to that sale.

Once accepted, the stock of the returned items is restored automatically and the refund is calculated as the sum of the returned products' prices.

Returns are stored in `data/returns.csv`.

New menu option (13 — Return management):
1. Register a new return
2. View all returns
3. View returns by customer
4. View returns by sale
5. View the monthly balance (total sales minus total returns for a given month)

### Warranties (v1.4.0)

Every console sold at GameZone comes with a free 6-month basic warranty, generated automatically at checkout. Customers can also pay for an extended 12-month warranty that covers accidental damage — this costs 10 % of the product's price and is added to the sale total.

The seller is asked about the extended warranty for each console during the sale registration flow. Warranties are stored in `data/warranties.csv` and can be queried by product, by sale, by active status, or by expiration window.

New menu option (14 — Warranty management):
1. Find the warranty for a product in a specific sale
2. List all warranties
3. List active warranties
4. List warranties expiring within the next 30 days

## Project layout

```
GameZoneUnicesar/
├── README.md
├── TEAM.md
├── CLAUDE.md
├── pom.xml
├── .gitignore
├── LICENSE
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── gamezone/
│                   ├── model/
│                   ├── persistence/
│                   ├── service/
│                   ├── ui/
│                   └── Main.java
├── data/
└── docs/
    ├── analysis.md
    ├── hierarchy-diagram.md
    ├── class-diagram.md
    ├── layers-diagram.md
    └── ai-usage/
        ├── leader-ai-log.md
        ├── developer1-ai-log.md
        └── developer2-ai-log.md
```

## Documentation

Design documentation lives under `docs/`:

- [Analysis](docs/analysis.md) — initial design of the base system
- [Hierarchy Diagram](docs/hierarchy-diagram.md)
- [Class Diagram](docs/class-diagram.md)
- [Layers Diagram](docs/layers-diagram.md)
- [Accessory Analysis](docs/accessory-analysis.md)
- [Promotion Analysis](docs/promotion-analysis.md)
- [Return Analysis](docs/return-analysis.md)
- [Warranty Analysis](docs/warranty-analysis.md)

## Team

Roles, module ownership, and individual activities are documented in [TEAM.md](TEAM.md).