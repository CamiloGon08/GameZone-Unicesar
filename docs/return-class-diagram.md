# Return Class Diagram — Requirement 3

This diagram shows the `Return` entity added in Requirement 3, its persistence and service classes, and its integration with `Sale`, `ProductService` and the console menu.

```mermaid
classDiagram
    direction TB

    %% ---------- model ----------
    class Return {
        -String id
        -LocalDate date
        -Sale originalSale
        -List~Product~ returnedProducts
        -String reason
        -double warrantyRefundAmount
        -double refundAmount
        +Return(String id, LocalDate date, Sale originalSale, List~Product~ returnedProducts, String reason)
        +Return(String id, LocalDate date, Sale originalSale, List~Product~ returnedProducts, String reason, double warrantyRefundAmount)
        +calculateRefundAmount() double
        +generateReturnReceipt() String
        +getId() String
        +getDate() LocalDate
        +getOriginalSale() Sale
        +getReturnedProducts() List~Product~
        +getReason() String
        +getRefundAmount() double
    }

    class Sale {
        -String id
        -LocalDate date
        -Customer customer
        -List~Product~ products
        -double discountAmount
        +canBeReturned() boolean
        +getSubtotal() double
        +getDiscountAmount() double
        +getProducts() List~Product~
    }

    class Product {
        <<abstract>>
        -String id
        -double price
        -int stock
        +adjustStock(int amount) void
    }

    class Customer

    %% ---------- persistence ----------
    class ReturnRepository {
        -String filePath
        -SaleService saleService
        -ProductService productService
        -AccessoryService accessoryService
        +ReturnRepository(String filePath, SaleService saleService, ProductService productService, AccessoryService accessoryService)
        +saveAll(List~Return~ returns) void
        +loadAll() List~Return~
        -toLine(Return returnRecord) String
        -toReturn(String line) Return
    }

    %% ---------- service ----------
    class ReturnService {
        -ReturnRepository repository
        -SaleService saleService
        -ProductService productService
        -AccessoryService accessoryService
        -WarrantyService warrantyService
        -List~Return~ returns
        +registerReturn(String saleId, List~String~ productIds, String reason) Return
        +viewAllReturns() List~Return~
        +viewReturnsByCustomer(Customer customer) List~Return~
        +viewReturnsBySale(String saleId) List~Return~
        +calculateMonthlySales(int month, int year) double
        +calculateMonthlyReturns(int month, int year) double
        +generateMonthlyBalance(int month, int year) double
    }

    class SaleService {
        +findById(String id) Sale
        +viewAllSales() List~Sale~
    }

    class ProductService {
        +findById(String id) Product
        +restoreStock(String id, int quantity) void
    }

    class AccessoryService {
        +findById(String id) Accessory
        +restoreStock(String accessoryId, int quantity) void
    }

    class WarrantyService {
        +cancelWarranties(String productId, String saleId) double
    }

    %% ---------- ui ----------
    class ConsoleMenu {
        -ReturnService returnService
        -returnMenu() void
        -registerReturn() void
        -viewAllReturns() void
        -viewReturnsByCustomer() void
        -viewReturnsBySale() void
        -generateMonthlyBalance() void
    }

    %% ---------- relationships ----------
    Return "0..*" --> "1" Sale : originalSale
    Return "0..*" o-- "1..*" Product : returnedProducts
    Sale "0..*" --> "1" Customer : customer
    Sale "0..*" o-- "1..*" Product : products

    ReturnRepository ..> Return : saves / loads
    ReturnRepository --> SaleService : resolves sales
    ReturnRepository --> ProductService : resolves products
    ReturnRepository --> AccessoryService : resolves accessories

    ReturnService "1" --> "1" ReturnRepository : repository
    ReturnService --> SaleService : finds original sale
    ReturnService --> ProductService : restoreStock()
    ReturnService --> AccessoryService : restoreStock()
    ReturnService --> WarrantyService : cancelWarranties()
    ReturnService ..> Return : creates

    ConsoleMenu "1" --> "1" ReturnService : returnService
```

**Notes**

- `Return` is associated with exactly one original `Sale` and keeps only the products that were returned, which may be a subset of the sale's products.
- `ProductService.restoreStock` (added by the Technical Lead) is the existing stock operation reused from the return flow, and `AccessoryService.restoreStock` covers returned accessories.
- The monthly balance (`generateMonthlyBalance`) lives in `ReturnService` because it combines sales (through `SaleService`) and returns.
