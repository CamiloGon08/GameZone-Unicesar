# Promotion Class Diagram — Requirement 2

This diagram shows the promotion hierarchy added in Requirement 2, its persistence and service classes, and how the best promotion is applied when `SaleService` registers a sale.

```mermaid
classDiagram
    direction TB

    %% ---------- model ----------
    class Promotion {
        <<abstract>>
        -String id
        -String name
        -LocalDate startDate
        -LocalDate endDate
        +Promotion(String id, String name, LocalDate startDate, LocalDate endDate)
        +isActive(LocalDate date) boolean
        +calculateDiscount(Sale sale)* double
        +getId() String
        +getName() String
        +getStartDate() LocalDate
        +getEndDate() LocalDate
    }

    class PercentageDiscount {
        -double percentage
        +PercentageDiscount(String id, String name, LocalDate startDate, LocalDate endDate, double percentage)
        +calculateDiscount(Sale sale) double
        +getPercentage() double
        +setPercentage(double percentage) void
    }

    class CategoryDiscount {
        -double percentage
        -String targetCategory
        +CategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate, double percentage, String targetCategory)
        +calculateDiscount(Sale sale) double
        +getTargetCategory() String
        +setTargetCategory(String targetCategory) void
    }

    class BulkPurchaseDiscount {
        -int minimumQuantity
        -double percentage
        +BulkPurchaseDiscount(String id, String name, LocalDate startDate, LocalDate endDate, int minimumQuantity, double percentage)
        +calculateDiscount(Sale sale) double
        +getMinimumQuantity() int
        +setMinimumQuantity(int minimumQuantity) void
    }

    class Sale {
        -String id
        -LocalDate date
        -List~Product~ products
        -double total
        -String appliedPromotionName
        -double discountAmount
        +getSubtotal() double
        +getTotal() double
        +setTotal(double total) void
        +setAppliedPromotionName(String name) void
        +setDiscountAmount(double discountAmount) void
        +generateReceipt() String
    }

    class Product {
        <<abstract>>
        +getPrice() double
    }
    class VideoGame
    class Console

    %% ---------- persistence ----------
    class PromotionRepository {
        -String PERCENTAGE_TYPE$
        -String CATEGORY_TYPE$
        -String BULK_TYPE$
        +saveAll(List~Promotion~ promotions) void
        +loadAll() List~Promotion~
        -formatPromotion(Promotion promotion) String
        -createPromotion(String[] data) Promotion
    }

    %% ---------- service ----------
    class PromotionService {
        -PromotionRepository promotionRepository
        +PromotionService(PromotionRepository promotionRepository)
        +registerPercentageDiscount(List~Promotion~ promotions, String id, String name, LocalDate startDate, LocalDate endDate, double percentage) void
        +registerCategoryDiscount(List~Promotion~ promotions, String id, String name, LocalDate startDate, LocalDate endDate, String category, double percentage) void
        +registerBulkPurchaseDiscount(List~Promotion~ promotions, String id, String name, LocalDate startDate, LocalDate endDate, int minimumQuantity, double percentage) void
        +listAllPromotions() List~Promotion~
        +listActivePromotions() List~Promotion~
        +findBestPromotionFor(Sale sale) Promotion
        +findById(String id) Promotion
        +saveAll(List~Promotion~ promotions) void
    }

    class SaleService {
        -PromotionService promotionService
        +registerSale(Customer customer, Seller seller, List~Product~ products, List~String~ productIdsWithExtendedWarranty) Sale
    }

    %% ---------- ui ----------
    class ConsoleMenu {
        -PromotionService promotionService
        -promotionMenu() void
        -registerPercentageDiscount() void
        -registerCategoryDiscount() void
        -registerBulkPurchaseDiscount() void
        -listAllPromotions() void
        -listActivePromotions() void
    }

    %% ---------- relationships ----------
    Promotion <|-- PercentageDiscount
    Promotion <|-- CategoryDiscount
    Promotion <|-- BulkPurchaseDiscount
    Product <|-- VideoGame
    Product <|-- Console

    Promotion ..> Sale : calculateDiscount(sale)
    CategoryDiscount ..> VideoGame : targetCategory VIDEOGAME
    CategoryDiscount ..> Console : targetCategory CONSOLE
    Sale "0..*" o-- "1..*" Product : products

    PromotionRepository ..> Promotion : saves / loads
    PromotionService "1" --> "1" PromotionRepository : promotionRepository
    PromotionService ..> Promotion : creates / selects best
    SaleService "1" --> "1" PromotionService : promotionService
    SaleService ..> Sale : applies discount
    ConsoleMenu "1" --> "1" PromotionService : promotionService
```

**Notes**

- `Sale` keeps only the name of the applied promotion and the discount amount (two additive attributes), not a reference to the `Promotion` object.
- `PromotionService.findBestPromotionFor` evaluates every active promotion and returns the one with the highest discount, or `null` when none gives a discount greater than zero. `SaleService.registerSale` then sets `appliedPromotionName`, `discountAmount` and the final total.
- `CategoryDiscount` also accepts the `ACCESSORY` category in the current code, in addition to `VIDEOGAME` and `CONSOLE`.
