# Warranty Class Diagram — Requirement 4

This diagram shows the warranty hierarchy added in Requirement 4, its persistence and service classes, and how `SaleService` assigns warranties when a sale is registered.

```mermaid
classDiagram
    direction TB

    %% ---------- model ----------
    class Warranty {
        <<abstract>>
        -String id
        -Product product
        -Sale sale
        -LocalDate startDate
        -LocalDate endDate
        +Warranty(String id, Product product, Sale sale, LocalDate startDate)
        +getDurationInMonths()* int
        +getWarrantyType()* String
        +getAdditionalCost()* double
        +isActive(LocalDate date) boolean
        +generateWarrantyCertificate() String
        +getId() String
        +getProduct() Product
        +getSale() Sale
        +getStartDate() LocalDate
        +getEndDate() LocalDate
    }

    class BasicWarranty {
        +BasicWarranty(String id, Product product, Sale sale, LocalDate startDate)
        +getDurationInMonths() int
        +getWarrantyType() String
        +getAdditionalCost() double
    }

    class ExtendedWarranty {
        -double EXTENDED_COST_RATE$
        +ExtendedWarranty(String id, Product product, Sale sale, LocalDate startDate)
        +getDurationInMonths() int
        +getWarrantyType() String
        +getAdditionalCost() double
    }

    class Product {
        <<abstract>>
        +getId() String
        +getPrice() double
    }
    class Console

    class Sale {
        -String id
        -LocalDate date
        -double total
        -double warrantyCost
        +getTotal() double
        +setTotal(double total) void
        +setWarrantyCost(double warrantyCost) void
    }

    %% ---------- persistence ----------
    class WarrantyRepository {
        -String filePath
        +WarrantyRepository()
        +saveAll(List~Warranty~ warranties) void
        +loadAll() List~WarrantyData~
        -toLine(Warranty warranty) String
        -toWarranty(String line) WarrantyData
    }

    class WarrantyData {
        <<static nested>>
        -String type
        -String id
        -String productId
        -String saleId
        -LocalDate startDate
        +getType() String
        +getId() String
        +getProductId() String
        +getSaleId() String
        +getStartDate() LocalDate
    }

    class SaleRepository {
        +findById(String id) Sale
    }

    %% ---------- service ----------
    class WarrantyService {
        -WarrantyRepository repository
        -SaleRepository saleRepository
        -ProductService productService
        -List~Warranty~ warranties
        +WarrantyService(WarrantyRepository repository, SaleRepository saleRepository, ProductService productService)
        +assignBasicWarranty(Product product, Sale sale) Warranty
        +assignExtendedWarranty(Product product, Sale sale) Warranty
        +findById(String id) Warranty
        +findByProductAndSale(String productId, String saleId) Warranty
        +cancelWarranties(String productId, String saleId) double
        +viewAllWarranties() List~Warranty~
        +viewActiveWarranties(LocalDate date) List~Warranty~
        +viewWarrantiesExpiringSoon(int days) List~Warranty~
    }

    class ProductService {
        +findById(String id) Product
    }

    class SaleService {
        -WarrantyService warrantyService
        +setWarrantyService(WarrantyService warrantyService) void
        +registerSale(Customer customer, Seller seller, List~Product~ products, List~String~ productIdsWithExtendedWarranty) Sale
    }

    %% ---------- ui ----------
    class ConsoleMenu {
        -WarrantyService warrantyService
        -registerSale() void
        -warrantyMenu() void
        -findWarrantyByProductAndSale() void
        -viewAllWarranties() void
        -viewActiveWarranties() void
        -viewWarrantiesExpiringSoon() void
    }

    %% ---------- relationships ----------
    Warranty <|-- BasicWarranty
    Warranty <|-- ExtendedWarranty
    Product <|-- Console

    Warranty "0..*" --> "1" Product : product
    Warranty "0..*" --> "1" Sale : sale

    WarrantyRepository ..> Warranty : saves
    WarrantyRepository ..> WarrantyData : loads

    WarrantyService "1" --> "1" WarrantyRepository : repository
    WarrantyService --> SaleRepository : resolves sales
    WarrantyService --> ProductService : resolves products
    WarrantyService ..> BasicWarranty : creates
    WarrantyService ..> ExtendedWarranty : creates

    SaleService "1" --> "0..1" WarrantyService : warrantyService
    SaleService ..> Console : instanceof check
    ConsoleMenu "1" --> "1" WarrantyService : warrantyService
    ConsoleMenu "1" --> "1" SaleService : saleService
```

**Notes**

- The end date is calculated once in the `Warranty` constructor as `startDate.plusMonths(getDurationInMonths())`, so each subclass only states its duration (6 or 12 months).
- During `SaleService.registerSale`, every `Console` gets a `BasicWarranty`. Each product whose ID is in `productIdsWithExtendedWarranty` also gets an `ExtendedWarranty`, and its `getAdditionalCost()` (10% of the price) is added to the sale total.
- `WarrantyService` is injected into `SaleService` with a setter after construction to avoid a circular dependency at startup.
