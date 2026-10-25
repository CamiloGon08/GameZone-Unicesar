# Accessory Class Diagram — Requirement 1

This diagram shows the accessory module added in Requirement 1 and how it integrates with the existing system. `Accessory` extends `Product`, so accessories reuse the common product attributes and can be sold together with video games and consoles in a single `Sale`.

```mermaid
classDiagram
    direction TB

    %% ---------- model ----------
    class Product {
        <<abstract>>
        -String id
        -String title
        -double price
        -int stock
        +Product(String id, String title, double price, int stock)
        +getDescription()* String
        +adjustStock(int amount) void
        +getId() String
        +getPrice() double
        +getStock() int
    }

    class Console {
        -String brand
        -String model
        -String generation
        +getDescription() String
    }

    class Accessory {
        <<abstract>>
        -List~String~ compatibleConsoleIds
        +Accessory(String id, String title, double price, int stock)
        +addCompatibleConsole(String consoleId) void
        +getCompatibleConsoleIds() List~String~
        +isCompatibleWith(String consoleId) boolean
    }

    class Controller {
        -String connectionType
        +Controller(String id, String title, double price, int stock, String connectionType)
        +getDescription() String
        +getConnectionType() String
        +setConnectionType(String connectionType) void
    }

    class Cable {
        -double lengthInMeters
        -String connectorType
        +Cable(String id, String title, double price, int stock, double lengthInMeters, String connectorType)
        +getDescription() String
        +getLengthInMeters() double
        +setLengthInMeters(double lengthInMeters) void
        +getConnectorType() String
        +setConnectorType(String connectorType) void
    }

    class Memory {
        -int capacityInGb
        -String memoryType
        +Memory(String id, String title, double price, int stock, int capacityInGb, String memoryType)
        +getDescription() String
        +getCapacityInGb() int
        +setCapacityInGb(int capacityInGb) void
        +getMemoryType() String
        +setMemoryType(String memoryType) void
    }

    class Sale {
        -String id
        -LocalDate date
        -List~Product~ products
        -double total
        +getProducts() List~Product~
        +getTotal() double
    }

    %% ---------- persistence ----------
    class AccessoryRepository {
        -String CONTROLLER_TYPE$
        -String CABLE_TYPE$
        -String MEMORY_TYPE$
        +saveAll(List~Accessory~ accessories) void
        +loadAll() List~Accessory~
        -formatAccessory(Accessory accessory) String
        -createAccessory(String[] data) Accessory
    }

    %% ---------- service ----------
    class AccessoryService {
        -AccessoryRepository accessoryRepository
        +AccessoryService(AccessoryRepository accessoryRepository)
        +registerController(List~Accessory~ accessories, String id, String title, double price, int stock, String connectionType, List~String~ compatibleConsoleIds) void
        +registerCable(List~Accessory~ accessories, String id, String title, double price, int stock, double lengthInMeters, String connectorType) void
        +registerMemory(List~Accessory~ accessories, String id, String title, double price, int stock, int capacityInGb, String memoryType, List~String~ compatibleConsoleIds) void
        +listAllAccessories() List~Accessory~
        +listAccessoriesByType(String type) List~Accessory~
        +findAccessoriesCompatibleWith(String consoleId) List~Accessory~
        +findById(String id) Accessory
        +updateStock(List~Accessory~ accessories, String accessoryId, int quantity) void
        +restoreStock(String accessoryId, int quantity) void
        +saveAll(List~Accessory~ accessories) void
    }

    class ProductService {
        +updateStock(String id, int amount) void
    }

    class SaleService {
        -ProductService productService
        -AccessoryService accessoryService
        +registerSale(Customer customer, Seller seller, List~Product~ products, List~String~ productIdsWithExtendedWarranty) Sale
    }

    %% ---------- ui ----------
    class ConsoleMenu {
        -AccessoryService accessoryService
        -accessoryMenu() void
        -registerController() void
        -registerCable() void
        -registerMemory() void
        -listAllAccessories() void
        -listAccessoriesByType() void
        -findAccessoriesCompatibleWith() void
    }

    %% ---------- relationships ----------
    Product <|-- Console
    Product <|-- Accessory
    Accessory <|-- Controller
    Accessory <|-- Cable
    Accessory <|-- Memory

    Sale "0..*" o-- "1..*" Product : products
    Accessory "0..*" ..> "0..*" Console : compatible with (by console id)

    AccessoryRepository ..> Accessory : saves / loads
    AccessoryService "1" --> "1" AccessoryRepository : accessoryRepository
    AccessoryService ..> Accessory : creates
    SaleService "1" --> "1" AccessoryService : accessoryService
    SaleService "1" --> "1" ProductService : productService
    ConsoleMenu "1" --> "1" AccessoryService : accessoryService
    ConsoleMenu "1" --> "1" SaleService : saleService
```

**Notes**

- Compatibility is stored on the accessory side as a list of console IDs (`compatibleConsoleIds`), persisted in the accessories data file after the type-specific fields.
- `SaleService.registerSale` accepts a single `List<Product>` that may mix products and accessories. Stock is deducted through `AccessoryService.updateStock` for accessories and `ProductService.updateStock` for video games and consoles.
