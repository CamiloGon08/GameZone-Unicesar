# Class Diagram

```mermaid
classDiagram
direction TB
    classDiagram
    direction TB

    %% ==========================================
    %% PACKAGE MODEL
    %% ==========================================
    
        class Person {
            <<abstract>>
            #String id
            #String name
            #String phone
        }

        class Customer {
            -String email
            -List~Sale~ purchaseHistory
            +addSale(Sale sale) void
        }

        class Seller {
            -String employeeCode
            -String shift
        }

        class Product {
            <<abstract>>
            #String id
            #String title
            #double price
            #int stock
            +getDescription()* String
        }

        class Videogame {
            -String platform
            -String genre
            -String ageRating
            +getDescription() String
        }

        class Console {
            -String brand
            -String model
            -String generation
            +getDescription() String
        }

        class Accessory {
            <<abstract>>
        }

        class Controller {
            -String connectionType
            -List~Console~ compatibleConsoles
            +isCompatibleWith(Console console) boolean
            +getDescription() String
        }

        class Cable {
            -double length
            -String connectorType
            +getDescription() String
        }

        class Memory {
            -int storageCapacity
            -String memoryType
            -List~Console~ compatibleConsoles
            +isCompatibleWith(Console console) boolean
            +getDescription() String
        }

        class Sale {
            -String id
            -LocalDate date
            -Customer customer
            -Seller seller
            -List~Product~ products
            -double total
            -String appliedPromotionName
            -double discountAmount
            +calculateSubtotal() double
            +canBeReturned() boolean
            +generateReceipt() String
        }

        class Promotion {
            <<abstract>>
            #String id
            #String name
            #LocalDate startDate
            #LocalDate endDate
            +isActive(LocalDate date) boolean
            +calculateDiscount(Sale sale)* double
        }

        class PercentageDiscount {
            -double discountPercentage
            +calculateDiscount(Sale sale) double
        }

        class CategoryDiscount {
            -double discountPercentage
            -String targetCategory
            +calculateDiscount(Sale sale) double
        }

        class BulkPurchaseDiscount {
            -int minQuantity
            -double discountPercentage
            +calculateDiscount(Sale sale) double
        }

        class Return {
            -String id
            -LocalDate returnDate
            -Sale originalSale
            -List~Product~ returnedProducts
            -String reason
            -double refundedAmount
            +calculateRefundAmount() double
            +generateReturnReceipt() String
        }

        class Warranty {
            <<abstract>>
            #String id
            #Product product
            #Sale sale
            #LocalDate startDate
            #LocalDate endDate
            +isActive(LocalDate date) boolean
            +generateWarrantyCertificate() String
            +getDurationInMonths()* int
            +getWarrantyType()* String
            +getAdditionalCost()* double
        }

        class BasicWarranty {
            +getDurationInMonths() int
            +getWarrantyType() String
            +getAdditionalCost() double
        }

        class ExtendedWarranty {
            +getDurationInMonths() int
            +getWarrantyType() String
            +getAdditionalCost() double
        }
    

    %% ==========================================
    %% PACKAGE PERSISTENCE
    %% ==========================================
    
        class PersonRepository {
            -String filePath
            +saveAll(List~Person~ persons) void
            +loadAll() List~Person~
        }

        class ProductRepository {
            -String filePath
            +saveAll(List~Product~ products) void
            +loadAll() List~Product~
        }

        class AccessoryRepository {
            -String filePath
            +saveAll(List~Accessory~ accessories) void
            +loadAll() List~Accessory~
        }

        class SaleRepository {
            -String filePath
            +saveAll(List~Sale~ sales) void
            +loadAll() List~Sale~
        }

        class PromotionRepository {
            -String filePath
            +saveAll(List~Promotion~ promotions) void
            +loadAll() List~Promotion~
        }

        class ReturnRepository {
            -String filePath
            +saveAll(List~Return~ returns) void
            +loadAll() List~Return~
        }

        class WarrantyRepository {
            -String filePath
            +saveAll(List~Warranty~ warranties) void
            +loadAll() List~Warranty~
        }
    

    %% ==========================================
    %% PACKAGE SERVICE
    %% ==========================================
    
        class PersonService {
            -PersonRepository personRepository
            +registerCustomer(Customer customer) void
            +registerSeller(Seller seller) void
            +listAllCustomers() List~Customer~
            +listAllSellers() List~Seller~
            +findPersonById(String id) Person
        }

        class ProductService {
            -ProductRepository productRepository
            +registerProduct(Product product) void
            +listAllProducts() List~Product~
            +findProductById(String id) Product
            +updateStock(String productId, int quantity) void
            +restoreStock(String productId, int quantity) void
        }

        class AccessoryService {
            -AccessoryRepository accessoryRepository
            +registerAccessory(Accessory accessory) void
            +listAllAccessories() List~Accessory~
            +listAccessoriesByType(String type) List~Accessory~
            +listCompatibleAccessories(Console console) List~Accessory~
            +findAccessoryById(String id) Accessory
            +updateStock(String accessoryId, int quantity) void
            +restoreStock(String accessoryId, int quantity) void
        }

        class PromotionService {
            -PromotionRepository promotionRepository
            +registerPercentageDiscount(PercentageDiscount promotion) void
            +registerCategoryDiscount(CategoryDiscount promotion) void
            +registerBulkPurchaseDiscount(BulkPurchaseDiscount promotion) void
            +listAllPromotions() List~Promotion~
            +listActivePromotions() List~Promotion~
            +findBestPromotionFor(Sale sale) Promotion
            +findById(String id) Promotion
        }

        class WarrantyService {
            -WarrantyRepository warrantyRepository
            +assignBasicWarranty(Product product, Sale sale, LocalDate startDate) BasicWarranty
            +assignExtendedWarranty(Product product, Sale sale, LocalDate startDate) ExtendedWarranty
            +findWarrantyByProduct(String productId, String saleId) Warranty
            +listAllWarranties() List~Warranty~
            +listActiveWarranties() List~Warranty~
            +listWarrantiesExpiringSoon(int daysAhead) List~Warranty~
        }

        class SaleService {
            -SaleRepository saleRepository
            -ProductService productService
            -AccessoryService accessoryService
            -PersonService personService
            -PromotionService promotionService
            -WarrantyService warrantyService
            +registerSale(String customerId, String sellerId, List~String~ itemIds, List~String~ productIdsWithExtendedWarranty) Sale
            +listAllSales() List~Sale~
            +listSalesByCustomer(String customerId) List~Customer~
            +listSalesBySeller(String sellerId) List~Seller~
            +findSaleById(String id) Sale
        }

        class ReturnService {
            -ReturnRepository returnRepository
            -SaleService saleService
            -ProductService productService
            +registerReturn(String saleId, List~String~ productIds, String reason) Return
            +viewAllReturns() List~Return~
            +viewReturnsByCustomer(String customerId) List~Return~
            +viewReturnsBySale(String saleId) List~Return~
            +generateMonthlyBalance(int month, int year) double
        }
    

    %% ==========================================
    %% PACKAGE UI
    %% ==========================================
    
        class ConsoleMenu {
            -PersonService personService
            -ProductService productService
            -AccessoryService accessoryService
            -SaleService saleService
            -PromotionService promotionService
            -ReturnService returnService
            -WarrantyService warrantyService
            +start() void
        }
    

    %% ==========================================
    %% RELACIONES DE HERENCIA
    %% ==========================================
    Person <|-- Customer
    Person <|-- Seller

    Product <|-- Videogame
    Product <|-- Console
    Product <|-- Accessory

    Accessory <|-- Controller
    Accessory <|-- Cable
    Accessory <|-- Memory

    Promotion <|-- PercentageDiscount
    Promotion <|-- CategoryDiscount
    Promotion <|-- BulkPurchaseDiscount

    Warranty <|-- BasicWarranty
    Warranty <|-- ExtendedWarranty

    %% ==========================================
    %% RELACIONES DEL MODELO DE DOMINIO
    %% ==========================================
    Sale "1" --> "1" Customer : customer
    Sale "1" --> "1" Seller : seller
    Sale "*" --> "1..*" Product : products

    Return "*" --> "1" Sale : originalSale
    Return "*" --> "*" Product : returnedProducts

    Warranty "*" --> "1" Product : product
    Warranty "*" --> "1" Sale : sale

    Controller "*" --> "*" Console : compatibleConsoles
    Memory "*" --> "*" Console : compatibleConsoles

    %% ==========================================
    %% DEPENDENCIAS Y ASOCIACIONES ENTRE CAPAS
    %% ==========================================
    ConsoleMenu --> PersonService
    ConsoleMenu --> ProductService
    ConsoleMenu --> AccessoryService
    ConsoleMenu --> SaleService
    ConsoleMenu --> PromotionService
    ConsoleMenu --> ReturnService
    ConsoleMenu --> WarrantyService

    PersonService --> PersonRepository
    ProductService --> ProductRepository
    AccessoryService --> AccessoryRepository
    PromotionService --> PromotionRepository
    WarrantyService --> WarrantyRepository

    SaleService --> SaleRepository
    SaleService --> ProductService
    SaleService --> AccessoryService
    SaleService --> PersonService
    SaleService --> PromotionService
    SaleService --> WarrantyService

    ReturnService --> ReturnRepository
    ReturnService --> SaleService
    ReturnService --> ProductService