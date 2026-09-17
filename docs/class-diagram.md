# Class Diagram

```mermaid
classDiagram
direction TB
    class Customer {
        -eMail: String
        -purchases: ArrayList<Sale>
    }

    class Seller {
        -employeeCode: String
        -shift: String
        -sales: ArrayLisy<Sale>
    }

    class Product {
        -id: String
        -title: String
        -price: double
        -stock: int
    }

    class Console {
        -brand: String
        -model: String
        -generation: String
    }

    class VideoGame {
        -platform: String
        -genre: String
        -ageRating: String
    }

    class Accesory {
    }

    class Controller {
        -connectionType: String
        -compatibleConsoles: ArrayList<Console>
    }

    class Cable {
        -length: double
        -connectorType: String
    }

    class Memory {
        -storageCapacity: int
        -memoryType: String
        -compatibleConsoles: ArrayList<Console>
    }

    class Promotion {
        -id: String
        -name: String
        -startDate: 
        -endDate:
        -discountPercentage: double
    }

    class PercentagePromotion {
        -totalAmount: double
    }

    class CategoryPromotion {
        -totalAmount: double
        -category: String
    }

    class VolumePromotion {
        -minimumQuantity: int
    }

    class Sale {
        -id: String
        -date: LocalDate
        -seller: Seller
        -customer: Customer
        -products: ArrayList<Product>
        -total: double
    }

    class Return {
        -id: String
        -date: LocalDate
        -originalSale: Sale
        -returnedProducts: ArrayList<Product>
        -reason: String
        -refundAmount: double
    }

    class Warranty {
        -startDate: LocalDate
        -endDate: LocalDate
        -associatedProduct: Product
    }

    class BasicWarranty {
        
    }

    class ExtendedWarranty {
        -warrantyCost: double

    }

    class Person {
	    -iD: String
        -name: String
        -contactNumber: long
    }

	<<abstract>> Product
	<<abstract>> Accesory
	<<abstract>> Promotion
	<<abstract>> Warranty
	<<abstract>> Person

    Person <|-- Customer
    Person <|-- Seller
    Product <|-- Console
    Product <|-- VideoGame
    Product <|-- Accesory
    Accesory <|-- Controller
    Accesory <|-- Cable
    Accesory <|-- Memory
    Promotion <|-- PercentagePromotion
    Promotion <|-- CategoryPromotion
    Promotion <|-- VolumePromotion
    Warranty <|-- BasicWarranty
    Warranty <|-- ExtendedWarranty
